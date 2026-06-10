package com.whl.scm.product.biz;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.framework.core.exception.ServiceException;
import com.whl.framework.core.utils.StringUtils;
import com.whl.scm.product.api.command.CreateCategoryCommand;
import com.whl.scm.product.api.command.DeleteCategoryCommand;
import com.whl.scm.product.api.command.UpdateCategoryCommand;
import com.whl.scm.product.api.dto.CategoryDto;
import com.whl.scm.product.api.error.ProductErrorCode;
import com.whl.scm.product.api.facade.CategoryFacade;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetCategoryQuery;
import com.whl.scm.product.api.query.ListCategoryTreeQuery;
import com.whl.scm.product.api.query.PageCategoryQuery;
import com.whl.scm.product.biz.domain.PrdCategory;
import com.whl.scm.product.biz.domain.PrdSku;
import com.whl.scm.product.biz.mapper.PrdCategoryMapper;
import com.whl.scm.product.biz.mapper.PrdSkuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 货品类目应用服务，实现 {@link CategoryFacade}。
 *
 * @author whl
 */
@Service
@RequiredArgsConstructor
public class CategoryAppService implements CategoryFacade {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final PrdCategoryMapper categoryMapper;
    private final PrdSkuMapper skuMapper;

    @Override
    public ScmPageResult<CategoryDto> pageCategory(PageCategoryQuery query) {
        Page<PrdCategory> page = categoryMapper.selectPage(buildPage(query), buildQueryWrapper(query));
        Map<Long, String> nameIndex = loadCategoryNameIndex(query.getTenantId());
        List<CategoryDto> rows = page.getRecords().stream()
            .map(entity -> toDto(entity, nameIndex))
            .toList();
        ScmPageResult<CategoryDto> result = new ScmPageResult<>();
        result.setRows(rows);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public List<CategoryDto> listCategoryTree(ListCategoryTreeQuery query) {
        Map<Long, String> nameIndex = loadCategoryNameIndex(query.getTenantId());
        return categoryMapper.selectList(
            Wrappers.<PrdCategory>lambdaQuery()
                .eq(PrdCategory::getTenantId, query.getTenantId())
                .eq(query.getStatus() != null, PrdCategory::getStatus, query.getStatus())
                .orderByAsc(PrdCategory::getSortOrder)
                .orderByAsc(PrdCategory::getId)
        ).stream().map(entity -> toDto(entity, nameIndex)).toList();
    }

    @Override
    public CategoryDto getCategory(GetCategoryQuery query) {
        return toDto(requireByKey(query.getTenantId(), query.getId()), loadCategoryNameIndex(query.getTenantId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryDto createCategory(CreateCategoryCommand command) {
        validateCategoryDefaults(command.getDefaultExpiryFlag(), command.getDefaultShelfLifeValue(),
            command.getDefaultShelfLifeUnit());
        Long parentId = normalizeParentId(command.getParentId());
        assertParentValid(command.getTenantId(), null, parentId);
        PrdCategory entity = new PrdCategory();
        entity.setTenantId(command.getTenantId());
        entity.setCategoryCode(command.getCategoryCode());
        entity.setCategoryName(command.getCategoryName());
        entity.setParentId(parentId);
        entity.setStatus(command.getStatus() == null ? ProductConstants.STATUS_CATEGORY_ENABLED : command.getStatus());
        entity.setSortOrder(command.getSortOrder() == null ? 0 : command.getSortOrder());
        entity.setRemark(command.getRemark());
        entity.setDefaultItemClass(command.getDefaultItemClass() == null ? 0 : command.getDefaultItemClass());
        entity.setDefaultProductType(command.getDefaultProductType() == null ? 0 : command.getDefaultProductType());
        entity.setDefaultExpiryFlag(command.getDefaultExpiryFlag() == null ? 0 : command.getDefaultExpiryFlag());
        entity.setDefaultBarcodePolicy(command.getDefaultBarcodePolicy() == null ? 0 : command.getDefaultBarcodePolicy());
        entity.setDefaultShelfLifeValue(command.getDefaultShelfLifeValue() == null ? 0 : command.getDefaultShelfLifeValue());
        entity.setDefaultShelfLifeUnit(command.getDefaultShelfLifeUnit() == null ? 0 : command.getDefaultShelfLifeUnit());
        entity.setDefaultNearExpiryValue(command.getDefaultNearExpiryValue() == null ? 0 : command.getDefaultNearExpiryValue());
        entity.setDefaultNearExpiryUnit(command.getDefaultNearExpiryUnit() == null ? 0 : command.getDefaultNearExpiryUnit());
        try {
            categoryMapper.insert(entity);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException(ProductErrorCode.PRD_CATEGORY_CODE_DUPLICATE.message());
        }
        return toDto(entity, loadCategoryNameIndex(command.getTenantId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryDto updateCategory(UpdateCategoryCommand command) {
        validateCategoryDefaults(command.getDefaultExpiryFlag(), command.getDefaultShelfLifeValue(),
            command.getDefaultShelfLifeUnit());
        PrdCategory entity = requireByKey(command.getTenantId(), command.getId());
        Long parentId = normalizeParentId(command.getParentId());
        assertParentValid(command.getTenantId(), entity.getId(), parentId);
        entity.setCategoryName(command.getCategoryName());
        entity.setParentId(parentId);
        entity.setStatus(command.getStatus());
        if (command.getSortOrder() != null) {
            entity.setSortOrder(command.getSortOrder());
        }
        entity.setRemark(command.getRemark());
        if (command.getDefaultItemClass() != null) {
            entity.setDefaultItemClass(command.getDefaultItemClass());
        }
        if (command.getDefaultProductType() != null) {
            entity.setDefaultProductType(command.getDefaultProductType());
        }
        if (command.getDefaultExpiryFlag() != null) {
            entity.setDefaultExpiryFlag(command.getDefaultExpiryFlag());
        }
        if (command.getDefaultBarcodePolicy() != null) {
            entity.setDefaultBarcodePolicy(command.getDefaultBarcodePolicy());
        }
        if (command.getDefaultShelfLifeValue() != null) {
            entity.setDefaultShelfLifeValue(command.getDefaultShelfLifeValue());
        }
        if (command.getDefaultShelfLifeUnit() != null) {
            entity.setDefaultShelfLifeUnit(command.getDefaultShelfLifeUnit());
        }
        if (command.getDefaultNearExpiryValue() != null) {
            entity.setDefaultNearExpiryValue(command.getDefaultNearExpiryValue());
        }
        if (command.getDefaultNearExpiryUnit() != null) {
            entity.setDefaultNearExpiryUnit(command.getDefaultNearExpiryUnit());
        }
        categoryMapper.updateById(entity);
        return toDto(entity, loadCategoryNameIndex(command.getTenantId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(DeleteCategoryCommand command) {
        if (StrUtil.hasBlank(command.getTenantId()) || command.getId() == null) {
            throw new ServiceException("删除类目参数不完整");
        }
        PrdCategory entity = requireByKey(command.getTenantId(), command.getId());
        Long childCount = categoryMapper.selectCount(
            Wrappers.<PrdCategory>lambdaQuery()
                .eq(PrdCategory::getTenantId, command.getTenantId())
                .eq(PrdCategory::getParentId, entity.getId())
        );
        if (childCount != null && childCount > 0) {
            throw new ServiceException(ProductErrorCode.PRD_CATEGORY_HAS_CHILDREN.message());
        }
        Long skuCount = skuMapper.selectCount(
            Wrappers.<PrdSku>lambdaQuery()
                .eq(PrdSku::getTenantId, command.getTenantId())
                .eq(PrdSku::getCategoryId, entity.getId())
        );
        if (skuCount != null && skuCount > 0) {
            throw new ServiceException(ProductErrorCode.PRD_CATEGORY_HAS_CHILDREN.message());
        }
        categoryMapper.deleteById(entity.getId());
    }

    private void validateCategoryDefaults(Integer defaultExpiryFlag, Integer defaultShelfLifeValue,
                                          Integer defaultShelfLifeUnit) {
        if (defaultExpiryFlag != null && defaultExpiryFlag == 1) {
            if (defaultShelfLifeValue == null || defaultShelfLifeValue <= 0
                || !isValidShelfLifeUnit(defaultShelfLifeUnit)) {
                throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：效期类目请填写默认保质期");
            }
        }
    }

    private boolean isValidShelfLifeUnit(Integer unit) {
        return unit != null && (unit == ProductConstants.SHELF_LIFE_UNIT_HOUR
            || unit == ProductConstants.SHELF_LIFE_UNIT_DAY);
    }

    private void assertParentValid(String tenantId, Long selfId, Long parentId) {
        if (parentId == null || parentId == ProductConstants.TOP_PARENT_ID) {
            return;
        }
        if (selfId != null && Objects.equals(selfId, parentId)) {
            throw new ServiceException(ProductErrorCode.PRD_CATEGORY_CYCLE.message());
        }
        PrdCategory parent = findByKey(tenantId, parentId);
        if (parent == null) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        if (selfId != null && isDescendant(tenantId, selfId, parentId)) {
            throw new ServiceException(ProductErrorCode.PRD_CATEGORY_CYCLE.message());
        }
    }

    private boolean isDescendant(String tenantId, Long ancestorId, Long nodeId) {
        Long current = nodeId;
        while (current != null && current != ProductConstants.TOP_PARENT_ID) {
            if (Objects.equals(current, ancestorId)) {
                return true;
            }
            PrdCategory node = findByKey(tenantId, current);
            if (node == null) {
                return false;
            }
            current = node.getParentId();
        }
        return false;
    }

    private Long normalizeParentId(Long parentId) {
        return parentId == null ? ProductConstants.TOP_PARENT_ID : parentId;
    }

    private PrdCategory requireByKey(String tenantId, Long id) {
        PrdCategory entity = findByKey(tenantId, id);
        if (entity == null) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        return entity;
    }

    private PrdCategory findByKey(String tenantId, Long id) {
        return categoryMapper.selectOne(
            Wrappers.<PrdCategory>lambdaQuery()
                .eq(PrdCategory::getTenantId, tenantId)
                .eq(PrdCategory::getId, id)
        );
    }

    private Map<Long, String> loadCategoryNameIndex(String tenantId) {
        List<PrdCategory> all = categoryMapper.selectList(
            Wrappers.<PrdCategory>lambdaQuery()
                .eq(PrdCategory::getTenantId, tenantId)
                .select(PrdCategory::getId, PrdCategory::getCategoryName)
        );
        Map<Long, String> index = new HashMap<>(all.size() + 1);
        index.put(ProductConstants.TOP_PARENT_ID, "—");
        for (PrdCategory category : all) {
            index.put(category.getId(), category.getCategoryName());
        }
        return index;
    }

    private LambdaQueryWrapper<PrdCategory> buildQueryWrapper(PageCategoryQuery query) {
        LambdaQueryWrapper<PrdCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(PrdCategory::getTenantId, query.getTenantId());
        lqw.eq(StringUtils.isNotBlank(query.getCategoryCode()), PrdCategory::getCategoryCode, query.getCategoryCode());
        lqw.like(StringUtils.isNotBlank(query.getCategoryName()), PrdCategory::getCategoryName, query.getCategoryName());
        lqw.eq(query.getStatus() != null, PrdCategory::getStatus, query.getStatus());
        lqw.eq(query.getParentId() != null, PrdCategory::getParentId, query.getParentId());
        lqw.orderByAsc(PrdCategory::getSortOrder);
        lqw.orderByDesc(PrdCategory::getId);
        return lqw;
    }

    private Page<PrdCategory> buildPage(PageCategoryQuery query) {
        int num = query.getPageNum() == null || query.getPageNum() <= 0 ? DEFAULT_PAGE_NUM : query.getPageNum();
        int size = query.getPageSize() == null || query.getPageSize() <= 0 ? DEFAULT_PAGE_SIZE : query.getPageSize();
        return new Page<>(num, size);
    }

    private CategoryDto toDto(PrdCategory entity, Map<Long, String> nameIndex) {
        if (entity == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getId());
        dto.setCategoryCode(entity.getCategoryCode());
        dto.setCategoryName(entity.getCategoryName());
        dto.setParentId(entity.getParentId());
        Long parentId = entity.getParentId() == null ? ProductConstants.TOP_PARENT_ID : entity.getParentId();
        dto.setParentName(nameIndex.getOrDefault(parentId,
            parentId == ProductConstants.TOP_PARENT_ID ? "—" : String.valueOf(parentId)));
        dto.setStatus(entity.getStatus());
        dto.setSortOrder(entity.getSortOrder());
        dto.setRemark(entity.getRemark());
        dto.setDefaultItemClass(entity.getDefaultItemClass());
        dto.setDefaultProductType(entity.getDefaultProductType());
        dto.setDefaultExpiryFlag(entity.getDefaultExpiryFlag());
        dto.setDefaultBarcodePolicy(entity.getDefaultBarcodePolicy());
        dto.setDefaultShelfLifeValue(entity.getDefaultShelfLifeValue());
        dto.setDefaultShelfLifeUnit(entity.getDefaultShelfLifeUnit());
        dto.setDefaultNearExpiryValue(entity.getDefaultNearExpiryValue());
        dto.setDefaultNearExpiryUnit(entity.getDefaultNearExpiryUnit());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

}
