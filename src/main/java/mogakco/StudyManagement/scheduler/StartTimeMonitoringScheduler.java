package mogakco.StudyManagement.scheduler;

import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.service.notice.NoticeService;

@Configuration
@EnableBatchProcessing
@Service
public class StartTimeMonitoringScheduler {

    private final NoticeService noticeService;

    @Value("${scheduling.interval}")
    private long schedulingInterval;

    public StartTimeMonitoringScheduler(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Scheduled(fixedRateString = "${scheduling.interval}")
    public void executeGeneralNotice() {
        try {
            noticeService.createGeneralNotice();
        } catch (Exception e) {
            System.err.println("구글 미트 링크 생성을 위한 스케줄 모니터링 작업 도중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
