package com.whl.scm.product.adapter.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.whl.framework.core.domain.R;
import com.whl.framework.log.annotation.Log;
import com.whl.framework.log.enums.BusinessType;
import com.whl.framework.mybatis.core.page.TableDataInfo;
import com.whl.framework.web.core.BaseController;
import com.whl.scm.product.adapter.admin.controller.request.SaveSkuRequest;
import com.whl.scm.product.adapter.admin.controller.request.SkuValidationGroups;
import com.whl.scm.product.adapter.admin.controller.vo.SkuBarcodeVo;
import com.whl.scm.product.adapter.admin.controller.vo.SkuVo;
import com.whl.scm.product.adapter.admin.support.ScmTenantResolver;
import com.whl.scm.product.api.command.CreateSkuCommand;
import com.whl.scm.product.api.command.DeleteSkuCommand;
import com.whl.scm.product.api.command.UpdateSkuCommand;
import com.whl.scm.product.api.dto.SkuBarcodeDto;
import com.whl.scm.product.api.dto.SkuDto;
import com.whl.scm.product.api.facade.SkuFacade;
import com.whl.scm.product.api.query.GetSkuQuery;
import com.whl.scm.product.api.query.PageSkuQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 货品管理端接口。
 * <p>
 * REST 前缀 {@code /api/scm/product/skus}，权限前缀 {@code scm:product:sku}。
 * 菜单 ID {@code 1713}。
 *
 * @author whl
 */
@Tag(name = "SCM Product - 货品")
@Validated
@SaCheckLogin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scm/product/skus")
public class ScmSkuController extends BaseController {

    private final SkuFacade skuFacade;

    @Operation(summary = "分页查询货品")
    @SaCheckPermission("scm:product:sku:list")
    @GetMapping
    public TableDataInfo<SkuVo> pageSku(PageSkuQuery query) {
        query.setTenantId(ScmTenantResolver.resolve(query.getTenantId()));
        var page = skuFacade.pageSku(query);
        return new TableDataInfo<>(page.getRows().stream().map(this::toVo).toList(), page.getTotal());
    }

    @Operation(summary = "查询货品详情")
    @SaCheckPermission("scm:product:sku:query")
    @GetMapping("/{skuId}")
    public R<SkuVo> getSku(
        @PathVariable Long skuId,
        @RequestParam(required = false) String tenantId
    ) {
        GetSkuQuery query = new GetSkuQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setSkuId(skuId);
        return R.ok(toVo(skuFacade.getSkuDetail(query)));
    }

    @Operation(summary = "创建货品")
    @SaCheckPermission("scm:product:sku:add")
    @Log(title = "货品", businessType = BusinessType.INSERT)
    @PostMapping
    public R<SkuVo> createSku(@Validated(SkuValidationGroups.Create.class) @RequestBody SaveSkuRequest request) {
        return R.ok(toVo(skuFacade.createSku(toCreateCommand(request))));
    }

    @Operation(summary = "更新货品")
    @SaCheckPermission("scm:product:sku:edit")
    @Log(title = "货品", businessType = BusinessType.UPDATE)
    @PutMapping("/{skuId}")
    public R<SkuVo> updateSku(
        @PathVariable Long skuId,
        @Validated(SkuValidationGroups.Update.class) @RequestBody SaveSkuRequest request
    ) {
        return R.ok(toVo(skuFacade.updateSku(toUpdateCommand(skuId, request))));
    }

    @Operation(summary = "删除货品")
    @SaCheckPermission("scm:product:sku:remove")
    @Log(title = "货品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{skuId}")
    public R<Void> deleteSku(
        @PathVariable Long skuId,
        @RequestParam(required = false) String tenantId
    ) {
        DeleteSkuCommand command = new DeleteSkuCommand();
        command.setTenantId(ScmTenantResolver.resolve(tenantId));
        command.setId(skuId);
        skuFacade.deleteSku(command);
        return R.ok();
    }

    private SkuVo toVo(SkuDto dto) {
        if (dto == null) {
            return null;
        }
        SkuVo vo = new SkuVo();
        vo.setSkuId(dto.getSkuId());
        vo.setSkuCode(dto.getSkuCode());
        vo.setSkuName(dto.getSkuName());
        vo.setSkuShortName(dto.getSkuShortName());
        vo.setSpec(dto.getSpec());
        vo.setStatus(dto.getStatus());
        vo.setSortOrder(dto.getSortOrder());
        vo.setRemark(dto.getRemark());
        vo.setCategoryId(dto.getCategoryId());
        vo.setCategoryName(dto.getCategoryName());
        vo.setBrandId(dto.getBrandId());
        vo.setBrandName(dto.getBrandName());
        vo.setUnitCode(dto.getUnitCode());
        vo.setItemClass(dto.getItemClass());
        vo.setPurchaseAllowed(dto.getPurchaseAllowed());
        vo.setSaleAllowed(dto.getSaleAllowed());
        vo.setIssueAllowed(dto.getIssueAllowed());
        vo.setProductType(dto.getProductType());
        vo.setExpiryFlag(dto.getExpiryFlag());
        vo.setQuotaFlag(dto.getQuotaFlag());
        vo.setShelfLifeValue(dto.getShelfLifeValue());
        vo.setShelfLifeUnit(dto.getShelfLifeUnit());
        vo.setNearExpiryValue(dto.getNearExpiryValue());
        vo.setNearExpiryUnit(dto.getNearExpiryUnit());
        vo.setDefaultOriginCode(dto.getDefaultOriginCode());
        vo.setTaxCategoryCode(dto.getTaxCategoryCode());
        vo.setInputTaxRate(dto.getInputTaxRate());
        vo.setOutputTaxRate(dto.getOutputTaxRate());
        vo.setBarcodePolicy(dto.getBarcodePolicy());
        vo.setPrimaryBarcode(dto.getPrimaryBarcode());
        if (dto.getBarcodes() != null) {
            vo.setBarcodes(dto.getBarcodes().stream().map(this::toBarcodeVo).toList());
        }
        vo.setUpdateTime(dto.getUpdateTime());
        return vo;
    }

    private SkuBarcodeVo toBarcodeVo(SkuBarcodeDto dto) {
        SkuBarcodeVo vo = new SkuBarcodeVo();
        vo.setId(dto.getId());
        vo.setBarcode(dto.getBarcode());
        vo.setDefaultFlag(dto.getDefaultFlag());
        vo.setStatus(dto.getStatus());
        vo.setRemark(dto.getRemark());
        return vo;
    }

    private List<SkuBarcodeDto> toBarcodeDtos(List<SkuBarcodeVo> barcodes) {
        if (barcodes == null) {
            return null;
        }
        return barcodes.stream().map(vo -> {
            SkuBarcodeDto dto = new SkuBarcodeDto();
            dto.setId(vo.getId());
            dto.setBarcode(vo.getBarcode());
            dto.setDefaultFlag(vo.getDefaultFlag());
            dto.setStatus(vo.getStatus());
            dto.setRemark(vo.getRemark());
            return dto;
        }).toList();
    }

    private CreateSkuCommand toCreateCommand(SaveSkuRequest request) {
        CreateSkuCommand command = new CreateSkuCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setSkuCode(request.getSkuCode());
        command.setSkuName(request.getSkuName());
        command.setSkuShortName(request.getSkuShortName());
        command.setSpec(request.getSpec());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        command.setCategoryId(request.getCategoryId());
        command.setBrandId(request.getBrandId());
        command.setUnitCode(request.getUnitCode());
        command.setItemClass(request.getItemClass());
        command.setPurchaseAllowed(request.getPurchaseAllowed());
        command.setSaleAllowed(request.getSaleAllowed());
        command.setIssueAllowed(request.getIssueAllowed());
        command.setProductType(request.getProductType());
        command.setExpiryFlag(request.getExpiryFlag());
        command.setQuotaFlag(request.getQuotaFlag());
        command.setShelfLifeValue(request.getShelfLifeValue());
        command.setShelfLifeUnit(request.getShelfLifeUnit());
        command.setNearExpiryValue(request.getNearExpiryValue());
        command.setNearExpiryUnit(request.getNearExpiryUnit());
        command.setDefaultOriginCode(request.getDefaultOriginCode());
        command.setTaxCategoryCode(request.getTaxCategoryCode());
        command.setInputTaxRate(request.getInputTaxRate());
        command.setOutputTaxRate(request.getOutputTaxRate());
        command.setBarcodePolicy(request.getBarcodePolicy());
        command.setBarcodes(toBarcodeDtos(request.getBarcodes()));
        return command;
    }

    private UpdateSkuCommand toUpdateCommand(Long skuId, SaveSkuRequest request) {
        UpdateSkuCommand command = new UpdateSkuCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setId(skuId);
        command.setSkuName(request.getSkuName());
        command.setSkuShortName(request.getSkuShortName());
        command.setSpec(request.getSpec());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        command.setCategoryId(request.getCategoryId());
        command.setBrandId(request.getBrandId());
        command.setUnitCode(request.getUnitCode());
        command.setItemClass(request.getItemClass());
        command.setPurchaseAllowed(request.getPurchaseAllowed());
        command.setSaleAllowed(request.getSaleAllowed());
        command.setIssueAllowed(request.getIssueAllowed());
        command.setProductType(request.getProductType());
        command.setExpiryFlag(request.getExpiryFlag());
        command.setQuotaFlag(request.getQuotaFlag());
        command.setShelfLifeValue(request.getShelfLifeValue());
        command.setShelfLifeUnit(request.getShelfLifeUnit());
        command.setNearExpiryValue(request.getNearExpiryValue());
        command.setNearExpiryUnit(request.getNearExpiryUnit());
        command.setDefaultOriginCode(request.getDefaultOriginCode());
        command.setTaxCategoryCode(request.getTaxCategoryCode());
        command.setInputTaxRate(request.getInputTaxRate());
        command.setOutputTaxRate(request.getOutputTaxRate());
        command.setBarcodePolicy(request.getBarcodePolicy());
        command.setBarcodes(toBarcodeDtos(request.getBarcodes()));
        return command;
    }

}
