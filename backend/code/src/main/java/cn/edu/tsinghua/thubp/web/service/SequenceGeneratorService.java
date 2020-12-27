package cn.edu.tsinghua.thubp.web.service;

import cn.edu.tsinghua.thubp.web.entity.DatabaseSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 用于给 entity 增加递增 id 的服务
 * 比如 User 中就存在自增的 userId
 */
@Service
public class SequenceGeneratorService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public String generateSequence(String seqName) {
        DatabaseSequence counter = mongoTemplate.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? String.valueOf(counter.getSeq()) : "1";

    }
}
