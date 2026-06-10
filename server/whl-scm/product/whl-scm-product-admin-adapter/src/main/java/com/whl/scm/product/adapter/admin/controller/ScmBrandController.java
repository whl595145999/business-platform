package com.whl.scm.product.adapter.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.whl.framework.core.domain.R;
import com.whl.framework.excel.utils.ExcelUtil;
import com.whl.framework.log.annotation.Log;
import com.whl.framework.log.enums.BusinessType;
import com.whl.framework.mybatis.core.page.TableDataInfo;
import com.whl.framework.web.core.BaseController;
import com.whl.scm.product.adapter.admin.controller.request.BrandValidationGroups;
import com.whl.scm.product.adapter.admin.controller.request.SaveBrandRequest;
import com.whl.scm.product.adapter.admin.controller.vo.BrandVo;
import com.whl.scm.product.adapter.admin.support.ScmTenantResolver;
import com.whl.scm.product.api.command.CreateBrandCommand;
import com.whl.scm.product.api.command.DeleteBrandCommand;
import com.whl.scm.product.api.command.UpdateBrandCommand;
import com.whl.scm.product.api.dto.BrandDto;
import com.whl.scm.product.api.facade.BrandFacade;
import com.whl.scm.product.api.query.GetBrandQuery;
import com.whl.scm.product.api.query.ListBrandOptionsQuery;
import com.whl.scm.product.api.query.PageBrandQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
 * 品牌管理端接口。
 * <p>
 * REST 前缀 {@code /api/scm/product/brands}，权限前缀 {@code scm:product:brand}。
 * 菜单 ID {@code 1712}。
 *
 * @author whl
 */
@Tag(name = "SCM Product - 品牌")
@Validated
@SaCheckLogin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scm/product/brands")
public class ScmBrandController extends BaseController {

    private final BrandFacade brandFacade;

    @Operation(summary = "分页查询品牌")
    @SaCheckPermission("scm:product:brand:list")
    @GetMapping
    public TableDataInfo<BrandVo> pageBrand(PageBrandQuery query) {
        query.setTenantId(ScmTenantResolver.resolve(query.getTenantId()));
        var page = brandFacade.pageBrand(query);
        return new TableDataInfo<>(page.getRows().stream().map(this::toVo).toList(), page.getTotal());
    }

    @Operation(summary = "导出品牌")
    @SaCheckPermission("scm:product:brand:export")
    @Log(title = "品牌", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportBrand(PageBrandQuery query, HttpServletResponse response) {
        query.setTenantId(ScmTenantResolver.resolve(query.getTenantId()));
        List<BrandVo> list = brandFacade.listBrand(query).stream().map(this::toVo).toList();
        ExcelUtil.exportExcel(list, "品牌", BrandVo.class, response);
    }

    @Operation(summary = "查询品牌选项")
    @SaCheckPermission("scm:product:brand:list")
    @GetMapping("/options")
    public R<List<BrandVo>> listBrandOptions(
        @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) Integer status
    ) {
        ListBrandOptionsQuery query = new ListBrandOptionsQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setStatus(status);
        return R.ok(brandFacade.listBrandOptions(query).stream().map(this::toVo).toList());
    }

    @Operation(summary = "查询单个品牌")
    @SaCheckPermission("scm:product:brand:query")
    @GetMapping("/{id}")
    public R<BrandVo> getBrand(
        @PathVariable Long id,
        @RequestParam(required = false) String tenantId
    ) {
        GetBrandQuery query = new GetBrandQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setId(id);
        return R.ok(toVo(brandFacade.getBrand(query)));
    }

    @Operation(summary = "创建品牌")
    @SaCheckPermission("scm:product:brand:add")
    @Log(title = "品牌", businessType = BusinessType.INSERT)
    @PostMapping
    public R<BrandVo> createBrand(@Validated(BrandValidationGroups.Create.class) @RequestBody SaveBrandRequest request) {
        return R.ok(toVo(brandFacade.createBrand(toCreateCommand(request))));
    }

    @Operation(summary = "更新品牌")
    @SaCheckPermission("scm:product:brand:edit")
    @Log(title = "品牌", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public R<BrandVo> updateBrand(
        @PathVariable Long id,
        @Validated(BrandValidationGroups.Update.class) @RequestBody SaveBrandRequest request
    ) {
        return R.ok(toVo(brandFacade.updateBrand(toUpdateCommand(id, request))));
    }

    @Operation(summary = "删除品牌")
    @SaCheckPermission("scm:product:brand:remove")
    @Log(title = "品牌", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> deleteBrand(
        @PathVariable Long id,
        @RequestParam(required = false) String tenantId
    ) {
        DeleteBrandCommand command = new DeleteBrandCommand();
        command.setTenantId(ScmTenantResolver.resolve(tenantId));
        command.setId(id);
        brandFacade.deleteBrand(command);
        return R.ok();
    }

    private BrandVo toVo(BrandDto dto) {
        if (dto == null) {
            return null;
        }
        BrandVo vo = new BrandVo();
        vo.setId(dto.getId());
        vo.setBrandCode(dto.getBrandCode());
        vo.setBrandName(dto.getBrandName());
        vo.setStatus(dto.getStatus());
        vo.setSortOrder(dto.getSortOrder());
        vo.setRemark(dto.getRemark());
        vo.setUpdateTime(dto.getUpdateTime());
        return vo;
    }

    private CreateBrandCommand toCreateCommand(SaveBrandRequest request) {
        CreateBrandCommand command = new CreateBrandCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setBrandCode(request.getBrandCode());
        command.setBrandName(request.getBrandName());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        return command;
    }

    private UpdateBrandCommand toUpdateCommand(Long id, SaveBrandRequest request) {
        UpdateBrandCommand command = new UpdateBrandCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setId(id);
        command.setBrandName(request.getBrandName());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        return command;
    }

}
