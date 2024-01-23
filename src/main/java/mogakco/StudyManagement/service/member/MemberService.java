package mogakco.StudyManagement.service.member;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberInfoRes;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.dto.StudyMembersRes;
import mogakco.StudyManagement.service.common.LoggingService;

public interface MemberService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    MemberLoginRes login(MemberLoginReq loginInfo, LoggingService lo);

    DTOResCommon join(MemberJoinReq joinInfo, LoggingService lo);

    boolean isIdDuplicated(MemberIdDuplReq idInfo, LoggingService lo);

    MemberInfoRes getMemberInfo(LoggingService lo);

    DTOResCommon setMemberInfo(MemberInfoUpdateReq updateInfo, LoggingService lo);

    StudyMembersRes getMembersBySchedule(LoggingService lo, String schedule, Pageable pageable);

    StudyMembersRes getMembersByWakeupTime(LoggingService lo, String time, Pageable pageable);
}