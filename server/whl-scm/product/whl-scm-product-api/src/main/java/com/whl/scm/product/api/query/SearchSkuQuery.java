package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 跨域货品模糊搜索（仅启用货品）。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class SearchSkuQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private String keyword;
    private Integer pageNum;
    private Integer pageSize;

}
