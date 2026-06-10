package com.whl.scm.product.adapter.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.whl.framework.core.domain.R;
import com.whl.framework.excel.utils.ExcelUtil;
import com.whl.framework.log.annotation.Log;
import com.whl.framework.log.enums.BusinessType;
import com.whl.framework.mybatis.core.page.TableDataInfo;
import com.whl.framework.web.core.BaseController;
import com.whl.scm.product.adapter.admin.controller.request.CategoryValidationGroups;
import com.whl.scm.product.adapter.admin.controller.request.SaveCategoryRequest;
import com.whl.scm.product.adapter.admin.controller.vo.CategoryDefaultsVo;
import com.whl.scm.product.adapter.admin.controller.vo.CategoryVo;
import com.whl.scm.product.adapter.admin.support.ScmTenantResolver;
import com.whl.scm.product.api.command.CreateCategoryCommand;
import com.whl.scm.product.api.command.DeleteCategoryCommand;
import com.whl.scm.product.api.command.UpdateCategoryCommand;
import com.whl.scm.product.api.dto.CategoryDto;
import com.whl.scm.product.api.facade.CategoryFacade;
import com.whl.scm.product.api.query.GetCategoryQuery;
import com.whl.scm.product.api.query.ListCategoryTreeQuery;
import com.whl.scm.product.api.query.PageCategoryQuery;
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
 * 货品类目管理端接口。
 * <p>
 * REST 前缀 {@code /api/scm/product/categories}，权限前缀 {@code scm:product:category}。
 * 菜单 ID {@code 1711}。
 *
 * @author whl
 */
@Tag(name = "SCM Product - 货品类目")
@Validated
@SaCheckLogin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scm/product/categories")
public class ScmCategoryController extends BaseController {

    private final CategoryFacade categoryFacade;

    @Operation(summary = "分页查询货品类目")
    @SaCheckPermission("scm:product:category:list")
    @GetMapping
    public TableDataInfo<CategoryVo> pageCategory(PageCategoryQuery query) {
        query.setTenantId(ScmTenantResolver.resolve(query.getTenantId()));
        var page = categoryFacade.pageCategory(query);
        return new TableDataInfo<>(page.getRows().stream().map(this::toVo).toList(), page.getTotal());
    }

    @Operation(summary = "查询货品类目树")
    @SaCheckPermission("scm:product:category:list")
    @GetMapping("/tree")
    public R<List<CategoryVo>> listCategoryTree(
        @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) Integer status
    ) {
        ListCategoryTreeQuery query = new ListCategoryTreeQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setStatus(status);
        return R.ok(categoryFacade.listCategoryTree(query).stream().map(this::toVo).toList());
    }

    @Operation(summary = "导出货品类目")
    @SaCheckPermission("scm:product:category:export")
    @Log(title = "货品类目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportCategory(
        @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) Integer status,
        HttpServletResponse response
    ) {
        ListCategoryTreeQuery query = new ListCategoryTreeQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setStatus(status);
        List<CategoryVo> list = categoryFacade.listCategoryTree(query).stream().map(this::toVo).toList();
        ExcelUtil.exportExcel(list, "货品类目", CategoryVo.class, response);
    }

    @Operation(summary = "查询类目默认策略")
    @SaCheckPermission("scm:product:category:query")
    @GetMapping("/{categoryId}/defaults")
    public R<CategoryDefaultsVo> getCategoryDefaults(
        @PathVariable Long categoryId,
        @RequestParam(required = false) String tenantId
    ) {
        GetCategoryQuery query = new GetCategoryQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setId(categoryId);
        return R.ok(toDefaultsVo(categoryFacade.getCategory(query)));
    }

    @Operation(summary = "查询单个货品类目")
    @SaCheckPermission("scm:product:category:query")
    @GetMapping("/{id}")
    public R<CategoryVo> getCategory(
        @PathVariable Long id,
        @RequestParam(required = false) String tenantId
    ) {
        GetCategoryQuery query = new GetCategoryQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setId(id);
        return R.ok(toVo(categoryFacade.getCategory(query)));
    }

    @Operation(summary = "创建货品类目")
    @SaCheckPermission("scm:product:category:add")
    @Log(title = "货品类目", businessType = BusinessType.INSERT)
    @PostMapping
    public R<CategoryVo> createCategory(
        @Validated(CategoryValidationGroups.Create.class) @RequestBody SaveCategoryRequest request
    ) {
        return R.ok(toVo(categoryFacade.createCategory(toCreateCommand(request))));
    }

    @Operation(summary = "更新货品类目")
    @SaCheckPermission("scm:product:category:edit")
    @Log(title = "货品类目", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public R<CategoryVo> updateCategory(
        @PathVariable Long id,
        @Validated(CategoryValidationGroups.Update.class) @RequestBody SaveCategoryRequest request
    ) {
        return R.ok(toVo(categoryFacade.updateCategory(toUpdateCommand(id, request))));
    }

    @Operation(summary = "删除货品类目")
    @SaCheckPermission("scm:product:category:remove")
    @Log(title = "货品类目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> deleteCategory(
        @PathVariable Long id,
        @RequestParam(required = false) String tenantId
    ) {
        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setTenantId(ScmTenantResolver.resolve(tenantId));
        command.setId(id);
        categoryFacade.deleteCategory(command);
        return R.ok();
    }

    private CategoryVo toVo(CategoryDto dto) {
        if (dto == null) {
            return null;
        }
        CategoryVo vo = new CategoryVo();
        vo.setId(dto.getId());
        vo.setCategoryCode(dto.getCategoryCode());
        vo.setCategoryName(dto.getCategoryName());
        vo.setParentId(dto.getParentId());
        vo.setParentName(dto.getParentName());
        vo.setStatus(dto.getStatus());
        vo.setSortOrder(dto.getSortOrder());
        vo.setRemark(dto.getRemark());
        vo.setDefaultItemClass(dto.getDefaultItemClass());
        vo.setDefaultProductType(dto.getDefaultProductType());
        vo.setDefaultExpiryFlag(dto.getDefaultExpiryFlag());
        vo.setDefaultBarcodePolicy(dto.getDefaultBarcodePolicy());
        vo.setDefaultShelfLifeValue(dto.getDefaultShelfLifeValue());
        vo.setDefaultShelfLifeUnit(dto.getDefaultShelfLifeUnit());
        vo.setDefaultNearExpiryValue(dto.getDefaultNearExpiryValue());
        vo.setDefaultNearExpiryUnit(dto.getDefaultNearExpiryUnit());
        vo.setUpdateTime(dto.getUpdateTime());
        return vo;
    }

    private CategoryDefaultsVo toDefaultsVo(CategoryDto dto) {
        if (dto == null) {
            return null;
        }
        CategoryDefaultsVo vo = new CategoryDefaultsVo();
        vo.setDefaultItemClass(dto.getDefaultItemClass());
        vo.setDefaultProductType(dto.getDefaultProductType());
        vo.setDefaultExpiryFlag(dto.getDefaultExpiryFlag());
        vo.setDefaultBarcodePolicy(dto.getDefaultBarcodePolicy());
        vo.setDefaultShelfLifeValue(dto.getDefaultShelfLifeValue());
        vo.setDefaultShelfLifeUnit(dto.getDefaultShelfLifeUnit());
        vo.setDefaultNearExpiryValue(dto.getDefaultNearExpiryValue());
        vo.setDefaultNearExpiryUnit(dto.getDefaultNearExpiryUnit());
        return vo;
    }

    private CreateCategoryCommand toCreateCommand(SaveCategoryRequest request) {
        CreateCategoryCommand command = new CreateCategoryCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setCategoryCode(request.getCategoryCode());
        command.setCategoryName(request.getCategoryName());
        command.setParentId(request.getParentId());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        command.setDefaultItemClass(request.getDefaultItemClass());
        command.setDefaultProductType(request.getDefaultProductType());
        command.setDefaultExpiryFlag(request.getDefaultExpiryFlag());
        command.setDefaultBarcodePolicy(request.getDefaultBarcodePolicy());
        command.setDefaultShelfLifeValue(request.getDefaultShelfLifeValue());
        command.setDefaultShelfLifeUnit(request.getDefaultShelfLifeUnit());
        command.setDefaultNearExpiryValue(request.getDefaultNearExpiryValue());
        command.setDefaultNearExpiryUnit(request.getDefaultNearExpiryUnit());
        return command;
    }

    private UpdateCategoryCommand toUpdateCommand(Long id, SaveCategoryRequest request) {
        UpdateCategoryCommand command = new UpdateCategoryCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setId(id);
        command.setCategoryName(request.getCategoryName());
        command.setParentId(request.getParentId());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        command.setDefaultItemClass(request.getDefaultItemClass());
        command.setDefaultProductType(request.getDefaultProductType());
        command.setDefaultExpiryFlag(request.getDefaultExpiryFlag());
        command.setDefaultBarcodePolicy(request.getDefaultBarcodePolicy());
        command.setDefaultShelfLifeValue(request.getDefaultShelfLifeValue());
        command.setDefaultShelfLifeUnit(request.getDefaultShelfLifeUnit());
        command.setDefaultNearExpiryValue(request.getDefaultNearExpiryValue());
        command.setDefaultNearExpiryUnit(request.getDefaultNearExpiryUnit());
        return command;
    }

}
