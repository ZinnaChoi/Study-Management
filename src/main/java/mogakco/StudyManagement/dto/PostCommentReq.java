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
public class PostCommentReq {

    @NotBlank(message = "게시판 댓글의 내용은 반드시 있어야 합니다")
    @Pattern(regexp = "^.{1,500}$", message = "게시판의 댓글은 500자 이하여야 합니다")
    @Schema(example = "와 정보 공유 감사합니다!")
    private String content;

    public PostCommentReq(String content) {
        this.content = content;
    }

}
