package org.sma.platform.app.amqp;

import org.sma.platform.core.service.ApiServiceBinding;
import org.sma.platform.core.service.ServiceBindingType;
import org.sma.platform.common.datamodel.amqp.AMQPConfigProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sma.api.amqp-binding")
public abstract class AbstractMessageService<T> extends ApiServiceBinding {

    private Class<T> messageType;

    private AMQPConfigProperties config;

    @Override
    protected void onServiceInit() {
        AMQPConfigProperties config = getConfig();
    }

    @Override
    protected ServiceBindingType getServiceBindingType() {
        return ServiceBindingType.AMQP;
    }

    public AMQPConfigProperties getConfig() {
        return config;
    }

    public void setConfig(AMQPConfigProperties config) {
        this.config = config;
    }


    public Class<T> getMessageType() {
        return messageType;
    }

    public void setMessageType(Class<T> messageType) {
        this.messageType = messageType;
    }
}
