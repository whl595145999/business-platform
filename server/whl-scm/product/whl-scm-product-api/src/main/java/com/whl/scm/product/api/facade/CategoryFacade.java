package com.whl.scm.product.api.facade;

import com.whl.scm.product.api.command.CreateCategoryCommand;
import com.whl.scm.product.api.command.DeleteCategoryCommand;
import com.whl.scm.product.api.command.UpdateCategoryCommand;
import com.whl.scm.product.api.dto.CategoryDto;
import com.whl.scm.product.api.page.ScmPageResult;
import com.whl.scm.product.api.query.GetCategoryQuery;
import com.whl.scm.product.api.query.ListCategoryTreeQuery;
import com.whl.scm.product.api.query.PageCategoryQuery;

import java.util.List;

/**
 * 货品类目 Facade（标准 CRUD + 分页 + 树）。
 *
 * @author whl
 */
public interface CategoryFacade {

    ScmPageResult<CategoryDto> pageCategory(PageCategoryQuery query);

    List<CategoryDto> listCategoryTree(ListCategoryTreeQuery query);

    CategoryDto getCategory(GetCategoryQuery query);

    CategoryDto createCategory(CreateCategoryCommand command);

    CategoryDto updateCategory(UpdateCategoryCommand command);

    void deleteCategory(DeleteCategoryCommand command);

}
