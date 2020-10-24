package test.dataservice;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContexts;
import test.binding.SSLClientRestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Component
@ConfigurationProperties(prefix = "server.url")
public class DataService {

    private String getMerchant;
    private String addMerchant;

    public String getGetMerchant() {
        return getMerchant;
    }

    public void setGetMerchant(String getMerchant) {
        this.getMerchant = getMerchant;
    }

    public String getAddMerchant() {
        return addMerchant;
    }

    public void setAddMerchant(String addMerchant) {
        this.addMerchant = addMerchant;
    }

    private RestTemplate restTemplate;

    @PostConstruct
    public void initRestTemplate() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        SSLContextBuilder builder = SSLContextBuilder.create();
        File file = new File("C:/Users/HP-PC/IdeaProjects/SSLClient/src/main/resources/ssl-server.jks");
        SSLContext sslContext = builder.loadTrustMaterial(file,"changeit".toCharArray()).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClientBuilder = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory =  new HttpComponentsClientHttpRequestFactory(httpClientBuilder);
        restTemplate =  new RestTemplate(httpRequestFactory);
    }

    public String invokePostAPI(String request){

        HttpEntity<String> httpEntity = new HttpEntity<String>(request);
        String rs;
        try{
            ResponseEntity<String> responseEntity = restTemplate.exchange(getAddMerchant(), HttpMethod.POST,httpEntity,String.class);
            rs = responseEntity.getBody();
        }
        catch(Exception e){
            rs = e.getMessage();
        }
        return rs;
    }

    public String invokeGetAPI(String  mid){

        HttpEntity<String> httpEntity = new HttpEntity<String>(mid);
        String requestURL = getGetMerchant().concat("?merchantId="+mid);
        String rs;
        try{
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestURL,HttpMethod.GET,httpEntity,String.class);
            rs = responseEntity.getBody();
        }
        catch(Exception e){
            rs = e.getMessage();
        }
        return rs;
    }
}
