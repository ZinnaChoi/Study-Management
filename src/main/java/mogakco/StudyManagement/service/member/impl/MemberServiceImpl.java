package mogakco.StudyManagement.service.member.impl;

import java.util.List;
import java.util.Optional;
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
                    .event_name(schedule)
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
                .map(MemberSchedule::getEvent_name)
                .collect(Collectors.toList());
        List<String> eventNames = schedules.stream()
                .map(Schedule::getEventName)
                .collect(Collectors.toList());

        result.setEventName(eventNames);
        WakeUp wakeUp = wakeUpRepository.findByMember(member);
        result.setWakeupTime(wakeUp == null ? null : wakeUp.getWakeupTime());

        return result;

    }

}
