package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.domain.DailyLog;

@Getter
@Setter
public class StatList {

    private String memberName;

    private String createdAt;

    private Integer score;

    private String date;

    public StatList(DailyLog dailyLog) {

        this.memberName = dailyLog.getMember().getName();
        this.score = dailyLog.getScore();
        this.createdAt = dailyLog.getCreatedAt();
        this.date = dailyLog.getDate();

    }

}