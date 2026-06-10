package com.whl.framework.event;

import com.whl.framework.kernel.event.DomainEventPayload;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 基于 Spring {@link ApplicationEventPublisher} 的本地事件发布。
 */
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishPayload(DomainEventPayload payload) {
        applicationEventPublisher.publishEvent(payload);
    }

}
