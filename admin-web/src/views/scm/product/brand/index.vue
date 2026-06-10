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
            <el-form-item label="品牌编码" prop="brandCode">
              <el-input v-model="queryParams.brandCode" placeholder="请输入品牌编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="品牌名称" prop="brandName">
              <el-input v-model="queryParams.brandName" placeholder="请输入品牌名称" clearable @keyup.enter="handleQuery" />
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
            <el-button v-hasPermi="['scm:product:brand:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList" />
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="brandList">
        <el-table-column label="品牌编码" align="center" prop="brandCode" min-width="120" />
        <el-table-column label="品牌名称" align="center" prop="brandName" min-width="140" />
        <el-table-column label="状态" align="center" prop="status" width="90">
          <template #default="scope">
            <dict-tag :options="wms_enable_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
        <el-table-column label="更新时间" align="center" prop="updateTime" width="170">
          <template #default="scope">{{ parseTime(scope.row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="140" fixed="right">
          <template #default="scope">
            <el-button v-hasPermi="['scm:product:brand:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" />
            <el-button v-hasPermi="['scm:product:brand:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)" />
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="520px" append-to-body>
      <el-form ref="brandFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item v-if="isSuperAdmin" label="租户编号" prop="tenantId">
          <el-select v-model="form.tenantId" class="w-full" placeholder="请选择租户">
            <el-option v-for="item in tenantOptions" :key="item.tenantId" :label="`${item.companyName} (${item.tenantId})`" :value="String(item.tenantId)" />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌编码" prop="brandCode">
          <el-input v-model="form.brandCode" :disabled="dialog.isEdit" placeholder="租户内唯一" />
        </el-form-item>
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="form.brandName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in wms_enable_status" :key="dict.value" :value="parseInt(dict.value)">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" class="w-full" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="buttonLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ScmProductBrand" lang="ts">
import { addBrand, delBrand, getBrand, pageBrand, updateBrand } from '@/api/scm/product';
import { BrandForm, BrandQuery, BrandVO } from '@/api/scm/types';
import { useScmTenant } from '@/composables/useScmTenant';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { wms_enable_status } = toRefs<any>(proxy?.useDict('wms_enable_status'));
const { isSuperAdmin, currentTenantId, tenantOptions, loadTenantOptions, resolveTenantId } = useScmTenant();

const brandList = ref<BrandVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const editId = ref(0);

const queryFormRef = ref<ElFormInstance>();
const brandFormRef = ref<ElFormInstance>();

const dialog = reactive({ visible: false, title: '', isEdit: false });

const initFormData: BrandForm = {
  tenantId: undefined,
  brandCode: undefined,
  brandName: undefined,
  status: 10,
  sortOrder: 0,
  remark: undefined
};

const data = reactive<PageData<BrandForm, BrandQuery>>({
  form: { ...initFormData },
  queryParams: { pageNum: 1, pageSize: 10, tenantId: undefined, brandCode: undefined, brandName: undefined, status: undefined },
  rules: {}
});

const { queryParams, form } = toRefs(data);

const rules = computed(() => ({
  tenantId: isSuperAdmin.value ? [{ required: true, message: '请选择租户', trigger: 'change' }] : [],
  brandCode: [{ required: true, message: '品牌编码不能为空', trigger: 'blur' }],
  brandName: [{ required: true, message: '品牌名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}));

const activeTenantId = () => resolveTenantId(queryParams.value.tenantId || currentTenantId.value);

const getList = async () => {
  loading.value = true;
  try {
    const res = await pageBrand({
      ...queryParams.value,
      tenantId: isSuperAdmin.value ? activeTenantId() : undefined
    });
    brandList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const reset = () => {
  form.value = { ...initFormData };
  brandFormRef.value?.resetFields();
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
  form.value.status = 10;
  form.value.sortOrder = 0;
  dialog.title = '新增品牌';
  dialog.visible = true;
};

const handleUpdate = async (row: BrandVO) => {
  reset();
  dialog.isEdit = true;
  editId.value = row.id;
  const res = await getBrand(row.id, isSuperAdmin.value ? activeTenantId() : undefined);
  Object.assign(form.value, res.data);
  if (isSuperAdmin.value) form.value.tenantId = activeTenantId();
  dialog.title = '修改品牌';
  dialog.visible = true;
};

const handleDelete = async (row: BrandVO) => {
  await proxy?.$modal.confirm(`确认删除品牌「${row.brandCode}」吗？`);
  await delBrand(row.id, isSuperAdmin.value ? activeTenantId() : undefined);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

const submitForm = () => {
  brandFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    buttonLoading.value = true;
    try {
      const payload: BrandForm = { ...form.value, tenantId: isSuperAdmin.value ? form.value.tenantId : undefined };
      if (dialog.isEdit) {
        await updateBrand(editId.value, payload);
        proxy?.$modal.msgSuccess('修改成功');
      } else {
        await addBrand(payload);
        proxy?.$modal.msgSuccess('新增成功');
      }
      dialog.visible = false;
      await getList();
    } finally {
      buttonLoading.value = false;
    }
  });
};

onMounted(async () => {
  await loadTenantOptions();
  if (!isSuperAdmin.value) queryParams.value.tenantId = currentTenantId.value;
  await getList();
});
</script>
