package mogakco.StudyManagement.controller;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import mogakco.StudyManagement.enums.ErrorCode;

public class CommonController {

    protected ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Value("${study.systemId}")
    protected String systemId;

    protected ErrorCode findErrorCodeByCode(int code) {
        for (ErrorCode ec : ErrorCode.values()) {
            if (ec.getCode().equals(code)) {
                return ec;
            }
        }
        return null;
    }

}
