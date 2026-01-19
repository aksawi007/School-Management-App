package org.sma.platform.common.datamodel.logevent;

import org.sma.platform.common.datamodel.amqp.SmaMessage;

import java.io.IOException;

public class CommonLogMessage extends SmaMessage<CommonLogEvent> {
   // CommonLogEvent commonLogEvent;

    public CommonLogMessage(CommonLogEvent commonLogEvent) {
        super(commonLogEvent, null);
       // this.commonLogEvent = commonLogEvent;
    }

    public CommonLogMessage(CommonLogEvent commonLogEvent, String correlationId) {
        super(commonLogEvent, correlationId);
        // this.commonLogEvent = commonLogEvent;
    }

    public CommonLogEvent getCommonLogEvent() {
        return getPayload();
    }

    public void setCommonLogEvent(CommonLogEvent commonLogEvent) {
        try {
            setPayload(commonLogEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
