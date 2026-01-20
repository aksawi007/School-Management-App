package org.sma.platform.app.amqp.sender;

import org.sma.platform.core.service.ServiceRequestContext;
import org.sma.platform.app.amqp.AMQPTemplate;
import org.sma.platform.app.amqp.AbstractMessageService;
import org.sma.platform.common.datamodel.app.ResultStatus;
import org.sma.platform.common.datamodel.app.Status;
import org.sma.platform.common.datamodel.amqp.AMQPDestinationConfigProperties;
import org.sma.platform.common.datamodel.amqp.SmaMessage;
import org.sma.platform.common.datamodel.logevent.CommonLogEvent;
import org.sma.platform.common.datamodel.logevent.CommonLogMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

public abstract class RabbitMqMessageSender<T> extends AbstractMessageService<T> {

    private AMQPTemplate amqpTemplate;


    @Override
    protected void onServiceInit() {
        super.onServiceInit();

        amqpTemplate = new AMQPTemplate(getConfig());

    }

    protected void sendMessage(ServiceRequestContext context, SmaMessage message) {
        ResultStatus resultStatus = new ResultStatus();
        try {
            AMQPDestinationConfigProperties destinationConfig = getConfig().getDest();
            if (destinationConfig != null) {
                String destRoutingKey = destinationConfig.getSender().getRoutingKey();
                String destExchange = destinationConfig.getSender().getExchangeName();

                MessageProperties messageProperties = new MessageProperties();
                if (context != null)
                    messageProperties.setCorrelationId(context.getTransactionId());


//                messageProperties.setType(getMessageType().getCanonicalName());

                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                // TODO implement reply to
//            if(destinationConfig.getSender())
//            messageProperties.setReplyTo();

                Message amqpMessage = amqpTemplate.getMessageConverter().toMessage(message.getByteMessage(), messageProperties);
                amqpMessage.getMessageProperties().setCorrelationId(message.getCorrelationId());
                if (getConfig().getDest().getSender().getExchangeName() != null)
                    amqpTemplate.send(destExchange, destRoutingKey, amqpMessage);
                else amqpTemplate.send(destRoutingKey, amqpMessage);
            }
            if (getInterfaceName() != null && context != null)
                sendLogMessage(context, message, resultStatus);
        } catch (Exception e) {
            resultStatus.setStatus(Status.FAILED);
            resultStatus.setErrorMessage(e.getMessage());
            if (e.getCause() != null)
                resultStatus.setErrorCode(e.getCause().toString());
            if(context!=null)
            sendLogMessage(context, message, resultStatus);
            throw e;
        }
    }

    private void sendLogMessage(ServiceRequestContext context, SmaMessage message, ResultStatus resultStatus) {
        CommonLogEvent commonLogEvent = createCommonLogEvent(context, null, message.getPayload(), getInterfaceName(), resultStatus);
        CommonLogMessage logMessage = new CommonLogMessage(commonLogEvent);
        publishLogMessage(logMessage);
    }

    protected abstract void sendSmaMessage(ServiceRequestContext context, SmaMessage message);
}
