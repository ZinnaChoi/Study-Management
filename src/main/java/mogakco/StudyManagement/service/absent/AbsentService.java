package mogakco.StudyManagement.service.absent;

import mogakco.StudyManagement.dto.AbsentListReq;
import mogakco.StudyManagement.dto.AbsentListRes;
import mogakco.StudyManagement.dto.AbsentRgstReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.service.common.LoggingService;

public interface AbsentService {

    DTOResCommon registerAbsentSchedule(AbsentRgstReq absentRgstReq, LoggingService lo);

    AbsentListRes getAbsentSchedule(AbsentListReq absentListReq, LoggingService lo);
}
