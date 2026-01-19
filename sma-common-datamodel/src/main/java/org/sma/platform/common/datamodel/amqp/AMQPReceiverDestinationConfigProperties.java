package org.sma.platform.common.datamodel.amqp;

import org.springframework.amqp.core.AcknowledgeMode;

public class AMQPReceiverDestinationConfigProperties {

    private String queueName;
    private String concurrency = "1-5";
    private boolean acknowledgeOnFailure = true;

    private String clientIdPrefix = "smaApi";

    private AcknowledgeMode ackMode = AcknowledgeMode.MANUAL;

    private AMQPSenderDestinationConfigProperties replyTo;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }

    public boolean isAcknowledgeOnFailure() {
        return acknowledgeOnFailure;
    }

    public void setAcknowledgeOnFailure(boolean acknowledgeOnFailure) {
        this.acknowledgeOnFailure = acknowledgeOnFailure;
    }

    public String getClientIdPrefix() {
        return clientIdPrefix;
    }

    public void setClientIdPrefix(String clientIdPrefix) {
        this.clientIdPrefix = clientIdPrefix;
    }

    public AcknowledgeMode getAckMode() {
        return ackMode;
    }

    public void setAckMode(AcknowledgeMode ackMode) {
        this.ackMode = ackMode;
    }

    public AMQPSenderDestinationConfigProperties getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(AMQPSenderDestinationConfigProperties replyTo) {
        this.replyTo = replyTo;
    }
}
