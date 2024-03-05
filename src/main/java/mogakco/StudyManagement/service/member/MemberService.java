package mogakco.StudyManagement.service.member;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberInfoRes;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberInfosReq;
import mogakco.StudyManagement.dto.MemberInfosRes;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberListRes;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.dto.RegistedScheduleRes;
import mogakco.StudyManagement.dto.RegistedWakeupRes;
import mogakco.StudyManagement.dto.StudyMembersRes;

public interface MemberService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    MemberLoginRes login(MemberLoginReq loginInfo);

    CommonRes logout();

    CommonRes join(MemberJoinReq joinInfo);

    CommonRes withdraw();

    boolean isIdDuplicated(MemberIdDuplReq idInfo);

    MemberInfoRes getMemberInfo();

    MemberInfosRes getMembersInfo(MemberInfosReq memberInfosReq, Pageable pageable);

    MemberListRes getMemberList();

    CommonRes setMemberInfo(MemberInfoUpdateReq updateInfo);

    StudyMembersRes getMembersBySchedule(String schedule, Pageable pageable);

    StudyMembersRes getMembersByWakeupTime(String time, Pageable pageable);

    RegistedScheduleRes getRegistedSchedule();

    RegistedWakeupRes getRegistedWakeup();
}