package cn.edu.tsinghua.thubp.plugin;

import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import cn.edu.tsinghua.thubp.common.intf.ModifiableSource;
import cn.edu.tsinghua.thubp.common.intf.ModifiableTarget;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameResult implements ModifiableSource, ModifiableTarget {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class GameRoundResult {
        @JsonProperty("score0")
        private int score0;
        @JsonProperty("score1")
        private int score1;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class GameFinalResult {
        @JsonProperty("winner")
        private int winner;
        @JsonProperty("output0")
        private int output0;
        @JsonProperty("output1")
        private int output1;
    }

    @JsonProperty("rounds")
    @AutoModify
    public List<GameRoundResult> rounds;

    @JsonProperty("result")
    @AutoModify
    public GameFinalResult result;

    @JsonProperty(value = "extra")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AutoModify
    public Map<String, Object> extra;

}
