package com.example.http2client.controller;

import com.example.http2client.interceptor.LoggingInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/http-client")
public class Http2ClientController {

//    private static final String url = "https://www.finnq.com";
    private static final String url = "https://localhost:8443/http2-server/2";

    @RequestMapping(method = RequestMethod.GET, value = "http2/version")
    @ResponseBody
    private String getHttp2Version() {
        String result = "error";
        RestTemplate restTemplate = new RestTemplate();

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            OkHttp3ClientHttpRequestFactory crf = new OkHttp3ClientHttpRequestFactory(okHttpClient);
            restTemplate.setRequestFactory(crf);

            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new LoggingInterceptor());
            restTemplate.setInterceptors(interceptors);
            result = restTemplate.getForObject(url, String.class);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "http/version")
    @ResponseBody
    private String getHttpVersion() {
        String result = "error";
        RestTemplate restTemplate = new RestTemplate();

        result = restTemplate.getForObject(url, String.class);
        return result;
    }
}
