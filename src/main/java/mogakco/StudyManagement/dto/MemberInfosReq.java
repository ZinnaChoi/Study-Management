package mogakco.StudyManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
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

    @Schema(example = ">= or <=")
    @Pattern(regexp = "^(>=|<=)$")
    private String comparisonOperators;

    public MemberInfosReq(String searchKeyWord, MemberSearchType searchType, String comparisonOperators) {
        this.searchKeyWord = searchKeyWord;
        this.searchType = searchType;
        this.comparisonOperators = comparisonOperators;
    }
}
