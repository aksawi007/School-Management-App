package org.sma.clep.app.app.amqp;

import org.sma.platform.app.amqp.receiver.RabbitMqMessageReceiver;
import org.sma.platform.common.datamodel.amqp.SmaMessage;
import org.sma.platform.common.datamodel.logevent.CommonLogEvent;
import org.sma.platform.core.annotation.AMQPReceiver;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AMQPReceiver
@ConfigurationProperties(prefix = "sma.app.amqp.clep.receiver")
public class CLEPReceiver extends RabbitMqMessageReceiver<CommonLogEvent> {
    @Override
    protected void processMessage(ServiceRequestContext context, SmaMessage message) {
        CommonLogEvent payload = (CommonLogEvent) message.getPayload();
        System.out.println("*** payload received ***" + payload.toString());
    }

    @Override
    protected String getInterfaceName() {
        return null;
    }
}
