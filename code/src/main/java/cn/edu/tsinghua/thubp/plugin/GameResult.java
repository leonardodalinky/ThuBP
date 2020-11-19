package cn.edu.tsinghua.thubp.plugin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameResult {

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
    public List<GameRoundResult> rounds;

    @JsonProperty("result")
    public GameFinalResult result;

}
