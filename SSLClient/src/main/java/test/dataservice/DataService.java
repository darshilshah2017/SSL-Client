package test.dataservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import test.datamodel.ErrorResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "server.config")
public class DataService{

    Logger logger = LoggerFactory.getLogger(DataService.class);

    private final int DEFAULT_TIMEOUT = (10 * 1000);

    private String getMerchantURL;
    private String addMerchantURL;
    private int readTimeout = DEFAULT_TIMEOUT;
    private int connectTimeout = DEFAULT_TIMEOUT;
    private int connectionRequestTimeout = DEFAULT_TIMEOUT;
    private String trustStore;
    private String trustStorePassword;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getGetMerchantURL() {
        return getMerchantURL;
    }

    public void setGetMerchantURL(String getMerchantURL) {
        this.getMerchantURL = getMerchantURL;
    }

    public String getAddMerchantURL() {
        return addMerchantURL;
    }

    public void setAddMerchantURL(String addMerchantURL) {
        this.addMerchantURL = addMerchantURL;
    }

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void initRestTemplate() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        objectMapper = new ObjectMapper();

        HttpComponentsClientHttpRequestFactory httpRequestFactory =  new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setReadTimeout(getReadTimeout());
        httpRequestFactory.setConnectTimeout(getConnectTimeout());
        httpRequestFactory.setConnectionRequestTimeout(getConnectionRequestTimeout());

        if(getTrustStore()!=null && getTrustStorePassword()!=null){
            SSLContextBuilder builder = SSLContextBuilder.create();
//            File file = new File(getTrustStore());
            URL url = getClass().getResource(getTrustStore());
            SSLContext sslContext = builder.loadTrustMaterial(url,getTrustStorePassword().toCharArray()).build();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient httpClientBuilder = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
            httpRequestFactory.setHttpClient(httpClientBuilder);
        }

        restTemplate =  new RestTemplate(httpRequestFactory);
    }

    public String invokePostAPI(String request){

        HttpEntity<String> httpEntity = new HttpEntity<String>(request);
        String rs;
        try{
            ResponseEntity<String> responseEntity = restTemplate.exchange(getAddMerchantURL(), HttpMethod.POST,httpEntity,String.class);
            rs = responseEntity.getBody();
        }
        catch (ResourceAccessException e){
            rs = e.getMessage();
        }
        catch(Exception e){
            rs = e.getMessage();
        }
        return rs;
    }

    public String invokeGetAPI(Map<String, String> map) throws JsonProcessingException {

        String uri = null;

        if(map!=null && !map.isEmpty()){
            UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromHttpUrl(getGetMerchantURL());
            for(Map.Entry<String, String> entry: map.entrySet()){
                componentsBuilder.queryParam(entry.getKey(), UriUtils.encode(entry.getValue(), Charset.forName("UTF-8")));
//                componentsBuilder.queryParam(entry.getKey(), UriUtils.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            uri = componentsBuilder.build(true).toString();
        }
        else{
            uri = getGetMerchantURL();
        }

        HttpEntity<String> httpEntity = null;
//        String requestURL = getGetMerchant().concat("?merchantId="+mid);
        String rs;
        try{
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri,HttpMethod.GET,httpEntity,String.class);
            rs = responseEntity.getBody();
        }
        catch (ResourceAccessException e){
            rs = e.getMessage();
        }
        catch (HttpServerErrorException e){
            logger.error("HttpServerErrorException when invoking endSystem's API");
            ErrorResponse errorResponse = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            logger.error("status: {}, message: {}",errorResponse.getCode(), errorResponse.getMessage());
            rs = e.getMessage();
        }
        catch(HttpClientErrorException e){
            logger.error("HttpClientErrorException when invoking endSystem's API");
            ErrorResponse errorResponse = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            logger.error("status: {}, message: {}",errorResponse.getCode(), errorResponse.getMessage());
            rs = e.getMessage();
        }
        catch(Exception e){
            rs = e.getMessage();
        }
        return rs;
    }
}
