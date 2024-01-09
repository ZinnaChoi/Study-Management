package mogakco.StudyManagement.service.member;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import mogakco.StudyManagement.domain.Login;

public interface MemberService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    String login(Login loginInfo);
}