package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 品牌分页查询。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class PageBrandQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private Integer pageNum;
    private Integer pageSize;
    private String brandCode;
    private String brandName;
    private Integer status;

}
