package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.domain.AbsentSchedule;

@Getter
@Setter
public class AbsentList {

    private String memberName;

    private String absentDate;

    private String eventName;

    private String description;

    private String createdAt;

    private String updatedAt;

    public AbsentList(AbsentSchedule absent) {
        this.memberName = absent.getMember().getName();
        this.absentDate = absent.getAbsentDate();
        this.eventName = absent.getSchedule().getEventName();
        this.description = absent.getDescription();
        this.createdAt = absent.getCreatedAt();
        this.updatedAt = absent.getUpdatedAt();
    }

}
