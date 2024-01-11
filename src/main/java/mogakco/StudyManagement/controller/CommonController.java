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

    protected <T extends DTOResCommon> T setCommonResult(ErrorCode result, LoggingService lo,
            Class<T> responseClass, String... vars) {
        T res;
        try {
            res = responseClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Response class instantiation failed", e);
        }

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

    protected <T> void startAPI(LoggingService lo, T... requestBody) {
        lo.setAPIStart();
        try {
            if (requestBody.length > 0 && requestBody[0] != null) {
                lo.setRequestBody(mapper.writeValueAsString(requestBody));
            }
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
