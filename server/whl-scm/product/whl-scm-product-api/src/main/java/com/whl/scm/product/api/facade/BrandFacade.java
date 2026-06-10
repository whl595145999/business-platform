package com.whl.scm.product.api.facade;

import com.whl.scm.product.api.command.CreateBrandCommand;
import com.whl.scm.product.api.command.DeleteBrandCommand;
import com.whl.scm.product.api.command.UpdateBrandCommand;
import com.whl.scm.product.api.dto.BrandDto;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetBrandQuery;
import com.whl.scm.product.api.query.ListBrandOptionsQuery;
import com.whl.scm.product.api.query.PageBrandQuery;

import java.util.List;

/**
 * 品牌 Facade（标准 CRUD + 分页 + 下拉选项）。
 *
 * @author whl
 */
public interface BrandFacade {

    ScmPageResult<BrandDto> pageBrand(PageBrandQuery query);

    List<BrandDto> listBrand(PageBrandQuery query);

    BrandDto getBrand(GetBrandQuery query);

    BrandDto createBrand(CreateBrandCommand command);

    BrandDto updateBrand(UpdateBrandCommand command);

    void deleteBrand(DeleteBrandCommand command);

    List<BrandDto> listBrandOptions(ListBrandOptionsQuery query);

}
