package org.sma.platform.app.amqp;

import org.sma.platform.common.datamodel.amqp.AMQPConfigProperties;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class AMQPTemplate extends RabbitTemplate {
    private CachingConnectionFactory connectionFactory;

    public AMQPTemplate(AMQPConfigProperties config) {
        super();
        connectionFactory = new CachingConnectionFactory();
        if (config != null) {
            connectionFactory.setAddresses(config.getAddress());
            connectionFactory.setAddressShuffleMode(AbstractConnectionFactory.AddressShuffleMode.RANDOM);
            connectionFactory.setUsername(config.getUserName());
            connectionFactory.setPassword(config.getPassword());
//            connectionFactory.setVirtualHost(config.getVirtualHost());
//            connectionFactory.setHost();
            connectionFactory.setPort(config.getPort());
        }
        setConnectionFactory(connectionFactory);
        setChannelTransacted(config.isTransactional());
    }

    @Override
    public CachingConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
