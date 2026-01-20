package org.sma.platform.core.service;

import org.sma.platform.core.utils.SmaApiUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServiceRequestContext {
    public ServiceRequestContext(String interfaceName, String transactionId, Object requestPayload) {
        this.transactionId = transactionId;
        this.interfaceName = interfaceName;
        this.requestPayload = SmaApiUtils.convertToJsonString(requestPayload);
        this.contextCreationTimeStamp = new Date(new Timestamp(System.currentTimeMillis()).getTime());
    }

    public ServiceRequestContext(String interfaceName, Object requestPayload) {
        this.interfaceName = interfaceName;
        this.requestPayload = SmaApiUtils.convertToJsonString(requestPayload);
        this.contextCreationTimeStamp = new Date(new Timestamp(System.currentTimeMillis()).getTime());
    }

    private String transactionId;
    private String requestPayload;
    private String responsePayload;
    private String interfaceName;
    private String requestLogMessage;
    private String responseLogMessage;
    private Date contextCreationTimeStamp;

    private Map<String,String> requestHeaders = new HashMap<>();
    private Map<String,String> resposeHeaders = new HashMap<>();

    public Date getContextCreationTimeStamp() {
        return contextCreationTimeStamp;
    }

    public void addRequestHeader(String key, String value){
        this.requestHeaders.put(key, value);
    }

    public void addResponseHeader(String key, String value){
        this.resposeHeaders.put(key, value);
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> getResposeHeaders() {
        return resposeHeaders;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getRequestLogMessage() {
        return requestLogMessage;
    }

    public void setRequestLogMessage(String requestLogMessage) {
        this.requestLogMessage = requestLogMessage;
    }

    public String getResponseLogMessage() {
        return responseLogMessage;
    }

    public void setResponseLogMessage(String responseLogMessage) {
        this.responseLogMessage = responseLogMessage;
    }
}
