package mogakco.StudyManagement.scheduler;

import org.springframework.context.annotation.Configuration;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.service.stat.StatService;

@Configuration
@EnableBatchProcessing
@Service
public class DailyWakeupScheduler {

    private final StatService statService;

    public DailyWakeupScheduler(StatService statService) {
        this.statService = statService;
    }

    @Scheduled(cron = "0 55 23 ? * MON-FRI")
    public void executeDailyLog() {
        try {
            statService.createEmptyWakeUpLog();
            System.out.println("일일 기상 로그가 저장되었습니다.");
        } catch (Exception e) {
            System.err.println("기상 로그 저장을 위한 배치 작업 도중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
