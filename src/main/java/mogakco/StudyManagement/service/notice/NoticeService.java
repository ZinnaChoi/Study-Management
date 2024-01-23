package mogakco.StudyManagement.service.notice;

import mogakco.StudyManagement.dto.NoticeGetRes;

import mogakco.StudyManagement.service.common.LoggingService;

public interface NoticeService {

    NoticeGetRes getNotice(Long memberId, LoggingService lo);
}
