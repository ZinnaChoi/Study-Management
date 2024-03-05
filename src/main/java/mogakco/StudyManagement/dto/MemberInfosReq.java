package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mogakco.StudyManagement.enums.MemberSearchType;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfosReq {
    @Schema(example = "관리자")
    private String searchKeyWord;

    private MemberSearchType searchType;

    public MemberInfosReq(String searchKeyWord, MemberSearchType searchType) {
        this.searchKeyWord = searchKeyWord;
        this.searchType = searchType;
    }
}
