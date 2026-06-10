import { ALL_PERMISSION, SUPER_ADMIN_ROLE } from '@/constants/app'
import { useUserStore } from '@/store/user'

export function checkPermi(permission: string) {
  const { permissions } = useUserStore()
  if (!permission)
    return false
  return permissions.includes(ALL_PERMISSION) || permissions.includes(permission)
}

export function checkPermiOr(permissionList: string[]) {
  return permissionList.some(item => checkPermi(item))
}

export function checkPermiAnd(permissionList: string[]) {
  return permissionList.every(item => checkPermi(item))
}

export function checkRole(role: string) {
  const { roles } = useUserStore()
  if (!role)
    return false
  return roles.includes(SUPER_ADMIN_ROLE) || roles.includes(role)
}

export function checkRoleOr(roleList: string[]) {
  return roleList.some(item => checkRole(item))
}

export function checkRoleAnd(roleList: string[]) {
  return roleList.every(item => checkRole(item))
}
