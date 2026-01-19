package org.sma.platform.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


public class SmaApiUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    protected final static Logger LOGGER = LoggerFactory.getLogger(SmaApiUtils.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static DatatypeFactory dataTypeFactory = null;

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Return current system TimeStamp in XMLGregorianCalendar
     *
     * @return XMLGregorianCalendar
     */
    public static XMLGregorianCalendar getXMLGregorianCalendar() {

        return getXMLGregorianCalendar(DATE_FORMAT);
    }

    public static int generateRandomNo_6_digits() {
        int randomNo = (int) Math.floor(100000 + Math.random() * 900000);
        return randomNo;
    }

    public static Date addMinutesInCurrentTime(int minutes) {
        Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        Date afterAddingMins = new Date(timeInSecs + (minutes * 60 * 1000));
        return afterAddingMins;
    }

    // public static createServiceRequest

    /**
     * Convert Object to JSON
     *
     * @param message
     * @return
     */
    public static String convertToJsonString(Object message) {

        String responseMsg = null;
        if (message != null)
            try {

                responseMsg = objectMapper.writeValueAsString(message);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        return responseMsg;
    }

    /**
     * @param jsonString
     * @param classz
     * @return
     */
    public static <T> Object convertJsonToObject(String jsonString, Class<T> classz) {

        try {

            if (SmaApiUtils.isNotBlankStr(jsonString))
                return objectMapper.readValue(jsonString, classz);

        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    /**
     * Return current system TimeStamp in XMLGregorianCalendar
     *
     * @param dateFormat
     * @return XMLGregorianCalendar
     */
    public static XMLGregorianCalendar getXMLGregorianCalendar(String dateFormat) {

        if (dateFormat == null)
            dateFormat = DATE_FORMAT;

        XMLGregorianCalendar value = null;
        try {

            if (dataTypeFactory == null)
                dataTypeFactory = DatatypeFactory.newInstance();

            // value = DatatypeFactory.newInstance().newXMLGregorianCalendar(new
            // SimpleDateFormat(dateFormat).format(new Date()));
            value = dataTypeFactory.newXMLGregorianCalendar(new SimpleDateFormat(dateFormat).format(new Date()));

        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            // DO Nothing
        }

        return value;
    }

    /**
     * @param dateStr
     * @return
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(String dateStr) {

        return toXMLGregorianCalendar(dateStr, DATE_FORMAT);
    }

    /**
     * @param dateStr
     * @param dateFormat
     * @return
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(String dateStr, String dateFormat) {

        XMLGregorianCalendar xmlGregorianCalendar = null;
        if (isBlankStr(dateFormat))
            dateFormat = DATE_FORMAT;

        if (isNotBlankStr(dateStr)) {
            try {

                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(new SimpleDateFormat(dateFormat).parse(dateStr));
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return xmlGregorianCalendar;
    }

    public static XMLGregorianCalendar toXMLGregorianDate(String dateStr) {

        XMLGregorianCalendar xmlGregorianCalendar = null;
        if (isNotBlankStr(dateStr)) {

            try {

                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
                xmlGregorianCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
                xmlGregorianCalendar.setHour(DatatypeConstants.FIELD_UNDEFINED);
                xmlGregorianCalendar.setMinute(DatatypeConstants.FIELD_UNDEFINED);
                xmlGregorianCalendar.setSecond(DatatypeConstants.FIELD_UNDEFINED);
                xmlGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return xmlGregorianCalendar;
    }

    public static Calendar toCalendar(String dateStr) {

        return toCalendar(dateStr, DATE_FORMAT);
    }

    public static Calendar toCalendar(String dateStr, String dateFormat) {

        Calendar cal = null;
        if (isBlankStr(dateStr))
            dateStr = DATE_FORMAT;

        try {
            cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat(dateFormat).parse(dateStr));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cal;
    }

    public static Calendar getCurrentDate() {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal;
    }

    public static String generateUUIDOnClientDetails(String hostName) {
        String UUID = "";
        if (hostName.contains("localhost")) {
            try {
                Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaceEnumeration.hasMoreElements()) {
                    for (InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement().getInterfaceAddresses())
                        if (interfaceAddress.getAddress().isSiteLocalAddress()) {
                            hostName = interfaceAddress.getAddress().getHostAddress();
                            break;
                        }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        UUID = UUID + hostName + getCurrentDateStr();
        UUID = UUID.replace(".", "").replace("-", "").replace(" ", "").replace(":", "");
        return UUID;
    }

    public static String getCurrentDateStr() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String format = df.format(timestamp);
        return format;
    }


    /**
     * @param multileValues
     * @return
     */
    public static boolean isBlankStr(String... multileValues) {
        for (String strValue : multileValues) {
            if (isBlankStr(strValue))
                return true;
        }

        return false;
    }

    /**
     * @param multileValues
     * @return
     */
    public static boolean isNotBlankStr(String... multileValues) {

        for (String strValue : multileValues) {
            if (isBlankStr(strValue))
                return false;
        }

        return true;
    }

    /**
     * Check not blank String value
     *
     * @param value
     * @return
     */
    public static boolean isNotBlankStr(String value) {
        if (value != null && value.trim().length() > 0)
            return true;
        else
            return false;

    }

    /**
     * @param value
     * @return
     */
    public static boolean isBlankStr(String value) {
        if (value == null || value.trim().length() == 0)
            return true;
        else
            return false;

    }

    /**
     * @param value1
     * @param value2
     * @return
     */
    public static boolean isEqualTo(String value1, String value2) {
        if (isBlankStr(value1))
            return false;

        if (isBlankStr(value2))
            return false;

        return value1.equalsIgnoreCase(value2);

    }

    /**
     * @param value1
     * @param value2
     * @return
     */
    public static boolean isNotEqualTo(String value1, String value2) {
        if (isBlankStr(value1))
            return false;

        return !value1.equalsIgnoreCase(value2);
    }

    /**
     * @param value
     * @return
     */
    public static String[] toArray(String value) {

        return toArray(value, ",");

    }

    /**
     * @param value
     * @param delimeter
     * @return
     */
    public static String[] toArray(String value, String delimeter) {

        if (isNotBlankStr(value))
            return value.split(delimeter);
        else
            return null;

    }

    /**
     * @param arrayValues
     * @param value
     * @return
     */
    public static boolean isAvailable(String[] arrayValues, String value) {

        if (arrayValues == null || arrayValues.length == 0)
            return false;

        else {
            for (String arrayValue : arrayValues) {
                if (arrayValue.equalsIgnoreCase(value))
                    return true;
            }
        }
        return false;
    }

    /**
     * @param arrayValues
     * @param value
     * @return
     */
    public static boolean isNotAvailable(String[] arrayValues, String value) {

        if (arrayValues == null || arrayValues.length == 0)
            return true;

        else {

            for (String arrayValue : arrayValues) {
                if (arrayValue.equalsIgnoreCase(value))
                    return false;
            }
        }

        return true;
    }

    public static String getHostName() {

        InetAddress addr;
        try {

            addr = InetAddress.getLocalHost();
            return addr.getHostName();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static boolean convertStringYNtoBoolean(String boolStr) {
        return (SmaApiUtils.isNotBlankStr(boolStr)) && boolStr.equalsIgnoreCase("Y");
    }


    public static String convertByteArrayToString(byte[] byteArray) {
        return new String(byteArray, StandardCharsets.US_ASCII);
    }

    public static byte[] convertStringToByteArray(String text) {
        byte[] byteArray = text.getBytes(StandardCharsets.UTF_8);
        return byteArray;
    }
}
