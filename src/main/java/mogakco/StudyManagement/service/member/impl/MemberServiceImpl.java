package mogakco.StudyManagement.service.member.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;
import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.domain.StudyInfo;
import mogakco.StudyManagement.domain.WakeUp;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.MemberDetails;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberInfoRes;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.MemberRole;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.StudyInfoRepository;
import mogakco.StudyManagement.repository.WakeUpRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.member.MemberService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.JWTUtil;
import mogakco.StudyManagement.util.SecurityUtil;

@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final WakeUpRepository wakeUpRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudyInfoRepository studyInfoRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${jwt.expired.time}")
    private Long expiredTime;

    @Value("${study.systemId}")
    protected String systemId;

    public MemberServiceImpl(MemberRepository memberRepository,
            MemberScheduleRepository memberScheduleRepository,
            WakeUpRepository wakeUpRepository,
            ScheduleRepository scheduleRepository,
            StudyInfoRepository studyInfoRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JWTUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.wakeUpRepository = wakeUpRepository;
        this.scheduleRepository = scheduleRepository;
        this.studyInfoRepository = studyInfoRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username);
        if (member != null) {
            return new MemberDetails(member);
        }
        return null;
    }

    @Override
    public MemberLoginRes login(MemberLoginReq loginInfo, LoggingService lo) {
        MemberLoginRes response = new MemberLoginRes();
        lo.setDBStart();
        Member member = memberRepository.findById(loginInfo.getId());
        lo.setDBEnd();

        if (member == null) {
            response.setRetMsg(ErrorCode.NOT_FOUND.getMessage("Member"));
            response.setRetCode(ErrorCode.NOT_FOUND.getCode());
            response.setToken("");
            return response;
        }

        String targetPwd = loginInfo.getPassword();
        String originPwd = member.getPassword();

        if (!bCryptPasswordEncoder.matches(targetPwd, originPwd)) {
            response.setRetMsg(ErrorCode.BAD_REQUEST.getMessage("비밀번호가 맞지 않습니다"));
            response.setRetCode(ErrorCode.BAD_REQUEST.getCode());
            response.setToken("");
            return response;
        }

        response.setRetMsg(ErrorCode.OK.getMessage());
        response.setRetCode(ErrorCode.OK.getCode());
        response.setToken(jwtUtil.createJwt(member.getId(), member.getRole().toString(), expiredTime));
        return response;
    }

    @Override
    @Transactional
    public DTOResCommon join(MemberJoinReq joinInfo, LoggingService lo) {
        DTOResCommon result = new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        Member member = Member.builder()
                .id(joinInfo.getId())
                .password(bCryptPasswordEncoder.encode(joinInfo.getPassword()))
                .name(joinInfo.getName())
                .contact(joinInfo.getContact())
                .role(MemberRole.USER) // 회원가입시 무조건 USER 권한
                .createdAt(DateUtil.getCurrentDateTime())
                .updatedAt(DateUtil.getCurrentDateTime())
                .build();
        WakeUp wakeUp = WakeUp.builder()
                .member(member)
                .wakeupTime(joinInfo.getWakeupTime())
                .build();

        Optional<Schedule> optSchedule = scheduleRepository.findById(joinInfo.getEventName());
        if (optSchedule.isPresent()) {
            Schedule schedule = optSchedule.get();
            MemberSchedule memberSchedule = MemberSchedule.builder()
                    .member(member)
                    .eventName(schedule)
                    .createdAt(DateUtil.getCurrentDateTime())
                    .updatedAt(DateUtil.getCurrentDateTime())
                    .build();
            lo.setDBStart();
            memberRepository.save(member);
            wakeUpRepository.save(wakeUp);
            memberScheduleRepository.save(memberSchedule);
            lo.setDBEnd();
        } else {
            result = new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                    ErrorCode.BAD_REQUEST.getMessage(joinInfo.getEventName() + " Schedule is Not Regist"));
        }

        return result;
    }

    @Override
    public boolean isIdDuplicated(MemberIdDuplReq idInfo, LoggingService lo) {
        lo.setDBStart();
        boolean isExist = memberRepository.existsById(idInfo.getId());
        lo.setDBEnd();

        return isExist;
    }

    @Override
    @Transactional
    public MemberInfoRes getMemberInfo(LoggingService lo) {
        MemberInfoRes result = new MemberInfoRes();

        result.setSystemId(systemId);
        result.setRetCode(ErrorCode.OK.getCode());
        result.setRetMsg(ErrorCode.OK.getMessage());
        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        result.setId(member.getId());
        result.setName(member.getName());
        result.setContact(member.getContact());
        result.setRole(member.getRole());

        List<StudyInfo> studyInfos = studyInfoRepository.findAll();
        // DB별 하나의 study 밖에 없으므로 0번 인덱스에서 조회
        result.setStudyName(studyInfos.size() == 0 ? null : studyInfos.get(0).getStudyName());

        List<MemberSchedule> memberSchedules = memberScheduleRepository.findAllByMember(member);
        List<Schedule> schedules = memberSchedules.stream()
                .map(MemberSchedule::getEventName)
                .collect(Collectors.toList());
        List<String> eventNames = schedules.stream()
                .map(Schedule::getEventName)
                .collect(Collectors.toList());

        result.setEventName(eventNames);
        WakeUp wakeUp = wakeUpRepository.findByMember(member);
        result.setWakeupTime(wakeUp == null ? null : wakeUp.getWakeupTime());

        return result;

    }

    @Override
    @Transactional
    public DTOResCommon setMemberInfo(MemberInfoUpdateReq updateInfo, LoggingService lo) {
        DTOResCommon result = new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());

        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        switch (updateInfo.getType()) {
            case NAME:
                // member 테이블 업데이트
                String changedName = updateInfo.getName();
                if (changedName == null || changedName.isBlank()) {
                    return new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                            ErrorCode.BAD_REQUEST.getMessage("변경할 이름을 입력해주세요"));
                }
                member.setName(changedName);
                lo.setDBStart();
                memberRepository.save(member);
                lo.setDBEnd();
                break;
            case EVENT_NAMES:
                // member_schedule 테이블 업데이트
                List<String> userEventNames = updateInfo.getEventName();
                if (userEventNames == null || userEventNames.size() == 0) {
                    return new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                            ErrorCode.BAD_REQUEST.getMessage("참여 스터디 이벤트를 선택해주세요"));
                }
                List<MemberSchedule> mSchedules = memberScheduleRepository.findAllByMember(member);
                List<Schedule> userSchedules = scheduleRepository.findByEventNameIn(userEventNames);

                List<String> dbEventNames = mSchedules.stream().map(m -> m.getEventName().getEventName())
                        .collect(Collectors.toList());

                List<Schedule> insertCandidates = userSchedules.stream()
                        .filter(u -> !dbEventNames.contains(u.getEventName()))
                        .collect(Collectors.toList());
                List<Schedule> updateCandidates = userSchedules.stream()
                        .filter(u -> dbEventNames.contains(u.getEventName()))
                        .collect(Collectors.toList());

                List<String> eventNamesToUpdate = updateCandidates.stream()
                        .map(u -> u.getEventName())
                        .collect(Collectors.toList());

                List<MemberSchedule> updateObj = mSchedules.stream()
                        .filter(schedule -> eventNamesToUpdate.contains(schedule.getEventName().getEventName()))
                        .map(schedule -> {
                            schedule.setUpdatedAt(DateUtil.getCurrentDateTime());
                            return schedule;
                        })
                        .collect(Collectors.toList());
                List<MemberSchedule> deleteObj = mSchedules.stream()
                        .filter(d -> !userEventNames.contains(d.getEventName().getEventName()))
                        .collect(Collectors.toList());
                List<MemberSchedule> insertObj = new ArrayList<>();
                for (Schedule iCdd : insertCandidates) {
                    insertObj.add(MemberSchedule.builder().member(member).eventName(iCdd)
                            .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime()).build());
                }
                lo.setDBStart();
                memberScheduleRepository.deleteAll(deleteObj);
                memberScheduleRepository.saveAll(insertObj);
                memberScheduleRepository.saveAll(updateObj);
                lo.setDBEnd();
                break;
            case WAKEUP:
                // wakeup 테이블 업데이트
                String changedWakeupTime = updateInfo.getWakeupTime();
                if (changedWakeupTime == null || changedWakeupTime.isBlank()) {
                    return new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                            ErrorCode.BAD_REQUEST.getMessage("변경할 기상 시간을 선택해주세요"));
                }
                WakeUp wakeUp = WakeUp.builder().member(member).wakeupTime(changedWakeupTime).build();
                lo.setDBStart();
                wakeUpRepository.save(wakeUp);
                lo.setDBEnd();
                break;
            case PASSWORD:
                // member 테이블 업데이트
                String changedPassword = updateInfo.getPassword();
                String originPassword = member.getPassword();
                // 비밀번호 현재 비밀번호랑 일치 여부 확인
                if (bCryptPasswordEncoder.matches(changedPassword, originPassword)) {
                    return new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                            ErrorCode.BAD_REQUEST.getMessage("현재 비밀번호와 동일합니다."));
                }
                // 비밀번호 암호화
                member.setPassword(bCryptPasswordEncoder.encode(changedPassword));
                lo.setDBStart();
                memberRepository.save(member);
                lo.setDBEnd();
                break;
            default:
                break;
        }

        return result;

    }

}
