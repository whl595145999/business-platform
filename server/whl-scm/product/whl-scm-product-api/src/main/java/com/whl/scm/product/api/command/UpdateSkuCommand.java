package com.whl.scm.product.api.command;

import com.whl.scm.product.api.dto.SkuBarcodeDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新货品命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class UpdateSkuCommand {

    private String tenantId;
    private Long id;
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
    private List<SkuBarcodeDto> barcodes;

}
