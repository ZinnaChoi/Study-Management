package mogakco.StudyManagement.service.notice;

import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.service.common.LoggingService;

public interface NoticeService {

    NoticeGetRes getNotice(Long memberId, LoggingService lo);

    DTOResCommon updateNotice(Long memberId, NoticeReq noticeReq, LoggingService lo);

    DTOResCommon createGeneralNotice(LoggingService lo);

}
