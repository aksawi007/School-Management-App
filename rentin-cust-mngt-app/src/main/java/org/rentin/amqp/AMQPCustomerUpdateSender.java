package org.rentin.amqp;

import org.rentin.jpa.model.customer.CustomerModel;
import org.rentin.platform.app.amqp.sender.RabbitMqMessageSender;
import org.rentin.platform.common.datamodel.app.RentInMessage;
import org.rentin.platform.core.annotation.AMQPSender;
import org.rentin.platform.core.service.ServiceRequestContext;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AMQPSender
@ConfigurationProperties(prefix = "rentin.app.amqp.customer.update")
public class AMQPCustomerUpdateSender extends RabbitMqMessageSender<CustomerModel> {


    @Override
    public void sendRentInMessage(ServiceRequestContext context, RentInMessage message) {
        sendMessage(context, message);
    }

    @Override
    protected String getInterfaceName() {
        return "Customer_Update_Sender";
    }
}
