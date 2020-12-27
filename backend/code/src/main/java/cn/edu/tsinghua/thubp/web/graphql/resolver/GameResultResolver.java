package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.plugin.GameResult;
import com.coxautodev.graphql.tools.GraphQLResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameResultResolver implements GraphQLResolver<GameResult> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public String extra(GameResult result) throws JsonProcessingException {
        if (result.getExtra() != null) {
            return objectMapper.writeValueAsString(result.getExtra());
        }
        return null;
    }
}
