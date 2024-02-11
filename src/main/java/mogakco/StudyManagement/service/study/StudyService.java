package mogakco.StudyManagement.service.study;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.StudyInfoRes;
import mogakco.StudyManagement.dto.StudyReq;

public interface StudyService {

        StudyInfoRes getStudy();

        CommonRes createStudy(StudyReq studyCreateReq, MultipartFile imageFile)
                        throws IOException;

        CommonRes updateStudy(StudyReq studyCreateReq, MultipartFile imageFile)
                        throws IOException;

        CommonRes deleteStudy(Long studyId);

}
