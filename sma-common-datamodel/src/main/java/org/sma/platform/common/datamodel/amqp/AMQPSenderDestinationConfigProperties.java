package org.sma.platform.common.datamodel.amqp;

public class AMQPSenderDestinationConfigProperties {

    private String exchangeName;
    private String routingKey;
    private int deliveryMode = 2;
    private String replyToRoutingKey;
    private String replyToExchangeName;

    public String getReplyToRoutingKey() {
        return replyToRoutingKey;
    }

    public void setReplyToRoutingKey(String replyToRoutingKey) {
        this.replyToRoutingKey = replyToRoutingKey;
    }

    public String getReplyToExchangeName() {
        return replyToExchangeName;
    }

    public void setReplyToExchangeName(String replyToExchangeName) {
        this.replyToExchangeName = replyToExchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }


}
