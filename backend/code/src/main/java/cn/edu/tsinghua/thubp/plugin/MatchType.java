package cn.edu.tsinghua.thubp.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MatchType {
    private final String matchTypeId;
    private final String matchTypeName;
}
