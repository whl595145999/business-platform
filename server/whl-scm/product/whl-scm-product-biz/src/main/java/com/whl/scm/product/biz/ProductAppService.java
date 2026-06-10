package com.whl.scm.product.biz;

import com.whl.scm.product.api.dto.ResolveBarcodeResult;
import com.whl.scm.product.api.dto.SkuDto;
import com.whl.scm.product.api.facade.ProductFacade;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetSkuQuery;
import com.whl.scm.product.api.query.ResolveBarcodeQuery;
import com.whl.scm.product.api.query.SearchSkuQuery;
import com.whl.scm.product.api.query.ValidateSkuQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 货品跨域应用服务，实现 {@link ProductFacade}。
 *
 * @author whl
 */
@Service
@RequiredArgsConstructor
public class ProductAppService implements ProductFacade {

    private final SkuAppService skuAppService;

    @Override
    public SkuDto getSku(GetSkuQuery query) {
        return skuAppService.getSkuDetail(query);
    }

    @Override
    public void validateSku(ValidateSkuQuery query) {
        skuAppService.validateSkuReference(query);
    }

    @Override
    public ResolveBarcodeResult resolveBarcode(ResolveBarcodeQuery query) {
        return skuAppService.resolveBarcode(query);
    }

    @Override
    public ScmPageResult<SkuDto> searchSku(SearchSkuQuery query) {
        return skuAppService.searchSku(query);
    }

}
