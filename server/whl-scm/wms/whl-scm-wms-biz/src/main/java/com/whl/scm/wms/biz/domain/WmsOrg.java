package com.whl.scm.wms.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.whl.framework.tenant.core.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 组织档案实体，映射表 {@code wms_org}。
 *
 * @author whl
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_org")
public class WmsOrg extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    /** 组织编码，租户内唯一 */
    private String orgCode;

    /** 组织名称 */
    private String orgName;

    /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
    private Integer orgType;

    /** 上级组织ID，0 表示顶级 */
    private Long parentOrgId;

    /** 关联 sys_dept.dept_id */
    private Long linkedDeptId;

    /** 状态：10-启用,20-停用 */
    private Integer status;

    /** 排序 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

    /** 删除标志 */
    @TableLogic
    private String delFlag;

}
