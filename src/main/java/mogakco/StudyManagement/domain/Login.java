package mogakco.StudyManagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Login {

    @Schema(example = "admin")
    private String username;

    @Schema(example = "password")
    private String password;
}
