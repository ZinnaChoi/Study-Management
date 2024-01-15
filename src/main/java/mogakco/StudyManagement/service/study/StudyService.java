package mogakco.StudyManagement.service.study;

import mogakco.StudyManagement.dto.StudyCreateReq;
import mogakco.StudyManagement.service.common.LoggingService;

public interface StudyService {
    void createStudy(StudyCreateReq studyCreateReq, LoggingService lo);

}
