/* #region scm-gen:wms-org — 组织档案 | wms_org */
export interface OrgVO {
  /** 主键 */
  id: number;
  /** 组织编码，租户内唯一 */
  orgCode: string;
  /** 组织名称 */
  orgName: string;
  /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
  orgType: number;
  /** 上级组织ID，0 表示顶级 */
  parentOrgId?: number;
  /** 上级组织名称 */
  parentOrgName?: string;
  /** 关联 sys_dept.dept_id */
  linkedDeptId?: number;
  /** 状态：10-启用,20-停用 */
  status: number;
  /** 排序 */
  sortOrder?: number;
  /** 备注 */
  remark?: string;
  /** 更新时间 */
  updateTime?: string;
}

export interface OrgForm {
  tenantId?: string;
  /** 组织编码，租户内唯一 */
  orgCode?: string;
  /** 组织名称 */
  orgName?: string;
  /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
  orgType?: number;
  /** 上级组织ID，0 表示顶级 */
  parentOrgId?: number;
  /** 关联 sys_dept.dept_id */
  linkedDeptId?: number;
  /** 状态：10-启用,20-停用 */
  status?: number;
  /** 排序 */
  sortOrder?: number;
  /** 备注 */
  remark?: string;
}

export interface OrgQuery {
  tenantId?: string;
  pageNum?: number;
  pageSize?: number;
  /** 组织编码，租户内唯一 */
  orgCode?: string;
  /** 组织名称 */
  orgName?: string;
  /** 组织类型：10-集团,20-法人,30-事业部,40-区域 */
  orgType?: number;
  /** 状态：10-启用,20-停用 */
  status?: number;
}
/* #endregion scm-gen:wms-org */

/* #region scm-gen:product-brand — 品牌 | prd_brand */
export interface BrandVO {
  id: number;
  brandCode: string;
  brandName: string;
  status: number;
  sortOrder?: number;
  remark?: string;
  updateTime?: string;
}

export interface BrandForm {
  tenantId?: string;
  brandCode?: string;
  brandName?: string;
  status?: number;
  sortOrder?: number;
  remark?: string;
}

export interface BrandQuery {
  tenantId?: string;
  pageNum?: number;
  pageSize?: number;
  brandCode?: string;
  brandName?: string;
  status?: number;
}
/* #endregion scm-gen:product-brand */

/* #region scm-gen:product-category — 类目 | prd_category */
export interface CategoryVO {
  id: number;
  categoryCode: string;
  categoryName: string;
  parentId?: number;
  parentName?: string;
  status: number;
  sortOrder?: number;
  remark?: string;
  defaultItemClass?: number;
  defaultProductType?: number;
  defaultExpiryFlag?: number;
  defaultBarcodePolicy?: number;
  defaultShelfLifeValue?: number;
  defaultShelfLifeUnit?: number;
  defaultNearExpiryValue?: number;
  defaultNearExpiryUnit?: number;
  updateTime?: string;
}

export interface CategoryDefaultsVO {
  defaultItemClass?: number;
  defaultProductType?: number;
  defaultExpiryFlag?: number;
  defaultBarcodePolicy?: number;
  defaultShelfLifeValue?: number;
  defaultShelfLifeUnit?: number;
  defaultNearExpiryValue?: number;
  defaultNearExpiryUnit?: number;
}

export interface CategoryForm {
  tenantId?: string;
  categoryCode?: string;
  categoryName?: string;
  parentId?: number;
  status?: number;
  sortOrder?: number;
  remark?: string;
  defaultItemClass?: number;
  defaultProductType?: number;
  defaultExpiryFlag?: number;
  defaultBarcodePolicy?: number;
  defaultShelfLifeValue?: number;
  defaultShelfLifeUnit?: number;
  defaultNearExpiryValue?: number;
  defaultNearExpiryUnit?: number;
}

export interface CategoryQuery {
  tenantId?: string;
  pageNum?: number;
  pageSize?: number;
  categoryCode?: string;
  categoryName?: string;
  status?: number;
}
/* #endregion scm-gen:product-category */

/* #region scm-gen:product-sku — 货品 | prd_sku */
export interface SkuBarcodeVO {
  id?: number;
  barcode?: string;
  defaultFlag?: number;
  status?: number;
  remark?: string;
}

export interface SkuVO {
  skuId: number;
  skuCode: string;
  skuName: string;
  skuShortName?: string;
  spec?: string;
  status: number;
  sortOrder?: number;
  remark?: string;
  categoryId?: number;
  categoryName?: string;
  brandId?: number;
  brandName?: string;
  unitCode?: string;
  itemClass?: number;
  purchaseAllowed?: number;
  saleAllowed?: number;
  issueAllowed?: number;
  productType?: number;
  expiryFlag?: number;
  quotaFlag?: number;
  shelfLifeValue?: number;
  shelfLifeUnit?: number;
  nearExpiryValue?: number;
  nearExpiryUnit?: number;
  defaultOriginCode?: string;
  taxCategoryCode?: string;
  inputTaxRate?: string;
  outputTaxRate?: string;
  barcodePolicy?: number;
  primaryBarcode?: string;
  barcodes?: SkuBarcodeVO[];
  updateTime?: string;
}

export interface SkuForm {
  tenantId?: string;
  skuCode?: string;
  skuName?: string;
  skuShortName?: string;
  spec?: string;
  status?: number;
  sortOrder?: number;
  remark?: string;
  categoryId?: number;
  brandId?: number;
  unitCode?: string;
  itemClass?: number;
  purchaseAllowed?: number;
  saleAllowed?: number;
  issueAllowed?: number;
  productType?: number;
  expiryFlag?: number;
  quotaFlag?: number;
  shelfLifeValue?: number;
  shelfLifeUnit?: number;
  nearExpiryValue?: number;
  nearExpiryUnit?: number;
  defaultOriginCode?: string;
  taxCategoryCode?: string;
  inputTaxRate?: string;
  outputTaxRate?: string;
  barcodePolicy?: number;
  barcodes?: SkuBarcodeVO[];
}

export interface SkuQuery {
  tenantId?: string;
  pageNum?: number;
  pageSize?: number;
  skuCode?: string;
  skuName?: string;
  categoryId?: number;
  itemClass?: number;
  productType?: number;
  status?: number;
}
/* #endregion scm-gen:product-sku */
