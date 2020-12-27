package cn.edu.tsinghua.thubp.bulletin.service;

import cn.edu.tsinghua.thubp.bulletin.entity.BulletinEntry;
import cn.edu.tsinghua.thubp.match.entity.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BulletinService {
    public final int MAX_BULLETIN_SIZE = 4;

    private final MongoTemplate mongoTemplate;


    public void update() {
        // 清空 bulletin
        mongoTemplate.dropCollection(BulletinEntry.class);
        _update();
    }

    public List<BulletinEntry> getAll() {
        return mongoTemplate.findAll(BulletinEntry.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void _update() {
        List<Match> matches = mongoTemplate.findAll(Match.class);
        PriorityQueue<BulletinEntry> queue = new PriorityQueue<>(
                Comparator.comparing(BulletinEntry::getHotValue)
        );
        for (Match match : matches) {
            // 不公开查询的比赛不进入
            if (!match.getPublicShowUp()) {
                continue;
            }
            BulletinEntry e = fromMatch(match);
            BulletinEntry m = queue.peek();
            if (Objects.isNull(m) || queue.size() < MAX_BULLETIN_SIZE) {
                queue.offer(e);
            } else if (m.getHotValue() < e.getHotValue()) {
                queue.poll();
                queue.offer(e);
            }
        }
        PriorityQueue<BulletinEntry> maxHeap = new PriorityQueue<>(
                (o1, o2) -> -(o1.getHotValue().compareTo(o2.getHotValue()))
        );
        maxHeap.addAll(queue);
        for (BulletinEntry e : maxHeap) {
            mongoTemplate.save(e);
        }
    }

    private static BulletinEntry fromMatch(Match match) {
        // 计算热度
        int hot = match.getParticipants().size();
        // 创建
        return BulletinEntry.builder()
                .matchId(match.getMatchId())
                .hotValue(hot)
                .build();
    }
}
