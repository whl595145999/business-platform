package com.whl.scm.product.api.facade;

import com.whl.scm.product.api.command.CreateSkuCommand;
import com.whl.scm.product.api.command.DeleteSkuCommand;
import com.whl.scm.product.api.command.UpdateSkuCommand;
import com.whl.scm.product.api.dto.SkuDto;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetSkuQuery;
import com.whl.scm.product.api.query.PageSkuQuery;

/**
 * 货品 Facade（CRUD + 分页 + 详情含条码子表）。
 *
 * @author whl
 */
public interface SkuFacade {

    ScmPageResult<SkuDto> pageSku(PageSkuQuery query);

    SkuDto getSkuDetail(GetSkuQuery query);

    SkuDto createSku(CreateSkuCommand command);

    SkuDto updateSku(UpdateSkuCommand command);

    void deleteSku(DeleteSkuCommand command);

}
