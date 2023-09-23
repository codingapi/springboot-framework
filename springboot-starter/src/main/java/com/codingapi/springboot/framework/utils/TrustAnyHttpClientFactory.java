package com.codingapi.springboot.framework.utils;


import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;


import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustAnyHttpClientFactory {

    private TrustAnyHttpClientFactory(){

    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    @SneakyThrows
    public static HttpClient createTrustAnyHttpClient() {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustAnyTrustManager trustAnyTrustManager = new TrustAnyTrustManager();
        sslContext.init(null, new TrustManager[] {trustAnyTrustManager}, null);
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        final Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory> create()
                        .register("https", sslConnectionSocketFactory)
                        .register("http", new PlainConnectionSocketFactory())
                        .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCircularRedirectsAllowed(true)
                .build();

        BasicHttpClientConnectionManager connectionManager
                = new BasicHttpClientConnectionManager(socketFactoryRegistry);

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }
}
