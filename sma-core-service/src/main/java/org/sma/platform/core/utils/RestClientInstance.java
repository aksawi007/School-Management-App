package org.sma.platform.core.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-client RestClient instance configurable with its own timeouts and base URL.
 */
public class RestClientInstance {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RestClientInstance(String baseUrl, int connectTimeoutMs, int readTimeoutMs) {
        this.baseUrl = baseUrl;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        this.restTemplate = new RestTemplate(factory);
    }

    private Map<String, Object> safeUriVars(Map<String, Object> uriVars) {
        return uriVars != null ? uriVars : new HashMap<>();
    }

    private HttpHeaders toHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        return httpHeaders;
    }

    public <T> T get(String path, Class<T> responseType) {
        String url = buildUrl(path);
        ResponseEntity<T> resp = restTemplate.getForEntity(url, responseType);
        return resp.getBody();
    }

    public <T> T get(String path, Class<T> responseType, Map<String, String> headers, Map<String, Object> uriVars) {
        String url = buildUrl(path);
        HttpHeaders httpHeaders = toHeaders(headers);
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.GET, entity, responseType, safeUriVars(uriVars));
        return resp.getBody();
    }

    public <T> T post(String path, Object body, Class<T> responseType, Map<String, String> headers, Map<String, Object> uriVars) {
        String url = buildUrl(path);
        HttpHeaders httpHeaders = toHeaders(headers);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.POST, entity, responseType, safeUriVars(uriVars));
        return resp.getBody();
    }

    public <T> T put(String path, Object body, Class<T> responseType, Map<String, String> headers, Map<String, Object> uriVars) {
        String url = buildUrl(path);
        HttpHeaders httpHeaders = toHeaders(headers);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType, safeUriVars(uriVars));
        return resp.getBody();
    }

    public void delete(String path, Map<String, Object> uriVars) {
        String url = buildUrl(path);
        restTemplate.delete(url, safeUriVars(uriVars));
    }

    public <T> T exchange(String path, HttpMethod method, Object body, Class<T> responseType, Map<String, String> headers, Map<String, Object> uriVars) {
        String url = buildUrl(path);
        HttpHeaders httpHeaders = toHeaders(headers);
        if (body != null && !httpHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<T> resp = restTemplate.exchange(url, method, entity, responseType, safeUriVars(uriVars));
        return resp.getBody();
    }

    private String buildUrl(String path) {
        if (path == null) {
            return baseUrl;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl + path.substring(1);
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }
}
