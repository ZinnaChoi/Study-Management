package mogakco.StudyManagement.service.stat;

import org.springframework.data.domain.Pageable;

import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.enums.LogType;

public interface StatService {
    StatGetRes getStat(LogType type, Pageable pageable);

    CommonRes createAbsentLog();

    CommonRes createWakeUpLog();

    CommonRes createEmptyWakeUpLog();
}
