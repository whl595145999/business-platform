package com.whl.scm.product.adapter.admin.controller.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.whl.framework.excel.annotation.ExcelDictFormat;
import com.whl.framework.excel.convert.ExcelDictConvert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 货品类目 HTTP 出参。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class CategoryVo {

    @ExcelProperty(value = "类目ID")
    private Long id;

    @ExcelProperty(value = "类目编码")
    private String categoryCode;

    @ExcelProperty(value = "类目名称")
    private String categoryName;

    private Long parentId;

    @ExcelProperty(value = "上级类目")
    private String parentName;

    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "10=启用,20=停用")
    private Integer status;

    @ExcelProperty(value = "排序")
    private Integer sortOrder;

    @ExcelProperty(value = "备注")
    private String remark;

    private Integer defaultItemClass;
    private Integer defaultProductType;
    private Integer defaultExpiryFlag;
    private Integer defaultBarcodePolicy;
    private Integer defaultShelfLifeValue;
    private Integer defaultShelfLifeUnit;
    private Integer defaultNearExpiryValue;
    private Integer defaultNearExpiryUnit;

    @ExcelProperty(value = "更新时间")
    private Date updateTime;

}
