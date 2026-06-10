<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item v-if="isSuperAdmin" label="租户编号" prop="tenantId">
              <el-select v-model="queryParams.tenantId" clearable placeholder="请选择租户" @change="handleQuery">
                <el-option
                  v-for="item in tenantOptions"
                  :key="item.tenantId"
                  :label="`${item.companyName} (${item.tenantId})`"
                  :value="String(item.tenantId)"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="组织编码" prop="orgCode">
              <el-input v-model="queryParams.orgCode" placeholder="请输入组织编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="组织名称" prop="orgName">
              <el-input v-model="queryParams.orgName" placeholder="请输入组织名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="组织类型" prop="orgType">
              <el-select v-model="queryParams.orgType" placeholder="请选择组织类型" clearable>
                <el-option
                  v-for="item in ORG_TYPE_OPTIONS"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option
                  v-for="dict in wms_enable_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button v-hasPermi="['scm:wms:org:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['scm:wms:org:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList" />
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="orgList">
        <el-table-column label="组织ID" align="center" prop="id" width="100" />
        <el-table-column label="组织编码" align="center" prop="orgCode" min-width="120" />
        <el-table-column label="组织名称" align="center" prop="orgName" min-width="140" />
        <el-table-column label="组织类型" align="center" prop="orgType">
          <template #default="scope">
            <span>{{ formatOrgTypeLabel(scope.row.orgType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="上级组织" align="center" prop="parentOrgName" min-width="120">
          <template #default="scope">{{ scope.row.parentOrgName || orgNameById(scope.row.parentOrgId) }}</template>
        </el-table-column>
        <el-table-column label="关联部门" align="center" prop="linkedDeptId" width="100" />
        <el-table-column label="状态" align="center" prop="status" width="90">
          <template #default="scope">
            <dict-tag :options="wms_enable_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="更新时间" align="center" prop="updateTime" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="140" fixed="right">
          <template #default="scope">
            <el-button v-hasPermi="['scm:wms:org:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" />
            <el-button v-hasPermi="['scm:wms:org:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)" />
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="640px" append-to-body>
      <el-form ref="orgFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col v-if="isSuperAdmin" :span="12">
            <el-form-item label="租户编号" prop="tenantId">
              <el-select v-model="form.tenantId" class="w-full" placeholder="请选择租户">
                <el-option
                  v-for="item in tenantOptions"
                  :key="item.tenantId"
                  :label="`${item.companyName} (${item.tenantId})`"
                  :value="String(item.tenantId)"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组织编码" prop="orgCode">
              <el-input v-model="form.orgCode" :disabled="dialog.isEdit" placeholder="如 ORG-BJ" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组织名称" prop="orgName">
              <el-input v-model="form.orgName" placeholder="请输入组织名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组织类型" prop="orgType">
              <el-select v-model="form.orgType" class="w-full" placeholder="请选择组织类型">
                <el-option
                  v-for="item in ORG_TYPE_OPTIONS"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上级组织" prop="parentOrgId">
              <el-tree-select
                v-model="form.parentOrgId"
                :data="parentOrgOptions"
                :props="{ value: 'id', label: 'label', children: 'children' } as any"
                value-key="id"
                placeholder="无上级可留空"
                check-strictly
                clearable
                class="w-full"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联部门" prop="linkedDeptId">
              <el-tree-select
                v-model="form.linkedDeptId"
                :data="enabledDeptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' } as any"
                value-key="id"
                placeholder="请选择关联部门"
                check-strictly
                clearable
                class="w-full"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in wms_enable_status"
                  :key="dict.value"
                  :value="parseInt(dict.value)"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="buttonLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ScmWmsOrg" lang="ts">
import { deptTreeSelect } from '@/api/system/post';
import { DeptTreeVO } from '@/api/system/dept/types';
import { addOrg, delOrg, getOrg, listOrgOptions, pageOrg, updateOrg } from '@/api/scm/wms';
import { OrgForm, OrgQuery, OrgVO } from '@/api/scm/types';
import { useScmTenant } from '@/composables/useScmTenant';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { wms_enable_status } = toRefs<any>(proxy?.useDict('wms_enable_status'));
const { isSuperAdmin, currentTenantId, tenantOptions, loadTenantOptions, resolveTenantId } = useScmTenant();

/** 组织类型码表，见 B1 PRODUCT §3 / wms_org_type 字典 */
const ORG_TYPE_OPTIONS = [
  { label: '集团', value: 10 },
  { label: '法人', value: 20 },
  { label: '事业部', value: 30 },
  { label: '区域', value: 40 }
] as const;

const formatOrgTypeLabel = (val?: number | string | null) => {
  if (val === undefined || val === null || val === '') return '';
  const n = typeof val === 'number' ? val : parseInt(String(val), 10);
  return ORG_TYPE_OPTIONS.find((o) => o.value === n)?.label ?? String(val);
};

const orgList = ref<OrgVO[]>([]);
const orgOptions = ref<OrgVO[]>([]);
const enabledDeptOptions = ref<DeptTreeVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const editId = ref<number>(0);

const queryFormRef = ref<ElFormInstance>();
const orgFormRef = ref<ElFormInstance>();

const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false
});

const initFormData: OrgForm = {
  tenantId: undefined,
  orgCode: undefined,
  orgName: undefined,
  orgType: undefined,
  parentOrgId: 0,
  linkedDeptId: undefined,
  status: 10,
  sortOrder: 0,
  remark: undefined
};

const data = reactive<PageData<OrgForm, OrgQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    tenantId: undefined,
    orgCode: undefined,
    orgName: undefined,
    orgType: undefined,
    status: undefined
  },
  rules: {}
});

const { queryParams, form } = toRefs(data);

const rules = computed(() => ({
  tenantId: isSuperAdmin.value ? [{ required: true, message: '请选择租户', trigger: 'change' }] : [],
  orgCode: [{ required: true, message: '组织编码不能为空', trigger: 'blur' }],
  orgName: [{ required: true, message: '组织名称不能为空', trigger: 'blur' }],
  orgType: [{ required: true, message: '请选择组织类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}));

interface OrgTreeNode {
  id: number;
  label: string;
  children?: OrgTreeNode[];
}

const parentOrgOptions = computed(() => {
  const tree = buildOrgTree(orgOptions.value, dialog.isEdit ? editId.value : undefined);
  return [{ id: 0, label: '顶级（无上级）', children: tree }];
});

const orgNameById = (id?: number) => {
  if (id === undefined || id === null) return '—';
  if (id === 0) return '—';
  return orgOptions.value.find((item) => item.id === id)?.orgName || String(id);
};

const buildOrgTree = (orgs: OrgVO[], excludeId?: number): OrgTreeNode[] => {
  const nodes = orgs
    .filter((org) => org.id !== excludeId)
    .map((org) => ({
      id: org.id,
      label: `${org.orgName} (${org.orgCode})`,
      parentOrgId: org.parentOrgId,
      children: [] as OrgTreeNode[]
    }));
  const map = new Map<number, OrgTreeNode & { parentOrgId?: number }>();
  nodes.forEach((node) => map.set(node.id, node));
  const roots: OrgTreeNode[] = [];
  map.forEach((node) => {
    if (node.parentOrgId && node.parentOrgId !== 0 && map.has(node.parentOrgId)) {
      map.get(node.parentOrgId)!.children!.push(node);
    } else {
      roots.push(node);
    }
  });
  return roots;
};

const filterDisabledDept = (deptList: DeptTreeVO[]): DeptTreeVO[] => {
  return deptList.filter((dept) => {
    if (dept.disabled) return false;
    if (dept.children?.length) dept.children = filterDisabledDept(dept.children);
    return true;
  });
};

const loadDeptTree = async () => {
  const res = await deptTreeSelect();
  enabledDeptOptions.value = filterDisabledDept(res.data || []);
};

const activeTenantId = () => resolveTenantId(queryParams.value.tenantId || currentTenantId.value);

const loadOrgOptionList = async () => {
  const res = await listOrgOptions(isSuperAdmin.value ? activeTenantId() : undefined);
  orgOptions.value = res.data || [];
};

const getList = async () => {
  loading.value = true;
  try {
    const params: OrgQuery = {
      ...queryParams.value,
      tenantId: isSuperAdmin.value ? activeTenantId() : undefined
    };
    const res = await pageOrg(params);
    orgList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const reset = () => {
  form.value = { ...initFormData };
  orgFormRef.value?.resetFields();
};

const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.value.pageNum = 1;
  queryParams.value.pageSize = 10;
  if (!isSuperAdmin.value) {
    queryParams.value.tenantId = currentTenantId.value;
  }
  handleQuery();
};

const handleAdd = () => {
  reset();
  dialog.isEdit = false;
  editId.value = 0;
  form.value.tenantId = isSuperAdmin.value ? undefined : currentTenantId.value;
  form.value.status = 10;
  form.value.parentOrgId = 0;
  form.value.sortOrder = 0;
  dialog.title = '新增组织档案';
  dialog.visible = true;
};

const handleUpdate = async (row: OrgVO) => {
  reset();
  dialog.isEdit = true;
  editId.value = row.id;
  const res = await getOrg(row.id, isSuperAdmin.value ? activeTenantId() : undefined);
  Object.assign(form.value, res.data);
  if (isSuperAdmin.value) {
    form.value.tenantId = activeTenantId();
  }
  dialog.title = '修改组织档案';
  dialog.visible = true;
};

const handleExport = () => {
  proxy?.download(
    'api/scm/wms/orgs/export',
    {
      ...queryParams.value,
      tenantId: isSuperAdmin.value ? activeTenantId() : undefined
    },
    `wms_org_${new Date().getTime()}.xlsx`
  );
};

const handleDelete = async (row: OrgVO) => {
  await proxy?.$modal.confirm(`确认删除组织「${row.orgCode}」吗？`);
  await delOrg(row.id, isSuperAdmin.value ? activeTenantId() : undefined);
  proxy?.$modal.msgSuccess('删除成功');
  await loadOrgOptionList();
  await getList();
};

const submitForm = () => {
  orgFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    buttonLoading.value = true;
    try {
      const payload: OrgForm = {
        ...form.value,
        tenantId: isSuperAdmin.value ? form.value.tenantId : undefined,
        parentOrgId: form.value.parentOrgId ?? 0
      };
      if (dialog.isEdit) {
        await updateOrg(editId.value, payload);
        proxy?.$modal.msgSuccess('修改成功');
      } else {
        await addOrg(payload);
        proxy?.$modal.msgSuccess('新增成功');
      }
      dialog.visible = false;
      await loadOrgOptionList();
      await getList();
    } finally {
      buttonLoading.value = false;
    }
  });
};

onMounted(async () => {
  await loadTenantOptions();
  await loadDeptTree();
  if (!isSuperAdmin.value) {
    queryParams.value.tenantId = currentTenantId.value;
  }
  await loadOrgOptionList();
  await getList();
});
</script>
