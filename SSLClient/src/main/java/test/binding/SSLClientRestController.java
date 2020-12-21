package test.binding;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import test.businessservice.BusinessService;
import test.dataservice.DataService;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant")

public class SSLClientRestController {

    private static final String ADDMERCHANT_URI = "/add";
    private static final String GETMERCHANT_URI = "/get";

    private String URL;

    Logger logger = LoggerFactory.getLogger(SSLClientRestController.class);

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Autowired
    BusinessService businessService;

    @PostMapping("/create")
    public String createMerchant(@RequestBody String merchantName){
        logger.info("createMerchant request received: {}",merchantName);
        return businessService.createMerchant(merchantName);
    }

    @GetMapping("/query")
    public String queryMerchant(@RequestParam(name = "merchantId", required = true) String merchantId) throws JsonProcessingException {
        logger.info("queryMerchant request received: {}",merchantId);

        return businessService.getMerchant(merchantId);
    }

}
