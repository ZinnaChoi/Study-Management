package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mogakco.StudyManagement.enums.PostSearchType;

@Getter
@Setter
@NoArgsConstructor
public class PostListReq extends DTOReqCommon {

    @NotBlank(message = "한 글자 이상 검색하셔야 합니다")
    @Schema(example = "관리자")
    private String searchKeyWord;

    private PostSearchType searchType;

    public PostListReq(String sendDate, String systemId, String searchKeyWord, PostSearchType searchType) {
        super(sendDate, systemId);
        this.searchKeyWord = searchKeyWord;
        this.searchType = searchType;
    }

}