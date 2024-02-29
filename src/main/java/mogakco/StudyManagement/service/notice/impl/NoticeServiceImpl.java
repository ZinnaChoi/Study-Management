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
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.enums.ErrorCode;

import mogakco.StudyManagement.enums.MessageType;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.exception.UnauthorizedAccessException;
import mogakco.StudyManagement.repository.AbsentScheduleRepository;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.NoticeRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
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
        Notice NoticeStat = noticeRepository.findByMember(member);

        if (NoticeStat != null) {

            if (NoticeStat.isNoticeChanged(noticeReq)) {
                noticeRepository.save(NoticeStat.updateNotice(noticeReq));
                result.setRetCode(ErrorCode.OK.getCode());
                result.setRetMsg(ErrorCode.OK.getMessage("알림 상태"));
            } else {
                result.setRetCode(ErrorCode.UNCHANGED.getCode());
                result.setRetMsg(ErrorCode.UNCHANGED.getMessage("알림 상태"));
            }
        } else {
            result.setRetCode(ErrorCode.NOT_FOUND.getCode());
            result.setRetMsg(ErrorCode.NOT_FOUND.getMessage(member.getName() + "의 알림 상태"));
        }
        return result;

    }

    @Override
    @Transactional
    public CommonRes createGeneralNotice() {

        CommonRes result = new CommonRes();

        String formattedTime = LocalDateTime.now().plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm"));

        Schedule upcomingSchedulesId = scheduleRepository.findScheduleIdMatchingStartTime(formattedTime);

        if (upcomingSchedulesId != null) {
            List<Member> scheduleParticipants = memberScheduleRepository
                    .findMembersByScheduleId(upcomingSchedulesId.getScheduleId());
            List<Member> participants = new ArrayList<>(scheduleParticipants);

            for (Member participant : scheduleParticipants) {
                List<Member> absentParticipants = absentScheduleRepository
                        .findAbsentParticipants(participant, DateUtil.getCurrentDateTime().substring(0, 8),
                                upcomingSchedulesId);
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

            result.setRetCode(ErrorCode.OK.getCode());
            result.setRetMsg(ErrorCode.INTERNAL_ERROR.getMessage("구글 미트 링크 알림 생성을 생성하였습니다."));

        } else {
            result.setRetCode(ErrorCode.INTERNAL_ERROR.getCode());
            result.setRetMsg(ErrorCode.INTERNAL_ERROR.getMessage("구글 미트 링크 알림 생성에 실패하였습니다."));
        }
        return result;

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

        List<Long> targetedMembers = new ArrayList<>();
        try {
            switch (type) {
                case ABSENT:
                    targetedMembers = noticeRepository.findMemberIdByAbsentIsTrue();
                    break;
                case NEW_POST:
                    targetedMembers = noticeRepository.findByNewPostTrue();
                    break;
                case WAKE_UP:
                    targetedMembers = noticeRepository.findByWakeupTrue();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Long targetedMemberId : targetedMembers) {
            Optional<Member> mmember = memberRepository.findById(targetedMemberId);
            sendEmailService.sendEmail(member.getName(), type, mmember.get().getEmail());
        }

    }

}