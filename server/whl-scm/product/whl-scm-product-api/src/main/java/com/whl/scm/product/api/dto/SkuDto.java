package com.whl.scm.product.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 货品 SKU DTO，对应表 {@code prd_sku}。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class SkuDto {

    /** 主键 */
    private Long skuId;

    /** 货品编码，租户内唯一 */
    private String skuCode;

    /** 货品名称 */
    private String skuName;

    /** 短名称 */
    private String skuShortName;

    /** 规格 */
    private String spec;

    /** 启停：1-启用,0-停用 */
    private Integer status;

    /** 排序 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

    /** 货品类目ID */
    private Long categoryId;

    /** 类目名称 */
    private String categoryName;

    /** 品牌ID，0=未选 */
    private Long brandId;

    /** 品牌名称 */
    private String brandName;

    /** 库存计量单位编码 */
    private String unitCode;

    /** 货品类型：10-贸易,20-物料,30-包材,40-药品,50-虚拟 */
    private Integer itemClass;

    /** 可采购：1-是,0-否 */
    private Integer purchaseAllowed;

    /** 可销售：1-是,0-否 */
    private Integer saleAllowed;

    /** 可领料：1-是,0-否 */
    private Integer issueAllowed;

    /** 履约类型：10-实物,20-虚拟 */
    private Integer productType;

    /** 是否效期品：1-是,0-否 */
    private Integer expiryFlag;

    /** 是否配额：1-是,0-否 */
    private Integer quotaFlag;

    /** 保质期数值 */
    private Integer shelfLifeValue;

    /** 保质期单位：10-小时,20-天 */
    private Integer shelfLifeUnit;

    /** 临期预警数值 */
    private Integer nearExpiryValue;

    /** 临期预警单位：10-小时,20-天 */
    private Integer nearExpiryUnit;

    /** 默认产地编码 */
    private String defaultOriginCode;

    /** 税收分类编码 */
    private String taxCategoryCode;

    /** 默认进项税率 */
    private String inputTaxRate;

    /** 默认销项税率 */
    private String outputTaxRate;

    /** 条码策略：10-必须有国标码,20-可无 */
    private Integer barcodePolicy;

    /** 默认扫码条码（冗余） */
    private String primaryBarcode;

    /** 条码明细 */
    private List<SkuBarcodeDto> barcodes;

    /** 更新时间 */
    private Date updateTime;

}
