package mogakco.StudyManagement.controller;

import mogakco.StudyManagement.domain.DTOReqCommon;
import mogakco.StudyManagement.domain.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @PostMapping("/api/comment")
    public DTOResCommon createUser(@RequestBody DTOReqCommon userRequest) {

        // 댓글이 없는 상황 예시
        // 에러 응답이 'Requested resource comment is not found'
        if (userRequest == null) {
            String resourceName = "comment";
            ErrorCode errorCode = ErrorCode.NOT_FOUND;
            return createErrorResponse(errorCode, resourceName);
        }

        return createSuccessResponse();
    }

    // 성공 시 response 생성
    private DTOResCommon createSuccessResponse() {
        DTOResCommon response = new DTOResCommon();
        response.setRetCode(ErrorCode.OK.getCode());
        response.setRetMsg(ErrorCode.OK.getMessage());
        return response;
    }

    // 에러 시 response 생성
    private DTOResCommon createErrorResponse(ErrorCode errorCode, String resourceName) {
        DTOResCommon response = new DTOResCommon();
        response.setRetCode(errorCode.getCode());
        response.setRetMsg(errorCode.getMessage(resourceName));
        return response;
    }

}
