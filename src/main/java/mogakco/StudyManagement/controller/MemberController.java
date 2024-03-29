package mogakco.StudyManagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberIdDuplRes;
import mogakco.StudyManagement.dto.MemberInfoRes;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberInfosReq;
import mogakco.StudyManagement.dto.MemberInfosRes;
import mogakco.StudyManagement.dto.MemberListRes;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.dto.RegistedScheduleRes;
import mogakco.StudyManagement.dto.RegistedWakeupRes;
import mogakco.StudyManagement.dto.StudyMembersRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.member.MemberService;

@Tag(name = "계정 및 권한", description = "계정 및 권한 관련 API 분류")
@RequestMapping(path = "/api/v1")
@RestController
public class MemberController extends CommonController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "로그인", description = "로그인을 통해 JWT 발급")
    @PostMapping("/login")
    public MemberLoginRes doLogin(@Valid @RequestBody MemberLoginReq loginInfo) {
        MemberLoginRes result = new MemberLoginRes();

        try {
            result = memberService.login(loginInfo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new MemberLoginRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), "", "");
        }
        return result;
    }

    @Operation(summary = "로그아웃", description = "로그아웃 통해 JWT 토큰 만료 처리")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/logout")
    public CommonRes doLogout() {
        CommonRes result = new CommonRes();

        try {
            result = memberService.logout();
        } catch (Exception e) {
            result = new MemberLoginRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), "", "");
        }
        return result;
    }

    @Operation(summary = "회원가입", description = "회원가입을 통해 사용자 정보 등록")
    @PostMapping("/join")
    public CommonRes doJoin(@Valid @RequestBody MemberJoinReq joinInfo) {
        CommonRes result = new CommonRes();

        try {
            result = memberService.join(joinInfo);
        } catch (Exception e) {
            e.printStackTrace();
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

    @Operation(summary = "중복 아이디 확인", description = "회원가입시 사용자가 입력한 아이디 중복 검증 true: 중복 false: 사용 가능")
    @PostMapping("/join/check-id")
    public MemberIdDuplRes checkIdDuplicated(@Valid @RequestBody MemberIdDuplReq idInfo) {
        MemberIdDuplRes result = new MemberIdDuplRes();

        try {
            boolean isDuplicated = memberService.isIdDuplicated(idInfo);
            result = new MemberIdDuplRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
            result.setDuplicated(isDuplicated);
        } catch (Exception e) {
            result = new MemberIdDuplRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;

    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 통해 사용자 정보 삭제")
    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/withdraw")
    public CommonRes doWithDraw() {
        CommonRes result = new CommonRes();

        try {
            result = memberService.withdraw();
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

    @Operation(summary = "MyPage 회원 정보 조회", description = "로그인 된 회원 정보 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/member")
    public MemberInfoRes getMemberInfo() {
        MemberInfoRes result = new MemberInfoRes();

        try {
            result = memberService.getMemberInfo();
        } catch (Exception e) {
            result = new MemberInfoRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null, null, null, null, null, null);
        }
        return result;

    }

    @Operation(summary = "회원 목록 조회", description = "스터디에 가입한 회원 목록 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/members")
    public MemberListRes getMemberList() {
        MemberListRes result = new MemberListRes();
        try {
            result = memberService.getMemberList();
        } catch (Exception e) {
            result = new MemberListRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        }
        return result;
    }

    @Operation(summary = "MyPage 회원 정보변경", description = "로그인된 회원 정보 변경(이름 or 스터디 시간 or 기상 시간 or 비밀번호)")
    @SecurityRequirement(name = "bearer-key")
    @PatchMapping("/member")
    public CommonRes setMemberInfo(@Valid @RequestBody MemberInfoUpdateReq updateInfo) {
        CommonRes result = new CommonRes();

        try {
            result = memberService.setMemberInfo(updateInfo);
        } catch (Exception e) {
            e.printStackTrace();
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

    @Operation(summary = "스터디원 정보 조건 조회", description = "스터디원 정보(참여 시간, 기상시간 조건) 및 페이지 별 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/members-info")
    public MemberInfosRes getMembersInfo(@ModelAttribute @Valid MemberInfosReq memberListReq,
            @PageableDefault(size = 10, page = 0, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        MemberInfosRes result = new MemberInfosRes();
        try {
            result = memberService.getMembersInfo(memberListReq, pageable);
        } catch (Exception e) {
            result = new MemberInfosRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        }
        return result;
    }

    /**
     * @deprecated
     *             홈 화면 재구성에 따른 미사용 API, 조건 조회를 기능을 포함하여 통합된 하기 메소드를 대신 사용
     *             <p>
     *             {@link MemberController#getMembersInfo(HttpServletRequest, MemberInfosReq, Pageable)}
     *             .
     */
    @Deprecated
    @Operation(summary = "스케줄 이름 별 멤버 조회", description = "스케줄 이름을 통한 멤버 조회(schedule == null일 경우 스터디 참여 인원 전체 조회)")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/members/schedule-name")
    public StudyMembersRes getMembersBySchedule(
            @Parameter(name = "paging", description = "paging") @PageableDefault(size = 10, page = 0, sort = "memberId", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "schedule", required = false) String schedule) {

        StudyMembersRes result = new StudyMembersRes();
        try {
            result = memberService.getMembersBySchedule(schedule, pageable);
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new StudyMembersRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        }
        return result;
    }

    /**
     * @deprecated
     *             홈 화면 재구성에 따른 미사용 API, 조건 조회를 기능을 포함하여 통합된 하기 메소드를 대신 사용
     *             <p>
     *             {@link MemberController#getMembersInfo(HttpServletRequest, MemberInfosReq, Pageable)}
     *             .
     */
    @Deprecated
    @Operation(summary = "기상 시간 별 멤버 조회", description = "기상 시간을 통한 멤버 조회(time == null일 경우 스터디 참여 인원 전체 조회)")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/members/wakeup-time")
    public StudyMembersRes getMembersByWakeup(
            @Parameter(name = "paging", description = "paging") @PageableDefault(size = 10, page = 0, sort = "memberId", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "(HHmm) format") @RequestParam(name = "time", required = false) String time) {

        StudyMembersRes result = new StudyMembersRes();
        try {
            result = memberService.getMembersByWakeupTime(time, pageable);
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new StudyMembersRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        }
        return result;
    }

    @Operation(summary = "등록 스케줄 조희", description = "현재 등록되어 있는 스케줄 정보를 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/schedules")
    public RegistedScheduleRes getRegistedSchedule() {

        RegistedScheduleRes result = new RegistedScheduleRes();
        try {
            result = memberService.getRegistedSchedule();
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new RegistedScheduleRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        }
        return result;
    }

    @Operation(summary = "등록 기상 시간 조희", description = "현재 등록되어 있는 기상 시간 정보를 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/wakeup-times")
    public RegistedWakeupRes getRegistedWakeupTime() {

        RegistedWakeupRes result = new RegistedWakeupRes();
        try {
            result = memberService.getRegistedWakeup();
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new RegistedWakeupRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        }
        return result;
    }
}
