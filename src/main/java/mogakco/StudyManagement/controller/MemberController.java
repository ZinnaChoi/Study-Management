package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberIdDuplRes;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
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
    public MemberLoginRes doLogin(HttpServletRequest request, @RequestBody MemberLoginReq loginInfo) {
        MemberLoginRes result = new MemberLoginRes();

        try {
            startAPI(lo, loginInfo);
            if (loginInfo.getSendDate() == null) {
                result = new MemberLoginRes(systemId, ErrorCode.NOT_FOUND.getCode(),
                        ErrorCode.NOT_FOUND.getMessage("sendDate"), "");
            } else {
                result = memberService.login(loginInfo, lo);
                result.setSendDate(DateUtil.getCurrentDateTime());
                result.setSystemId(systemId);
            }
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
    public DTOResCommon doJoin(HttpServletRequest request, @RequestBody MemberJoinReq joinInfo) {
        DTOResCommon result = new DTOResCommon();

        try {
            startAPI(lo, joinInfo);
            if (joinInfo.getSendDate() == null) {
                result = new DTOResCommon(systemId, ErrorCode.NOT_FOUND.getCode(),
                        ErrorCode.NOT_FOUND.getMessage("sendDate"));
            } else {
                result = memberService.join(joinInfo, lo);
            }
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
    @PostMapping("/id-duplicated")
    public MemberIdDuplRes checkIdDuplicated(HttpServletRequest request, @RequestBody MemberIdDuplReq idInfo) {
        MemberIdDuplRes result = new MemberIdDuplRes();

        try {
            startAPI(lo, idInfo);
            if (idInfo.getSendDate() == null) {
                result = new MemberIdDuplRes(systemId, ErrorCode.NOT_FOUND.getCode(),
                        ErrorCode.NOT_FOUND.getMessage("sendDate"));
            } else {
                boolean isDuplicated = memberService.isIdDuplicated(idInfo, lo);
                result = new MemberIdDuplRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
                result.setDuplicated(isDuplicated);
            }
        } catch (Exception e) {
            result = new MemberIdDuplRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;

    }

}
