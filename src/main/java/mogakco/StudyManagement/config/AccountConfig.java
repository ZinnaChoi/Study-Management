package mogakco.StudyManagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.enums.MemberRole;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.util.DateUtil;

@Component
public class AccountConfig {

    @Value("${account.admin.id}")
    private String adminId;

    @Value("${account.admin.password}")
    private String password;

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountConfig(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 최초 ADMIN 계정 생성
    @PostConstruct
    private void createAdminAccount() {

        if (!memberRepository.existsById(adminId)) {
            Member member = Member.builder()
                    .id("admin")
                    .password(bCryptPasswordEncoder.encode(password))
                    .name("관리자")
                    .contact("")
                    .role(MemberRole.ADMIN)
                    .createdAt(DateUtil.getCurrentDateTime())
                    .updatedAt(DateUtil.getCurrentDateTime())
                    .build();

            memberRepository.save(member);
        }
    }

}
