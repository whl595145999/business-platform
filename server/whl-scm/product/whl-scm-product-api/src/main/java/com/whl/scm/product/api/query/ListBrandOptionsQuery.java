package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 品牌下拉选项查询。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class ListBrandOptionsQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private Integer status;

}
