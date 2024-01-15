package mogakco.StudyManagement.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostListRes extends DTOResCommon {

    private List<PostList> content;
    private SimplePageable pageable;

    public PostListRes(String systemId, int retCode, String retMsg, List<PostList> content, SimplePageable pageable) {
        super(systemId, retCode, retMsg);
        this.content = content;
        this.pageable = pageable;
    }

}