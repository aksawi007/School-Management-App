package org.sma.platform.app.amqp.receiver;

import com.rabbitmq.client.Channel;
import org.sma.platform.core.service.ServiceRequestContext;
import org.sma.platform.app.amqp.AbstractMessageService;
import org.sma.platform.common.datamodel.app.ResultStatus;
import org.sma.platform.common.datamodel.app.Status;
import org.sma.platform.common.datamodel.amqp.AMQPDestinationConfigProperties;
import org.sma.platform.common.datamodel.amqp.SmaMessage;
import org.sma.platform.common.datamodel.logevent.CommonLogEvent;
import org.sma.platform.common.datamodel.logevent.CommonLogMessage;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.util.List;
import java.util.UUID;


public abstract class RabbitMqMessageReceiver<T> extends AbstractMessageService<T> implements RabbitListenerConfigurer, ChannelAwareMessageListener {

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        AMQPDestinationConfigProperties destinationConfig = getConfig().getDest();


        // Create Default JMSListenerContainer Factory


        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(getConnectionFactory());
        factory.setMessageConverter(new SimpleMessageConverter());

        // Set Session AcknowledgeMode
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        // Set this for listening over topic
//        factory.set(destination.isPubSubDomain());

        // Set Subscription Durable for the topic

        // TODO - check where to set pubsub for Amqp
//        if (destination.isPubSubDomain())
//            factory.setSubscriptionDurable(destination.isSubscriptionDurable());

        // Create and Configure JmsListener Endpoint
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        // SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(this);

        endpoint.setId(destinationConfig.getReceiver().getQueueName());

        // TODO - need to check topic/exchange consumer
        Queue queues = new Queue(destinationConfig.getReceiver().getQueueName());
        endpoint.setQueues(queues);
        endpoint.setConcurrency(destinationConfig.getReceiver().getConcurrency());

        // Set Properties
//        processPropertiesOnly = destination.isProcessPropertiesOnly();

        // Register Endpoint
        rabbitListenerEndpointRegistrar.registerEndpoint(endpoint, factory);

//        LOGGER.info(">>> Register endpoint =  {} >>>", endpoint.getId());
//        LOGGER.info("JMS Destination Queue Name={}, AckMode={},  with Concurrency = {} ",
//                endpoint.getQueues().toString(), destination.getAckMode(), endpoint.getConcurrency());


    }

    private ConnectionFactory getConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        connectionFactory.setAddresses(getConfig().getAddress());
        connectionFactory.setAddressShuffleMode(AbstractConnectionFactory.AddressShuffleMode.RANDOM);
        connectionFactory.setUsername(getConfig().getUserName());
        connectionFactory.setPassword(getConfig().getPassword());
//        connectionFactory.setVirtualHost(getConfig().getVirtualHost());
//            connectionFactory.setHost();
        connectionFactory.setPort(getConfig().getPort());

        return connectionFactory;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
//        if (messageProperties.getType() == getMessageType().getCanonicalName()) {

        ResultStatus resultStatus = new ResultStatus();
        // TODO - Need to handle properties
        byte[] messageBody_byte = message.getBody();
        SmaMessage<T> smaMessage = new SmaMessage<>(messageBody_byte, message.getMessageProperties().getCorrelationId());
        T payload = smaMessage.getPayload();
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(getInterfaceName(), payload);
        if (messageProperties.getCorrelationId() != null)
            serviceRequestContext.setTransactionId(messageProperties.getCorrelationId());
        else serviceRequestContext.setTransactionId(UUID.randomUUID().toString());

        try {
            processMessage(serviceRequestContext, smaMessage);
            channel.basicAck(messageProperties.getDeliveryTag(), true);
            if (serviceRequestContext != null && getInterfaceName() != null)
                sendLogMessage(resultStatus, payload, serviceRequestContext);
        } catch (Exception e) {
            resultStatus.setStatus(Status.FAILED);
            resultStatus.setErrorMessage(e.getMessage());
            if(e.getCause()!=null)
            resultStatus.setErrorCode(e.getCause().toString());
            sendLogMessage(resultStatus, payload, serviceRequestContext);
            throw new RuntimeException(e);
        }

//        }
    }

    private void sendLogMessage(ResultStatus resultStatus, T payload, ServiceRequestContext serviceRequestContext) {
        CommonLogEvent commonLogEvent = createCommonLogEvent(serviceRequestContext, payload, null, getInterfaceName(), resultStatus);
        CommonLogMessage logMessage = new CommonLogMessage(commonLogEvent);
        publishLogMessage(logMessage);
    }

    @Override
    public void onMessage(Message message) {
        try {
            this.onMessage(message, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageBatch(List<Message> messages, Channel channel) {
        // ChannelAwareMessageListener.super.onMessageBatch(messages, channel);
        messages.forEach(message -> {
            try {
                onMessage(message, channel);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void containerAckMode(AcknowledgeMode mode) {

        ChannelAwareMessageListener.super.containerAckMode(mode);
    }

    @Override
    public void onMessageBatch(List<Message> messages) {

        onMessageBatch(messages, null);
    }

    protected abstract void processMessage(ServiceRequestContext context, SmaMessage message);
}
