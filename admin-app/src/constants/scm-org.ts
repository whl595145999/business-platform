/** 组织类型（与 PC ORG_TYPE_OPTIONS / wms_org.org_type 一致） */
export const ORG_TYPE_OPTIONS = [
  { label: '集团', value: 10 },
  { label: '法人', value: 20 },
  { label: '事业部', value: 30 },
  { label: '区域', value: 40 },
] as const

/** 启用状态（wms_org.status） */
export const ORG_STATUS_OPTIONS = [
  { label: '启用', value: 10 },
  { label: '停用', value: 20 },
] as const

export function formatOrgTypeLabel(value?: number | null) {
  if (value === undefined || value === null)
    return '-'
  return ORG_TYPE_OPTIONS.find(item => item.value === value)?.label ?? String(value)
}

export function formatOrgStatusLabel(value?: number | null) {
  if (value === undefined || value === null)
    return '-'
  return ORG_STATUS_OPTIONS.find(item => item.value === value)?.label ?? String(value)
}
