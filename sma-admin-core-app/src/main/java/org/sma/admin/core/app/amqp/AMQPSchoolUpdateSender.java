package org.sma.admin.core.app.amqp;

import org.sma.platform.common.datamodel.amqp.SmaMessage;
import org.sma.platform.core.annotation.AMQPSender;
import org.sma.platform.app.amqp.sender.RabbitMqMessageSender;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AMQP Sender for School Update Events
 * Publishes school update messages to message queue
 */
@AMQPSender
@ConfigurationProperties(prefix = "sma.app.amqp.school.update")
public class AMQPSchoolUpdateSender extends RabbitMqMessageSender<Object> {

    @Override
    public void sendSmaMessage(ServiceRequestContext context, SmaMessage message) {
        sendMessage(context, message);
    }

    @Override
    protected String getInterfaceName() {
        return "School_Update_Sender";
    }
}
