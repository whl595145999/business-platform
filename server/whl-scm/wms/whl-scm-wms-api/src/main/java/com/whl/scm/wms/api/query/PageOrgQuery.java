package com.whl.scm.wms.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 组织档案分页查询。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class PageOrgQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 租户编号 */
    private String tenantId;

    /** 当前页码 */
    private Integer pageNum;

    /** 每页条数 */
    private Integer pageSize;

    /** 组织编码，租户内唯一 */
    private String orgCode;

    /** 组织名称 */
    private String orgName;

    /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
    private Integer orgType;

    /** 状态：10-启用,20-停用 */
    private Integer status;

}
