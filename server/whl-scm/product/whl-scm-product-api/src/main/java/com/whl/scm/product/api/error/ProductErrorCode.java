package com.whl.scm.product.api.error;

import com.whl.framework.kernel.error.ScmErrorCode;

/**
 * Product 域错误码，对应 TECH §14.6。
 *
 * @author whl
 */
public enum ProductErrorCode implements ScmErrorCode {

    /** 货品编码重复 */
    PRD_SKU_CODE_DUPLICATE("PRD_SKU_CODE_DUPLICATE", "货品编码已存在"),
    /** 货品保存校验失败 */
    PRD_SKU_VALIDATION("PRD_SKU_VALIDATION", "货品校验失败"),
    /** 货品已停用或不可用于当前操作 */
    PRD_SKU_DISABLED("PRD_SKU_DISABLED", "货品已停用或不可用于当前操作"),
    /** 货品不存在 */
    PRD_SKU_NOT_FOUND("PRD_SKU_NOT_FOUND", "货品不存在"),
    /** 类目编码重复 */
    PRD_CATEGORY_CODE_DUPLICATE("PRD_CATEGORY_CODE_DUPLICATE", "类目编码已存在"),
    /** 上级类目成环 */
    PRD_CATEGORY_CYCLE("PRD_CATEGORY_CYCLE", "上级类目不能为自己或下级"),
    /** 类目存在子节点或被引用，不可删除 */
    PRD_CATEGORY_HAS_CHILDREN("PRD_CATEGORY_HAS_CHILDREN", "类目存在下级或被货品引用，不可删除"),
    /** 品牌编码重复 */
    PRD_BRAND_CODE_DUPLICATE("PRD_BRAND_CODE_DUPLICATE", "品牌编码已存在"),
    /** 条码租户内重复 */
    PRD_BARCODE_DUPLICATE("PRD_BARCODE_DUPLICATE", "条码已被其他货品占用"),
    /** 扫码无匹配 */
    PRD_BARCODE_NOT_FOUND("PRD_BARCODE_NOT_FOUND", "条码未找到对应货品");

    private final String code;
    private final String message;

    ProductErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

}
