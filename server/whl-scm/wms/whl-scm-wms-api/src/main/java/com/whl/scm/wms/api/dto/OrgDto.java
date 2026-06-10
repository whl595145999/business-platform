package com.whl.scm.wms.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 组织档案 DTO，对应表 {@code wms_org}。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class OrgDto {

    /** 主键（API 字段名 orgId / id 均可） */
    private Long id;

    /** 组织编码，租户内唯一 */
    private String orgCode;

    /** 组织名称 */
    private String orgName;

    /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
    private Integer orgType;

    /** 上级组织ID，0 表示顶级 */
    private Long parentOrgId;

    /** 上级组织名称 */
    private String parentOrgName;

    /** 关联 sys_dept.dept_id */
    private Long linkedDeptId;

    /** 状态：10-启用,20-停用 */
    private Integer status;

    /** 排序 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

    /** 更新时间 */
    private Date updateTime;

}
