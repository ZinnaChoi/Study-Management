package mogakco.StudyManagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import mogakco.StudyManagement.dto.DTOReqCommon;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberIdDuplRes;
import mogakco.StudyManagement.dto.MemberInfoRes;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.dto.RegistedScheduleRes;
import mogakco.StudyManagement.dto.RegistedWakeupRes;
import mogakco.StudyManagement.dto.StudyMembersRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.member.MemberService;
import mogakco.StudyManagement.util.DateUtil;

@Tag(name = "계정 및 권한", description = "계정 및 권한 관련 API 분류")
@RequestMapping(path = "/api/v1")
@RestController
public class MemberController extends CommonController {

    private final MemberService memberService;

    public MemberController(MemberService memberService, LoggingService lo) {
        super(lo);
        this.memberService = memberService;
    }

    @Operation(summary = "로그인", description = "로그인을 통해 JWT 발급")
    @PostMapping("/login")
    public MemberLoginRes doLogin(HttpServletRequest request, @Valid @RequestBody MemberLoginReq loginInfo) {
        MemberLoginRes result = new MemberLoginRes();

        try {
            startAPI(lo, loginInfo);
            result = memberService.login(loginInfo, lo);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new MemberLoginRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), "");
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "회원가입", description = "회원가입을 통해 사용자 정보 등록")
    @PostMapping("/join")
    public DTOResCommon doJoin(HttpServletRequest request, @Valid @RequestBody MemberJoinReq joinInfo) {
        DTOResCommon result = new DTOResCommon();

        try {
            startAPI(lo, joinInfo);
            result = memberService.join(joinInfo, lo);
        } catch (Exception e) {
            e.printStackTrace();
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "중복 아이디 확인", description = "회원가입시 사용자가 입력한 아이디 중복 검증 true: 중복 false: 사용 가능")
    @PostMapping("/check-id")
    public MemberIdDuplRes checkIdDuplicated(HttpServletRequest request, @Valid @RequestBody MemberIdDuplReq idInfo) {
        MemberIdDuplRes result = new MemberIdDuplRes();

        try {
            startAPI(lo, idInfo);
            boolean isDuplicated = memberService.isIdDuplicated(idInfo, lo);
            result = new MemberIdDuplRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
            result.setDuplicated(isDuplicated);
        } catch (Exception e) {
            result = new MemberIdDuplRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;

    }

    @Operation(summary = "MyPage 회원 정보 조회", description = "로그인 된 회원 정보 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/member")
    public MemberInfoRes getMemberInfo(HttpServletRequest request, @Valid @ModelAttribute DTOReqCommon info) {
        MemberInfoRes result = new MemberInfoRes();

        try {
            startAPI(lo, info);
            result = memberService.getMemberInfo(lo);
            result.setSendDate(DateUtil.getCurrentDateTime());
        } catch (Exception e) {
            result = new MemberInfoRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null, null, null, null, null, null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;

    }

    @Operation(summary = "MyPage 회원 정보변경", description = "로그인된 회원 정보 변경(이름 or 스터디 시간 or 기상 시간 or 비밀번호)")
    @SecurityRequirement(name = "bearer-key")
    @PatchMapping("/member")
    public DTOResCommon setMemberInfo(HttpServletRequest request, @Valid @RequestBody MemberInfoUpdateReq updateInfo) {
        DTOResCommon result = new DTOResCommon();

        try {
            startAPI(lo, updateInfo);
            result = memberService.setMemberInfo(updateInfo, lo);
        } catch (Exception e) {
            e.printStackTrace();
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "스케줄 이름 별 멤버 조회", description = "스케줄 이름을 통한 멤버 조회(schedule == null일 경우 스터디 참여 인원 전체 조회)")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/members/schedule-name")
    public StudyMembersRes getMembersBySchedule(
            HttpServletRequest request,
            @Parameter(name = "info", description = "요청 시 필수 값") @ModelAttribute @Valid DTOReqCommon info,
            @Parameter(name = "paging", description = "paging") @PageableDefault(size = 10, page = 0, sort = "memberId", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "schedule", required = false) String schedule) {

        StudyMembersRes result = new StudyMembersRes();
        try {
            startAPI(lo, info);
            result = memberService.getMembersBySchedule(lo, schedule, pageable);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new StudyMembersRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "기상 시간 별 멤버 조회", description = "기상 시간을 통한 멤버 조회(time == null일 경우 스터디 참여 인원 전체 조회)")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/members/wakeup-time")
    public StudyMembersRes getMembersByWakeup(
            HttpServletRequest request,
            @Parameter(name = "info", description = "요청 시 필수 값") @ModelAttribute @Valid DTOReqCommon info,
            @Parameter(name = "paging", description = "paging") @PageableDefault(size = 10, page = 0, sort = "memberId", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "(HHmm) format") @RequestParam(name = "time", required = false) String time) {

        StudyMembersRes result = new StudyMembersRes();
        try {
            startAPI(lo, info);
            result = memberService.getMembersByWakeupTime(lo, time, pageable);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new StudyMembersRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "등록 스케줄 조희", description = "현재 등록되어 있는 스케줄 정보를 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/registed/schedules")
    public RegistedScheduleRes getRegistedSchedule(
            HttpServletRequest request,
            @Parameter(name = "info", description = "요청 시 필수 값") @ModelAttribute @Valid DTOReqCommon info) {

        RegistedScheduleRes result = new RegistedScheduleRes();
        try {
            startAPI(lo, info);
            result = memberService.getRegistedSchedule(lo);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new RegistedScheduleRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "등록 기상 시간 조희", description = "현재 등록되어 있는 기상 시간 정보를 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/registed/wakeup-times")
    public RegistedWakeupRes getRegistedWakeupTime(
            HttpServletRequest request,
            @Parameter(name = "info", description = "요청 시 필수 값") @ModelAttribute @Valid DTOReqCommon info) {

        RegistedWakeupRes result = new RegistedWakeupRes();
        try {
            startAPI(lo, info);
            result = memberService.getRegistedWakeup(lo);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new RegistedWakeupRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }
}
