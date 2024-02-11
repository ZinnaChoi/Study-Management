package mogakco.StudyManagement.service.notice;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.enums.MessageType;

public interface NoticeService {

    NoticeGetRes getNotice(Long memberId);

    CommonRes updateNotice(Long memberId, NoticeReq noticeReq);

    CommonRes createGeneralNotice();

    void createSpecificNotice(Member member, MessageType type);

}
