package mogakco.StudyManagement.dto;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistedWakeupRes extends CommonRes {

    private Set<String> registedWakeups;

    public RegistedWakeupRes(String systemId, Integer retCode, String retMsg,
            Set<String> registedWakeups) {
        super(systemId, retCode, retMsg);
        this.registedWakeups = registedWakeups;
    }
}
