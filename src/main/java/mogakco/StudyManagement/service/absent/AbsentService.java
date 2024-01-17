package mogakco.StudyManagement.service.absent;

import mogakco.StudyManagement.dto.AbsentRgstReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.service.common.LoggingService;

public interface AbsentService {

    DTOResCommon registerAbsentSchedule(AbsentRgstReq absentRgstReq, LoggingService lo);

}
