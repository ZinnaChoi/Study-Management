package mogakco.StudyManagement.service.absent;

import mogakco.StudyManagement.dto.AbsentCalendarReq;
import mogakco.StudyManagement.dto.AbsentCalendarRes;
import mogakco.StudyManagement.dto.AbsentDelReq;
import mogakco.StudyManagement.dto.AbsentDetailReq;
import mogakco.StudyManagement.dto.AbsentDetailRes;
import mogakco.StudyManagement.dto.AbsentReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.service.common.LoggingService;

public interface AbsentService {

    DTOResCommon registerAbsentSchedule(AbsentReq absentReq, LoggingService lo);

    AbsentCalendarRes getAbsentScheduleByMonth(AbsentCalendarReq absentCalendarReq, LoggingService lo);

    AbsentDetailRes getAbsentScheduleDetail(AbsentDetailReq absentDetailReq, LoggingService lo);

    DTOResCommon updateAbsentSchedule(AbsentReq absentReq, LoggingService lo);

    DTOResCommon deleteAbsentSchedule(AbsentDelReq absentDelReq, LoggingService lo);

}
