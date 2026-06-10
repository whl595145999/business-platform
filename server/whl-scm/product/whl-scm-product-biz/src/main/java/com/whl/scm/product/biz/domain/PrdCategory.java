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
 * 货品类目实体，映射表 {@code prd_category}。
 *
 * @author whl
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_category")
public class PrdCategory extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    private String categoryCode;
    private String categoryName;
    private Long parentId;
    private Integer status;
    private Integer sortOrder;
    private String remark;
    private Integer defaultItemClass;
    private Integer defaultProductType;
    private Integer defaultExpiryFlag;
    private Integer defaultBarcodePolicy;
    private Integer defaultShelfLifeValue;
    private Integer defaultShelfLifeUnit;
    private Integer defaultNearExpiryValue;
    private Integer defaultNearExpiryUnit;

    @TableLogic
    private String delFlag;

}
