package org.sma.platform.common.datamodel.amqp;

import org.sma.platform.common.datamodel.ApiBaseModel;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmaMessage<T>  extends ApiBaseModel {
    private byte[] byteMessage;
    private Date messageCreationTimeStamp;
    private Map<String, String> stringProperties;
    private Map<String, Integer> integerProperties;

    String correlationId;

//    public SmaMessage() {
//        this.messageCreationTimeStamp = new Date();
//    }

    public SmaMessage(byte[] byteMessage, String correlationId) {
        this.byteMessage = byteMessage;
        this.messageCreationTimeStamp = new Date(new Timestamp(System.currentTimeMillis()).getTime());
        this.correlationId = correlationId;
    }

    public SmaMessage(T payload, String correlationId)  {
        try {
            this.byteMessage = ObjectToByteArray(payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.messageCreationTimeStamp = new Date(new Timestamp(System.currentTimeMillis()).getTime());
        this.correlationId = correlationId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public T getPayload() {
        return (T) byteArrayToObject(this.byteMessage);
    }

    public void setPayload(Object byteMessage) throws IOException {
        this.byteMessage = ObjectToByteArray(byteMessage);
    }

    public byte[] getByteMessage() {
        return byteMessage;
    }

    public Date getMessageCreationTimeStamp() {
        return messageCreationTimeStamp;
    }

    public void setMessageCreationTimeStamp(Date messageCreationTimeStamp) {
        this.messageCreationTimeStamp = messageCreationTimeStamp;
    }

    public Map<String, String> getStringProperties() {
        return stringProperties;
    }

    public void addStringProperties(Map<String, String> stringProperties) {
        if (this.stringProperties == null) {
            this.stringProperties = new HashMap<>();
        }
        this.stringProperties.putAll(stringProperties);
    }

    public void addStringProperties(String key, String value) {
        if (this.stringProperties == null) {
            this.stringProperties = new HashMap<>();
        }
        this.stringProperties.put(key, value);
    }

    public Map<String, Integer> getIntegerProperties() {
        return integerProperties;
    }

    public void addIntegerProperties(String key, int value) {
        if (this.integerProperties == null) {
            this.integerProperties = new HashMap<>();
        }
        this.integerProperties.put(key, value);
    }

    public void addIntegerProperties(Map<String, Integer> integerProperties) {
        if (this.integerProperties == null) {
            this.integerProperties = new HashMap<>();
        }
        this.integerProperties.putAll(integerProperties);
    }

    public byte[] ObjectToByteArray(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        return bos.toByteArray();
    }

    public T byteArrayToObject(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return (T) is.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
