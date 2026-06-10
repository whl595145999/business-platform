package com.whl.framework.kernel.event;

/**
 * 领域事件 payload 标记接口，定义在 *-api，元数据由 {@link EventMetadata} 承载。
 */
public interface DomainEventPayload {

    /**
     * 事件类型，格式：域.名词.动词，如 {@code wms.shipping.ShipmentCompleted}
     */
    String eventType();

    /**
     * 事件版本，不兼容变更时递增。
     */
    String version();

}
