package org.sma.platform.common.datamodel.amqp;

public class AMQPConfigProperties {

    private String address;
    private int port;
    private String userName;
    private String password;
    private String virtualHost;
    private boolean transactional = false;
//    private AMQPJNDIConfigProperties jndi;
//    private AMQPConnectionConfigProperties conn;
    private AMQPDestinationConfigProperties dest;
    private SSLContext ssl;

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public AMQPDestinationConfigProperties getDest() {
        return dest;
    }

    public void setDest(AMQPDestinationConfigProperties dest) {
        this.dest = dest;
    }

    public SSLContext getSsl() {
        return ssl;
    }

    public void setSsl(SSLContext ssl) {
        this.ssl = ssl;
    }

//    public AMQPConnectionConfigProperties getConn() {
//        return conn;
//    }
//
//    public void setConn(AMQPConnectionConfigProperties conn) {
//        this.conn = conn;
//    }

//    public AMQPJNDIConfigProperties getJndi() {
//        return jndi;
//    }
//
//    public void setJndi(AMQPJNDIConfigProperties jndi) {
//        this.jndi = jndi;
//    }
}
