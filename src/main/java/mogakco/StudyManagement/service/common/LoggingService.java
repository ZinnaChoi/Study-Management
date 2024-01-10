package mogakco.StudyManagement.service.common;

import jakarta.servlet.http.HttpServletRequest;

public interface LoggingService {

    void setAPIStart();

    void setAPIEnd(HttpServletRequest request, String systemId);

    void setDBStart();

    void setDBEnd();

}