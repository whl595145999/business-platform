package com.whl.scm.product.api.facade;

import com.whl.scm.product.api.dto.ResolveBarcodeResult;
import com.whl.scm.product.api.dto.SkuDto;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetSkuQuery;
import com.whl.scm.product.api.query.ResolveBarcodeQuery;
import com.whl.scm.product.api.query.SearchSkuQuery;
import com.whl.scm.product.api.query.ValidateSkuQuery;

/**
 * 货品跨域 Facade（只读 + 校验），供 OMS / WMS / Inventory 调用。
 *
 * @author whl
 */
public interface ProductFacade {

    SkuDto getSku(GetSkuQuery query);

    void validateSku(ValidateSkuQuery query);

    ResolveBarcodeResult resolveBarcode(ResolveBarcodeQuery query);

    ScmPageResult<SkuDto> searchSku(SearchSkuQuery query);

}
