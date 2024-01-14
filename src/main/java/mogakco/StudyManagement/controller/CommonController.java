package mogakco.StudyManagement.controller;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.util.DateUtil;

public class CommonController {

    protected final LoggingService lo;

    public CommonController(LoggingService lo) {
        this.lo = lo;
    }

    protected ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Value("${study.systemId}")
    protected String systemId;

    protected <T> void startAPI(LoggingService lo, T requestBody) {
        lo.setAPIStart();
        try {
            lo.setRequestBody(mapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException e) {
        }
    }

    protected <T> void endAPI(HttpServletRequest request, ErrorCode result, LoggingService lo, T responseBody) {
        try {
            lo.setResponseBody(mapper.writeValueAsString(responseBody));
        } catch (JsonProcessingException e) {
        }
        lo.setAPIEnd(request, result, systemId);

    }

    protected ErrorCode findErrorCodeByCode(int code) {
        for (ErrorCode ec : ErrorCode.values()) {
            if (ec.getCode().equals(code)) {
                return ec;
            }
        }
        return null;
    }

}
