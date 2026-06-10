package com.whl.boot.monitor.notifier;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static de.codecentric.boot.admin.server.domain.values.StatusInfo.*;

/**
 * Spring Boot Admin 实例状态变更通知
 *
 * @author whl
 */
@Slf4j
@Component
public class CustomNotifier extends AbstractEventNotifier {

    protected CustomNotifier(InstanceRepository repository) {
        super(repository);
    }

    @Override
    @SuppressWarnings("all")
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if (event instanceof InstanceStatusChangedEvent) {
                String registName = instance.getRegistration().getName();
                String instanceId = event.getInstance().getValue();
                String status = ((InstanceStatusChangedEvent) event).getStatusInfo().getStatus();
                String serviceUrl = instance.getRegistration().getServiceUrl();
                String statusName = switch (status) {
                    case STATUS_UP -> "服务上线";
                    case STATUS_OFFLINE -> "服务离线";
                    case STATUS_RESTRICTED -> "服务受限";
                    case STATUS_OUT_OF_SERVICE -> "停止服务状态";
                    case STATUS_DOWN -> "服务下线";
                    case STATUS_UNKNOWN -> "服务未知异常";
                    default -> "未知状态";
                };
                log.info("Instance Status Change: 状态名称【{}】, 注册名称【{}】, 实例ID【{}】, 状态【{}】, 服务URL【{}】",
                    statusName, registName, instanceId, status, serviceUrl);
            }
        });
    }
}
