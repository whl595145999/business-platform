package com.whl.scm.wms.api.error;

import com.whl.framework.kernel.error.ScmErrorCode;

/**
 * WMS 域错误码，对应 PRD §14.6。
 *
 * @author whl
 */
public enum WmsErrorCode implements ScmErrorCode {

    /** 出库任务不存在（兼容旧码 WMS001） */
    WMS001("WMS001", "出库任务不存在"),
    /** 出库任务状态不允许发货（兼容旧码 WMS002） */
    WMS002("WMS002", "出库任务状态不允许发货"),
    /** 仓库不存在 */
    WMS_WAREHOUSE_NOT_FOUND("WMS_WAREHOUSE_NOT_FOUND", "仓库不存在"),
    /** 仓库编码已存在 */
    WMS_WAREHOUSE_CODE_EXISTS("WMS_WAREHOUSE_CODE_EXISTS", "仓库编码已存在"),
    /** 仓型大类与细类不匹配 */
    WMS_WAREHOUSE_CATEGORY_MISMATCH("WMS_WAREHOUSE_CATEGORY_MISMATCH", "仓库大类与细类不匹配"),
    /** 仓库已停用 */
    WMS_WAREHOUSE_DISABLED("WMS_WAREHOUSE_DISABLED", "仓库已停用"),
    /** 入库单不存在 */
    WMS_INBOUND_NOT_FOUND("WMS_INBOUND_NOT_FOUND", "入库单不存在"),
    /** 入库单状态不允许收货 */
    WMS_INBOUND_STATUS_INVALID("WMS_INBOUND_STATUS_INVALID", "入库单状态不允许收货"),
    /** 出库任务不存在 */
    WMS_OUTBOUND_NOT_FOUND("WMS_OUTBOUND_NOT_FOUND", "出库任务不存在"),
    /** 出库任务状态不允许发货 */
    WMS_OUTBOUND_STATUS_INVALID("WMS_OUTBOUND_STATUS_INVALID", "出库任务状态不允许发货"),
    /** 幂等键重复提交 */
    WMS_DUPLICATE_IDEMPOTENCY("WMS_DUPLICATE_IDEMPOTENCY", "请勿重复提交"),
    /** 组织不存在 */
    WMS_ORG_NOT_FOUND("WMS_ORG_NOT_FOUND", "组织不存在"),
    /** 组织编码重复 */
    WMS_ORG_CODE_DUPLICATE("WMS_ORG_CODE_DUPLICATE", "组织编码已存在"),
    /** 上级组织成环 */
    WMS_ORG_PARENT_CYCLE("WMS_ORG_PARENT_CYCLE", "上级组织不能为自身或下级"),
    /** 组织已停用不可选 */
    WMS_ORG_DISABLED("WMS_ORG_DISABLED", "组织已停用，不可选择"),
    /** 组织存在下级，不可删除 */
    WMS_ORG_HAS_CHILDREN("WMS_ORG_HAS_CHILDREN", "存在下级组织，不可删除");

    private final String code;
    private final String message;

    WmsErrorCode(String code, String message) {
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
