package mogakco.StudyManagement.controller;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.util.DateUtil;

public class CommonController {

    protected ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Value("${study.systemId}")
    protected String systemId;

    protected DTOResCommon setResult(ErrorCode result, String... vars) {
        DTOResCommon res = new DTOResCommon();
        res.setSendDate(DateUtil.getCurrentDateTime());
        res.setSystemId(systemId);
        res.setRetCode(result.getCode());

        if (vars.length > 0 && vars[0] != null && !vars[0].isEmpty()) {
            res.setRetMsg(result.getMessage(vars[0]));
        } else {
            res.setRetMsg(result.getMessage());
        }
        return res;
    }

}
