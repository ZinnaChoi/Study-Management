package mogakco.StudyManagement.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.MemberDetails;
import mogakco.StudyManagement.entity.Member;
import mogakco.StudyManagement.repository.MemberRepository;

@Service
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findById(username);

        if (member != null) {

            return new MemberDetails(member);
        }

        return null;
    }

}
