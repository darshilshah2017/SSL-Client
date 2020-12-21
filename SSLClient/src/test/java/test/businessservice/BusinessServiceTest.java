package test.businessservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import test.dataservice.DataService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

    @Mock
    private DataService dataService;

    @InjectMocks
    BusinessService businessService;

    @Test
    void createMerchant() {
        when(dataService.invokePostAPI("Ramesh")).thenReturn("SUCCESS");
        String response = businessService.createMerchant("Ramesh");
        assertEquals("SUCCESS", response);
    }

    @Test
//    @Disabled
    void getMerchant() throws JsonProcessingException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("merchantId", "12212233432");

        when(dataService.invokeGetAPI(map)).thenReturn("SUCCESS");

        String response = dataService.invokeGetAPI(map);
        assertEquals("SUCCESS", response);
    }
}