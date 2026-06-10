package com.whl.scm.product.biz;

/**
 * 货品主数据常量。
 *
 * @author whl
 */
public final class ProductConstants {

    public static final int STATUS_CATEGORY_ENABLED = 10;
    public static final int STATUS_CATEGORY_DISABLED = 20;
    public static final int STATUS_BRAND_ENABLED = 10;
    public static final int STATUS_BRAND_DISABLED = 20;
    public static final int STATUS_SKU_ENABLED = 1;
    public static final int STATUS_SKU_DISABLED = 0;
    public static final long TOP_PARENT_ID = 0L;

    public static final int PRODUCT_TYPE_PHYSICAL = 10;
    public static final int PRODUCT_TYPE_VIRTUAL = 20;

    public static final int BARCODE_POLICY_REQUIRED = 10;
    public static final int BARCODE_POLICY_OPTIONAL = 20;

    public static final int ITEM_CLASS_TRADE = 10;
    public static final int ITEM_CLASS_MATERIAL = 20;
    public static final int ITEM_CLASS_PACK = 30;
    public static final int ITEM_CLASS_DRUG = 40;
    public static final int ITEM_CLASS_VIRTUAL = 50;
    public static final int ITEM_CLASS_SERVICE = 60;
    public static final int ITEM_CLASS_SEMI = 70;

    public static final int SHELF_LIFE_UNIT_HOUR = 10;
    public static final int SHELF_LIFE_UNIT_DAY = 20;

    public static final int BARCODE_ENABLED = 1;
    public static final int BARCODE_DISABLED = 0;
    public static final int BARCODE_DEFAULT = 1;

    public static final String DICT_UNIT_CODE = "prd_unit_code";
    public static final String DICT_TAX_RATE = "prd_tax_rate";

    public static final String CAPABILITY_PURCHASE = "PURCHASE";
    public static final String CAPABILITY_SALE = "SALE";
    public static final String CAPABILITY_ISSUE = "ISSUE";

    private ProductConstants() {
    }

}
