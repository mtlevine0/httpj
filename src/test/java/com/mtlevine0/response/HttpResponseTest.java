package com.mtlevine0.response;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.request.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;

public class HttpResponseTest {
    HttpResponse httpResponse;
    String rawHttpResponseHeaders;
    String rawHttpResponseBody;

    @Before
    public void setup() {
        httpResponse = generateHttpResponse();
        rawHttpResponseBody = generateBody();
    }

    private String generateRawHttpResponseHeaders(int contentLength, boolean isGzip) {
        String response = HttpResponse.HTTP_PROTOCOL_VERSION + " 200 Ok\r\n" +
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
                "Server: Apache/2.2.14 (Win32)\r\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: Closed\r\n";
        if (isGzip) {
            response = response + "Content-Encoding: gzip\r\n";
        }
        return response + "\r\n";
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

    private HttpRequest generateHttpRequest(boolean isGzip) {
        HttpRequest httpRequest = new HttpRequest();
        if (isGzip) {
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(HttpResponse.ACCEPT_ENCODING_HEADER, "gzip, compress");
            httpRequest.setHeaders(headers);
        }
        return httpRequest;
    }

    @Test
    public void GivenHttpResponse_WhenParsingResponse_ThenResponseShouldMatchRawResponse() throws IOException {
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.GZIP_ENCODING);
        rawHttpResponseHeaders = generateRawHttpResponseHeaders(generateBody().length(), false);
        assertArrayEquals((rawHttpResponseHeaders + rawHttpResponseBody).getBytes(),
                httpResponse.getResponse(generateHttpRequest(false)));
    }

    @Test
    public void GivenHttpResponseGzip_WhenParsingResponse_ThenResponseShouldMatchRawResponse() throws IOException {
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.GZIP_ENCODING);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gzipOutputStream.write(rawHttpResponseBody.getBytes());
        gzipOutputStream.close();

        rawHttpResponseHeaders = generateRawHttpResponseHeaders(byteArrayOutputStream.toByteArray().length, true);
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        byteArrayOutputStream1.write(rawHttpResponseHeaders.getBytes());
        byteArrayOutputStream1.write(byteArrayOutputStream.toByteArray());

        assertArrayEquals(byteArrayOutputStream1.toByteArray(),
                httpResponse.getResponse(generateHttpRequest(true)));
    }

}
