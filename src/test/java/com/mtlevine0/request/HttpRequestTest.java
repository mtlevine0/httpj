package com.mtlevine0.request;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpRequestTest {
    HttpRequest httpRequest;
    HttpRequest mockHttpRequest;
    String rawHttpRequest;
    
    @Before
    public void setup() {
        mockHttpRequest = generateMockHttpRequest();
        InputStream in = new ByteArrayInputStream(mockHttpRequest.toString().getBytes());
        httpRequest = new HttpRequest(in);
        rawHttpRequest = generateRawHttpRequest();
    }

    private String generateRawHttpRequest() {
        return "POST /test?foo=123&bar=456 HTTP/1.1\r\n" +
                "Content-Type: text/plain\r\n" +
                "User-Agent: PostmanRuntime/7.26.8\r\n" +
                "Accept: */*\r\n" +
                "Postman-Token: 1bd29bd4-95a8-41cf-b2dd-1150f2c9f6c0\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "\r\n" +
                "test\r\n\r\n";
    }

    private String generateRawHttpRequest(String params) {
        return "POST /test" + params + " HTTP/1.1\r\n" +
                "Content-Type: text/plain\r\n" +
                "User-Agent: PostmanRuntime/7.26.8\r\n" +
                "Accept: */*\r\n" +
                "Postman-Token: 1bd29bd4-95a8-41cf-b2dd-1150f2c9f6c0\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "\r\n" +
                "test\r\n\r\n";
    }

    private HttpRequest generateMockHttpRequest() {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(HttpMethod.GET);
        httpRequest.setPath("/test");
        httpRequest.setProtocolVersion("HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Test", "this: is a test header");
        headers.put("Host", "localhost:8080");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0");
        headers.put("Cookie", "R_USERNAME=admin; R_PCS=dark; R_LOCALE=en-us; default-theme=ngax; CSRF=13a9ba62d1; R_SESS=token-d4kwj:jvvljhrh7qjslgwjjnhzcl9t566hbkgp5tksqnm48z9h7v7hmhlql7");
        httpRequest.setHeaders(headers);
        httpRequest.setBody("this is a test body");
        return httpRequest;
    }

    @Test
    public void GivenAGetRequest_WhenParseMethod_ThenMethodIsGet() {
        assertEquals(HttpMethod.GET, httpRequest.getMethod());
    }

    @Test
    public void GivenAHttpPathOfTest_WhenParseHttpPath_ThenPathIsTest() {
        assertEquals("/test", httpRequest.getPath());
    }

    @Test
    public void GivenAProtocolVersionOf1point1_WhenParseProtocolVersion_ThenProtocolVersionIs1point1() {
        assertEquals("HTTP/1.1", httpRequest.getProtocolVersion());
    }

    @Test
    public void GivenAHttpRequest_WhenParseHeaders_ThenCountHeaders() {
        Map<String, String> headers = httpRequest.getHeaders();
        assertEquals(mockHttpRequest.getHeaders().size(), headers.size());
    }

    @Test
    public void GivenAHttpRequestHeaderTestWithColon_WhenParseHeaders_ThenFullValueIsIntact() {
        Map<String, String> headers = httpRequest.getHeaders();
        assertEquals("this: is a test header", headers.get("Test"));
    }

    @Test
    public void GivenAHttpRequestBody_WhenParseRequest_ThenBodyReturnBody() {
        String body = "this is a test body";
        assertEquals(body, httpRequest.getBody());
    }

    @Test
    public void GivenAHttpRequest_WhenParseRequest_ThenRebuildRawRequest() {
        assertEquals(mockHttpRequest.toString(), httpRequest.toString());
    }

    @Test
    public void GivenARawHttpRequestString_WhenParseRequest_ThenEnsureHeadersRemainInOrder() {
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(rawHttpRequest.getBytes()));
        assertEquals(rawHttpRequest, request.toString());
    }

    @Test
    public void getQueryParams_ShouldReturnQueryParamsMap_WhenHttpRequestPathIncludesOneOrMoreParams() {
        InputStream in = new ByteArrayInputStream(rawHttpRequest.getBytes());
        HttpRequest request = new HttpRequest(in);
        assertEquals("123", request.getQueryParams().get("foo"));
        assertEquals("456", request.getQueryParams().get("bar"));
    }

    @Test
    public void getQueryParams_ShouldReturnNull_WhenHttpRequestPathIncludesQuestionMarkWithNoParams() {
        InputStream in = new ByteArrayInputStream(generateRawHttpRequest("?").getBytes());
        HttpRequest request = new HttpRequest(in);
        assertEquals(null, request.getQueryParams().get("foo"));
    }

    @Test
    public void getQueryParams_ShouldReturnValue_WhenHttpRequestPathIncludesQueryParamWithNoValue() {
        InputStream in = new ByteArrayInputStream(generateRawHttpRequest("?foo=&bar=456").getBytes());
        HttpRequest request = new HttpRequest(in);
        assertEquals(null, request.getQueryParams().get("foo"));
        assertEquals("456", request.getQueryParams().get("bar"));
        assertEquals(null, request.getQueryParams().get("dne"));
    }
}
