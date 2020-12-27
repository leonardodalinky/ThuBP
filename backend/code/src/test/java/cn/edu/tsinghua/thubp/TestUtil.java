package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TestUtil {

    public TestRestTemplate user3Template = new TestRestTemplate();
    public TestRestTemplate user2Template = new TestRestTemplate();
    public String user2Id;
    public String user3Id;

    @FunctionalInterface
    public interface TestUnit {
        void apply();
    }

    private Map<Object, SimpleResponse> responseMap = new ConcurrentHashMap<>();

    public <T extends SimpleResponse> T saveResponse(Object tag, T response) {
        responseMap.put(tag, response);
        return response;
    }

    @SuppressWarnings("unchecked")
    public <T extends SimpleResponse> T getResponse(Object tag) {
        return (T) responseMap.get(tag);
    }

}
