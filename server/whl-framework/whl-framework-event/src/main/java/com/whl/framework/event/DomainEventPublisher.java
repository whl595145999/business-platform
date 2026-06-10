package com.whl.framework.event;

import com.whl.framework.kernel.event.DomainEventPayload;

/**
 * 领域事件发布器，单体默认 Spring Event 实现。
 */
public interface DomainEventPublisher {

    void publishPayload(DomainEventPayload payload);

}
