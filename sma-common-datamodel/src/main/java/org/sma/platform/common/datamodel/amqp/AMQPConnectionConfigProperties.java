package org.sma.platform.common.datamodel.amqp;

public class AMQPConnectionConfigProperties {

    private String userName;
    private String password;
    private String topicConnectionFactory;
    private String queueConnectionFactory;
    private boolean useJndi = false;
    private String clientId;
    private String clientIdPrefix;
    private String exchange;
    private String replyExchange;
    private String routingKey;
    private String replyRoutingKey;
    private String defaultExchange;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTopicConnectionFactory() {
        return topicConnectionFactory;
    }

    public void setTopicConnectionFactory(String topicConnectionFactory) {
        this.topicConnectionFactory = topicConnectionFactory;
    }

    public String getQueueConnectionFactory() {
        return queueConnectionFactory;
    }

    public void setQueueConnectionFactory(String queueConnectionFactory) {
        this.queueConnectionFactory = queueConnectionFactory;
    }

    public boolean isUseJndi() {
        return useJndi;
    }

    public void setUseJndi(boolean useJndi) {
        this.useJndi = useJndi;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientIdPrefix() {
        return clientIdPrefix;
    }

    public void setClientIdPrefix(String clientIdPrefix) {
        this.clientIdPrefix = clientIdPrefix;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getReplyExchange() {
        return replyExchange;
    }

    public void setReplyExchange(String replyExchange) {
        this.replyExchange = replyExchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getReplyRoutingKey() {
        return replyRoutingKey;
    }

    public void setReplyRoutingKey(String replyRoutingKey) {
        this.replyRoutingKey = replyRoutingKey;
    }

    public String getDefaultExchange() {
        return defaultExchange;
    }

    public void setDefaultExchange(String defaultExchange) {
        this.defaultExchange = defaultExchange;
    }
}
