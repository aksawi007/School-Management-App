package org.sma.platform.app.amqp.sender;

import org.sma.platform.common.datamodel.amqp.SmaMessage;
import org.sma.platform.common.datamodel.logevent.CommonLogEvent;
import org.sma.platform.common.datamodel.logevent.CommonLogMessage;
import org.sma.platform.core.contract.CommonLogMessagePublisher;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sma.app.amqp.commonlog.sender")
public class CommonLogMessagePublisherImpl extends RabbitMqMessageSender<CommonLogEvent> implements CommonLogMessagePublisher {

    @Override
    protected void sendSmaMessage(ServiceRequestContext context, SmaMessage message) {
        sendMessage(context, message);
    }

    @Override
    protected String getInterfaceName() {
        return null;
    }

    @Override
    public void publishCommonLogMessage(CommonLogMessage message) {
        sendSmaMessage(null, message);

    }
}
