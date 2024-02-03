package mogakco.StudyManagement.service.stat.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.DailyLog;
import mogakco.StudyManagement.dto.SimplePageable;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.dto.StatList;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.InvalidRequestException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.repository.StatRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.stat.StatService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.PageUtil;

import mogakco.StudyManagement.domain.AbsentSchedule;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.WakeUp;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.repository.AbsentScheduleRepository;
import mogakco.StudyManagement.repository.DailyLogRepository;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.WakeUpRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.util.SecurityUtil;

@Service
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final AbsentScheduleRepository absentScheduleRepository;
    private final DailyLogRepository dailyLogRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final MemberRepository memberRepository;
    private final WakeUpRepository wakeUpRepository;

    public StatServiceImpl(StatRepository statRepository, AbsentScheduleRepository absentScheduleRepository,
            DailyLogRepository dailyLogRepository,
            MemberScheduleRepository memberScheduleRepository, MemberRepository memberRepository,
            WakeUpRepository wakeUpRepository) {
        this.statRepository = statRepository;
        this.absentScheduleRepository = absentScheduleRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.memberRepository = memberRepository;
        this.wakeUpRepository = wakeUpRepository;
    }

    protected Member getLoginMember() {
        return memberRepository.findById(SecurityUtil.getLoginUserId());
    }

    @Value("${study.systemId}")
    protected String systemId;

    @Override
    public StatGetRes getStat(LogType type, LoggingService lo, Pageable pageable) {
        try {

            Page<DailyLog> dailyLogs;

            lo.setDBStart();
            dailyLogs = statRepository.findByType(type, pageable);
            lo.setDBEnd();

            List<StatList> dailyLogLists = dailyLogs.getContent().stream().map(StatList::new)
                    .collect(Collectors.toList());

            SimplePageable simplePageable = PageUtil.createSimplePageable(dailyLogs);

            return new StatGetRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), dailyLogLists,
                    simplePageable);
        } catch (Exception e) {
            return new StatGetRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        }

    }

    @Override
    @Transactional
    public DTOResCommon createAbsentLog(LoggingService lo) {

        try {
            lo.setDBStart();
            List<Member> allMembers = memberRepository.findAll();
            lo.setDBEnd();
            DailyLog newLog = new DailyLog();

            for (Member member : allMembers) {

                lo.setDBStart();
                Integer totalScoreForMember = memberScheduleRepository.countByMember(member);
                lo.setDBEnd();

                if (totalScoreForMember != 0) {

                    lo.setDBStart();
                    List<AbsentSchedule> todayAbsentSchedules = absentScheduleRepository
                            .findByAbsentDateAndMember(DateUtil.getCurrentDate(), member);
                    lo.setDBEnd();

                    if (todayAbsentSchedules.size() == 0) {
                        newLog = new DailyLog(member, DateUtil.getCurrentDate(), LogType.ABSENT,
                                totalScoreForMember,
                                DateUtil.getCurrentDateTime());
                    } else {
                        int todayScore = (int) (totalScoreForMember - todayAbsentSchedules.size());
                        newLog = new DailyLog(member, DateUtil.getCurrentDate(), LogType.ABSENT, todayScore,
                                DateUtil.getCurrentDateTime());
                    }

                    lo.setDBStart();
                    dailyLogRepository.save(newLog);
                    lo.setDBEnd();
                }

            }
            return new DTOResCommon(systemId, ErrorCode.OK.getCode(), "부재 일정 확인 및 로그 업데이트가 성공적으로 완료되었습니다.");
        } catch (Exception e) {

            return new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage("부재 일정 확인 및 로그 업데이트 중 오류"));
        }
    }

    @Override
    @Transactional
    public DTOResCommon createWakeUpLog(LoggingService lo) {
        try {
            Member member = getLoginMember();
            lo.setDBStart();
            WakeUp wakeUpMember = wakeUpRepository.findByMember(member);
            lo.setDBEnd();

            if (wakeUpMember == null) {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage("member의 목표 기상 시간"));
            }

            DailyLog newLog;

            String currentTime = DateUtil.getCurrentDateTime().substring(8, 12);

            if (currentTime.compareTo(wakeUpMember.getWakeupTime()) < 0) {
                newLog = new DailyLog(wakeUpMember.getMember(), DateUtil.getCurrentDate(), LogType.WAKEUP, 1,
                        DateUtil.getCurrentDateTime());
            } else {
                newLog = new DailyLog(wakeUpMember.getMember(), DateUtil.getCurrentDate(), LogType.WAKEUP, 0,
                        DateUtil.getCurrentDateTime());
            }

            lo.setDBStart();
            dailyLogRepository.save(newLog);
            lo.setDBEnd();

            return new DTOResCommon(systemId, ErrorCode.OK.getCode(), "기상 로그 업데이트가 성공적으로 완료되었습니다.");
        } catch (NotFoundException | InvalidRequestException e) {
            return ExceptionUtil.handleException(e);
        }
    }
}
