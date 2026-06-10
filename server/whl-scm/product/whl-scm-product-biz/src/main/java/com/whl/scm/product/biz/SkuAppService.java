package com.whl.scm.product.biz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.framework.core.exception.ServiceException;
import com.whl.framework.core.service.DictService;
import com.whl.framework.core.utils.StringUtils;
import com.whl.scm.product.api.command.CreateSkuCommand;
import com.whl.scm.product.api.command.DeleteSkuCommand;
import com.whl.scm.product.api.command.UpdateSkuCommand;
import com.whl.scm.product.api.dto.SkuBarcodeDto;
import com.whl.scm.product.api.dto.SkuDto;
import com.whl.scm.product.api.error.ProductErrorCode;
import com.whl.scm.product.api.facade.SkuFacade;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetSkuQuery;
import com.whl.scm.product.api.query.PageSkuQuery;
import com.whl.scm.product.api.query.ResolveBarcodeQuery;
import com.whl.scm.product.api.query.SearchSkuQuery;
import com.whl.scm.product.api.query.ValidateSkuQuery;
import com.whl.scm.product.biz.domain.PrdBrand;
import com.whl.scm.product.biz.domain.PrdCategory;
import com.whl.scm.product.biz.domain.PrdSku;
import com.whl.scm.product.biz.domain.PrdSkuBarcode;
import com.whl.scm.product.biz.mapper.PrdBrandMapper;
import com.whl.scm.product.biz.mapper.PrdCategoryMapper;
import com.whl.scm.product.biz.mapper.PrdSkuBarcodeMapper;
import com.whl.scm.product.biz.mapper.PrdSkuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 货品应用服务，实现 {@link SkuFacade}。
 *
 * @author whl
 */
@Service
@RequiredArgsConstructor
public class SkuAppService implements SkuFacade {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final PrdSkuMapper skuMapper;
    private final PrdSkuBarcodeMapper barcodeMapper;
    private final PrdCategoryMapper categoryMapper;
    private final PrdBrandMapper brandMapper;
    private final DictService dictService;

    @Override
    public ScmPageResult<SkuDto> pageSku(PageSkuQuery query) {
        Page<PrdSku> page = skuMapper.selectPage(buildPage(query), buildQueryWrapper(query));
        Map<Long, String> categoryNames = loadCategoryNames(query.getTenantId());
        Map<Long, String> brandNames = loadBrandNames(query.getTenantId());
        List<SkuDto> rows = page.getRecords().stream()
            .map(entity -> toDto(entity, categoryNames, brandNames, null))
            .toList();
        ScmPageResult<SkuDto> result = new ScmPageResult<>();
        result.setRows(rows);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public SkuDto getSkuDetail(GetSkuQuery query) {
        PrdSku entity = requireSku(query.getTenantId(), query.getSkuId(), query.getSkuCode());
        return toDto(entity, loadCategoryNames(query.getTenantId()), loadBrandNames(query.getTenantId()),
            loadBarcodes(query.getTenantId(), entity.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SkuDto createSku(CreateSkuCommand command) {
        PrdSku entity = new PrdSku();
        applyCommandToEntity(entity, command);
        entity.setTenantId(command.getTenantId());
        entity.setSkuCode(command.getSkuCode());
        entity.setStatus(command.getStatus() == null ? ProductConstants.STATUS_SKU_ENABLED : command.getStatus());
        validateAndNormalizeSku(entity, command.getBarcodes(), true);
        try {
            skuMapper.insert(entity);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_CODE_DUPLICATE.message());
        }
        saveBarcodes(command.getTenantId(), entity.getId(), command.getBarcodes());
        syncPrimaryBarcode(entity);
        return getSkuDetail(buildGetQuery(command.getTenantId(), entity.getId(), null));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SkuDto updateSku(UpdateSkuCommand command) {
        PrdSku entity = requireById(command.getTenantId(), command.getId());
        applyUpdateCommandToEntity(entity, command);
        validateAndNormalizeSku(entity, command.getBarcodes(), false);
        skuMapper.updateById(entity);
        replaceBarcodes(command.getTenantId(), entity.getId(), command.getBarcodes());
        syncPrimaryBarcode(entity);
        return getSkuDetail(buildGetQuery(command.getTenantId(), entity.getId(), null));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSku(DeleteSkuCommand command) {
        if (StrUtil.hasBlank(command.getTenantId()) || command.getId() == null) {
            throw new ServiceException("删除货品参数不完整");
        }
        requireById(command.getTenantId(), command.getId());
        barcodeMapper.delete(
            Wrappers.<PrdSkuBarcode>lambdaQuery()
                .eq(PrdSkuBarcode::getTenantId, command.getTenantId())
                .eq(PrdSkuBarcode::getSkuId, command.getId())
        );
        skuMapper.deleteById(command.getId());
    }

    public void validateSkuReference(ValidateSkuQuery query) {
        if (StrUtil.isBlank(query.getSkuCode())) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        PrdSku sku = skuMapper.selectOne(
            Wrappers.<PrdSku>lambdaQuery()
                .eq(PrdSku::getTenantId, query.getTenantId())
                .eq(PrdSku::getSkuCode, query.getSkuCode())
        );
        if (sku == null) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        if (!Objects.equals(ProductConstants.STATUS_SKU_ENABLED, sku.getStatus())) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_DISABLED.message());
        }
        assertCategoryEnabled(query.getTenantId(), sku.getCategoryId());
        assertBrandEnabledIfPresent(query.getTenantId(), sku.getBrandId());
        assertProductTypeRules(sku);
        assertCapability(sku, query.getRequiredCapability());
    }

    public com.whl.scm.product.api.dto.ResolveBarcodeResult resolveBarcode(ResolveBarcodeQuery query) {
        if (StrUtil.isBlank(query.getBarcode())) {
            throw new ServiceException(ProductErrorCode.PRD_BARCODE_NOT_FOUND.message());
        }
        PrdSkuBarcode barcode = barcodeMapper.selectOne(
            Wrappers.<PrdSkuBarcode>lambdaQuery()
                .eq(PrdSkuBarcode::getTenantId, query.getTenantId())
                .eq(PrdSkuBarcode::getBarcode, query.getBarcode())
                .eq(PrdSkuBarcode::getStatus, ProductConstants.BARCODE_ENABLED)
        );
        if (barcode == null) {
            throw new ServiceException(ProductErrorCode.PRD_BARCODE_NOT_FOUND.message());
        }
        PrdSku sku = requireById(query.getTenantId(), barcode.getSkuId());
        com.whl.scm.product.api.dto.ResolveBarcodeResult result =
            new com.whl.scm.product.api.dto.ResolveBarcodeResult();
        result.setSkuCode(sku.getSkuCode());
        return result;
    }

    public ScmPageResult<SkuDto> searchSku(SearchSkuQuery query) {
        Set<Long> skuIdsFromBarcode = findSkuIdsByBarcodeKeyword(query.getTenantId(), query.getKeyword());
        LambdaQueryWrapper<PrdSku> lqw = Wrappers.lambdaQuery();
        lqw.eq(PrdSku::getTenantId, query.getTenantId());
        lqw.eq(PrdSku::getStatus, ProductConstants.STATUS_SKU_ENABLED);
        if (StringUtils.isNotBlank(query.getKeyword())) {
            lqw.and(w -> {
                w.like(PrdSku::getSkuCode, query.getKeyword())
                    .or().like(PrdSku::getSkuName, query.getKeyword());
                if (!skuIdsFromBarcode.isEmpty()) {
                    w.or().in(PrdSku::getId, skuIdsFromBarcode);
                }
            });
        }
        lqw.orderByDesc(PrdSku::getId);
        Page<PrdSku> page = skuMapper.selectPage(buildSearchPage(query), lqw);
        Map<Long, String> categoryNames = loadCategoryNames(query.getTenantId());
        Map<Long, String> brandNames = loadBrandNames(query.getTenantId());
        List<SkuDto> rows = page.getRecords().stream()
            .map(entity -> toDto(entity, categoryNames, brandNames, null))
            .toList();
        ScmPageResult<SkuDto> result = new ScmPageResult<>();
        result.setRows(rows);
        result.setTotal(page.getTotal());
        return result;
    }

    private Set<Long> findSkuIdsByBarcodeKeyword(String tenantId, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return Set.of();
        }
        return barcodeMapper.selectList(
            Wrappers.<PrdSkuBarcode>lambdaQuery()
                .eq(PrdSkuBarcode::getTenantId, tenantId)
                .like(PrdSkuBarcode::getBarcode, keyword)
        ).stream().map(PrdSkuBarcode::getSkuId).collect(Collectors.toSet());
    }

    private void validateAndNormalizeSku(PrdSku entity, List<SkuBarcodeDto> barcodes, boolean isCreate) {
        if (StrUtil.hasBlank(entity.getSkuCode(), entity.getSkuName(), entity.getUnitCode())
            || entity.getCategoryId() == null || entity.getCategoryId() == 0
            || entity.getItemClass() == null || entity.getItemClass() == 0
            || entity.getProductType() == null || entity.getProductType() == 0) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：请填写必填项");
        }
        if (entity.getItemClass() == ProductConstants.ITEM_CLASS_SERVICE
            || entity.getItemClass() == ProductConstants.ITEM_CLASS_SEMI) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：不支持的货品类型");
        }
        assertDictValue(ProductConstants.DICT_UNIT_CODE, entity.getUnitCode(), "计量单位无效");
        assertTaxRateIfPresent(entity.getInputTaxRate());
        assertTaxRateIfPresent(entity.getOutputTaxRate());
        assertCategoryEnabled(entity.getTenantId(), entity.getCategoryId());
        assertBrandEnabledIfPresent(entity.getTenantId(), entity.getBrandId());
        assertItemClassProductType(entity);
        assertProductTypeRules(entity);
        assertExpiryFields(entity);
        assertNearExpiry(entity);
        if (entity.getItemClass() == ProductConstants.ITEM_CLASS_DRUG
            && !Objects.equals(1, entity.getExpiryFlag())) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：药品必须为效期品");
        }
        List<SkuBarcodeDto> safeBarcodes = barcodes == null ? List.of() : barcodes;
        validateBarcodes(entity, safeBarcodes, isCreate);
        String primaryBarcode = resolvePrimaryBarcode(safeBarcodes);
        entity.setPrimaryBarcode(primaryBarcode == null ? "" : primaryBarcode);
    }

    private void assertItemClassProductType(PrdSku entity) {
        if (entity.getItemClass() == ProductConstants.ITEM_CLASS_VIRTUAL) {
            if (!Objects.equals(ProductConstants.PRODUCT_TYPE_VIRTUAL, entity.getProductType())) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：虚拟货品须为虚拟履约类型");
            }
        } else {
            if (!Objects.equals(ProductConstants.PRODUCT_TYPE_PHYSICAL, entity.getProductType())) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：贸易/物料/包材/药品须为实物履约类型");
            }
        }
    }

    private void assertProductTypeRules(PrdSku entity) {
        if (Objects.equals(ProductConstants.PRODUCT_TYPE_PHYSICAL, entity.getProductType())) {
            if (entity.getExpiryFlag() == null || (entity.getExpiryFlag() != 0 && entity.getExpiryFlag() != 1)) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：实物货品效期标识无效");
            }
            if (!Objects.equals(0, entity.getQuotaFlag())) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：实物货品不可启用配额");
            }
        } else if (Objects.equals(ProductConstants.PRODUCT_TYPE_VIRTUAL, entity.getProductType())) {
            if (!Objects.equals(0, entity.getExpiryFlag())) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：虚拟货品不可启用效期");
            }
            if (entity.getQuotaFlag() == null || (entity.getQuotaFlag() != 0 && entity.getQuotaFlag() != 1)) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：虚拟货品配额标识无效");
            }
        }
    }

    private void assertExpiryFields(PrdSku entity) {
        if (Objects.equals(1, entity.getExpiryFlag())) {
            if (entity.getShelfLifeValue() == null || entity.getShelfLifeValue() <= 0
                || !isValidShelfLifeUnit(entity.getShelfLifeUnit())) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：效期品请填写保质期");
            }
        } else {
            entity.setShelfLifeValue(0);
            entity.setShelfLifeUnit(0);
            entity.setNearExpiryValue(0);
            entity.setNearExpiryUnit(0);
        }
    }

    private void assertNearExpiry(PrdSku entity) {
        if (entity.getNearExpiryValue() != null && entity.getNearExpiryValue() > 0) {
            if (!isValidShelfLifeUnit(entity.getNearExpiryUnit())) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：临期预警单位无效");
            }
            if (Objects.equals(1, entity.getExpiryFlag())
                && entity.getShelfLifeValue() != null
                && entity.getNearExpiryValue() >= entity.getShelfLifeValue()) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：临期预警须小于保质期");
            }
        }
    }

    private void validateBarcodes(PrdSku entity, List<SkuBarcodeDto> barcodes, boolean isCreate) {
        int enabledCount = 0;
        int defaultCount = 0;
        Set<String> seen = new HashSet<>();
        for (SkuBarcodeDto dto : barcodes) {
            if (dto == null || StrUtil.isBlank(dto.getBarcode())) {
                continue;
            }
            String code = dto.getBarcode().trim();
            if (!seen.add(code)) {
                throw new ServiceException(ProductErrorCode.PRD_BARCODE_DUPLICATE.message());
            }
            PrdSkuBarcode existing = barcodeMapper.selectOne(
                Wrappers.<PrdSkuBarcode>lambdaQuery()
                    .eq(PrdSkuBarcode::getTenantId, entity.getTenantId())
                    .eq(PrdSkuBarcode::getBarcode, code)
            );
            if (existing != null && (isCreate || !Objects.equals(existing.getSkuId(), entity.getId()))) {
                throw new ServiceException(ProductErrorCode.PRD_BARCODE_DUPLICATE.message());
            }
            int status = dto.getStatus() == null ? ProductConstants.BARCODE_ENABLED : dto.getStatus();
            if (status == ProductConstants.BARCODE_ENABLED) {
                enabledCount++;
            }
            int defaultFlag = dto.getDefaultFlag() == null ? 0 : dto.getDefaultFlag();
            if (defaultFlag == ProductConstants.BARCODE_DEFAULT) {
                defaultCount++;
            }
        }
        if (defaultCount > 1) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：每条货品最多一个默认条码");
        }
        if (Objects.equals(ProductConstants.BARCODE_POLICY_REQUIRED, entity.getBarcodePolicy()) && enabledCount < 1) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：必须有国标码时请至少维护一条启用条码");
        }
    }

    private String resolvePrimaryBarcode(List<SkuBarcodeDto> barcodes) {
        for (SkuBarcodeDto dto : barcodes) {
            if (dto != null && Objects.equals(ProductConstants.BARCODE_DEFAULT, dto.getDefaultFlag())
                && StrUtil.isNotBlank(dto.getBarcode())) {
                return dto.getBarcode().trim();
            }
        }
        return "";
    }

    private void saveBarcodes(String tenantId, Long skuId, List<SkuBarcodeDto> barcodes) {
        if (CollUtil.isEmpty(barcodes)) {
            return;
        }
        for (SkuBarcodeDto dto : barcodes) {
            if (dto == null || StrUtil.isBlank(dto.getBarcode())) {
                continue;
            }
            PrdSkuBarcode entity = new PrdSkuBarcode();
            entity.setTenantId(tenantId);
            entity.setSkuId(skuId);
            entity.setBarcode(dto.getBarcode().trim());
            entity.setDefaultFlag(dto.getDefaultFlag() == null ? 0 : dto.getDefaultFlag());
            entity.setStatus(dto.getStatus() == null ? ProductConstants.BARCODE_ENABLED : dto.getStatus());
            entity.setRemark(dto.getRemark());
            try {
                barcodeMapper.insert(entity);
            } catch (DuplicateKeyException ex) {
                throw new ServiceException(ProductErrorCode.PRD_BARCODE_DUPLICATE.message());
            }
        }
    }

    private void replaceBarcodes(String tenantId, Long skuId, List<SkuBarcodeDto> barcodes) {
        barcodeMapper.delete(
            Wrappers.<PrdSkuBarcode>lambdaQuery()
                .eq(PrdSkuBarcode::getTenantId, tenantId)
                .eq(PrdSkuBarcode::getSkuId, skuId)
        );
        saveBarcodes(tenantId, skuId, barcodes);
    }

    private void syncPrimaryBarcode(PrdSku entity) {
        skuMapper.updateById(entity);
    }

    private void assertCategoryEnabled(String tenantId, Long categoryId) {
        if (categoryId == null || categoryId == 0) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：请选择启用的货品类目");
        }
        PrdCategory category = categoryMapper.selectOne(
            Wrappers.<PrdCategory>lambdaQuery()
                .eq(PrdCategory::getTenantId, tenantId)
                .eq(PrdCategory::getId, categoryId)
        );
        if (category == null) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        if (!Objects.equals(ProductConstants.STATUS_CATEGORY_ENABLED, category.getStatus())) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：类目已停用");
        }
    }

    private void assertBrandEnabledIfPresent(String tenantId, Long brandId) {
        if (brandId == null || brandId == 0) {
            return;
        }
        PrdBrand brand = brandMapper.selectOne(
            Wrappers.<PrdBrand>lambdaQuery()
                .eq(PrdBrand::getTenantId, tenantId)
                .eq(PrdBrand::getId, brandId)
        );
        if (brand == null) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        if (!Objects.equals(ProductConstants.STATUS_BRAND_ENABLED, brand.getStatus())) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：品牌已停用");
        }
    }

    private void assertCapability(PrdSku sku, String requiredCapability) {
        if (StrUtil.isBlank(requiredCapability)) {
            return;
        }
        switch (requiredCapability) {
            case ProductConstants.CAPABILITY_PURCHASE -> {
                if (!Objects.equals(1, sku.getPurchaseAllowed())) {
                    throw new ServiceException(ProductErrorCode.PRD_SKU_DISABLED.message());
                }
            }
            case ProductConstants.CAPABILITY_SALE -> {
                if (!Objects.equals(1, sku.getSaleAllowed())) {
                    throw new ServiceException(ProductErrorCode.PRD_SKU_DISABLED.message());
                }
            }
            case ProductConstants.CAPABILITY_ISSUE -> {
                if (!Objects.equals(1, sku.getIssueAllowed())) {
                    throw new ServiceException(ProductErrorCode.PRD_SKU_DISABLED.message());
                }
            }
            default -> throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：未知能力类型");
        }
    }

    private void assertDictValue(String dictType, String dictValue, String message) {
        if (StringUtils.isBlank(dictValue)) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：" + message);
        }
        if (StringUtils.isBlank(dictService.getDictLabel(dictType, dictValue))) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：" + message);
        }
    }

    private void assertTaxRateIfPresent(String taxRate) {
        if (StringUtils.isNotBlank(taxRate)
            && StringUtils.isBlank(dictService.getDictLabel(ProductConstants.DICT_TAX_RATE, taxRate))) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：税率无效");
        }
    }

    private boolean isValidShelfLifeUnit(Integer unit) {
        return unit != null && (unit == ProductConstants.SHELF_LIFE_UNIT_HOUR
            || unit == ProductConstants.SHELF_LIFE_UNIT_DAY);
    }

    private PrdSku requireSku(String tenantId, Long skuId, String skuCode) {
        if (skuId != null) {
            return requireById(tenantId, skuId);
        }
        if (StrUtil.isNotBlank(skuCode)) {
            PrdSku sku = skuMapper.selectOne(
                Wrappers.<PrdSku>lambdaQuery()
                    .eq(PrdSku::getTenantId, tenantId)
                    .eq(PrdSku::getSkuCode, skuCode)
            );
            if (sku != null) {
                return sku;
            }
        }
        throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
    }

    private PrdSku requireById(String tenantId, Long id) {
        PrdSku entity = skuMapper.selectOne(
            Wrappers.<PrdSku>lambdaQuery()
                .eq(PrdSku::getTenantId, tenantId)
                .eq(PrdSku::getId, id)
        );
        if (entity == null) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        return entity;
    }

    private List<SkuBarcodeDto> loadBarcodes(String tenantId, Long skuId) {
        return barcodeMapper.selectList(
            Wrappers.<PrdSkuBarcode>lambdaQuery()
                .eq(PrdSkuBarcode::getTenantId, tenantId)
                .eq(PrdSkuBarcode::getSkuId, skuId)
                .orderByDesc(PrdSkuBarcode::getDefaultFlag)
                .orderByAsc(PrdSkuBarcode::getId)
        ).stream().map(this::toBarcodeDto).toList();
    }

    private Map<Long, String> loadCategoryNames(String tenantId) {
        List<PrdCategory> list = categoryMapper.selectList(
            Wrappers.<PrdCategory>lambdaQuery()
                .eq(PrdCategory::getTenantId, tenantId)
                .select(PrdCategory::getId, PrdCategory::getCategoryName)
        );
        Map<Long, String> map = new HashMap<>(list.size());
        for (PrdCategory c : list) {
            map.put(c.getId(), c.getCategoryName());
        }
        return map;
    }

    private Map<Long, String> loadBrandNames(String tenantId) {
        List<PrdBrand> list = brandMapper.selectList(
            Wrappers.<PrdBrand>lambdaQuery()
                .eq(PrdBrand::getTenantId, tenantId)
                .select(PrdBrand::getId, PrdBrand::getBrandName)
        );
        Map<Long, String> map = new HashMap<>(list.size());
        for (PrdBrand b : list) {
            map.put(b.getId(), b.getBrandName());
        }
        return map;
    }

    private LambdaQueryWrapper<PrdSku> buildQueryWrapper(PageSkuQuery query) {
        LambdaQueryWrapper<PrdSku> lqw = Wrappers.lambdaQuery();
        lqw.eq(PrdSku::getTenantId, query.getTenantId());
        lqw.eq(StringUtils.isNotBlank(query.getSkuCode()), PrdSku::getSkuCode, query.getSkuCode());
        lqw.like(StringUtils.isNotBlank(query.getSkuName()), PrdSku::getSkuName, query.getSkuName());
        lqw.eq(query.getCategoryId() != null, PrdSku::getCategoryId, query.getCategoryId());
        lqw.eq(query.getItemClass() != null, PrdSku::getItemClass, query.getItemClass());
        lqw.eq(query.getProductType() != null, PrdSku::getProductType, query.getProductType());
        lqw.eq(query.getStatus() != null, PrdSku::getStatus, query.getStatus());
        lqw.orderByAsc(PrdSku::getSortOrder);
        lqw.orderByDesc(PrdSku::getId);
        return lqw;
    }

    private Page<PrdSku> buildPage(PageSkuQuery query) {
        int num = query.getPageNum() == null || query.getPageNum() <= 0 ? DEFAULT_PAGE_NUM : query.getPageNum();
        int size = query.getPageSize() == null || query.getPageSize() <= 0 ? DEFAULT_PAGE_SIZE : query.getPageSize();
        return new Page<>(num, size);
    }

    private Page<PrdSku> buildSearchPage(SearchSkuQuery query) {
        int num = query.getPageNum() == null || query.getPageNum() <= 0 ? DEFAULT_PAGE_NUM : query.getPageNum();
        int size = query.getPageSize() == null || query.getPageSize() <= 0 ? DEFAULT_PAGE_SIZE : query.getPageSize();
        return new Page<>(num, size);
    }

    private GetSkuQuery buildGetQuery(String tenantId, Long skuId, String skuCode) {
        GetSkuQuery query = new GetSkuQuery();
        query.setTenantId(tenantId);
        query.setSkuId(skuId);
        query.setSkuCode(skuCode);
        return query;
    }

    private void applyCommandToEntity(PrdSku entity, CreateSkuCommand command) {
        entity.setSkuName(command.getSkuName());
        entity.setSkuShortName(command.getSkuShortName());
        entity.setSpec(command.getSpec());
        entity.setSortOrder(command.getSortOrder() == null ? 0 : command.getSortOrder());
        entity.setRemark(command.getRemark());
        entity.setCategoryId(command.getCategoryId());
        entity.setBrandId(command.getBrandId() == null ? 0L : command.getBrandId());
        entity.setUnitCode(command.getUnitCode());
        entity.setItemClass(command.getItemClass());
        entity.setPurchaseAllowed(command.getPurchaseAllowed() == null ? 1 : command.getPurchaseAllowed());
        entity.setSaleAllowed(command.getSaleAllowed() == null ? 1 : command.getSaleAllowed());
        entity.setIssueAllowed(command.getIssueAllowed() == null ? 0 : command.getIssueAllowed());
        entity.setProductType(command.getProductType());
        entity.setExpiryFlag(command.getExpiryFlag() == null ? 0 : command.getExpiryFlag());
        entity.setQuotaFlag(command.getQuotaFlag() == null ? 0 : command.getQuotaFlag());
        entity.setShelfLifeValue(command.getShelfLifeValue() == null ? 0 : command.getShelfLifeValue());
        entity.setShelfLifeUnit(command.getShelfLifeUnit() == null ?  0 : command.getShelfLifeUnit());
        entity.setNearExpiryValue(command.getNearExpiryValue() == null ? 0 : command.getNearExpiryValue());
        entity.setNearExpiryUnit(command.getNearExpiryUnit() == null ? 0 : command.getNearExpiryUnit());
        entity.setDefaultOriginCode(command.getDefaultOriginCode());
        entity.setTaxCategoryCode(command.getTaxCategoryCode());
        entity.setInputTaxRate(command.getInputTaxRate());
        entity.setOutputTaxRate(command.getOutputTaxRate());
        entity.setBarcodePolicy(command.getBarcodePolicy() == null
            ? ProductConstants.BARCODE_POLICY_OPTIONAL : command.getBarcodePolicy());
    }

    private void applyUpdateCommandToEntity(PrdSku entity, UpdateSkuCommand command) {
        entity.setSkuName(command.getSkuName());
        entity.setSkuShortName(command.getSkuShortName());
        entity.setSpec(command.getSpec());
        if (command.getStatus() != null) {
            entity.setStatus(command.getStatus());
        }
        if (command.getSortOrder() != null) {
            entity.setSortOrder(command.getSortOrder());
        }
        entity.setRemark(command.getRemark());
        entity.setCategoryId(command.getCategoryId());
        entity.setBrandId(command.getBrandId() == null ? 0L : command.getBrandId());
        entity.setUnitCode(command.getUnitCode());
        entity.setItemClass(command.getItemClass());
        if (command.getPurchaseAllowed() != null) {
            entity.setPurchaseAllowed(command.getPurchaseAllowed());
        }
        if (command.getSaleAllowed() != null) {
            entity.setSaleAllowed(command.getSaleAllowed());
        }
        if (command.getIssueAllowed() != null) {
            entity.setIssueAllowed(command.getIssueAllowed());
        }
        entity.setProductType(command.getProductType());
        entity.setExpiryFlag(command.getExpiryFlag());
        entity.setQuotaFlag(command.getQuotaFlag());
        entity.setShelfLifeValue(command.getShelfLifeValue());
        entity.setShelfLifeUnit(command.getShelfLifeUnit());
        entity.setNearExpiryValue(command.getNearExpiryValue());
        entity.setNearExpiryUnit(command.getNearExpiryUnit());
        entity.setDefaultOriginCode(command.getDefaultOriginCode());
        entity.setTaxCategoryCode(command.getTaxCategoryCode());
        entity.setInputTaxRate(command.getInputTaxRate());
        entity.setOutputTaxRate(command.getOutputTaxRate());
        if (command.getBarcodePolicy() != null) {
            entity.setBarcodePolicy(command.getBarcodePolicy());
        }
    }

    private SkuDto toDto(PrdSku entity, Map<Long, String> categoryNames, Map<Long, String> brandNames,
                         List<SkuBarcodeDto> barcodes) {
        if (entity == null) {
            return null;
        }
        SkuDto dto = new SkuDto();
        dto.setSkuId(entity.getId());
        dto.setSkuCode(entity.getSkuCode());
        dto.setSkuName(entity.getSkuName());
        dto.setSkuShortName(entity.getSkuShortName());
        dto.setSpec(entity.getSpec());
        dto.setStatus(entity.getStatus());
        dto.setSortOrder(entity.getSortOrder());
        dto.setRemark(entity.getRemark());
        dto.setCategoryId(entity.getCategoryId());
        dto.setCategoryName(categoryNames.get(entity.getCategoryId()));
        dto.setBrandId(entity.getBrandId());
        if (entity.getBrandId() != null && entity.getBrandId() > 0) {
            dto.setBrandName(brandNames.get(entity.getBrandId()));
        }
        dto.setUnitCode(entity.getUnitCode());
        dto.setItemClass(entity.getItemClass());
        dto.setPurchaseAllowed(entity.getPurchaseAllowed());
        dto.setSaleAllowed(entity.getSaleAllowed());
        dto.setIssueAllowed(entity.getIssueAllowed());
        dto.setProductType(entity.getProductType());
        dto.setExpiryFlag(entity.getExpiryFlag());
        dto.setQuotaFlag(entity.getQuotaFlag());
        dto.setShelfLifeValue(entity.getShelfLifeValue());
        dto.setShelfLifeUnit(entity.getShelfLifeUnit());
        dto.setNearExpiryValue(entity.getNearExpiryValue());
        dto.setNearExpiryUnit(entity.getNearExpiryUnit());
        dto.setDefaultOriginCode(entity.getDefaultOriginCode());
        dto.setTaxCategoryCode(entity.getTaxCategoryCode());
        dto.setInputTaxRate(entity.getInputTaxRate());
        dto.setOutputTaxRate(entity.getOutputTaxRate());
        dto.setBarcodePolicy(entity.getBarcodePolicy());
        dto.setPrimaryBarcode(entity.getPrimaryBarcode());
        dto.setBarcodes(barcodes);
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    private SkuBarcodeDto toBarcodeDto(PrdSkuBarcode entity) {
        SkuBarcodeDto dto = new SkuBarcodeDto();
        dto.setId(entity.getId());
        dto.setBarcode(entity.getBarcode());
        dto.setDefaultFlag(entity.getDefaultFlag());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        return dto;
    }

}
