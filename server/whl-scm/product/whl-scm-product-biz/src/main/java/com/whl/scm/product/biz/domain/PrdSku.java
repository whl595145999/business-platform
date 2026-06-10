package com.whl.scm.product.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.whl.framework.tenant.core.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 货品 SKU 实体，映射表 {@code prd_sku}。
 *
 * @author whl
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_sku")
public class PrdSku extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    private String skuCode;
    private String skuName;
    private String skuShortName;
    private String spec;
    private Integer status;
    private Integer sortOrder;
    private String remark;
    private Long categoryId;
    private Long brandId;
    private String unitCode;
    private Integer itemClass;
    private Integer purchaseAllowed;
    private Integer saleAllowed;
    private Integer issueAllowed;
    private Integer productType;
    private Integer expiryFlag;
    private Integer quotaFlag;
    private Integer shelfLifeValue;
    private Integer shelfLifeUnit;
    private Integer nearExpiryValue;
    private Integer nearExpiryUnit;
    private String defaultOriginCode;
    private String taxCategoryCode;
    private String inputTaxRate;
    private String outputTaxRate;
    private Integer barcodePolicy;
    private String primaryBarcode;

    @TableLogic
    private String delFlag;

}
