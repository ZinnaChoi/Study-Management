package mogakco.StudyManagement.service.absent.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;
import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.dto.AbsentList;
import mogakco.StudyManagement.dto.AbsentListReq;
import mogakco.StudyManagement.dto.AbsentListRes;
import mogakco.StudyManagement.dto.AbsentRgstReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.repository.AbsentScheduleRepository;
import mogakco.StudyManagement.repository.AbsentScheduleSpecification;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.exception.ConflictException;
import mogakco.StudyManagement.exception.InvalidRequestException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.service.absent.AbsentService;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.SecurityUtil;

@Service
public class AbsentServiceImpl implements AbsentService {

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final AbsentScheduleRepository absentScheduleRepository;

    public AbsentServiceImpl(MemberRepository memberRepository, ScheduleRepository scheduleRepository,
            MemberScheduleRepository memberScheduleRepository,
            AbsentScheduleRepository absentScheduleRepository) {
        this.memberRepository = memberRepository;
        this.scheduleRepository = scheduleRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.absentScheduleRepository = absentScheduleRepository;
    }

    @Override
    public DTOResCommon registerAbsentSchedule(AbsentRgstReq absentRgstReq, LoggingService lo) {
        try {
            lo.setDBStart();
            Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
            List<Schedule> scheduleList = scheduleRepository.findByEventNameIn(absentRgstReq.getEventNameList());
            List<MemberSchedule> memeberScheduleList = memberScheduleRepository.findAllByMember(member);
            lo.setDBEnd();

            // Check if the Event Name List is Empty
            if (absentRgstReq.getEventNameList().isEmpty()) {
                throw new InvalidRequestException("하나 이상의 부재 일정을 등록해야 합니다");
            }

            // Check if the Event Name exists
            Set<String> eventNameSet = new HashSet<>();
            for (Schedule schedule : scheduleList) {
                eventNameSet.add(schedule.getEventName());
            }
            for (String eventName : absentRgstReq.getEventNameList()) {
                if (!eventNameSet.contains(eventName)) {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("스터디 타임 " + eventName));

                }
            }

            // Validate member schedule
            Set<String> eventTimeSet = new HashSet<>();
            for (MemberSchedule ms : memeberScheduleList) {
                eventTimeSet.add(ms.getEventName().getEventName());
            }
            for (String eventTime : absentRgstReq.getEventNameList()) {
                if (!eventTimeSet.contains(eventTime)) {
                    throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("참여하지 않는 타임의 부재 신청은 할 수 없습니다"));
                }
            }

            // Check if the absent schedule already exists
            for (Schedule schedule : scheduleList) {
                Specification<AbsentSchedule> spec = AbsentScheduleSpecification.withAbsentDateAndScheduleAndMember(
                        absentRgstReq.getAbsentDate(), schedule, member);

                lo.setDBStart();
                long absentScheduleCnt = absentScheduleRepository.count(spec);
                lo.setDBEnd();
                if (absentScheduleCnt > 0) {
                    throw new ConflictException(ErrorCode.CONFLICT.getMessage("부재 일정: " +
                            absentRgstReq.getAbsentDate() + " " + schedule.getEventName()));
                }
            }

            // Register absent schedule
            for (Schedule schedule : scheduleList) {
                AbsentSchedule absentSchedule = AbsentSchedule.builder().absentDate(absentRgstReq.getAbsentDate())
                        .schedule(schedule).member(member).description(absentRgstReq.getDescription())
                        .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime()).build();

                lo.setDBStart();
                absentScheduleRepository.save(absentSchedule);
                lo.setDBEnd();
            }
            return new DTOResCommon(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        } catch (NotFoundException | InvalidRequestException | ConflictException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    @Override
    public AbsentListRes getAbsentSchedule(AbsentListReq absentListReq, LoggingService lo) {
        try {
            Specification<AbsentSchedule> spec = AbsentScheduleSpecification
                    .withYearMonth(absentListReq.getYearMonth());

            List<Member> members = new ArrayList<>();
            if (absentListReq.getMemberNameList() == null || absentListReq.getMemberNameList().isEmpty()) {
                lo.setDBStart();
                members = memberRepository.findAll();
                lo.setDBEnd();
            } else {
                for (String name : absentListReq.getMemberNameList()) {
                    lo.setDBStart();
                    Member member = memberRepository.findByName(name);
                    lo.setDBEnd();
                    if (member == null)
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("스터디원 " + name));
                    members.add(member);
                }
            }
            spec = spec.and(AbsentScheduleSpecification.withMemberIn(members));

            lo.setDBStart();
            List<AbsentSchedule> absentSchedules = absentScheduleRepository.findAll(spec);
            lo.setDBEnd();

            return new AbsentListRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), absentSchedules.stream()
                    .map(AbsentList::new)
                    .collect(Collectors.toList()));

        } catch (NotFoundException e) {
            DTOResCommon res = ExceptionUtil.handleException(e);
            return new AbsentListRes(res.getSystemId(), res.getRetCode(), res.getRetMsg(), null);
        }
    }
}
