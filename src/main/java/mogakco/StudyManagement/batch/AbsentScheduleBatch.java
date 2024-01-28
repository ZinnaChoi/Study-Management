package mogakco.StudyManagement.batch;

import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.service.stat.StatService;

@Configuration
@EnableBatchProcessing
@Service
public class AbsentScheduleBatch {
    private final StatService statService;

    public AbsentScheduleBatch(StatService statService) {
        this.statService = statService;
    }

    @Scheduled(cron = "0 59 23 * * ?")
    public void executeDailyBatch() {
        try {
            statService.checkAbsentSchedulesAndLog(null);
            System.out.println("매일 23시 59분에 실행되는 배치 작업이 완료되었습니다.");
        } catch (Exception e) {
            System.err.println("배치 작업 도중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
