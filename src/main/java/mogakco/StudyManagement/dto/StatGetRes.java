package mogakco.StudyManagement.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatGetRes extends CommonRes {

    private List<StatList> content;

    private List<Map<String, Object>> attendanceMaxScore;

    public StatGetRes(String systemId, int retCode, String retMsg, List<StatList> content,
            List<Map<String, Object>> attendanceMaxScore) {
        super(systemId, retCode, retMsg);
        this.content = content;
        this.attendanceMaxScore = attendanceMaxScore;
    }

}
