package mogakco.StudyManagement.service.notice.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.Notice;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeList;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.repository.NoticeRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.notice.NoticeService;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;

    }

    @Override
    public NoticeGetRes getNotice(Long memberId, LoggingService lo) {

        List<Notice> notice;

        lo.setDBStart();
        notice = noticeRepository.findByMember_MemberId(memberId);
        lo.setDBEnd();

        List<NoticeList> noticeLists = notice.stream().map(NoticeList::new).collect(Collectors.toList());

        return new NoticeGetRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), noticeLists);

    }

}
