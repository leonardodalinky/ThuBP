package cn.edu.tsinghua.thubp.schedule.timer;

import cn.edu.tsinghua.thubp.bulletin.service.BulletinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BulletinTimer {
    private final BulletinService bulletinService;

    @Scheduled(cron = "0 0/10 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void autoUpdate() {
        log.info("定期更新走马灯。");
        bulletinService.update();
    }
}
