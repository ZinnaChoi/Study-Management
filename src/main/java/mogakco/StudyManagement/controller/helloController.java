package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "예제 API", description = "Swagger 테스트용 API")
@RestController
public class HelloController {

    @Operation(summary = "시큐리티 및 Swagger 테스트 API", description = "1. 그낭 이 API 요청해본다, 2. login API를 통해 받은 토큰을 통해 Swagger login, 3. 다시 이 API 요청해본다")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/api/v1/hello")
    public String test() {
        return "Hello~!~~!~!!!";
    }
}
