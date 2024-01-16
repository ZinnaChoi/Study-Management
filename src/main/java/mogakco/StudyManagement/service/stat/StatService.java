package mogakco.StudyManagement.service.stat;

import org.springframework.data.domain.Pageable;

import mogakco.StudyManagement.dto.StatGetReq;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.service.common.LoggingService;

public interface StatService {
    StatGetRes getStat(StatGetReq statGetReq, LogType type, LoggingService lo, Pageable pageable);
}
