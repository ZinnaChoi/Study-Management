package mogakco.StudyManagement.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyInfoRes extends DTOResCommon {

    private Long studyId;

    private String studyName;

    private byte[] logo;

    private List<ScheduleRes> schedules;

    public StudyInfoRes(String systemId, int retCode, String retMsg, Long studyId, String studyName, byte[] logo,
            List<ScheduleRes> schedules) {
        super(systemId, retCode, retMsg);
        this.studyId = studyId;
        this.studyName = studyName;
        this.logo = logo;
        this.schedules = schedules;
    }
}
