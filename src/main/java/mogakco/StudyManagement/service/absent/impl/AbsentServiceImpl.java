package mogakco.StudyManagement.service.absent.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;
import mogakco.StudyManagement.domain.Post;
import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.dto.AbsentCalendar;
import mogakco.StudyManagement.dto.AbsentCalendarReq;
import mogakco.StudyManagement.dto.AbsentCalendarRes;
import mogakco.StudyManagement.dto.AbsentDetail;
import mogakco.StudyManagement.dto.AbsentDetailReq;
import mogakco.StudyManagement.dto.AbsentDetailRes;
import mogakco.StudyManagement.dto.AbsentReq;
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
    @Transactional
    public DTOResCommon registerAbsentSchedule(AbsentReq absentReq, LoggingService lo) {
        try {
            lo.setDBStart();
            Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
            List<Schedule> scheduleList = scheduleRepository.findByEventNameIn(absentReq.getEventNameList());
            List<MemberSchedule> memeberScheduleList = memberScheduleRepository.findAllByMember(member);
            lo.setDBEnd();

            // Check if the Event Name List is Empty
            if (absentReq.getEventNameList().isEmpty()) {
                throw new InvalidRequestException("하나 이상의 부재 일정을 등록해야 합니다");
            }

            validateEventNames(scheduleList, absentReq.getEventNameList());

            // Validate member schedule
            Set<String> eventTimeSet = new HashSet<>();
            for (MemberSchedule ms : memeberScheduleList) {
                eventTimeSet.add(ms.getEventName().getEventName());
            }
            for (String eventTime : absentReq.getEventNameList()) {
                if (!eventTimeSet.contains(eventTime)) {
                    throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("참여하지 않는 타임의 부재 신청은 할 수 없습니다"));
                }
            }

            // Check if the absent schedule already exists
            for (Schedule schedule : scheduleList) {
                Specification<AbsentSchedule> spec = AbsentScheduleSpecification.withAbsentDateAndScheduleAndMember(
                        absentReq.getAbsentDate(), schedule, member);

                lo.setDBStart();
                long absentScheduleCnt = absentScheduleRepository.count(spec);
                lo.setDBEnd();
                if (absentScheduleCnt > 0) {
                    throw new ConflictException(ErrorCode.CONFLICT.getMessage("부재 일정: " +
                            absentReq.getAbsentDate() + " " + schedule.getEventName()));
                }
            }

            // Register absent schedule
            for (Schedule schedule : scheduleList) {
                AbsentSchedule absentSchedule = AbsentSchedule.builder().absentDate(absentReq.getAbsentDate())
                        .schedule(schedule).member(member).description(absentReq.getDescription())
                        .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime()).build();

                lo.setDBStart();
                absentScheduleRepository.save(absentSchedule);
                lo.setDBEnd();
            }
            return new DTOResCommon(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage());
        } catch (NotFoundException | InvalidRequestException | ConflictException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    @Override
    public AbsentCalendarRes getAbsentScheduleByMonth(AbsentCalendarReq absentCalendarReq, LoggingService lo) {
        try {
            Specification<AbsentSchedule> spec = AbsentScheduleSpecification
                    .withYearMonth(absentCalendarReq.getYearMonth());

            List<Member> members = new ArrayList<>();
            if (absentCalendarReq.getMemberNameList() == null || absentCalendarReq.getMemberNameList().isEmpty()) {
                lo.setDBStart();
                members = memberRepository.findAll();
                lo.setDBEnd();
            } else {
                for (String name : absentCalendarReq.getMemberNameList()) {
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
            // Get AbsentSchedules By YearMonth and MemberNameList
            List<AbsentSchedule> absentSchedules = absentScheduleRepository.findAll(spec);
            lo.setDBEnd();

            Map<String, AbsentCalendar> groupedSchedules = new HashMap<>();
            for (AbsentSchedule schedule : absentSchedules) {
                // Group By AbsentDate & EventName
                String key = schedule.getAbsentDate() + "-" + schedule.getSchedule().getEventName();

                AbsentCalendar calendarList = groupedSchedules.getOrDefault(key,
                        new AbsentCalendar(schedule));
                calendarList.addMemberName(schedule.getMember().getName());

                groupedSchedules.put(key, calendarList);
            }

            // Sort By AbsentDate, EventName
            List<AbsentCalendar> sortedCalendarList = new ArrayList<>(groupedSchedules.values());
            Collections.sort(sortedCalendarList, (o1, o2) -> {
                int dateCompare = o1.getAbsentDate().compareTo(o2.getAbsentDate());
                if (dateCompare != 0) {
                    return dateCompare;
                } else {
                    return o1.getEventName().compareTo(o2.getEventName());
                }
            });

            return new AbsentCalendarRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), sortedCalendarList);

        } catch (NotFoundException e) {
            DTOResCommon res = ExceptionUtil.handleException(e);
            return new AbsentCalendarRes(res.getSystemId(), res.getRetCode(), res.getRetMsg(), null);
        }
    }

    @Override
    public AbsentDetailRes getAbsentScheduleDetail(AbsentDetailReq absentDetailReq, LoggingService lo) {

        List<AbsentSchedule> absentSchedules = new ArrayList<>();
        Specification<AbsentSchedule> spec = AbsentScheduleSpecification
                .withAbsentDate(absentDetailReq.getAbsentDate());

        lo.setDBStart();
        absentSchedules = absentScheduleRepository.findAll(spec);
        lo.setDBEnd();

        Map<String, AbsentDetail> groupedSchedules = new HashMap<>();
        for (AbsentSchedule schedule : absentSchedules) {
            String memberName = schedule.getMember().getName();

            AbsentDetail detail = groupedSchedules.getOrDefault(memberName, new AbsentDetail(schedule));
            detail.addEventName(schedule.getSchedule().getEventName());

            groupedSchedules.put(memberName, detail);
        }

        return new AbsentDetailRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(),
                new ArrayList<>(groupedSchedules.values()));

    }

    @Override
    @Transactional
    public DTOResCommon updateAbsentSchedule(AbsentReq absentReq, LoggingService lo) {

        DTOResCommon result = new DTOResCommon();
        try {
            if (absentReq.getEventNameList().isEmpty()) {
                throw new InvalidRequestException("하나 이상의 스터디 시간을 선택해야 합니다");
            }

            lo.setDBStart();
            Member loginMember = memberRepository.findById(SecurityUtil.getLoginUserId());
            lo.setDBEnd();

            Specification<AbsentSchedule> spec = AbsentScheduleSpecification.withAbsentDate(absentReq.getAbsentDate())
                    .and(AbsentScheduleSpecification
                            .withAbsentDateAndMember(absentReq.getAbsentDate(), loginMember));

            lo.setDBStart();
            List<Schedule> scheduleList = scheduleRepository.findByEventNameIn(absentReq.getEventNameList());
            List<AbsentSchedule> absentScheduleList = absentScheduleRepository.findAll(spec);
            lo.setDBEnd();

            validateEventNames(scheduleList, absentReq.getEventNameList());

            Set<String> existEventNames = absentScheduleList.stream()
                    .map(as -> as.getSchedule().getEventName())
                    .collect(Collectors.toSet());
            Set<String> reqEventNames = new HashSet<>(absentReq.getEventNameList());

            boolean isDescriptionChanged = absentScheduleList.get(0).isDescriptionChanged(absentReq);
            boolean isEventNameListChanged = !existEventNames.equals(reqEventNames);

            if (isDescriptionChanged || isEventNameListChanged) {

                Set<String> removedEventNames = new HashSet<>(existEventNames);
                removedEventNames.removeAll(reqEventNames);

                Set<String> addedEventNames = new HashSet<>(reqEventNames);
                addedEventNames.removeAll(existEventNames);

                for (AbsentSchedule schedule : absentScheduleList) {
                    schedule.updateUpdatedAt(DateUtil.getCurrentDateTime());
                    if (isDescriptionChanged) {
                        schedule.updateDescription(absentReq.getDescription());
                        lo.setDBStart();
                        absentScheduleRepository.save(schedule);
                        lo.setDBEnd();
                    }

                    // EventNames to be Removed
                    if (removedEventNames.contains(schedule.getSchedule().getEventName())) {
                        lo.setDBStart();
                        absentScheduleRepository.delete(schedule);
                        lo.setDBEnd();
                    }

                }

                // Add new EventName
                for (String addedEventName : addedEventNames) {
                    lo.setDBStart();
                    Schedule addedSchedule = scheduleRepository.findByEventName(addedEventName);
                    lo.setDBEnd();

                    AbsentSchedule newSchedule = AbsentSchedule.builder().absentDate(absentReq.getAbsentDate())
                            .schedule(addedSchedule).member(loginMember).description(absentReq.getDescription())
                            .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime()).build();

                    lo.setDBStart();
                    absentScheduleRepository.save(newSchedule);
                    lo.setDBEnd();
                }

                result.setRetCode(ErrorCode.OK.getCode());
                result.setRetMsg(ErrorCode.OK.getMessage());

            } else {
                result.setRetCode(ErrorCode.UNCHANGED.getCode());
                result.setRetMsg(ErrorCode.UNCHANGED.getMessage("부재 일정"));
            }
            return result;

        } catch (InvalidRequestException | NotFoundException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    private void validateEventNames(List<Schedule> scheduleList, List<String> eventNameList) {
        Set<String> eventNameSet = scheduleList.stream()
                .map(Schedule::getEventName)
                .collect(Collectors.toSet());

        for (String eventName : eventNameList) {
            if (!eventNameSet.contains(eventName)) {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("스터디 타임 " + eventName));
            }
        }
    }

}
