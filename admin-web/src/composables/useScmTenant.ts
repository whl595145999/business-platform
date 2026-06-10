import { listTenant } from '@/api/system/tenant';
import { TenantVO } from '@/api/system/tenant/types';
import { useUserStore } from '@/store/modules/user';

/**
 * SCM 页面租户上下文：超管可选租户，普通用户固定为登录租户。
 */
export function useScmTenant() {
  const userStore = useUserStore();

  const isSuperAdmin = computed(() => String(userStore.userId) === '1');
  const currentTenantId = computed(() => userStore.tenantId || '');
  const tenantOptions = ref<TenantVO[]>([]);

  const loadTenantOptions = async () => {
    if (!isSuperAdmin.value) {
      return;
    }
    const res = await listTenant({ pageNum: 1, pageSize: 500 } as any);
    tenantOptions.value = res.rows || [];
  };

  /** 表单/查询使用的有效租户编号 */
  const resolveTenantId = (formTenantId?: string) => {
    if (isSuperAdmin.value && formTenantId) {
      return formTenantId;
    }
    return currentTenantId.value;
  };

  return {
    isSuperAdmin,
    currentTenantId,
    tenantOptions,
    loadTenantOptions,
    resolveTenantId
  };
}
