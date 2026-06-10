import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { OrgForm, OrgQuery, OrgVO } from '@/api/scm/types';

/* #region scm-gen:wms-org — 组织档案 | wms_org */
/** 分页查询组织档案 */
export const pageOrg = (query?: OrgQuery) => {
  return request({
    url: '/api/scm/wms/orgs',
    method: 'get',
    params: query
  });
};

/** 查询组织档案详情 */
export const getOrg = (id: string | number, tenantId?: string): AxiosPromise<OrgVO> => {
  return request({
    url: '/api/scm/wms/orgs/' + id,
    method: 'get',
    params: { tenantId }
  });
};

/** 新增组织档案 */
export const addOrg = (data: OrgForm): AxiosPromise<OrgVO> => {
  return request({
    url: '/api/scm/wms/orgs',
    method: 'post',
    data
  });
};

/** 修改组织档案 */
export const updateOrg = (id: string | number, data: OrgForm): AxiosPromise<OrgVO> => {
  return request({
    url: '/api/scm/wms/orgs/' + id,
    method: 'put',
    data
  });
};

/** 删除组织档案 */
export const delOrg = (id: string | number, tenantId?: string): AxiosPromise<void> => {
  return request({
    url: '/api/scm/wms/orgs/' + id,
    method: 'delete',
    params: { tenantId }
  });
};

/** 查询组织档案下拉选项 */
export const listOrgOptions = (tenantId?: string, status?: number): AxiosPromise<OrgVO[]> => {
  return request({
    url: '/api/scm/wms/orgs/options',
    method: 'get',
    params: { tenantId, status }
  });
};
/* #endregion scm-gen:wms-org */
