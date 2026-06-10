package com.whl.scm.product.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 货品类目 DTO，对应表 {@code prd_category}。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class CategoryDto {

    /** 主键 */
    private Long id;

    /** 类目编码，租户内唯一 */
    private String categoryCode;

    /** 类目名称 */
    private String categoryName;

    /** 上级类目ID，0 表示顶级 */
    private Long parentId;

    /** 上级类目名称 */
    private String parentName;

    /** 状态：10-启用,20-停用 */
    private Integer status;

    /** 排序 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

    /** 默认货品类型：10-贸易,20-物料,30-包材,40-药品,50-虚拟，0=不继承 */
    private Integer defaultItemClass;

    /** 默认履约类型：10-实物,20-虚拟，0=不继承 */
    private Integer defaultProductType;

    /** 默认是否效期品：1-是,0-否 */
    private Integer defaultExpiryFlag;

    /** 默认条码策略：10-必须有国标码,20-可无，0=不继承 */
    private Integer defaultBarcodePolicy;

    /** 默认保质期数值 */
    private Integer defaultShelfLifeValue;

    /** 默认保质期单位：10-小时,20-天 */
    private Integer defaultShelfLifeUnit;

    /** 默认临期预警数值 */
    private Integer defaultNearExpiryValue;

    /** 默认临期预警单位：10-小时,20-天 */
    private Integer defaultNearExpiryUnit;

    /** 更新时间 */
    private Date updateTime;

}
