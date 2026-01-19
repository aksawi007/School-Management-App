package org.sma.platform.common.datamodel.amqp;

public class AMQPJNDIConfigProperties {

    private String contextFactory;
    private String contextUrl;
    private String vpnName;
    private String userName;
    private String password;
    private int port;
    private String virtualHost;
    private boolean transactional;
    private String address;

    public String getContextFactory() {
        return contextFactory;
    }

    public void setContextFactory(String contextFactory) {
        this.contextFactory = contextFactory;
    }

    public String getContextUrl() {
        return contextUrl;
    }

    public void setContextUrl(String contextUrl) {
        this.contextUrl = contextUrl;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
