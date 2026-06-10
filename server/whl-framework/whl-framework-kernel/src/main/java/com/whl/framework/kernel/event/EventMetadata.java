package com.whl.framework.kernel.event;

import java.util.UUID;

/**
 * 领域事件元数据，跨域异步协作的统一信封字段。
 */
public record EventMetadata(
    String eventId,
    String eventType,
    String version,
    String tenantId,
    long occurredAtEpochMilli
) {

    public static EventMetadata of(String eventType, String version, String tenantId) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            eventType,
            version,
            tenantId,
            System.currentTimeMillis()
        );
    }

}
