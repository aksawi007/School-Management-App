package org.sma.platform.core.config.datamodel;

import org.springframework.beans.factory.annotation.Value;


public class ApplicationProperties {

    private String hostName;


    @Value("${sma.app.base.componentName}")
    private String componentName = "default";
    @Value("${sma.app.base.logginEnabled}")
    private boolean logginEnabled = false;
    @Value("${sma.app.base.payloadLogginEnabled}")
    private boolean payloadLogginEnabled = false;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public boolean isLogginEnabled() {
        return logginEnabled;
    }

    public void setLogginEnabled(boolean logginEnabled) {
        this.logginEnabled = logginEnabled;
    }

    public boolean isPayloadLogginEnabled() {
        return payloadLogginEnabled;
    }

    public void setPayloadLogginEnabled(boolean payloadLogginEnabled) {
        this.payloadLogginEnabled = payloadLogginEnabled;
    }
}
