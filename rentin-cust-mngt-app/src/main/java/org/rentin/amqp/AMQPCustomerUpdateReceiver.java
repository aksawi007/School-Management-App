package org.rentin.amqp;

import com.netflix.discovery.converters.Auto;
import org.rentin.jpa.model.customer.CustomerModel;
import org.rentin.platform.app.amqp.receiver.RabbitMqMessageReceiver;
import org.rentin.platform.common.datamodel.app.RentInMessage;
import org.rentin.platform.core.annotation.AMQPReceiver;
import org.rentin.platform.core.exception.RentInException;
import org.rentin.platform.core.service.ServiceRequestContext;
import org.rentin.service.CustomerBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AMQPReceiver
@ConfigurationProperties(prefix = "rentin.app.amqp.customer.update")
public class AMQPCustomerUpdateReceiver extends RabbitMqMessageReceiver<CustomerModel> {

    @Autowired
    CustomerBusinessService customerBusinessService;

    @Override
    protected void processMessage(ServiceRequestContext context, RentInMessage message) {

        CustomerModel customerModel = (CustomerModel) message.getPayload();
        int i = 0;
        try {
            i = customerBusinessService.updateCustomer(context, customerModel);
        } catch (RentInException e) {
            throw new RentInException("Unable to register customer.", e);
        }
    }

    @Override
    protected String getInterfaceName() {
        return "Customer_Update_Receiver";
    }
}
