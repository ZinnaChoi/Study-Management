package mogakco.StudyManagement.service.stat;

import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.enums.LogType;

public interface StatService {
    StatGetRes getStat(LogType type, String startDate, String endDate);

    CommonRes createAbsentLog();

    CommonRes createWakeUpLog();
}
