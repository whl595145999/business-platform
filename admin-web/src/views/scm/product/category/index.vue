<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item v-if="isSuperAdmin" label="租户编号" prop="tenantId">
              <el-select v-model="queryParams.tenantId" clearable placeholder="请选择租户" @change="handleQuery">
                <el-option v-for="item in tenantOptions" :key="item.tenantId" :label="`${item.companyName} (${item.tenantId})`" :value="String(item.tenantId)" />
              </el-select>
            </el-form-item>
            <el-form-item label="类目编码" prop="categoryCode">
              <el-input v-model="queryParams.categoryCode" placeholder="请输入类目编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="类目名称" prop="categoryName">
              <el-input v-model="queryParams.categoryName" placeholder="请输入类目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option v-for="dict in wms_enable_status" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)" />
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
            <el-button v-hasPermi="['scm:product:category:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList" />
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="categoryList">
        <el-table-column label="类目编码" align="center" prop="categoryCode" min-width="120" />
        <el-table-column label="类目名称" align="center" prop="categoryName" min-width="140" />
        <el-table-column label="上级类目" align="center" prop="parentName" min-width="120" />
        <el-table-column label="状态" align="center" prop="status" width="90">
          <template #default="scope"><dict-tag :options="wms_enable_status" :value="scope.row.status" /></template>
        </el-table-column>
        <el-table-column label="更新时间" align="center" prop="updateTime" width="170">
          <template #default="scope">{{ parseTime(scope.row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="140" fixed="right">
          <template #default="scope">
            <el-button v-hasPermi="['scm:product:category:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" />
            <el-button v-hasPermi="['scm:product:category:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)" />
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="720px" append-to-body>
      <el-form ref="categoryFormRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="20">
          <el-col v-if="isSuperAdmin" :span="12">
            <el-form-item label="租户编号" prop="tenantId">
              <el-select v-model="form.tenantId" class="w-full" placeholder="请选择租户">
                <el-option v-for="item in tenantOptions" :key="item.tenantId" :label="`${item.companyName} (${item.tenantId})`" :value="String(item.tenantId)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类目编码" prop="categoryCode">
              <el-input v-model="form.categoryCode" :disabled="dialog.isEdit" placeholder="租户内唯一" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类目名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入类目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上级类目" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="parentCategoryOptions"
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
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in wms_enable_status" :key="dict.value" :value="parseInt(dict.value)">{{ dict.label }}</el-radio>
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
              <el-input v-model="form.remark" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">新建 SKU 默认策略</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="默认货品类型">
              <el-select v-model="form.defaultItemClass" class="w-full" clearable placeholder="0=不继承">
                <el-option v-for="item in ITEM_CLASS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认履约类型">
              <el-select v-model="form.defaultProductType" class="w-full" clearable placeholder="0=不继承">
                <el-option v-for="item in PRODUCT_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认是否效期">
              <el-radio-group v-model="form.defaultExpiryFlag">
                <el-radio :value="0">否</el-radio>
                <el-radio :value="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认条码策略">
              <el-select v-model="form.defaultBarcodePolicy" class="w-full" clearable placeholder="0=不继承">
                <el-option label="10 必须有码" :value="10" />
                <el-option label="20 可无" :value="20" />
              </el-select>
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

<script setup name="ScmProductCategory" lang="ts">
import { addCategory, delCategory, getCategory, listCategoryTree, pageCategory, updateCategory } from '@/api/scm/product';
import { CategoryForm, CategoryQuery, CategoryVO } from '@/api/scm/types';
import { useScmTenant } from '@/composables/useScmTenant';

const ITEM_CLASS_OPTIONS = [
  { label: '贸易', value: 10 },
  { label: '物料', value: 20 },
  { label: '包材', value: 30 },
  { label: '药品', value: 40 },
  { label: '虚拟', value: 50 }
];

const PRODUCT_TYPE_OPTIONS = [
  { label: '实物', value: 10 },
  { label: '虚拟', value: 20 }
];

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { wms_enable_status } = toRefs<any>(proxy?.useDict('wms_enable_status'));
const { isSuperAdmin, currentTenantId, tenantOptions, loadTenantOptions, resolveTenantId } = useScmTenant();

const categoryList = ref<CategoryVO[]>([]);
const categoryTree = ref<CategoryVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const editId = ref(0);

const queryFormRef = ref<ElFormInstance>();
const categoryFormRef = ref<ElFormInstance>();
const dialog = reactive({ visible: false, title: '', isEdit: false });

const initFormData: CategoryForm = {
  tenantId: undefined,
  categoryCode: undefined,
  categoryName: undefined,
  parentId: 0,
  status: 10,
  sortOrder: 0,
  remark: undefined,
  defaultItemClass: 0,
  defaultProductType: 0,
  defaultExpiryFlag: 0,
  defaultBarcodePolicy: 0,
  defaultShelfLifeValue: 0,
  defaultShelfLifeUnit: 0,
  defaultNearExpiryValue: 0,
  defaultNearExpiryUnit: 0
};

const data = reactive<PageData<CategoryForm, CategoryQuery>>({
  form: { ...initFormData },
  queryParams: { pageNum: 1, pageSize: 10, tenantId: undefined, categoryCode: undefined, categoryName: undefined, status: undefined },
  rules: {}
});

const { queryParams, form } = toRefs(data);

const rules = computed(() => ({
  tenantId: isSuperAdmin.value ? [{ required: true, message: '请选择租户', trigger: 'change' }] : [],
  categoryCode: [{ required: true, message: '类目编码不能为空', trigger: 'blur' }],
  categoryName: [{ required: true, message: '类目名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}));

interface TreeNode { id: number; label: string; parentId?: number; children?: TreeNode[] }

const parentCategoryOptions = computed(() => {
  const tree = buildCategoryTree(categoryTree.value, dialog.isEdit ? editId.value : undefined);
  return [{ id: 0, label: '顶级（无上级）', children: tree }];
});

const buildCategoryTree = (list: CategoryVO[], excludeId?: number): TreeNode[] => {
  const nodes = list
    .filter((c) => c.id !== excludeId)
    .map((c) => ({ id: c.id, label: `${c.categoryName} (${c.categoryCode})`, parentId: c.parentId, children: [] as TreeNode[] }));
  const map = new Map<number, TreeNode & { parentId?: number }>();
  nodes.forEach((n) => map.set(n.id, n));
  const roots: TreeNode[] = [];
  map.forEach((node) => {
    if (node.parentId && node.parentId !== 0 && map.has(node.parentId)) {
      map.get(node.parentId)!.children!.push(node);
    } else {
      roots.push(node);
    }
  });
  return roots;
};

const activeTenantId = () => resolveTenantId(queryParams.value.tenantId || currentTenantId.value);

const loadCategoryTree = async () => {
  const res = await listCategoryTree(isSuperAdmin.value ? activeTenantId() : undefined);
  categoryTree.value = res.data || [];
};

const getList = async () => {
  loading.value = true;
  try {
    const res = await pageCategory({ ...queryParams.value, tenantId: isSuperAdmin.value ? activeTenantId() : undefined });
    categoryList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const reset = () => {
  form.value = { ...initFormData };
  categoryFormRef.value?.resetFields();
};

const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.value.pageNum = 1;
  if (!isSuperAdmin.value) queryParams.value.tenantId = currentTenantId.value;
  handleQuery();
};

const handleAdd = () => {
  reset();
  dialog.isEdit = false;
  editId.value = 0;
  form.value.tenantId = isSuperAdmin.value ? undefined : currentTenantId.value;
  form.value.parentId = 0;
  form.value.status = 10;
  dialog.title = '新增类目';
  dialog.visible = true;
};

const handleUpdate = async (row: CategoryVO) => {
  reset();
  dialog.isEdit = true;
  editId.value = row.id;
  const res = await getCategory(row.id, isSuperAdmin.value ? activeTenantId() : undefined);
  Object.assign(form.value, res.data);
  if (isSuperAdmin.value) form.value.tenantId = activeTenantId();
  dialog.title = '修改类目';
  dialog.visible = true;
};

const handleDelete = async (row: CategoryVO) => {
  await proxy?.$modal.confirm(`确认删除类目「${row.categoryCode}」吗？`);
  await delCategory(row.id, isSuperAdmin.value ? activeTenantId() : undefined);
  proxy?.$modal.msgSuccess('删除成功');
  await loadCategoryTree();
  await getList();
};

const submitForm = () => {
  categoryFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    buttonLoading.value = true;
    try {
      const payload: CategoryForm = { ...form.value, tenantId: isSuperAdmin.value ? form.value.tenantId : undefined, parentId: form.value.parentId ?? 0 };
      if (dialog.isEdit) {
        await updateCategory(editId.value, payload);
        proxy?.$modal.msgSuccess('修改成功');
      } else {
        await addCategory(payload);
        proxy?.$modal.msgSuccess('新增成功');
      }
      dialog.visible = false;
      await loadCategoryTree();
      await getList();
    } finally {
      buttonLoading.value = false;
    }
  });
};

onMounted(async () => {
  await loadTenantOptions();
  if (!isSuperAdmin.value) queryParams.value.tenantId = currentTenantId.value;
  await loadCategoryTree();
  await getList();
});
</script>
