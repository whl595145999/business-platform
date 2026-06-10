package com.whl.scm.wms.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新组织档案命令（业务键不可变）。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class UpdateOrgCommand {

    /** 租户编号 */
    private String tenantId;

    /** 主键 */
    private Long id;

    /** 组织名称 */
    private String orgName;

    /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
    private Integer orgType;

    /** 上级组织ID */
    private Long parentOrgId;

    /** 关联 sys_dept.dept_id */
    private Long linkedDeptId;

    /** 状态：10-启用,20-停用 */
    private Integer status;

    /** 排序 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

}
