package mogakco.StudyManagement.service.notice.impl;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Notice;
import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeList;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.exception.UnauthorizedAccessException;
import mogakco.StudyManagement.repository.AbsentScheduleRepository;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.NoticeRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.external.EmailService;
import mogakco.StudyManagement.service.notice.NoticeService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final ScheduleRepository scheduleRepository;
    private final AbsentScheduleRepository absentScheduleRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final MemberRepository memberRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository, ScheduleRepository scheduleRepository,
            AbsentScheduleRepository absentScheduleRepository, MemberScheduleRepository memberScheduleRepository,
            MemberRepository memberRepository) {
        this.noticeRepository = noticeRepository;
        this.scheduleRepository = scheduleRepository;
        this.absentScheduleRepository = absentScheduleRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.memberRepository = memberRepository;

    }

    @Value("${study.systemId}")
    protected String systemId;

    @Autowired
    EmailService emailService;

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

    @Override
    @Transactional
    public DTOResCommon createGeneralNotice(LoggingService lo) {
        try {
            DTOResCommon result = new DTOResCommon();

            String formattedTime = LocalDateTime.now().plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm"));

            lo.setDBStart();
            Schedule upcomingSchedulesId = scheduleRepository.findScheduleIdMatchingStartTime(formattedTime);
            lo.setDBEnd();

            if (upcomingSchedulesId != null) {

                lo.setDBStart();
                List<Member> scheduleParticipants = memberScheduleRepository
                        .findMembersByScheduleId(upcomingSchedulesId.getScheduleId());
                lo.setDBEnd();

                List<Member> participants = new ArrayList<>(scheduleParticipants);

                for (Member participant : scheduleParticipants) {

                    lo.setDBStart();
                    List<Member> absentParticipants = absentScheduleRepository
                            .findAbsentParticipants(participant, DateUtil.getCurrentDateTime().substring(0, 8),
                                    upcomingSchedulesId);
                    lo.setDBEnd();

                    participants.removeAll(absentParticipants);
                }

                Long notifier = findNotifier(participants);

                lo.setDBStart();
                Optional<Member> notifierMember = memberRepository.findById(notifier);
                lo.setDBEnd();

                try {

                    Boolean linkShareValue = noticeRepository.findByMember_MemberId(notifier).get().getLinkShare();
                    if (linkShareValue != null && linkShareValue) {
                        emailService.sendEmail(notifierMember.get().getName(), "general",
                                notifierMember.get().getContact());
                        lo.setDBStart();
                    }

                    lo.setDBStart();
                    noticeRepository.updateLastShareDateByMemberId(DateUtil.getCurrentDateTime().substring(0, 12),
                            notifier);
                    lo.setDBEnd();

                    result.setRetCode(ErrorCode.OK.getCode());
                } catch (NotFoundException | UnauthorizedAccessException e) {
                    return ExceptionUtil.handleException(e);
                }
            }

            return result;
        } catch (NotFoundException | UnauthorizedAccessException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    public Long findNotifier(List<Member> participants) {
        List<Long> sortedMember = noticeRepository.findLastShareDateByMemberId();

        for (Long memberId : sortedMember) {
            for (Member participant : participants) {
                if (memberId.equals(participant.getMemberId())) {
                    return memberId;
                }
            }
        }

        return null;
    }

}