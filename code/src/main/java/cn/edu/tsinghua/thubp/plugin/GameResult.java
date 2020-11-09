package cn.edu.tsinghua.thubp.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResult {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class GameRoundResult {
        @JsonProperty("score_a")
        private int scoreA;
        @JsonProperty("score_b")
        private int scoreB;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class GameFinalResult {
        @JsonProperty("winner")
        private String winner;
        @JsonProperty("output_a")
        private int outputA;
        @JsonProperty("output_b")
        private int outputB;
    }

    @JsonProperty("rounds")
    public GameRoundResult[] rounds;

    @JsonProperty("result")
    public GameFinalResult result;

}
