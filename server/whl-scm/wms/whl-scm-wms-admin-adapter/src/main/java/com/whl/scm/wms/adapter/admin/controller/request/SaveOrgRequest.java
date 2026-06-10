package com.whl.scm.wms.adapter.admin.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 保存组织档案 HTTP 请求体（创建/更新共用）。
 *
 * @author whl
 */
@Data
public class SaveOrgRequest {

    /** 租户编号 */
    private String tenantId;

    /** 组织编码，租户内唯一（仅创建必填） */
    @NotBlank(message = "组织编码不能为空", groups = OrgValidationGroups.Create.class)
    private String orgCode;

    /** 组织名称 */
    @NotBlank(message = "组织名称不能为空", groups = {OrgValidationGroups.Create.class, OrgValidationGroups.Update.class})
    private String orgName;

    /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
    @NotNull(message = "组织类型不能为空", groups = {OrgValidationGroups.Create.class, OrgValidationGroups.Update.class})
    private Integer orgType;

    /** 上级组织ID，0 表示顶级 */
    private Long parentOrgId;

    /** 关联 sys_dept.dept_id */
    private Long linkedDeptId;

    /** 状态：10-启用,20-停用 */
    @NotNull(message = "状态不能为空", groups = {OrgValidationGroups.Create.class, OrgValidationGroups.Update.class})
    private Integer status;

    /** 排序 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

}
