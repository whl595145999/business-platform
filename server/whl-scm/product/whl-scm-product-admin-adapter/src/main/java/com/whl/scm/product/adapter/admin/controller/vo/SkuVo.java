package com.whl.scm.product.adapter.admin.controller.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.whl.framework.excel.annotation.ExcelDictFormat;
import com.whl.framework.excel.convert.ExcelDictConvert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 货品 HTTP 出参。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class SkuVo {

    private Long skuId;

    @ExcelProperty(value = "货品编码")
    private String skuCode;

    @ExcelProperty(value = "货品名称")
    private String skuName;

    private String skuShortName;
    private String spec;

    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=启用,0=停用")
    private Integer status;

    private Integer sortOrder;
    private String remark;
    private Long categoryId;

    @ExcelProperty(value = "类目")
    private String categoryName;

    private Long brandId;
    private String brandName;
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
    private List<SkuBarcodeVo> barcodes;

    @ExcelProperty(value = "更新时间")
    private Date updateTime;

}
