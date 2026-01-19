package org.sma.platform.common.datamodel.amqp;

import java.util.List;

public class AMQPDestinationConfigProperties {
    private AMQPReceiverDestinationConfigProperties receiver;

    private AMQPSenderDestinationConfigProperties sender;

    private List<String> exceptionList;

    private boolean stopOnException = false;

    public AMQPReceiverDestinationConfigProperties getReceiver() {
        return receiver;
    }

    public void setReceiver(AMQPReceiverDestinationConfigProperties receiver) {
        this.receiver = receiver;
    }

    public AMQPSenderDestinationConfigProperties getSender() {
        return sender;
    }

    public void setSender(AMQPSenderDestinationConfigProperties sender) {
        this.sender = sender;
    }

    public List<String> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(List<String> exceptionList) {
        this.exceptionList = exceptionList;
    }

    public boolean isStopOnException() {
        return stopOnException;
    }

    public void setStopOnException(boolean stopOnException) {
        this.stopOnException = stopOnException;
    }
}
