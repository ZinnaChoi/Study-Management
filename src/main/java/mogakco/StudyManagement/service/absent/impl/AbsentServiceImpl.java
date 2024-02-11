package mogakco.StudyManagement.service.absent.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.AbsentSchedule;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;
import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.dto.AbsentCalendar;
import mogakco.StudyManagement.dto.AbsentCalendarReq;
import mogakco.StudyManagement.dto.AbsentCalendarRes;
import mogakco.StudyManagement.dto.AbsentDetail;
import mogakco.StudyManagement.dto.AbsentDetailReq;
import mogakco.StudyManagement.dto.AbsentDetailRes;
import mogakco.StudyManagement.dto.AbsentReq;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.MessageType;
import mogakco.StudyManagement.repository.AbsentScheduleRepository;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.spec.AbsentScheduleSpecification;
import mogakco.StudyManagement.exception.ConflictException;
import mogakco.StudyManagement.exception.InvalidRequestException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.service.absent.AbsentService;
import mogakco.StudyManagement.service.notice.NoticeService;
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

    @Autowired
    NoticeService noticeService;

    @Override
    @Transactional
    public CommonRes registerAbsentSchedule(AbsentReq absentReq) {
        try {
            Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
            List<Schedule> scheduleList = scheduleRepository.findAllByScheduleNameIn(absentReq.getScheduleNameList());
            List<MemberSchedule> memeberScheduleList = memberScheduleRepository.findAllByMember(member);

            // Check if the Schedule Name List is Empty
            if (absentReq.getScheduleNameList().isEmpty()) {
                throw new InvalidRequestException("하나 이상의 부재 일정을 등록해야 합니다");
            }

            validateScheduleNames(scheduleList, absentReq.getScheduleNameList());

            // Validate member schedule
            Set<String> ScheduleTimeSet = new HashSet<>();
            for (MemberSchedule ms : memeberScheduleList) {
                ScheduleTimeSet.add(ms.getSchedule().getScheduleName());
            }
            for (String scheduleTime : absentReq.getScheduleNameList()) {
                if (!ScheduleTimeSet.contains(scheduleTime)) {
                    throw new InvalidRequestException(ErrorCode.BAD_REQUEST.getMessage("참여하지 않는 타임의 부재 신청은 할 수 없습니다"));
                }
            }

            // Check if the absent schedule already exists
            for (Schedule schedule : scheduleList) {
                long absentScheduleCnt = absentScheduleRepository
                        .countByAbsentDateAndScheduleAndMember(absentReq.getAbsentDate(), schedule, member);

                if (absentScheduleCnt > 0) {
                    throw new ConflictException(ErrorCode.CONFLICT.getMessage("부재 일정: " +
                            absentReq.getAbsentDate() + " " + schedule.getScheduleName()));
                }
            }

            // Register absent schedule
            for (Schedule schedule : scheduleList) {
                AbsentSchedule absentSchedule = AbsentSchedule.builder().absentDate(absentReq.getAbsentDate())
                        .schedule(schedule).member(member).description(absentReq.getDescription())
                        .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime()).build();
                absentScheduleRepository.save(absentSchedule);

            }
            noticeService.createSpecificNotice(member, MessageType.ABSENT);

            return new CommonRes(null, ErrorCode.CREATED.getCode(), ErrorCode.CREATED.getMessage("부재 일정"));
        } catch (NotFoundException | InvalidRequestException | ConflictException e) {
            return ExceptionUtil.handleException(e);
        }

    }

    @Override
    public AbsentCalendarRes getAbsentScheduleByMonth(AbsentCalendarReq absentCalendarReq) {
        try {
            Specification<AbsentSchedule> spec = AbsentScheduleSpecification
                    .withYearMonth(absentCalendarReq.getYearMonth());

            List<Member> members = new ArrayList<>();
            if (absentCalendarReq.getMemberNameList() == null || absentCalendarReq.getMemberNameList().isEmpty()) {
                members = memberRepository.findAll();
            } else {
                for (String name : absentCalendarReq.getMemberNameList()) {
                    Member member = memberRepository.findByName(name);
                    if (member == null)
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("스터디원 " + name));
                    members.add(member);
                }
            }
            spec = spec.and(AbsentScheduleSpecification.withMemberIn(members));

            // Get AbsentSchedules By YearMonth and MemberNameList
            List<AbsentSchedule> absentSchedules = absentScheduleRepository.findAll(spec);

            Map<String, AbsentCalendar> groupedSchedules = new HashMap<>();
            for (AbsentSchedule schedule : absentSchedules) {
                // Group By AbsentDate & ScheduleName
                String key = schedule.getAbsentDate() + "-" + schedule.getSchedule().getScheduleName();
                AbsentCalendar calendarList = groupedSchedules.getOrDefault(key,
                        new AbsentCalendar(schedule));
                calendarList.addMemberName(schedule.getMember().getName());

                groupedSchedules.put(key, calendarList);
            }

            // Sort By AbsentDate, ScheduleName
            List<AbsentCalendar> sortedCalendarList = new ArrayList<>(groupedSchedules.values());
            Collections.sort(sortedCalendarList, (o1, o2) -> {
                int dateCompare = o1.getAbsentDate().compareTo(o2.getAbsentDate());
                if (dateCompare != 0) {
                    return dateCompare;
                } else {
                    return o1.getScheduleName().compareTo(o2.getScheduleName());
                }
            });

            return new AbsentCalendarRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), sortedCalendarList);

        } catch (NotFoundException e) {
            CommonRes res = ExceptionUtil.handleException(e);
            return new AbsentCalendarRes(res.getSystemId(), res.getRetCode(), res.getRetMsg(), null);
        }
    }

    @Override
    public AbsentDetailRes getAbsentScheduleDetail(AbsentDetailReq absentDetailReq) {
        List<AbsentSchedule> absentSchedules = absentScheduleRepository
                .findByAbsentDate(absentDetailReq.getAbsentDate());

        Map<String, AbsentDetail> groupedSchedules = new HashMap<>();
        for (AbsentSchedule schedule : absentSchedules) {
            String memberName = schedule.getMember().getName();

            AbsentDetail detail = groupedSchedules.getOrDefault(memberName, new AbsentDetail(schedule));
            detail.addScheduleName(schedule.getSchedule().getScheduleName());

            groupedSchedules.put(memberName, detail);
        }

        return new AbsentDetailRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(),
                new ArrayList<>(groupedSchedules.values()));

    }

    @Override
    @Transactional
    public CommonRes updateAbsentSchedule(AbsentReq absentReq) {

        CommonRes result = new CommonRes();
        try {
            if (absentReq.getScheduleNameList().isEmpty()) {
                throw new InvalidRequestException("하나 이상의 스터디 시간을 선택해야 합니다");
            }
            Member loginMember = memberRepository.findById(SecurityUtil.getLoginUserId());
            List<Schedule> scheduleList = scheduleRepository.findAllByScheduleNameIn(absentReq.getScheduleNameList());
            List<AbsentSchedule> absentScheduleList = absentScheduleRepository
                    .findByAbsentDateAndMember(absentReq.getAbsentDate(), loginMember);

            validateScheduleNames(scheduleList, absentReq.getScheduleNameList());

            Set<String> existScheduleNames = absentScheduleList.stream()
                    .map(as -> as.getSchedule().getScheduleName())
                    .collect(Collectors.toSet());
            Set<String> reqScheduleNames = new HashSet<>(absentReq.getScheduleNameList());

            boolean isDescriptionChanged = absentScheduleList.get(0).isDescriptionChanged(absentReq);
            boolean isScheduleNameListChanged = !existScheduleNames.equals(reqScheduleNames);

            if (isDescriptionChanged || isScheduleNameListChanged) {

                Set<String> removedScheduleNames = new HashSet<>(existScheduleNames);
                removedScheduleNames.removeAll(reqScheduleNames);
                Set<String> addedScheduleNames = new HashSet<>(reqScheduleNames);
                addedScheduleNames.removeAll(existScheduleNames);

                for (AbsentSchedule schedule : absentScheduleList) {
                    if (isDescriptionChanged) {
                        schedule.updateAbsentSchedule(absentReq);
                        absentScheduleRepository.save(schedule);
                    }
                    // ScheduleNames to be Removed
                    if (removedScheduleNames.contains(schedule.getSchedule().getScheduleName())) {
                        absentScheduleRepository.delete(schedule);
                    }
                }

                // Add new ScheduleName
                for (String addedScheduleName : addedScheduleNames) {
                    Schedule addedSchedule = scheduleRepository.findByScheduleName(addedScheduleName);

                    AbsentSchedule newSchedule = AbsentSchedule.builder().absentDate(absentReq.getAbsentDate())
                            .schedule(addedSchedule).member(loginMember).description(absentReq.getDescription())
                            .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime()).build();

                    absentScheduleRepository.save(newSchedule);
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

    @Override
    @Transactional
    public CommonRes deleteAbsentSchedule(String absentDate) {

        try {
            Member loginMember = memberRepository.findById(SecurityUtil.getLoginUserId());
            List<AbsentSchedule> absentScheduleList = absentScheduleRepository.findByAbsentDateAndMember(absentDate,
                    loginMember);
            if (absentScheduleList.isEmpty()) {
                throw new NotFoundException(
                        ErrorCode.NOT_FOUND.getMessage(absentDate + " " + loginMember.getName() + "의 부재일정"));
            }
            for (AbsentSchedule schedule : absentScheduleList) {
                absentScheduleRepository.delete(schedule);
            }
            return new CommonRes(null, ErrorCode.DELETED.getCode(),
                    ErrorCode.DELETED.getMessage("부재일정"));
        } catch (NotFoundException e) {
            return ExceptionUtil.handleException(e);
        }
    }

    private void validateScheduleNames(List<Schedule> scheduleList, List<String> scheduleNameList) {
        Set<String> scheduleNameSet = scheduleList.stream()
                .map(Schedule::getScheduleName)
                .collect(Collectors.toSet());

        for (String scheduleName : scheduleNameList) {
            if (!scheduleNameSet.contains(scheduleName)) {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("스터디 타임 " + scheduleName));
            }
        }
    }

}
