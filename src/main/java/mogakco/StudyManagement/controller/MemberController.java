package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.member.impl.MemberServiceImpl;

@Tag(name = "계정 및 권한", description = "계정 및 권한 관련 API 분류")
@RequestMapping(path = "/api/v1")
@RestController
public class MemberController extends CommonController {

    private final MemberServiceImpl memberServiceImpl;

    public MemberController(MemberServiceImpl memberServiceImpl) {
        this.memberServiceImpl = memberServiceImpl;
    }

    @Operation(summary = "로그인", description = "로그인을 통해 JWT 발급")
    @PostMapping("/login")
    public DTOResCommon doLogin(@RequestBody MemberLoginReq loginInfo) {
        MemberLoginRes result = new MemberLoginRes();
        DTOResCommon res = new DTOResCommon();

        if (loginInfo.getSendDate() == null) {
            return setResult(ErrorCode.NOT_FOUND, "sendDate");
        }

        try {
            result = memberServiceImpl.login(loginInfo);
            if (result.getToken().isEmpty()) {
                res = setResult(ErrorCode.BAD_REQUEST);
            } else {
                res = setResult(ErrorCode.OK);
            }

            result.setRetCode(res.getRetCode());
            result.setRetMsg(res.getRetMsg());
            result.setSendDate(res.getSendDate());
            result.setSystemId(res.getSystemId());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
