package mogakco.StudyManagement.service.stat.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import mogakco.StudyManagement.repository.spec.AbsentScheduleSpecification;
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

                    Specification<AbsentSchedule> spec = AbsentScheduleSpecification
                            .withAbsentDateAndMember(DateUtil.getCurrentDate(), member);

                    lo.setDBStart();
                    List<AbsentSchedule> todayAbsentSchedules = absentScheduleRepository.findAll(spec);
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

}
