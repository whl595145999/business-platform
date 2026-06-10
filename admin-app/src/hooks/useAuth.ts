import { checkPermi, checkPermiAnd, checkPermiOr, checkRole, checkRoleAnd, checkRoleOr } from '@/utils/permission'

/** 业务平台权限校验 Composable */
export function useAuth() {
  return {
    hasPermi: checkPermi,
    hasPermiOr: checkPermiOr,
    hasPermiAnd: checkPermiAnd,
    hasRole: checkRole,
    hasRoleOr: checkRoleOr,
    hasRoleAnd: checkRoleAnd,
  }
}
