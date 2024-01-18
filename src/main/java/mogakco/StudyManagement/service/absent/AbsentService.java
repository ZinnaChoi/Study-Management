package mogakco.StudyManagement.service.absent;

import mogakco.StudyManagement.dto.AbsentCalendarReq;
import mogakco.StudyManagement.dto.AbsentCalendarRes;
import mogakco.StudyManagement.dto.AbsentDetailReq;
import mogakco.StudyManagement.dto.AbsentDetailRes;
import mogakco.StudyManagement.dto.AbsentRgstReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.service.common.LoggingService;

public interface AbsentService {

    DTOResCommon registerAbsentSchedule(AbsentRgstReq absentRgstReq, LoggingService lo);

    AbsentCalendarRes getAbsentScheduleByMonth(AbsentCalendarReq absentCalendarReq, LoggingService lo);

    AbsentDetailRes getAbsentScheduleDetail(AbsentDetailReq absentDetailReq, LoggingService lo);
}
