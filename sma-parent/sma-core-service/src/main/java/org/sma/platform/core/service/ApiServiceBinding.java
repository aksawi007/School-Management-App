package org.sma.platform.core.service;


import org.sma.platform.core.config.datamodel.ApplicationProperties;
import org.sma.platform.core.contract.CommonLogMessagePublisher;
import org.sma.platform.core.utils.SmaApiUtils;
import org.sma.platform.common.datamodel.app.ResultStatus;
import org.sma.platform.common.datamodel.app.Status;
import org.sma.platform.common.datamodel.logevent.CommonLogEvent;
import org.sma.platform.common.datamodel.logevent.CommonLogMessage;
import org.springframework.beans.factory.annotation.Autowired;


import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public abstract class ApiServiceBinding extends ApplicationProperties {

    @Autowired(required = false)
    CommonLogMessagePublisher commonLogMessagePublisher;

    @PostConstruct
    public void init() {
        setHostName(SmaApiUtils.getHostName());
        onServiceInit();
    }

    protected abstract void onServiceInit();

    protected abstract ServiceBindingType getServiceBindingType();

    //    protected abstract String getTransactionId();
    protected abstract String getInterfaceName();


    public ServiceRequestContext createServiceRequestContext(String interfaceName, String transactionId, Object requestPayload) {
        return new ServiceRequestContext(interfaceName, transactionId, requestPayload);
    }

    protected void publishLogMessage(CommonLogMessage message) {
        commonLogMessagePublisher.publishCommonLogMessage(message);
        System.out.println(SmaApiUtils.convertToJsonString(message));
    }

    protected CommonLogEvent createCommonLogEvent(ServiceRequestContext context, Object receivedPayload, Object processedPayload,
                                                  String interfaceName, ResultStatus resultStatus) {
        CommonLogEvent commonLogEvent = new CommonLogEvent();
        commonLogEvent.setBindingType(getServiceBindingType().toString());
        commonLogEvent.setComponentName(getComponentName());
        if (interfaceName != null) {
            commonLogEvent.setInterfaceName(interfaceName);
        } else {
            commonLogEvent.setInterfaceName(context.getInterfaceName());
        }

        if (context.getTransactionId() != null)
            commonLogEvent.setTransactionId(context.getTransactionId());
        else
            commonLogEvent.setTransactionId(UUID.randomUUID().toString());
        if (isPayloadLogginEnabled()) {
            if (receivedPayload != null)
                commonLogEvent.setReceivedPayload(SmaApiUtils.convertToJsonString(receivedPayload));
            if (processedPayload != null)
                commonLogEvent.setProcessedPayload(SmaApiUtils.convertToJsonString(processedPayload));
        }
        // TODO - implement log message from props
        commonLogEvent.setReceivedLogMessage("");
        commonLogEvent.setProcessedLogMessage("");
        commonLogEvent.setStatus(ResultStatus.SUCCESS);
        commonLogEvent.setProcessedTimeStamp(new Date(new Timestamp(System.currentTimeMillis()).getTime()));
        commonLogEvent.setReceivedTimeStamp(context.getContextCreationTimeStamp());
        if (resultStatus.getStatus() == Status.FAILED) {
            commonLogEvent.setStatus(ResultStatus.FAILED);
            commonLogEvent.setErrorCode(resultStatus.getErrorCode());
            commonLogEvent.setErrorMessage(resultStatus.getErrorMessage());
        }
        return commonLogEvent;
    }
}
