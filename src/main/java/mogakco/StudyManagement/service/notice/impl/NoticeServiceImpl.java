package mogakco.StudyManagement.service.notice.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Notice;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeList;
import mogakco.StudyManagement.dto.NoticeReq;
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

    @Value("${study.systemId}")
    protected String systemId;

    @Override
    public NoticeGetRes getNotice(Long memberId, LoggingService lo) {

        List<Notice> notice;

        lo.setDBStart();
        notice = noticeRepository.findByMember_MemberId(memberId);
        lo.setDBEnd();

        List<NoticeList> noticeLists = notice.stream().map(NoticeList::new).collect(Collectors.toList());

        if (noticeLists.size() == 0) {
            return new NoticeGetRes(systemId, ErrorCode.NOT_FOUND.getCode(),
                    ErrorCode.NOT_FOUND.getMessage("memberId"), null);
        }

        return new NoticeGetRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), noticeLists);

    }

    @Override
    @Transactional
    public DTOResCommon updateNotice(Long memberId, NoticeReq noticeReq, LoggingService lo) {

        try {
            DTOResCommon result = new DTOResCommon();
            lo.setDBStart();
            List<Notice> notices = noticeRepository.findByMember_MemberId(memberId);

            if (!notices.isEmpty()) {
                Notice notice = notices.get(0);

                if (notice.isNoticeChanged(noticeReq)) {
                    notice.updateWakeup(noticeReq.getWakeup());
                    notice.updateAbsent(noticeReq.getAbsent());
                    notice.updateNewPost(noticeReq.getNewPost());
                    notice.updateLinkShare(noticeReq.getLinkShare());
                    lo.setDBStart();
                    noticeRepository.save(notice);
                    lo.setDBEnd();
                    result.setRetCode(ErrorCode.OK.getCode());
                    result.setRetMsg(ErrorCode.OK.getMessage("알림 상태"));
                } else {
                    result.setRetCode(ErrorCode.UNCHANGED.getCode());
                    result.setRetMsg(ErrorCode.UNCHANGED.getMessage("알림 상태"));
                }
            } else {
                result.setRetCode(ErrorCode.NOT_FOUND.getCode());
                result.setRetMsg(ErrorCode.NOT_FOUND.getMessage("memberId"));
            }
            return result;
        } catch (Exception e) {
            return new DTOResCommon(systemId, ErrorCode.NOT_FOUND.getCode(), ErrorCode.NOT_FOUND.getMessage());
        }

    }

}