package org.sma.platform.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class SmaImageUtils {
    public static String compressImage(String data) {
        byte[] byteData;
        try {
             byteData = data.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(byteData);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(byteData.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    public static String decompressImage(String data) {
        byte[] dataBytes;
        try {
             dataBytes = data.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Inflater inflater = new Inflater();
        inflater.setInput(dataBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(dataBytes.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception exception) {
        }
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
