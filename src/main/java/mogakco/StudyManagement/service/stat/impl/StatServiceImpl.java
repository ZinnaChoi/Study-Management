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
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.repository.StatRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.stat.StatService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.PageUtil;

import mogakco.StudyManagement.domain.AbsentSchedule;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.repository.AbsentScheduleRepository;
import mogakco.StudyManagement.repository.DailyLogRepository;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;

@Service
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final AbsentScheduleRepository absentScheduleRepository;
    private final DailyLogRepository dailyLogRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final MemberRepository memberRepository;

    public StatServiceImpl(StatRepository statRepository, AbsentScheduleRepository absentScheduleRepository,
            DailyLogRepository dailyLogRepository,
            MemberScheduleRepository memberScheduleRepository, MemberRepository memberRepository) {
        this.statRepository = statRepository;
        this.absentScheduleRepository = absentScheduleRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.memberRepository = memberRepository;
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
    public DTOResCommon checkAbsentSchedulesAndLog(LoggingService lo) {

        try {
            List<Member> allMembers = memberRepository.findAll();
            DailyLog newLog = new DailyLog();

            for (Member member : allMembers) {

                long totalScoreForMember = memberScheduleRepository.countByMember(member);

                if (totalScoreForMember != 0) {

                    List<AbsentSchedule> todayAbsentSchedules = absentScheduleRepository
                            .findAbsentSchedulesByMember_MemberIdAndAbsentDate(member.getMemberId(),
                                    DateUtil.getCurrentDate());

                    if (todayAbsentSchedules.size() == 0) {
                        newLog = new DailyLog(member, DateUtil.getCurrentDate(), LogType.ABSENT,
                                (int) totalScoreForMember,
                                DateUtil.getCurrentDateTime());
                    }
                    int todayScore = (int) (totalScoreForMember - todayAbsentSchedules.size());
                    newLog = new DailyLog(member, DateUtil.getCurrentDate(), LogType.ABSENT, todayScore,
                            DateUtil.getCurrentDateTime());

                    dailyLogRepository.save(newLog);
                }

            }
            return new DTOResCommon(systemId, ErrorCode.OK.getCode(), "부재 일정 확인 및 로그 업데이트가 성공적으로 완료되었습니다.");
        } catch (Exception e) {

            return new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage("부재 일정 확인 및 로그 업데이트 중 오류"));
        }
    }

}
