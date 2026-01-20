package org.sma.platform.core.restcontroller;

import org.sma.platform.core.service.ApiServiceBinding;
import org.sma.platform.core.service.ServiceBindingType;
import org.sma.platform.core.service.ServiceRequestContext;
import org.sma.platform.common.datamodel.app.ResultStatus;
import org.sma.platform.common.datamodel.logevent.CommonLogEvent;
import org.sma.platform.common.datamodel.logevent.CommonLogMessage;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@ConfigurationProperties(prefix = "sma.api.rest-binding")
public abstract class ApiRestServiceBinding extends ApiServiceBinding {

    @Override
    protected void onServiceInit() {

    }

    @Override
    protected ServiceBindingType getServiceBindingType() {
        return ServiceBindingType.REST;
    }

    protected <T> ResponseEntity<T> processResponse(ServiceRequestContext context, T serviceResponse) {
        return processResponse(context, serviceResponse, null);
    }

    protected <T> ResponseEntity<T> processResponse(ServiceRequestContext context, T serviceResponse, Map<String, String> responseHeaderMap) {
        if (isLogginEnabled()) {
            ResultStatus resultStatus = new ResultStatus();
            CommonLogEvent commonLogEvent = createCommonLogEvent(context, context.getRequestPayload(), serviceResponse, context.getInterfaceName(), resultStatus);
            CommonLogMessage message = new CommonLogMessage(commonLogEvent);
            publishLogMessage(message);
        }
        if (responseHeaderMap != null && responseHeaderMap.size() > 0) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaderMap.forEach((k, v) -> responseHeaders.add(k, v));
            return ResponseEntity.ok().headers(responseHeaders).body(serviceResponse);
        } else {
            return ResponseEntity.ok().body(serviceResponse);
        }
    }

    @Override
    protected String getInterfaceName() {
        return null;
    }


}
