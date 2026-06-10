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
 * 品牌实体，映射表 {@code prd_brand}。
 *
 * @author whl
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_brand")
public class PrdBrand extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    private String brandCode;
    private String brandName;
    private Integer status;
    private Integer sortOrder;
    private String remark;

    @TableLogic
    private String delFlag;

}
