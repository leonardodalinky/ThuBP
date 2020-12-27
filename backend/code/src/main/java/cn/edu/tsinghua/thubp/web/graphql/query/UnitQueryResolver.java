package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.service.MatchService;
import cn.edu.tsinghua.thubp.match.service.UnitService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UnitQueryResolver implements GraphQLQueryResolver {
    private final UnitService unitService;

    public Unit findUnitById(String unitId) {
        return unitService.findByUnitId(unitId);
    }

    public List<Unit> findUnits(List<String> unitIds) {
        return unitService.findByUnitIds(unitIds);
    }
}
