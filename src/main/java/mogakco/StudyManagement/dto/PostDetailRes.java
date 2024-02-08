package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostDetailRes extends CommonRes {

    private PostDetail postDetail;

    public PostDetailRes(String systemId, Integer retCode, String retMsg, PostDetail postDetail) {
        super(systemId, retCode, retMsg);
        this.postDetail = postDetail;
    }

}
