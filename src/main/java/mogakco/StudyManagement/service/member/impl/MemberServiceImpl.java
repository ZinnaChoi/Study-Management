package mogakco.StudyManagement.service.member.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.dto.MemberDetails;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.service.member.MemberService;
import mogakco.StudyManagement.util.JWTUtil;

@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${jwt.expired.time}")
    private Long expiredTime;

    public MemberServiceImpl(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            JWTUtil jwtUtil) {
        this.memberRepository = memberRepository;
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
    public String login(MemberLoginReq loginInfo) {

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
