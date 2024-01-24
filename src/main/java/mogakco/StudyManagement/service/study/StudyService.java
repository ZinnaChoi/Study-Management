package mogakco.StudyManagement.service.study;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.StudyReq;
import mogakco.StudyManagement.service.common.LoggingService;

public interface StudyService {
        DTOResCommon createStudy(StudyReq studyCreateReq, MultipartFile imageFile, LoggingService lo)
                        throws IOException;

        DTOResCommon updateStudy(StudyReq studyCreateReq, MultipartFile imageFile, LoggingService lo)
                        throws IOException;

}
