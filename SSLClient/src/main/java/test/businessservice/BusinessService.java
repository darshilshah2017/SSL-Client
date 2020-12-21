package test.businessservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.dataservice.DataService;

import java.util.HashMap;
import java.util.Map;

@Component
public class BusinessService {

    @Autowired
    DataService dataService;

    public String createMerchant(String name){
        return dataService.invokePostAPI(name);
    }

    public String getMerchant(String merchantId) throws JsonProcessingException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("merchantId", merchantId);

        return dataService.invokeGetAPI(map);
    }


}
