package cn.edu.tsinghua.thubp.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleResponse {
    public static final String OK = "ok";
    private String message;

    public SimpleResponse() {
        this.message = OK;
    }
}
