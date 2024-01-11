package mogakco.StudyManagement.service.common;

import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.enums.ErrorCode;

public interface LoggingService {

    void setRequestBody(String request);

    void setResponseBody(String response);

    void setAPIStart();

    void setAPIEnd(HttpServletRequest request, ErrorCode errorCode, String systemId);

    void setDBStart();

    void setDBEnd();

}