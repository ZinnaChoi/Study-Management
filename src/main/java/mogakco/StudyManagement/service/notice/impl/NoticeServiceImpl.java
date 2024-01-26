package mogakco.StudyManagement.service.notice.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Notice;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeList;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.exception.UnauthorizedAccessException;
import mogakco.StudyManagement.repository.NoticeRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.notice.NoticeService;
import mogakco.StudyManagement.util.ExceptionUtil;

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

        lo.setDBStart();
        Optional<Notice> noticeOptional = noticeRepository.findByMember_MemberId(memberId);
        lo.setDBEnd();

        if (noticeOptional.isPresent()) {
            Notice notice = noticeOptional.get();
            NoticeList noticeList = new NoticeList(notice);
            return new NoticeGetRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), List.of(noticeList));
        } else {
            return new NoticeGetRes(systemId, ErrorCode.NOT_FOUND.getCode(),
                    ErrorCode.NOT_FOUND.getMessage("memberId"), null);
        }
    }

    @Override
    @Transactional
    public DTOResCommon updateNotice(Long memberId, NoticeReq noticeReq, LoggingService lo) {

        try {
            DTOResCommon result = new DTOResCommon();
            lo.setDBStart();
            Optional<Notice> noticeOptional = noticeRepository.findByMember_MemberId(memberId);

            if (noticeOptional.isPresent()) {
                Notice notice = noticeOptional.get();

                if (notice.isNoticeChanged(noticeReq)) {
                    lo.setDBStart();
                    noticeRepository.save(notice.updateNotice(noticeReq));
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
        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }
    }

}