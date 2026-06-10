package com.whl.scm.wms.biz;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.framework.core.exception.ServiceException;
import com.whl.framework.core.utils.StringUtils;
import com.whl.scm.wms.api.command.CreateOrgCommand;
import com.whl.scm.wms.api.command.DeleteOrgCommand;
import com.whl.scm.wms.api.command.UpdateOrgCommand;
import com.whl.scm.wms.api.dto.OrgDto;
import com.whl.scm.wms.api.error.WmsErrorCode;
import com.whl.scm.wms.api.facade.OrgFacade;
import com.whl.scm.wms.api.page.ScmPageResult;
import com.whl.scm.wms.api.query.GetOrgQuery;
import com.whl.scm.wms.api.query.ListOrgOptionsQuery;
import com.whl.scm.wms.api.query.PageOrgQuery;
import com.whl.scm.wms.biz.domain.WmsOrg;
import com.whl.scm.wms.biz.mapper.WmsOrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 组织档案应用服务，实现 {@link OrgFacade}。
 *
 * @author whl
 */
@Service
@RequiredArgsConstructor
public class OrgAppService implements OrgFacade {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int STATUS_ENABLED = 10;
    private static final long TOP_PARENT_ID = 0L;

    private final WmsOrgMapper orgMapper;

    /** {@inheritDoc} */
    @Override
    public ScmPageResult<OrgDto> pageOrg(PageOrgQuery query) {
        LambdaQueryWrapper<WmsOrg> lqw = buildQueryWrapper(query);
        Page<WmsOrg> page = orgMapper.selectPage(buildPage(query), lqw);
        Map<Long, String> nameIndex = loadOrgNameIndex(query.getTenantId());
        List<OrgDto> rows = page.getRecords().stream()
            .map(entity -> toDto(entity, nameIndex))
            .toList();
        ScmPageResult<OrgDto> result = new ScmPageResult<>();
        result.setRows(rows);
        result.setTotal(page.getTotal());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<OrgDto> listOrg(PageOrgQuery query) {
        LambdaQueryWrapper<WmsOrg> lqw = buildQueryWrapper(query);
        Map<Long, String> nameIndex = loadOrgNameIndex(query.getTenantId());
        return orgMapper.selectList(lqw).stream()
            .map(entity -> toDto(entity, nameIndex))
            .toList();
    }

    /** {@inheritDoc} */
    @Override
    public OrgDto getOrg(GetOrgQuery query) {
        WmsOrg entity = requireByKey(query.getTenantId(), query.getId());
        return toDto(entity, loadOrgNameIndex(query.getTenantId()));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrgDto createOrg(CreateOrgCommand command) {
        Long parentOrgId = normalizeParentOrgId(command.getParentOrgId());
        assertParentValid(command.getTenantId(), null, parentOrgId);
        WmsOrg entity = new WmsOrg();
        entity.setTenantId(command.getTenantId());
        entity.setOrgCode(command.getOrgCode());
        entity.setOrgName(command.getOrgName());
        entity.setOrgType(command.getOrgType());
        entity.setParentOrgId(parentOrgId);
        entity.setLinkedDeptId(command.getLinkedDeptId());
        entity.setStatus(command.getStatus() == null ? STATUS_ENABLED : command.getStatus());
        entity.setSortOrder(command.getSortOrder() == null ? 0 : command.getSortOrder());
        entity.setRemark(command.getRemark());
        try {
            orgMapper.insert(entity);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException(WmsErrorCode.WMS_ORG_CODE_DUPLICATE.message());
        }
        return toDto(entity, loadOrgNameIndex(command.getTenantId()));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrgDto updateOrg(UpdateOrgCommand command) {
        WmsOrg entity = requireByKey(command.getTenantId(), command.getId());
        Long parentOrgId = normalizeParentOrgId(command.getParentOrgId());
        assertParentValid(command.getTenantId(), entity.getId(), parentOrgId);
        entity.setOrgName(command.getOrgName());
        entity.setOrgType(command.getOrgType());
        entity.setParentOrgId(parentOrgId);
        entity.setLinkedDeptId(command.getLinkedDeptId());
        entity.setStatus(command.getStatus());
        if (command.getSortOrder() != null) {
            entity.setSortOrder(command.getSortOrder());
        }
        entity.setRemark(command.getRemark());
        orgMapper.updateById(entity);
        return toDto(entity, loadOrgNameIndex(command.getTenantId()));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrg(DeleteOrgCommand command) {
        if (StrUtil.hasBlank(command.getTenantId()) || command.getId() == null) {
            throw new ServiceException("删除组织档案参数不完整");
        }
        WmsOrg entity = requireByKey(command.getTenantId(), command.getId());
        Long childCount = orgMapper.selectCount(
            Wrappers.<WmsOrg>lambdaQuery()
                .eq(WmsOrg::getTenantId, command.getTenantId())
                .eq(WmsOrg::getParentOrgId, entity.getId())
        );
        if (childCount != null && childCount > 0) {
            throw new ServiceException(WmsErrorCode.WMS_ORG_HAS_CHILDREN.message());
        }
        orgMapper.deleteById(entity.getId());
    }

    /** {@inheritDoc} */
    @Override
    public List<OrgDto> listOrgOptions(ListOrgOptionsQuery query) {
        Integer status = query.getStatus() == null ? STATUS_ENABLED : query.getStatus();
        Map<Long, String> nameIndex = loadOrgNameIndex(query.getTenantId());
        return orgMapper.selectList(
            Wrappers.<WmsOrg>lambdaQuery()
                .eq(WmsOrg::getTenantId, query.getTenantId())
                .eq(status != null, WmsOrg::getStatus, status)
                .orderByAsc(WmsOrg::getSortOrder)
                .orderByAsc(WmsOrg::getId)
        ).stream().map(entity -> toDto(entity, nameIndex)).toList();
    }

    private void assertParentValid(String tenantId, Long selfId, Long parentOrgId) {
        if (parentOrgId == null || parentOrgId == TOP_PARENT_ID) {
            return;
        }
        if (selfId != null && Objects.equals(selfId, parentOrgId)) {
            throw new ServiceException(WmsErrorCode.WMS_ORG_PARENT_CYCLE.message());
        }
        WmsOrg parent = findByKey(tenantId, parentOrgId);
        if (parent == null) {
            throw new ServiceException(WmsErrorCode.WMS_ORG_NOT_FOUND.message());
        }
        if (!Objects.equals(STATUS_ENABLED, parent.getStatus())) {
            throw new ServiceException(WmsErrorCode.WMS_ORG_DISABLED.message());
        }
        if (selfId != null && isDescendant(tenantId, selfId, parentOrgId)) {
            throw new ServiceException(WmsErrorCode.WMS_ORG_PARENT_CYCLE.message());
        }
    }

    private boolean isDescendant(String tenantId, Long ancestorId, Long nodeId) {
        Long current = nodeId;
        while (current != null && current != TOP_PARENT_ID) {
            if (Objects.equals(current, ancestorId)) {
                return true;
            }
            WmsOrg node = findByKey(tenantId, current);
            if (node == null) {
                return false;
            }
            current = node.getParentOrgId();
        }
        return false;
    }

    private Long normalizeParentOrgId(Long parentOrgId) {
        return parentOrgId == null ? TOP_PARENT_ID : parentOrgId;
    }

    private WmsOrg requireByKey(String tenantId, Long id) {
        WmsOrg entity = findByKey(tenantId, id);
        if (entity == null) {
            throw new ServiceException(WmsErrorCode.WMS_ORG_NOT_FOUND.message());
        }
        return entity;
    }

    private WmsOrg findByKey(String tenantId, Long id) {
        return orgMapper.selectOne(
            Wrappers.<WmsOrg>lambdaQuery()
                .eq(WmsOrg::getTenantId, tenantId)
                .eq(WmsOrg::getId, id)
        );
    }

    private Map<Long, String> loadOrgNameIndex(String tenantId) {
        List<WmsOrg> all = orgMapper.selectList(
            Wrappers.<WmsOrg>lambdaQuery()
                .eq(WmsOrg::getTenantId, tenantId)
                .select(WmsOrg::getId, WmsOrg::getOrgName)
        );
        Map<Long, String> index = new HashMap<>(all.size() + 1);
        index.put(TOP_PARENT_ID, "—");
        for (WmsOrg org : all) {
            index.put(org.getId(), org.getOrgName());
        }
        return index;
    }

    private LambdaQueryWrapper<WmsOrg> buildQueryWrapper(PageOrgQuery query) {
        LambdaQueryWrapper<WmsOrg> lqw = Wrappers.lambdaQuery();
        lqw.eq(WmsOrg::getTenantId, query.getTenantId());
        lqw.eq(StringUtils.isNotBlank(query.getOrgCode()), WmsOrg::getOrgCode, query.getOrgCode());
        lqw.like(StringUtils.isNotBlank(query.getOrgName()), WmsOrg::getOrgName, query.getOrgName());
        lqw.eq(query.getOrgType() != null, WmsOrg::getOrgType, query.getOrgType());
        lqw.eq(query.getStatus() != null, WmsOrg::getStatus, query.getStatus());
        lqw.orderByAsc(WmsOrg::getSortOrder);
        lqw.orderByDesc(WmsOrg::getId);
        return lqw;
    }

    private Page<WmsOrg> buildPage(PageOrgQuery query) {
        int num = query.getPageNum() == null || query.getPageNum() <= 0 ? DEFAULT_PAGE_NUM : query.getPageNum();
        int size = query.getPageSize() == null || query.getPageSize() <= 0 ? DEFAULT_PAGE_SIZE : query.getPageSize();
        return new Page<>(num, size);
    }

    private OrgDto toDto(WmsOrg entity, Map<Long, String> nameIndex) {
        if (entity == null) {
            return null;
        }
        OrgDto dto = new OrgDto();
        dto.setId(entity.getId());
        dto.setOrgCode(entity.getOrgCode());
        dto.setOrgName(entity.getOrgName());
        dto.setOrgType(entity.getOrgType());
        dto.setParentOrgId(entity.getParentOrgId());
        Long parentId = entity.getParentOrgId() == null ? TOP_PARENT_ID : entity.getParentOrgId();
        dto.setParentOrgName(nameIndex.getOrDefault(parentId, parentId == TOP_PARENT_ID ? "—" : String.valueOf(parentId)));
        dto.setLinkedDeptId(entity.getLinkedDeptId());
        dto.setStatus(entity.getStatus());
        dto.setSortOrder(entity.getSortOrder());
        dto.setRemark(entity.getRemark());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

}
