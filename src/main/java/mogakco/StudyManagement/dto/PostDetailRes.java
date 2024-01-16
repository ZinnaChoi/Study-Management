package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostDetailRes extends DTOResCommon {

    private PostList postList;

    public PostDetailRes(String systemId, int retCode, String retMsg, PostList postList) {

        super(systemId, retCode, retMsg);
        this.postList = postList;
    }

}
