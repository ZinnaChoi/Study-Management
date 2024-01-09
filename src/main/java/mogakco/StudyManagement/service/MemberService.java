package mogakco.StudyManagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.Login;
import mogakco.StudyManagement.entity.Member;
import mogakco.StudyManagement.jwt.JWTUtil;
import mogakco.StudyManagement.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${jwt.expired.time}")
    private Long expiredTime;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            JWTUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(Login loginInfo) {

        Member member = memberRepository.findById(loginInfo.getUsername());

        if (member == null) {
            return "해당 ID로 존재하는 사용자가 없습니다.";
        }

        String targetPwd = loginInfo.getPassword();
        String originPwd = member.getPassword();

        if (!bCryptPasswordEncoder.matches(targetPwd, originPwd)) {
            return "비밀번호가 맞지 않습니다.";
        }

        return jwtUtil.createJwt(member.getId(), member.getRole().toString(), expiredTime);
    }
}
