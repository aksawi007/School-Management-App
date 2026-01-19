package org.sma.platform.core.contract;

import org.sma.platform.common.datamodel.logevent.CommonLogMessage;

public interface CommonLogMessagePublisher {

    public void publishCommonLogMessage(CommonLogMessage message);

}
