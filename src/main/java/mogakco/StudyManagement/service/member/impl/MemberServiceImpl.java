package mogakco.StudyManagement.service.member.impl;

import java.util.Optional;

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
import mogakco.StudyManagement.domain.WakeUp;
import mogakco.StudyManagement.dto.MemberDetails;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.MemberRole;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.WakeUpRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.member.MemberService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.JWTUtil;

@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final WakeUpRepository wakeUpRepository;
    private final ScheduleRepository scheduleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${jwt.expired.time}")
    private Long expiredTime;

    public MemberServiceImpl(MemberRepository memberRepository,
            MemberScheduleRepository memberScheduleRepository,
            WakeUpRepository wakeUpRepository,
            ScheduleRepository scheduleRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JWTUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.wakeUpRepository = wakeUpRepository;
        this.scheduleRepository = scheduleRepository;
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
        Member member = memberRepository.findById(loginInfo.getUsername());
        lo.setDBEnd();

        if (member == null) {
            response.setRetMsg("Member");
            response.setRetCode(ErrorCode.NOT_FOUND.getCode());
            response.setToken("");
            return response;
        }

        String targetPwd = loginInfo.getPassword();
        String originPwd = member.getPassword();

        if (!bCryptPasswordEncoder.matches(targetPwd, originPwd)) {
            response.setRetMsg("비밀번호가 맞지 않습니다");
            response.setRetCode(ErrorCode.BAD_REQUEST.getCode());
            response.setToken("");
            return response;
        }

        response.setRetCode(ErrorCode.OK.getCode());
        response.setToken(jwtUtil.createJwt(member.getId(), member.getRole().toString(), expiredTime));
        return response;
    }

    @Override
    @Transactional
    public void join(MemberJoinReq joinInfo, LoggingService lo) {
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
    }
}
