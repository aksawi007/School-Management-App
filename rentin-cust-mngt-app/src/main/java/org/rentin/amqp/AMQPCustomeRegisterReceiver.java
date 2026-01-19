package org.rentin.amqp;

import org.rentin.jpa.model.customer.CustomerModel;
import org.rentin.platform.common.datamodel.app.CustomerRegistrationRequest;
import org.rentin.platform.core.annotation.AMQPReceiver;
import org.rentin.platform.core.exception.RentInException;
import org.rentin.platform.core.service.ServiceRequestContext;
import org.rentin.platform.app.amqp.receiver.RabbitMqMessageReceiver;
import org.rentin.platform.common.datamodel.app.RentInMessage;
import org.rentin.service.CustomerBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AMQPReceiver
@ConfigurationProperties(prefix = "rentin.app.amqp.customer.register")
public class AMQPCustomeRegisterReceiver extends RabbitMqMessageReceiver<CustomerRegistrationRequest> {

    @Autowired
    CustomerBusinessService customerBusinessService;
    @Override
    protected void processMessage(ServiceRequestContext context, RentInMessage message) {
        CustomerRegistrationRequest request = (CustomerRegistrationRequest) message.getPayload();
        CustomerModel customerModelDBRes = null;
        try {
            customerModelDBRes = customerBusinessService.registerCustomer(context, request);
        } catch (RentInException e) {
           throw new RentInException("Unable to register customer.", e);
        }
    }

    @Override
    protected String getInterfaceName() {
        return "Customer_Register_Receiver";
    }
}
