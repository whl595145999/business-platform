package com.whl.scm.wms.api.facade;

import com.whl.scm.wms.api.command.CreateOrgCommand;
import com.whl.scm.wms.api.command.DeleteOrgCommand;
import com.whl.scm.wms.api.command.UpdateOrgCommand;
import com.whl.scm.wms.api.dto.OrgDto;
import com.whl.scm.wms.api.page.ScmPageResult;
import com.whl.scm.wms.api.query.GetOrgQuery;
import com.whl.scm.wms.api.query.ListOrgOptionsQuery;
import com.whl.scm.wms.api.query.PageOrgQuery;

import java.util.List;

/**
 * 组织档案 Facade（标准 CRUD + 分页 + 下拉选项）。
 * <p>
 * 对应表 {@code wms_org}，REST 前缀 {@code /api/scm/wms/orgs}。
 *
 * @author whl
 */
public interface OrgFacade {

    /**
     * 分页查询组织档案。
     *
     * @param query 分页与筛选条件
     * @return 分页结果
     */
    ScmPageResult<OrgDto> pageOrg(PageOrgQuery query);

    /**
     * 按筛选条件查询组织档案全量列表（导出等场景，不分页）。
     *
     * @param query 筛选条件（忽略 pageNum/pageSize）
     * @return 列表
     */
    List<OrgDto> listOrg(PageOrgQuery query);

    /**
     * 按业务键查询单条组织档案。
     *
     * @param query 租户 + 业务键
     * @return DTO
     */
    OrgDto getOrg(GetOrgQuery query);

    /**
     * 创建组织档案。
     *
     * @param command 创建命令
     * @return 新建 DTO
     */
    OrgDto createOrg(CreateOrgCommand command);

    /**
     * 更新组织档案。
     *
     * @param command 更新命令
     * @return 更新后 DTO
     */
    OrgDto updateOrg(UpdateOrgCommand command);

    /**
     * 删除组织档案。
     *
     * @param command 删除命令
     */
    void deleteOrg(DeleteOrgCommand command);

    /**
     * 查询组织档案下拉选项。
     *
     * @param query 租户编号
     * @return 选项列表
     */
    List<OrgDto> listOrgOptions(ListOrgOptionsQuery query);

}
