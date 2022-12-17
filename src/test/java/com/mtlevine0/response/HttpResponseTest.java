package com.mtlevine0.response;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpResponseTest {
    HttpResponse httpResponse;
    String rawHttpResponse;

    @Before
    public void setup() {
        httpResponse = generateHttpResponse();
        rawHttpResponse = generateRawHttpResponse();
    }

    private String generateRawHttpResponse() {
        return HttpResponse.HTTP_PROTOCOL_VERSION + " 200 Ok\r\n" +
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
                "Server: Apache/2.2.14 (Win32)\r\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                "Content-Length: " + String.valueOf(generateBody().length()) + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: Closed\r\n" +
                "\r\n" +
                generateBody();
    }

    private HttpResponse generateHttpResponse() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Date", "Mon, 27 Jul 2009 12:28:53 GMT");
        headers.put("Server", "Apache/2.2.14 (Win32)");
        headers.put("Last-Modified", "Wed, 22 Jul 2009 19:15:56 GMT");
        headers.put("Content-Length", String.valueOf(generateBody().length()));
        headers.put("Content-Type", "text/html");
        headers.put("Connection", "Closed");

        String body = generateBody();

        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(body.getBytes())
                .headers(headers)
                .build();
    }

    private String generateBody() {
        String body = "<html>\r\n" +
                "<body>\r\n" +
                "<h1>Hello, World!</h1>\r\n" +
                "</body>\r\n" +
                "</html>";
        return body;
    }

    @Test
    public void GivenHttpResponse_WhenParsingResponse_ThenResponseShouldMatchRawResponse() throws IOException {
        assertArrayEquals(rawHttpResponse.getBytes(), httpResponse.getResponse());
    }

}
