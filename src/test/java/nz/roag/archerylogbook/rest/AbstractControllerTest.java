package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.SubscriberRepository;
import nz.roag.archerylogbook.db.model.Subscriber;
import nz.roag.archerylogbook.security.HmacUtil;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class AbstractControllerTest {

    @MockitoBean
    private SubscriberRepository subscriberRepository;
    private Subscriber subscriber;


    protected void init(){
        subscriber = new Subscriber();
        subscriber.setAccessKey("testKey");
        subscriber.setSecretKey("testSecret");

        //Common mocks
        given(subscriberRepository.findById(anyString()))
                .willReturn(Optional.of(subscriber));
    }

    protected Subscriber getSubscriber() {
        return this.subscriber;
    }

    protected HttpHeaders getHttpHeaders(String path) {
        var key = subscriber.getAccessKey();
        var nonce = "testNonce";
        var timestamp = "2023-01-01T11:00:00";
        var secret = subscriber.getSecretKey();
        var signature = new HmacUtil().calculateHMAC(secret, path + key + nonce + timestamp);
        var httpHeaders = new HttpHeaders();
        httpHeaders.add("nonce", nonce);
        httpHeaders.add("key", key);
        httpHeaders.add("timestamp", timestamp);
        httpHeaders.add("signature", signature);
        return httpHeaders;
    }
}
