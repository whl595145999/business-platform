package com.whl.scm.product.biz;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.framework.core.exception.ServiceException;
import com.whl.framework.core.utils.StringUtils;
import com.whl.scm.product.api.command.CreateBrandCommand;
import com.whl.scm.product.api.command.DeleteBrandCommand;
import com.whl.scm.product.api.command.UpdateBrandCommand;
import com.whl.scm.product.api.dto.BrandDto;
import com.whl.scm.product.api.error.ProductErrorCode;
import com.whl.scm.product.api.facade.BrandFacade;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetBrandQuery;
import com.whl.scm.product.api.query.ListBrandOptionsQuery;
import com.whl.scm.product.api.query.PageBrandQuery;
import com.whl.scm.product.biz.domain.PrdBrand;
import com.whl.scm.product.biz.domain.PrdSku;
import com.whl.scm.product.biz.mapper.PrdBrandMapper;
import com.whl.scm.product.biz.mapper.PrdSkuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 品牌应用服务，实现 {@link BrandFacade}。
 *
 * @author whl
 */
@Service
@RequiredArgsConstructor
public class BrandAppService implements BrandFacade {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final PrdBrandMapper brandMapper;
    private final PrdSkuMapper skuMapper;

    @Override
    public ScmPageResult<BrandDto> pageBrand(PageBrandQuery query) {
        Page<PrdBrand> page = brandMapper.selectPage(buildPage(query), buildQueryWrapper(query));
        List<BrandDto> rows = page.getRecords().stream().map(this::toDto).toList();
        ScmPageResult<BrandDto> result = new ScmPageResult<>();
        result.setRows(rows);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public List<BrandDto> listBrand(PageBrandQuery query) {
        return brandMapper.selectList(buildQueryWrapper(query)).stream().map(this::toDto).toList();
    }

    @Override
    public BrandDto getBrand(GetBrandQuery query) {
        return toDto(requireByKey(query.getTenantId(), query.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BrandDto createBrand(CreateBrandCommand command) {
        PrdBrand entity = new PrdBrand();
        entity.setTenantId(command.getTenantId());
        entity.setBrandCode(command.getBrandCode());
        entity.setBrandName(command.getBrandName());
        entity.setStatus(command.getStatus() == null ? ProductConstants.STATUS_BRAND_ENABLED : command.getStatus());
        entity.setSortOrder(command.getSortOrder() == null ? 0 : command.getSortOrder());
        entity.setRemark(command.getRemark());
        try {
            brandMapper.insert(entity);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException(ProductErrorCode.PRD_BRAND_CODE_DUPLICATE.message());
        }
        return toDto(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BrandDto updateBrand(UpdateBrandCommand command) {
        PrdBrand entity = requireByKey(command.getTenantId(), command.getId());
        entity.setBrandName(command.getBrandName());
        entity.setStatus(command.getStatus());
        if (command.getSortOrder() != null) {
            entity.setSortOrder(command.getSortOrder());
        }
        entity.setRemark(command.getRemark());
        brandMapper.updateById(entity);
        return toDto(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(DeleteBrandCommand command) {
        if (StrUtil.hasBlank(command.getTenantId()) || command.getId() == null) {
            throw new ServiceException("删除品牌参数不完整");
        }
        PrdBrand entity = requireByKey(command.getTenantId(), command.getId());
        Long skuCount = skuMapper.selectCount(
            Wrappers.<PrdSku>lambdaQuery()
                .eq(PrdSku::getTenantId, command.getTenantId())
                .eq(PrdSku::getBrandId, entity.getId())
        );
        if (skuCount != null && skuCount > 0) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_VALIDATION.message() + "：品牌已被货品引用，不可删除");
        }
        brandMapper.deleteById(entity.getId());
    }

    @Override
    public List<BrandDto> listBrandOptions(ListBrandOptionsQuery query) {
        Integer status = query.getStatus() == null ? ProductConstants.STATUS_BRAND_ENABLED : query.getStatus();
        return brandMapper.selectList(
            Wrappers.<PrdBrand>lambdaQuery()
                .eq(PrdBrand::getTenantId, query.getTenantId())
                .eq(status != null, PrdBrand::getStatus, status)
                .orderByAsc(PrdBrand::getSortOrder)
                .orderByAsc(PrdBrand::getId)
        ).stream().map(this::toDto).toList();
    }

    private PrdBrand requireByKey(String tenantId, Long id) {
        PrdBrand entity = findByKey(tenantId, id);
        if (entity == null) {
            throw new ServiceException(ProductErrorCode.PRD_SKU_NOT_FOUND.message());
        }
        return entity;
    }

    private PrdBrand findByKey(String tenantId, Long id) {
        return brandMapper.selectOne(
            Wrappers.<PrdBrand>lambdaQuery()
                .eq(PrdBrand::getTenantId, tenantId)
                .eq(PrdBrand::getId, id)
        );
    }

    private LambdaQueryWrapper<PrdBrand> buildQueryWrapper(PageBrandQuery query) {
        LambdaQueryWrapper<PrdBrand> lqw = Wrappers.lambdaQuery();
        lqw.eq(PrdBrand::getTenantId, query.getTenantId());
        lqw.eq(StringUtils.isNotBlank(query.getBrandCode()), PrdBrand::getBrandCode, query.getBrandCode());
        lqw.like(StringUtils.isNotBlank(query.getBrandName()), PrdBrand::getBrandName, query.getBrandName());
        lqw.eq(query.getStatus() != null, PrdBrand::getStatus, query.getStatus());
        lqw.orderByAsc(PrdBrand::getSortOrder);
        lqw.orderByDesc(PrdBrand::getId);
        return lqw;
    }

    private Page<PrdBrand> buildPage(PageBrandQuery query) {
        int num = query.getPageNum() == null || query.getPageNum() <= 0 ? DEFAULT_PAGE_NUM : query.getPageNum();
        int size = query.getPageSize() == null || query.getPageSize() <= 0 ? DEFAULT_PAGE_SIZE : query.getPageSize();
        return new Page<>(num, size);
    }

    private BrandDto toDto(PrdBrand entity) {
        if (entity == null) {
            return null;
        }
        BrandDto dto = new BrandDto();
        dto.setId(entity.getId());
        dto.setBrandCode(entity.getBrandCode());
        dto.setBrandName(entity.getBrandName());
        dto.setStatus(entity.getStatus());
        dto.setSortOrder(entity.getSortOrder());
        dto.setRemark(entity.getRemark());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

}
