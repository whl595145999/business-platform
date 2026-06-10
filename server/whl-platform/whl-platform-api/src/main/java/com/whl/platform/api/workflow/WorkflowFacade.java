package com.whl.platform.api.workflow;

/**
 * 工作流 Facade：OMS/WMS 发起审批时使用。
 * <p>实现位于 {@code whl-platform-workflow}，阶段 5 业务落地时补全。</p>
 */
public interface WorkflowFacade {

    /**
     * 按流程定义 key 发起实例。
     *
     * @param processKey  流程定义 key
     * @param businessKey 业务单号
     * @return 流程实例 ID
     */
    String startByKey(String processKey, String businessKey);
}
