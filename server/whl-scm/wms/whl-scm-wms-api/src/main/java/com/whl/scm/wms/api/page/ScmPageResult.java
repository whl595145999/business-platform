package com.whl.scm.wms.api.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SCM 分页结果（无 Spring 依赖，供 Facade 与 Adapter 转换）。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class ScmPageResult<T> {

    /** 当前页数据 */
    private List<T> rows;

    /** 总记录数 */
    private long total;

}
