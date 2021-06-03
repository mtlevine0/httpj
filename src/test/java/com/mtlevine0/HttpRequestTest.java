package com.mtlevine0;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpRequestTest {
    HttpRequest httpRequest;
    HttpRequest mockHttpRequest;
    
    @Before
    public void setup() {
        mockHttpRequest = generateMockHttpRequest();
        httpRequest = new HttpRequest(mockHttpRequest.toString());
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
}
