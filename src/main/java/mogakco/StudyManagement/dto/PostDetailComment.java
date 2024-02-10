package mogakco.StudyManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDetailComment {

    private Long commentId;

    private String memberName;

    private String content;

    private String createdAt;

    private String updatedAt;

    private Integer replyCnt;

}
