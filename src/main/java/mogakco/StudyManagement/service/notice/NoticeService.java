package mogakco.StudyManagement.service.notice;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.enums.MessageType;
import mogakco.StudyManagement.service.common.LoggingService;

public interface NoticeService {

    NoticeGetRes getNotice(Long memberId, LoggingService lo);

    CommonRes updateNotice(Long memberId, NoticeReq noticeReq, LoggingService lo);

    CommonRes createGeneralNotice(LoggingService lo);

    void createSpecificNotice(Member member, MessageType type, LoggingService lo);

}
