package mogakco.StudyManagement.service.stat.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.domain.DailyLog;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.dto.StatList;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.InvalidRequestException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.enums.MessageType;
import mogakco.StudyManagement.repository.StatRepository;
import mogakco.StudyManagement.service.notice.NoticeService;
import mogakco.StudyManagement.service.stat.StatService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.domain.AbsentSchedule;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.WakeUp;
import mogakco.StudyManagement.dto.CommonRes;
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

    @Autowired
    NoticeService noticeService;

    @Override
    public StatGetRes getStat(LogType type, String startDate, String endDate) {
        try {
            List<DailyLog> dailyLogs = statRepository.findByTypeAndDateBetween(type, startDate, endDate);

            List<StatList> dailyLogLists = dailyLogs.stream().map(StatList::new).collect(Collectors.toList());

            List<Map<String, Object>> attendanceMaxScore = calculateMaxScore();

            return new StatGetRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), dailyLogLists,
                    attendanceMaxScore);
        } catch (Exception e) {
            return new StatGetRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage(),
                    null, null);
        }
    }

    private List<Map<String, Object>> calculateMaxScore() {
        List<Map<String, Object>> result = new ArrayList<>();

        List<Map<String, Object>> maxSchedules = memberScheduleRepository.findMaxSchedulePerMember();

        for (Map<String, Object> maxSchedule : maxSchedules) {
            String memberName = (String) maxSchedule.get("memberName");
            Long maxScore = (Long) maxSchedule.get("maxSchedule");

            Map<String, Object> maxScoreMap = new HashMap<>();
            maxScoreMap.put("memberName", memberName);
            maxScoreMap.put("score", maxScore);

            result.add(maxScoreMap);
        }

        return result;
    }

    @Override
    @Transactional
    public CommonRes createAbsentLog() {

        try {
            List<Member> allMembers = memberRepository.findAll();
            DailyLog newLog = new DailyLog();

            for (Member member : allMembers) {
                Integer totalScoreForMember = memberScheduleRepository.countByMember(member);

                if (totalScoreForMember != 0) {
                    List<AbsentSchedule> todayAbsentSchedules = absentScheduleRepository
                            .findByAbsentDateAndMember(DateUtil.getCurrentDate(), member);

                    if (todayAbsentSchedules.size() == 0) {
                        newLog = new DailyLog(member, DateUtil.getCurrentDate(), LogType.ABSENT,
                                totalScoreForMember,
                                DateUtil.getCurrentDateTime());
                    } else {
                        int todayScore = (int) (totalScoreForMember - todayAbsentSchedules.size());
                        newLog = new DailyLog(member, DateUtil.getCurrentDate(), LogType.ABSENT, todayScore,
                                DateUtil.getCurrentDateTime());
                    }
                    dailyLogRepository.save(newLog);
                }

            }
            return new CommonRes(systemId, ErrorCode.OK.getCode(), "부재 일정 확인 및 로그 업데이트가 성공적으로 완료되었습니다.");
        } catch (Exception e) {

            return new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage("부재 일정 확인 및 로그 업데이트 중 오류가 발생하였습니다."));
        }
    }

    @Override
    @Transactional
    public CommonRes createWakeUpLog() {
        try {

            Member member = getLoginMember();

            WakeUp wakeUpMember = wakeUpRepository.findByMember(member);

            if (wakeUpMember == null) {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getMessage(member.getName() + "의 목표 기상 시간"));
            }

            boolean checkdailylog = dailyLogRepository.existsByTypeAndDateAndMember(LogType.WAKEUP,
                    DateUtil.getCurrentDate(),
                    member);
            if (checkdailylog) {
                throw new NotFoundException(ErrorCode.CONFLICT.getMessage(member.getName() + "의 금일 기상 로그"));
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

            dailyLogRepository.save(newLog);
            noticeService.createSpecificNotice(member, MessageType.WAKE_UP);

            return new CommonRes(systemId, ErrorCode.OK.getCode(), "기상 로그 업데이트가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            return new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(), "기상 로그 업데이트에 실패하였습니다.");
        }
    }

    @Override
    @Transactional
    public CommonRes createEmptyWakeUpLog() {
        try {

            List<WakeUp> WakeUp = wakeUpRepository.findAll();
            DailyLog newLog = null;

            for (WakeUp w : WakeUp) {

                if (!dailyLogRepository.existsByTypeAndDateAndMember(LogType.WAKEUP, DateUtil.getCurrentDate(),
                        w.getMember())) {

                    newLog = new DailyLog(w.getMember(), DateUtil.getCurrentDate(), LogType.WAKEUP, 0,
                            DateUtil.getCurrentDateTime());

                }

                dailyLogRepository.save(newLog);
            }

            return new CommonRes(systemId, ErrorCode.OK.getCode(), "기상 로그 업데이트가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            return new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(), "기상 로그 업데이트에 실패하였습니다.");
        }

    }

}
