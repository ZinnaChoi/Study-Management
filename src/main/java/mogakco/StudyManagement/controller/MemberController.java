package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.member.MemberService;

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
    public MemberLoginRes doLogin(HttpServletRequest request, @RequestBody MemberLoginReq loginInfo) {
        MemberLoginRes result = new MemberLoginRes();

        startAPI(lo, loginInfo);
        if (loginInfo.getSendDate() == null) {
            result = setCommonResult(ErrorCode.NOT_FOUND, lo, MemberLoginRes.class, "sendDate");
            endAPI(request, ErrorCode.NOT_FOUND, lo, result);
            return result;
        }
        try {
            MemberLoginRes res = memberService.login(loginInfo, lo);
            ErrorCode code = findErrorCodeByCode(res.getRetCode());
            result = setCommonResult(code, lo, MemberLoginRes.class,
                    res.getRetMsg());
            result.setToken(res.getToken());
            endAPI(request, code, lo, result);

        } catch (Exception e) {
            result = setCommonResult(ErrorCode.INTERNAL_ERROR, lo, MemberLoginRes.class);
            endAPI(request, ErrorCode.INTERNAL_ERROR, lo, result);
        }
        return result;

    }

    @Operation(summary = "회원가입", description = "회원가입을 통해 사용자 정보 등록")
    @PostMapping("/join")
    public DTOResCommon doJoin(HttpServletRequest request, @RequestBody MemberJoinReq joinInfo) {
        DTOResCommon result = new DTOResCommon();

        startAPI(lo, joinInfo);
        if (joinInfo.getSendDate() == null) {
            result = setCommonResult(ErrorCode.NOT_FOUND, lo, DTOResCommon.class, "sendDate");
            endAPI(request, ErrorCode.NOT_FOUND, lo, result);
            return result;
        }
        try {
            memberService.join(joinInfo, lo);
            result = setCommonResult(ErrorCode.OK, lo, DTOResCommon.class);
            endAPI(request, ErrorCode.OK, lo, result);

        } catch (Exception e) {
            e.printStackTrace();
            result = setCommonResult(ErrorCode.INTERNAL_ERROR, lo, DTOResCommon.class);
            endAPI(request, ErrorCode.INTERNAL_ERROR, lo, result);
        }
        return result;
    }

}
