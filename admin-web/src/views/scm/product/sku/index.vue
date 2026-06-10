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
            <el-form-item label="货品编码" prop="skuCode">
              <el-input v-model="queryParams.skuCode" placeholder="请输入货品编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="货品名称" prop="skuName">
              <el-input v-model="queryParams.skuName" placeholder="请输入货品名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="类目" prop="categoryId">
              <el-tree-select
                v-model="queryParams.categoryId"
                :data="categoryTreeOptions"
                :props="{ value: 'id', label: 'label', children: 'children' } as any"
                value-key="id"
                placeholder="请选择类目"
                check-strictly
                clearable
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择" clearable>
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
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
            <el-button v-hasPermi="['scm:product:sku:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList" />
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="skuList">
        <el-table-column label="货品编码" align="center" prop="skuCode" min-width="120" />
        <el-table-column label="货品名称" align="center" prop="skuName" min-width="160" />
        <el-table-column label="类目" align="center" prop="categoryName" min-width="120" />
        <el-table-column label="履约" align="center" prop="productType" width="80">
          <template #default="scope">{{ scope.row.productType === 20 ? '虚拟' : '实物' }}</template>
        </el-table-column>
        <el-table-column label="效期" align="center" prop="expiryFlag" width="70">
          <template #default="scope">{{ scope.row.expiryFlag === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="80">
          <template #default="scope">{{ scope.row.status === 1 ? '启用' : '停用' }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="140" fixed="right">
          <template #default="scope">
            <el-button v-hasPermi="['scm:product:sku:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" />
            <el-button v-hasPermi="['scm:product:sku:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)" />
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="880px" append-to-body destroy-on-close>
      <el-form ref="skuFormRef" :model="form" :rules="rules" label-width="110px">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基础" name="basic">
            <el-row :gutter="20">
              <el-col v-if="isSuperAdmin" :span="12">
                <el-form-item label="租户编号" prop="tenantId">
                  <el-select v-model="form.tenantId" class="w-full" placeholder="请选择租户">
                    <el-option v-for="item in tenantOptions" :key="item.tenantId" :label="`${item.companyName} (${item.tenantId})`" :value="String(item.tenantId)" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="货品编码" prop="skuCode">
                  <el-input v-model="form.skuCode" :disabled="dialog.isEdit" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="货品名称" prop="skuName">
                  <el-input v-model="form.skuName" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="类目" prop="categoryId">
                  <el-tree-select
                    v-model="form.categoryId"
                    :data="categoryTreeOptions"
                    :props="{ value: 'id', label: 'label', children: 'children' } as any"
                    value-key="id"
                    check-strictly
                    class="w-full"
                    @change="onCategoryChange"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="品牌">
                  <el-select v-model="form.brandId" class="w-full" clearable placeholder="可选">
                    <el-option v-for="b in brandOptions" :key="b.id" :label="b.brandName" :value="b.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="计量单位" prop="unitCode">
                  <el-select v-model="form.unitCode" class="w-full" placeholder="请选择">
                    <el-option v-for="dict in prd_unit_code" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="货品类型" prop="itemClass">
                  <el-select v-model="form.itemClass" class="w-full">
                    <el-option v-for="item in ITEM_CLASS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="form.status">
                    <el-radio :value="1">启用</el-radio>
                    <el-radio :value="0">停用</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="可采购"><el-switch v-model="form.purchaseAllowed" :active-value="1" :inactive-value="0" /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="可销售"><el-switch v-model="form.saleAllowed" :active-value="1" :inactive-value="0" /></el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="履约" name="fulfill">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="履约类型" prop="productType">
                  <el-radio-group v-model="form.productType">
                    <el-radio v-for="item in PRODUCT_TYPE_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col v-if="form.productType === 10" :span="12">
                <el-form-item label="是否效期品">
                  <el-switch v-model="form.expiryFlag" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col v-if="form.productType === 20" :span="12">
                <el-form-item label="是否配额">
                  <el-switch v-model="form.quotaFlag" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <template v-if="form.productType === 10 && form.expiryFlag === 1">
                <el-col :span="12">
                  <el-form-item label="保质期">
                    <el-input-number v-model="form.shelfLifeValue" :min="0" class="w-full" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="保质期单位">
                    <el-select v-model="form.shelfLifeUnit" class="w-full">
                      <el-option label="小时" :value="10" />
                      <el-option label="天" :value="20" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </template>
              <el-col :span="12">
                <el-form-item label="条码策略">
                  <el-select v-model="form.barcodePolicy" class="w-full">
                    <el-option label="10 必须有码" :value="10" />
                    <el-option label="20 可无" :value="20" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="条码" name="barcode">
            <el-button type="primary" plain icon="Plus" class="mb-2" @click="addBarcodeRow">添加条码</el-button>
            <el-table border :data="form.barcodes">
              <el-table-column label="条码" min-width="160">
                <template #default="scope">
                  <el-input v-model="scope.row.barcode" placeholder="保留前导0" />
                </template>
              </el-table-column>
              <el-table-column label="默认" width="80" align="center">
                <template #default="scope">
                  <el-checkbox :model-value="scope.row.defaultFlag === 1" @change="(v: boolean) => setDefaultBarcode(scope.$index, v)" />
                </template>
              </el-table-column>
              <el-table-column label="启用" width="80" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="scope">
                  <el-button link type="danger" icon="Delete" @click="removeBarcodeRow(scope.$index)" />
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="buttonLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ScmProductSku" lang="ts">
import { addSku, delSku, getCategoryDefaults, getSku, listBrandOptions, listCategoryTree, pageSku, updateSku } from '@/api/scm/product';
import { BrandVO, CategoryVO, SkuBarcodeVO, SkuForm, SkuQuery, SkuVO } from '@/api/scm/types';
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
const { prd_unit_code } = toRefs<any>(proxy?.useDict('prd_unit_code'));
const { isSuperAdmin, currentTenantId, tenantOptions, loadTenantOptions, resolveTenantId } = useScmTenant();

const skuList = ref<SkuVO[]>([]);
const categoryTree = ref<CategoryVO[]>([]);
const brandOptions = ref<BrandVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const editSkuId = ref(0);
const activeTab = ref('basic');

const queryFormRef = ref<ElFormInstance>();
const skuFormRef = ref<ElFormInstance>();
const dialog = reactive({ visible: false, title: '', isEdit: false });

const initFormData: SkuForm = {
  tenantId: undefined,
  skuCode: undefined,
  skuName: undefined,
  status: 1,
  sortOrder: 0,
  categoryId: undefined,
  brandId: 0,
  unitCode: undefined,
  itemClass: 10,
  purchaseAllowed: 1,
  saleAllowed: 1,
  issueAllowed: 0,
  productType: 10,
  expiryFlag: 0,
  quotaFlag: 0,
  shelfLifeValue: 0,
  shelfLifeUnit: 0,
  nearExpiryValue: 0,
  nearExpiryUnit: 0,
  barcodePolicy: 20,
  barcodes: []
};

const data = reactive<PageData<SkuForm, SkuQuery>>({
  form: { ...initFormData, barcodes: [] },
  queryParams: { pageNum: 1, pageSize: 10, tenantId: undefined, skuCode: undefined, skuName: undefined, categoryId: undefined, status: undefined },
  rules: {}
});

const { queryParams, form } = toRefs(data);

const rules = computed(() => ({
  tenantId: isSuperAdmin.value ? [{ required: true, message: '请选择租户', trigger: 'change' }] : [],
  skuCode: [{ required: true, message: '货品编码不能为空', trigger: 'blur' }],
  skuName: [{ required: true, message: '货品名称不能为空', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择类目', trigger: 'change' }],
  unitCode: [{ required: true, message: '请选择计量单位', trigger: 'change' }],
  itemClass: [{ required: true, message: '请选择货品类型', trigger: 'change' }],
  productType: [{ required: true, message: '请选择履约类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}));

interface TreeNode { id: number; label: string; parentId?: number; children?: TreeNode[] }

const buildCategoryTree = (list: CategoryVO[]): TreeNode[] => {
  const nodes = list.map((c) => ({ id: c.id, label: `${c.categoryName} (${c.categoryCode})`, parentId: c.parentId, children: [] as TreeNode[] }));
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

const categoryTreeOptions = computed(() => buildCategoryTree(categoryTree.value));

const activeTenantId = () => resolveTenantId(queryParams.value.tenantId || currentTenantId.value);

const loadRefs = async () => {
  const tid = isSuperAdmin.value ? activeTenantId() : undefined;
  const [catRes, brandRes] = await Promise.all([listCategoryTree(tid, 10), listBrandOptions(tid, 10)]);
  categoryTree.value = catRes.data || [];
  brandOptions.value = brandRes.data || [];
};

const getList = async () => {
  loading.value = true;
  try {
    const res = await pageSku({ ...queryParams.value, tenantId: isSuperAdmin.value ? activeTenantId() : undefined });
    skuList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const reset = () => {
  form.value = { ...initFormData, barcodes: [] };
  skuFormRef.value?.resetFields();
  activeTab.value = 'basic';
};

const onCategoryChange = async (categoryId: number) => {
  if (!categoryId || dialog.isEdit) return;
  try {
    const res = await getCategoryDefaults(categoryId, isSuperAdmin.value ? activeTenantId() : undefined);
    const d = res.data;
    if (!d) return;
    if (d.defaultItemClass) form.value.itemClass = d.defaultItemClass;
    if (d.defaultProductType) form.value.productType = d.defaultProductType;
    if (d.defaultExpiryFlag != null) form.value.expiryFlag = d.defaultExpiryFlag;
    if (d.defaultBarcodePolicy) form.value.barcodePolicy = d.defaultBarcodePolicy;
    if (d.defaultShelfLifeValue) form.value.shelfLifeValue = d.defaultShelfLifeValue;
    if (d.defaultShelfLifeUnit) form.value.shelfLifeUnit = d.defaultShelfLifeUnit;
  } catch {
    /* 忽略预填失败 */
  }
};

const addBarcodeRow = () => {
  if (!form.value.barcodes) form.value.barcodes = [];
  form.value.barcodes.push({ barcode: '', defaultFlag: 0, status: 1, remark: '' });
};

const removeBarcodeRow = (index: number) => {
  form.value.barcodes?.splice(index, 1);
};

const setDefaultBarcode = (index: number, checked: boolean) => {
  form.value.barcodes?.forEach((row, i) => {
    row.defaultFlag = checked && i === index ? 1 : 0;
  });
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
  editSkuId.value = 0;
  form.value.tenantId = isSuperAdmin.value ? undefined : currentTenantId.value;
  dialog.title = '新增货品';
  dialog.visible = true;
};

const handleUpdate = async (row: SkuVO) => {
  reset();
  dialog.isEdit = true;
  editSkuId.value = row.skuId;
  const res = await getSku(row.skuId, isSuperAdmin.value ? activeTenantId() : undefined);
  Object.assign(form.value, res.data);
  form.value.barcodes = res.data.barcodes?.length ? [...res.data.barcodes] : [];
  if (isSuperAdmin.value) form.value.tenantId = activeTenantId();
  dialog.title = '修改货品';
  dialog.visible = true;
};

const handleDelete = async (row: SkuVO) => {
  await proxy?.$modal.confirm(`确认删除货品「${row.skuCode}」吗？`);
  await delSku(row.skuId, isSuperAdmin.value ? activeTenantId() : undefined);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

const submitForm = () => {
  skuFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    buttonLoading.value = true;
    try {
      const payload: SkuForm = {
        ...form.value,
        tenantId: isSuperAdmin.value ? form.value.tenantId : undefined,
        brandId: form.value.brandId || 0,
        barcodes: form.value.barcodes || []
      };
      if (dialog.isEdit) {
        await updateSku(editSkuId.value, payload);
        proxy?.$modal.msgSuccess('修改成功');
      } else {
        await addSku(payload);
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
  await loadRefs();
  await getList();
});
</script>
