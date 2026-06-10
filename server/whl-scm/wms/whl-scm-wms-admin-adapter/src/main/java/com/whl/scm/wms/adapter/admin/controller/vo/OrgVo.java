package com.whl.scm.wms.adapter.admin.controller.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.whl.framework.excel.annotation.ExcelDictFormat;
import com.whl.framework.excel.convert.ExcelDictConvert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 组织档案 HTTP 出参（管理端展示）。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class OrgVo {

    /** 主键 */
    @ExcelProperty(value = "组织ID")
    private Long id;

    /** 组织编码，租户内唯一 */
    @ExcelProperty(value = "组织编码")
    private String orgCode;

    /** 组织名称 */
    @ExcelProperty(value = "组织名称")
    private String orgName;

    /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
    @ExcelProperty(value = "组织类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "10=集团,20=法人,30=事业部,40=区域")
    private Integer orgType;

    /** 上级组织ID，0 表示顶级 */
    private Long parentOrgId;

    /** 上级组织名称 */
    @ExcelProperty(value = "上级组织")
    private String parentOrgName;

    /** 关联 sys_dept.dept_id */
    private Long linkedDeptId;

    /** 状态：10-启用,20-停用 */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "10=启用,20=停用")
    private Integer status;

    /** 排序 */
    @ExcelProperty(value = "排序")
    private Integer sortOrder;

    /** 备注 */
    @ExcelProperty(value = "备注")
    private String remark;

    /** 更新时间 */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

}
