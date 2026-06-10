package com.whl.scm.wms.adapter.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.whl.framework.core.domain.R;
import com.whl.framework.excel.utils.ExcelUtil;
import com.whl.framework.log.annotation.Log;
import com.whl.framework.log.enums.BusinessType;
import com.whl.framework.mybatis.core.page.TableDataInfo;
import com.whl.framework.web.core.BaseController;
import jakarta.servlet.http.HttpServletResponse;
import com.whl.scm.wms.adapter.admin.controller.request.OrgValidationGroups;
import com.whl.scm.wms.adapter.admin.controller.request.SaveOrgRequest;
import com.whl.scm.wms.adapter.admin.controller.vo.OrgVo;
import com.whl.scm.wms.adapter.admin.support.ScmTenantResolver;
import com.whl.scm.wms.api.command.CreateOrgCommand;
import com.whl.scm.wms.api.command.DeleteOrgCommand;
import com.whl.scm.wms.api.command.UpdateOrgCommand;
import com.whl.scm.wms.api.dto.OrgDto;
import com.whl.scm.wms.api.facade.OrgFacade;
import com.whl.scm.wms.api.query.GetOrgQuery;
import com.whl.scm.wms.api.query.ListOrgOptionsQuery;
import com.whl.scm.wms.api.query.PageOrgQuery;
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
 * 组织档案管理端接口。
 * <p>
 * REST 前缀 {@code /api/scm/wms/orgs}，权限前缀 {@code scm:wms:org}。
 * 菜单 ID {@code 1708}，组件 {@code scm/wms/org/index}。
 *
 * @author whl
 */
@Tag(name = "SCM WMS - 组织档案")
@Validated
@SaCheckLogin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scm/wms/orgs")
public class ScmOrgController extends BaseController {

    private final OrgFacade orgFacade;

    /**
     * 分页查询组织档案。
     */
    @Operation(summary = "分页查询组织档案")
    @SaCheckPermission("scm:wms:org:list")
    @GetMapping
    public TableDataInfo<OrgVo> pageOrg(PageOrgQuery query) {
        query.setTenantId(ScmTenantResolver.resolve(query.getTenantId()));
        var page = orgFacade.pageOrg(query);
        return new TableDataInfo<>(page.getRows().stream().map(this::toVo).toList(), page.getTotal());
    }

    /**
     * 导出组织档案（按当前查询条件，导出全部匹配记录）。
     */
    @Operation(summary = "导出组织档案")
    @SaCheckPermission("scm:wms:org:export")
    @Log(title = "组织档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportOrg(PageOrgQuery query, HttpServletResponse response) {
        query.setTenantId(ScmTenantResolver.resolve(query.getTenantId()));
        List<OrgVo> list = orgFacade.listOrg(query).stream().map(this::toVo).toList();
        ExcelUtil.exportExcel(list, "组织档案", OrgVo.class, response);
    }

    /**
     * 查询组织档案下拉选项。
     */
    @Operation(summary = "查询组织档案选项")
    @SaCheckPermission("scm:wms:org:list")
    @GetMapping("/options")
    public R<List<OrgVo>> listOrgOptions(
        @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) Integer status
    ) {
        ListOrgOptionsQuery query = new ListOrgOptionsQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setStatus(status);
        return R.ok(orgFacade.listOrgOptions(query).stream().map(this::toVo).toList());
    }

    /**
     * 按业务键查询单条组织档案。
     */
    @Operation(summary = "查询单个组织档案")
    @SaCheckPermission("scm:wms:org:query")
    @GetMapping("/{id}")
    public R<OrgVo> getOrg(
        @PathVariable Long id,
        @RequestParam(required = false) String tenantId
    ) {
        GetOrgQuery query = new GetOrgQuery();
        query.setTenantId(ScmTenantResolver.resolve(tenantId));
        query.setId(id);
        return R.ok(toVo(orgFacade.getOrg(query)));
    }

    /**
     * 新建组织档案。
     */
    @Operation(summary = "创建组织档案")
    @SaCheckPermission("scm:wms:org:add")
    @Log(title = "组织档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<OrgVo> createOrg(@Validated(OrgValidationGroups.Create.class) @RequestBody SaveOrgRequest request) {
        return R.ok(toVo(orgFacade.createOrg(toCreateCommand(request))));
    }

    /**
     * 更新组织档案。
     */
    @Operation(summary = "更新组织档案")
    @SaCheckPermission("scm:wms:org:edit")
    @Log(title = "组织档案", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public R<OrgVo> updateOrg(
        @PathVariable Long id,
        @Validated(OrgValidationGroups.Update.class) @RequestBody SaveOrgRequest request
    ) {
        return R.ok(toVo(orgFacade.updateOrg(toUpdateCommand(id, request))));
    }

    /**
     * 删除组织档案。
     */
    @Operation(summary = "删除组织档案")
    @SaCheckPermission("scm:wms:org:remove")
    @Log(title = "组织档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> deleteOrg(
        @PathVariable Long id,
        @RequestParam(required = false) String tenantId
    ) {
        DeleteOrgCommand command = new DeleteOrgCommand();
        command.setTenantId(ScmTenantResolver.resolve(tenantId));
        command.setId(id);
        orgFacade.deleteOrg(command);
        return R.ok();
    }

    private OrgVo toVo(OrgDto dto) {
        if (dto == null) {
            return null;
        }
        OrgVo vo = new OrgVo();
        vo.setId(dto.getId());
        vo.setOrgCode(dto.getOrgCode());
        vo.setOrgName(dto.getOrgName());
        vo.setOrgType(dto.getOrgType());
        vo.setParentOrgId(dto.getParentOrgId());
        vo.setParentOrgName(dto.getParentOrgName());
        vo.setLinkedDeptId(dto.getLinkedDeptId());
        vo.setStatus(dto.getStatus());
        vo.setSortOrder(dto.getSortOrder());
        vo.setRemark(dto.getRemark());
        vo.setUpdateTime(dto.getUpdateTime());
        return vo;
    }

    private CreateOrgCommand toCreateCommand(SaveOrgRequest request) {
        CreateOrgCommand command = new CreateOrgCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setOrgCode(request.getOrgCode());
        command.setOrgName(request.getOrgName());
        command.setOrgType(request.getOrgType());
        command.setParentOrgId(request.getParentOrgId());
        command.setLinkedDeptId(request.getLinkedDeptId());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        return command;
    }

    private UpdateOrgCommand toUpdateCommand(Long id, SaveOrgRequest request) {
        UpdateOrgCommand command = new UpdateOrgCommand();
        command.setTenantId(ScmTenantResolver.resolve(request.getTenantId()));
        command.setId(id);
        command.setOrgName(request.getOrgName());
        command.setOrgType(request.getOrgType());
        command.setParentOrgId(request.getParentOrgId());
        command.setLinkedDeptId(request.getLinkedDeptId());
        command.setStatus(request.getStatus());
        command.setSortOrder(request.getSortOrder());
        command.setRemark(request.getRemark());
        return command;
    }

}
