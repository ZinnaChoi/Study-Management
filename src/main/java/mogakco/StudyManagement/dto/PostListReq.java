package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mogakco.StudyManagement.enums.PostSearchType;

@Getter
@Setter
@NoArgsConstructor
public class PostListReq {

    @Schema(example = "관리자")
    private String searchKeyWord;

    private PostSearchType searchType;

    public PostListReq(String searchKeyWord, PostSearchType searchType) {
        this.searchKeyWord = searchKeyWord;
        this.searchType = searchType;
    }

}