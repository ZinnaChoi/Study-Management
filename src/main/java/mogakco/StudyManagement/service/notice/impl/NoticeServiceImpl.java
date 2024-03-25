package mogakco.StudyManagement.service.notice.impl;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.Notice;
import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.enums.ErrorCode;

import mogakco.StudyManagement.enums.MessageType;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.repository.AbsentScheduleRepository;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.NoticeRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.spec.NoticeSpecification;
import mogakco.StudyManagement.service.external.SendEmailService;
import mogakco.StudyManagement.service.notice.NoticeService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.SecurityUtil;

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

    protected Member getLoginMember() {
        return memberRepository.findById(SecurityUtil.getLoginUserId());
    }

    @Value("${study.systemId}")
    protected String systemId;

    @Autowired
    SendEmailService sendEmailService;

    @Override
    public NoticeGetRes getNotice() {

        Member member = getLoginMember();
        Notice NoticeStat = noticeRepository.findByMember(member);

        if (NoticeStat != null) {
            return new NoticeGetRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), NoticeStat);
        } else {
            return new NoticeGetRes(systemId, ErrorCode.NOT_FOUND.getCode(),
                    ErrorCode.NOT_FOUND.getMessage(member.getName() + "의 알림 상태"), null);
        }
    }

    @Override
    @Transactional
    public CommonRes updateNotice(NoticeReq noticeReq) {
        CommonRes result = new CommonRes();

        Member member = getLoginMember();
        Notice noticeStat = noticeRepository.findByMember(member);

        if (noticeStat != null) {
            if (noticeStat.isNoticeChanged(noticeReq)) {
                noticeRepository.save(noticeStat.updateNotice(noticeReq));
                return new CommonRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage("알림 상태"));
            } else {
                return new CommonRes(null, ErrorCode.UNCHANGED.getCode(), ErrorCode.UNCHANGED.getMessage("알림 상태"));
            }
        }

        return result;
    }

    @Override
    @Transactional
    public CommonRes createGeneralNotice() {

        try {
            String formattedTime = LocalDateTime.now().plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm"));

            Schedule upcomingSchedulesId = scheduleRepository.findScheduleIdMatchingStartTime(formattedTime);

            if (upcomingSchedulesId != null) {

                List<Member> scheduleParticipants = memberScheduleRepository
                        .findMembersByScheduleId(upcomingSchedulesId.getScheduleId());
                List<Member> participants = new ArrayList<>(scheduleParticipants);

                for (Member participant : scheduleParticipants) {
                    List<Member> absentParticipants = absentScheduleRepository.findAbsentParticipants(participant,
                            DateUtil.getCurrentDateTime().substring(0, 8), upcomingSchedulesId);
                    participants.removeAll(absentParticipants);
                }

                Long notifier = findNotifier(participants);
                Optional<Member> notifierMember = memberRepository.findById(notifier);

                Boolean linkShareValue = noticeRepository.findByMember_MemberId(notifier).get().getLinkShare();
                if (linkShareValue != null && linkShareValue) {
                    sendEmailService.sendEmail(notifierMember.get().getName(), MessageType.GENERAL,
                            notifierMember.get().getEmail());
                }
                noticeRepository.updateLastShareDateByMemberId(DateUtil.getCurrentDateTime().substring(0, 12),
                        notifier);

                return new CommonRes(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("구글 미트 링크 알림"));
            } else {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("스터디"));
            }
        } catch (NotFoundException e) {
            return ExceptionUtil.handleException(new NotFoundException(e.getMessage()));

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

    public void createSpecificNotice(Member member, MessageType type) {

        Specification<Notice> spec = NoticeSpecification.matchMessageType(type);

        List<Notice> targetedNotice = noticeRepository.findAll(spec);

        for (Notice notice : targetedNotice) {
            Member targetMember = memberRepository.findById(notice.getMember().getId());
            sendEmailService.sendEmail(member.getName(), type, targetMember.getEmail());
        }

    }

}