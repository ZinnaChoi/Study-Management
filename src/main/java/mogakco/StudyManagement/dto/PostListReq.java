package mogakco.StudyManagement.dto;

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
    private String searchKeyWord;

    private PostSearchType serachType;

}