package mogakco.StudyManagement.controller;

import mogakco.StudyManagement.dto.DTOReqCommon;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.example.ExampleService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "예시 컨트롤러")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1/common")
@RestController
public class ExampleController extends CommonController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService, LoggingService lo) {
        super(lo);
        this.exampleService = exampleService;
    }

    @Operation(summary = "공동 Domain 적용 API", description = "1. 그대로 요청 2. sendDate \"string\"에서 null로 바꾼 뒤 요청")
    @PostMapping("/comment")
    public DTOResCommon example(HttpServletRequest request, @RequestBody DTOReqCommon reqCommon) {

        lo.setAPIStart();
        try {
            lo.setRequestBody(mapper.writeValueAsString(reqCommon));
        } catch (JsonProcessingException e) {
            setResult(request, ErrorCode.BAD_REQUEST, lo);
        }

        exampleService.exampleMethod(reqCommon.getSendDate(), lo);

        // 댓글이 없는 상황 예시
        // 에러 응답: 'comment not found'
        if (reqCommon.getSendDate() == null) {
            return setResult(request, ErrorCode.NOT_FOUND, lo, "comment");
        }
        return setResult(request, ErrorCode.OK, lo);
    }

    @Operation(summary = "시큐리티 및 Swagger 테스트 API", description = "1. 그낭 이 API 요청해본다, 2. login API를 통해 받은 토큰을 통해 Swagger login, 3. 다시 이 API 요청해본다")
    @GetMapping("/hello")
    public DTOResCommon test(HttpServletRequest request) {

        lo.setAPIStart();

        return setResult(request, ErrorCode.OK, lo);
    }
}
