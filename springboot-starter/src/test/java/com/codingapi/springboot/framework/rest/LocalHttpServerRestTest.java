package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.RestParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 基于 JDK 内置 HttpServer 的本地回环 HTTP 测试, 无外部网络依赖,
 * 覆盖 HttpRequest / RestClient / SessionClient 的 GET/POST 主要路径
 */
class LocalHttpServerRestTest {

    private static HttpServer server;
    private static String baseUrl;

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        // 固定 JSON 响应
        server.createContext("/json", exchange ->
                respond(exchange, "{\"name\":\"codingapi\"}"));
        // 回显请求体
        server.createContext("/echo", exchange ->
                respond(exchange, new String(readBody(exchange.getRequestBody()), StandardCharsets.UTF_8)));
        // 回显查询字符串
        server.createContext("/query", exchange -> {
            String query = exchange.getRequestURI().getRawQuery();
            respond(exchange, query == null ? "" : query);
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static byte[] readBody(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[2048];
        int len;
        while ((len = in.read(chunk)) != -1) {
            buffer.write(chunk, 0, len);
        }
        return buffer.toByteArray();
    }

    private static void respond(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
        exchange.close();
    }

    @Test
    void restClientGetJson() {
        RestClient restClient = new RestClient(baseUrl);
        String response = restClient.get("/json");
        JSONObject json = JSONObject.parseObject(response);
        assertNotNull(json);
        assertEquals("codingapi", json.getString("name"));
    }

    @Test
    void restClientGetWithQueryParams() {
        RestClient restClient = new RestClient(baseUrl);
        String response = restClient.get("/query", RestParam.create()
                .add("name", "zhang")
                .add("age", "18"));
        assertTrue(response.contains("name=zhang"));
        assertTrue(response.contains("age=18"));
    }

    @Test
    void restClientGetWithHeaders() {
        RestClient restClient = new RestClient(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        String response = restClient.get("/json", headers);
        assertNotNull(response);
    }

    @Test
    void restClientPostJson() {
        RestClient restClient = new RestClient(baseUrl);
        JSONObject body = new JSONObject();
        body.put("username", "admin");
        String response = restClient.post("/echo", body);
        JSONObject echo = JSONObject.parseObject(response);
        assertNotNull(echo);
        assertEquals("admin", echo.getString("username"));
    }

    @Test
    void restClientPostWithRestParam() {
        RestClient restClient = new RestClient(baseUrl);
        String response = restClient.post("/echo", RestParam.create().add("k", "v"));
        JSONObject echo = JSONObject.parseObject(response);
        assertNotNull(echo);
        assertEquals("v", echo.getString("k"));
    }

    @Test
    void sessionClientGetJson() {
        SessionClient sessionClient = new SessionClient();
        String response = sessionClient.getJson(baseUrl + "/json");
        JSONObject json = JSONObject.parseObject(response);
        assertNotNull(json);
        assertEquals("codingapi", json.getString("name"));
    }

    @Test
    void sessionClientGetJsonWithParams() {
        SessionClient sessionClient = new SessionClient();
        String response = sessionClient.getJson(baseUrl + "/query", RestParam.create().add("q", "1"));
        assertTrue(response.contains("q=1"));
    }

    @Test
    void sessionClientPostJson() {
        SessionClient sessionClient = new SessionClient();
        String response = sessionClient.postJson(baseUrl + "/echo", RestParam.create().add("action", "login"));
        JSONObject echo = JSONObject.parseObject(response);
        assertNotNull(echo);
        assertEquals("login", echo.getString("action"));
    }

    @Test
    void sessionClientPostJsonBody() {
        SessionClient sessionClient = new SessionClient();
        JSONObject body = new JSONObject();
        body.put("from", "body");
        String response = sessionClient.postJson(baseUrl + "/echo", body);
        JSONObject echo = JSONObject.parseObject(response);
        assertNotNull(echo);
        assertEquals("body", echo.getString("from"));
    }

    @Test
    void sessionClientPostForm() {
        SessionClient sessionClient = new SessionClient();
        String response = sessionClient.postForm(baseUrl + "/echo", RestParam.create().add("f", "1"));
        assertTrue(response.contains("f=1"));
    }

    @Test
    void sessionClientGetHtml() {
        SessionClient sessionClient = new SessionClient();
        String response = sessionClient.getHtml(baseUrl + "/json");
        assertNotNull(response);
    }

    @Test
    void sessionClientAddHeader() {
        SessionClient sessionClient = new SessionClient();
        sessionClient.addHeader("X-Test", "1");
        assertEquals("1", sessionClient.getHttpHeaders().getFirst("X-Test"));
    }

    @Test
    void httpRequestGetRequestExecute() {
        HttpRequest httpRequest = new HttpRequest();
        MultiValueMap<String, String> variables = new LinkedMultiValueMap<>();
        variables.add("a", "1");
        String response = httpRequest.getGetRequest(baseUrl + "/query", new HttpHeaders(), variables).execute();
        assertTrue(response.contains("a=1"));
    }

    @Test
    void httpRequestGetWithoutVariables() {
        HttpRequest httpRequest = new HttpRequest();
        String response = httpRequest.getGetRequest(baseUrl + "/json", new HttpHeaders(), null).execute();
        assertNotNull(response);
    }

    @Test
    void httpRequestPostJsonRequest() {
        HttpRequest httpRequest = new HttpRequest();
        JSONObject body = new JSONObject();
        body.put("via", "request");
        String response = httpRequest.getPostRequest(baseUrl + "/echo", new HttpHeaders(), (com.alibaba.fastjson.JSON) body).execute();
        JSONObject echo = JSONObject.parseObject(response);
        assertNotNull(echo);
        assertEquals("request", echo.getString("via"));
    }

    @Test
    void httpRequestPostFormRequest() {
        HttpRequest httpRequest = new HttpRequest();
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("field", "x");
        String response = httpRequest.getPostRequest(baseUrl + "/echo", new HttpHeaders(), form).execute();
        assertTrue(response.contains("field=x"));
    }
}
