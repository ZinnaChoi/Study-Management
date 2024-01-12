package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateReq extends DTOReqCommon {

    @NotBlank(message = "게시글의 제목은 반드시 있어야 합니다")
    @Pattern(regexp = "^.{1,60}$", message = "게시글의 제목은 60자 이하여야 합니다")
    @Schema(example = "2024 2월 개발 뉴스 공유드립니다")
    private String title;

    @NotBlank(message = "게시글의 내용은 반드시 있어야 합니다")
    @Pattern(regexp = "^.{1,20000}$", message = "게시글의 내용은 20000자 이하여야 합니다")
    @Schema(example = "chatGPT 5.0 도입")
    private String content;

    public PostCreateReq(String sendDate, String systemId, String title, String content) {
        super(sendDate, systemId);
        this.title = title;
        this.content = content;
    }

}
