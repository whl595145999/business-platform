import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import {
  BrandForm,
  BrandQuery,
  BrandVO,
  CategoryDefaultsVO,
  CategoryForm,
  CategoryQuery,
  CategoryVO,
  SkuForm,
  SkuQuery,
  SkuVO
} from '@/api/scm/types';

/* #region scm-gen:product-brand — 品牌 | prd_brand */
export const pageBrand = (query?: BrandQuery) => {
  return request({
    url: '/api/scm/product/brands',
    method: 'get',
    params: query
  });
};

export const getBrand = (id: string | number, tenantId?: string): AxiosPromise<BrandVO> => {
  return request({
    url: '/api/scm/product/brands/' + id,
    method: 'get',
    params: { tenantId }
  });
};

export const addBrand = (data: BrandForm): AxiosPromise<BrandVO> => {
  return request({
    url: '/api/scm/product/brands',
    method: 'post',
    data
  });
};

export const updateBrand = (id: string | number, data: BrandForm): AxiosPromise<BrandVO> => {
  return request({
    url: '/api/scm/product/brands/' + id,
    method: 'put',
    data
  });
};

export const delBrand = (id: string | number, tenantId?: string): AxiosPromise<void> => {
  return request({
    url: '/api/scm/product/brands/' + id,
    method: 'delete',
    params: { tenantId }
  });
};

export const listBrandOptions = (tenantId?: string, status?: number): AxiosPromise<BrandVO[]> => {
  return request({
    url: '/api/scm/product/brands/options',
    method: 'get',
    params: { tenantId, status }
  });
};
/* #endregion scm-gen:product-brand */

/* #region scm-gen:product-category — 类目 | prd_category */
export const pageCategory = (query?: CategoryQuery) => {
  return request({
    url: '/api/scm/product/categories',
    method: 'get',
    params: query
  });
};

export const listCategoryTree = (tenantId?: string, status?: number): AxiosPromise<CategoryVO[]> => {
  return request({
    url: '/api/scm/product/categories/tree',
    method: 'get',
    params: { tenantId, status }
  });
};

export const getCategory = (id: string | number, tenantId?: string): AxiosPromise<CategoryVO> => {
  return request({
    url: '/api/scm/product/categories/' + id,
    method: 'get',
    params: { tenantId }
  });
};

export const getCategoryDefaults = (categoryId: string | number, tenantId?: string): AxiosPromise<CategoryDefaultsVO> => {
  return request({
    url: '/api/scm/product/categories/' + categoryId + '/defaults',
    method: 'get',
    params: { tenantId }
  });
};

export const addCategory = (data: CategoryForm): AxiosPromise<CategoryVO> => {
  return request({
    url: '/api/scm/product/categories',
    method: 'post',
    data
  });
};

export const updateCategory = (id: string | number, data: CategoryForm): AxiosPromise<CategoryVO> => {
  return request({
    url: '/api/scm/product/categories/' + id,
    method: 'put',
    data
  });
};

export const delCategory = (id: string | number, tenantId?: string): AxiosPromise<void> => {
  return request({
    url: '/api/scm/product/categories/' + id,
    method: 'delete',
    params: { tenantId }
  });
};
/* #endregion scm-gen:product-category */

/* #region scm-gen:product-sku — 货品 | prd_sku */
export const pageSku = (query?: SkuQuery) => {
  return request({
    url: '/api/scm/product/skus',
    method: 'get',
    params: query
  });
};

export const getSku = (skuId: string | number, tenantId?: string): AxiosPromise<SkuVO> => {
  return request({
    url: '/api/scm/product/skus/' + skuId,
    method: 'get',
    params: { tenantId }
  });
};

export const addSku = (data: SkuForm): AxiosPromise<SkuVO> => {
  return request({
    url: '/api/scm/product/skus',
    method: 'post',
    data
  });
};

export const updateSku = (skuId: string | number, data: SkuForm): AxiosPromise<SkuVO> => {
  return request({
    url: '/api/scm/product/skus/' + skuId,
    method: 'put',
    data
  });
};

export const delSku = (skuId: string | number, tenantId?: string): AxiosPromise<void> => {
  return request({
    url: '/api/scm/product/skus/' + skuId,
    method: 'delete',
    params: { tenantId }
  });
};
/* #endregion scm-gen:product-sku */
