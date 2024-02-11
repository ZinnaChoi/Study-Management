package mogakco.StudyManagement.service.absent;

import mogakco.StudyManagement.dto.AbsentCalendarReq;
import mogakco.StudyManagement.dto.AbsentCalendarRes;
import mogakco.StudyManagement.dto.AbsentDetailReq;
import mogakco.StudyManagement.dto.AbsentDetailRes;
import mogakco.StudyManagement.dto.AbsentReq;
import mogakco.StudyManagement.dto.CommonRes;

public interface AbsentService {

    CommonRes registerAbsentSchedule(AbsentReq absentReq);

    AbsentCalendarRes getAbsentScheduleByMonth(AbsentCalendarReq absentCalendarReq);

    AbsentDetailRes getAbsentScheduleDetail(AbsentDetailReq absentDetailReq);

    CommonRes updateAbsentSchedule(AbsentReq absentReq);

    CommonRes deleteAbsentSchedule(String absentDate);

}
