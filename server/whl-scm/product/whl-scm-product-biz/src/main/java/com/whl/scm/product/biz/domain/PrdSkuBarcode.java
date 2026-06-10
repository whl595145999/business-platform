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
 * 货品条码实体，映射表 {@code prd_sku_barcode}。
 *
 * @author whl
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_sku_barcode")
public class PrdSkuBarcode extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    private Long skuId;
    private String barcode;
    private Integer defaultFlag;
    private Integer status;
    private String remark;

    @TableLogic
    private String delFlag;

}
