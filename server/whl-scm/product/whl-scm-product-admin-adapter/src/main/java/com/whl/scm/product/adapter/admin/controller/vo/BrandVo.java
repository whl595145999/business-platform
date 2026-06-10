package com.whl.scm.product.adapter.admin.controller.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.whl.framework.excel.annotation.ExcelDictFormat;
import com.whl.framework.excel.convert.ExcelDictConvert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 品牌 HTTP 出参。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class BrandVo {

    @ExcelProperty(value = "品牌ID")
    private Long id;

    @ExcelProperty(value = "品牌编码")
    private String brandCode;

    @ExcelProperty(value = "品牌名称")
    private String brandName;

    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "10=启用,20=停用")
    private Integer status;

    @ExcelProperty(value = "排序")
    private Integer sortOrder;

    @ExcelProperty(value = "备注")
    private String remark;

    @ExcelProperty(value = "更新时间")
    private Date updateTime;

}
