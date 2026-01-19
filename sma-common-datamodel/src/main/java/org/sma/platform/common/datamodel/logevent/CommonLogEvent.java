package org.sma.platform.common.datamodel.logevent;

import org.sma.platform.common.datamodel.ApiBaseModel;

import java.io.Serializable;
import java.util.Date;

public class CommonLogEvent extends ApiBaseModel implements Serializable {

    private String transactionId;
    private Date receivedTimeStamp;
    private Date processedTimeStamp;
    private String receivedPayload;
    private String processedPayload;
    private String receivedLogMessage;
    private String processedLogMessage;
    private String componentName;
    private String BindingType;
    private String interfaceName;
    private String errorCode;
    private String errorMessage;
    private String stackTrace;
    private String status;

    public String getReceivedLogMessage() {
        return receivedLogMessage;
    }

    public void setReceivedLogMessage(String receivedLogMessage) {
        this.receivedLogMessage = receivedLogMessage;
    }

    public String getProcessedLogMessage() {
        return processedLogMessage;
    }

    public void setProcessedLogMessage(String processedLogMessage) {
        this.processedLogMessage = processedLogMessage;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }



    public Date getReceivedTimeStamp() {
        return receivedTimeStamp;
    }

    public void setReceivedTimeStamp(Date receivedTimeStamp) {
        this.receivedTimeStamp = receivedTimeStamp;
    }

    public Date getProcessedTimeStamp() {
        return processedTimeStamp;
    }

    public void setProcessedTimeStamp(Date processedTimeStamp) {
        this.processedTimeStamp = processedTimeStamp;
    }

    public String getReceivedPayload() {
        return receivedPayload;
    }

    public void setReceivedPayload(String receivedPayload) {
        this.receivedPayload = receivedPayload;
    }

    public String getProcessedPayload() {
        return processedPayload;
    }

    public void setProcessedPayload(String processedPayload) {
        this.processedPayload = processedPayload;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getBindingType() {
        return BindingType;
    }

    public void setBindingType(String bindingType) {
        BindingType = bindingType;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stacktrace) {
        this.stackTrace = stacktrace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CommonLogEvent{" +
                "transactionId='" + transactionId + '\'' +
                ", receivedTimeStamp=" + receivedTimeStamp +
                ", processedTimeStamp=" + processedTimeStamp +
                ", receivedPayload='" + receivedPayload + '\'' +
                ", processedPayload='" + processedPayload + '\'' +
                ", receivedLogMessage='" + receivedLogMessage + '\'' +
                ", processedLogMessage='" + processedLogMessage + '\'' +
                ", componentName='" + componentName + '\'' +
                ", BindingType='" + BindingType + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", stackTrace='" + stackTrace + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
