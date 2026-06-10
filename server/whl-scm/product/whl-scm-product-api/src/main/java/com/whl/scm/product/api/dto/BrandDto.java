package com.whl.scm.product.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 品牌 DTO，对应表 {@code prd_brand}。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class BrandDto {

    /** 主键 */
    private Long id;

    /** 品牌编码，租户内唯一 */
    private String brandCode;

    /** 品牌名称 */
    private String brandName;

    /** 状态：10-启用,20-停用 */
    private Integer status;

    /** 排序 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

    /** 更新时间 */
    private Date updateTime;

}
