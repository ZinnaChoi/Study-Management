package mogakco.StudyManagement.service.common.impl;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;

@Getter
@Setter
@Service
public class LoggingServiceImpl implements LoggingService {

    protected static final Logger logger = LoggerFactory.getLogger(LoggingServiceImpl.class);

    private Long timeAPI = 0L;
    private Long timeDB = 0L;
    private Long dbStartTime = 0L;

    @Override
    public void setRequestBody(String request) {
        logger.info(request);

    }

    @Override
    public void setResponseBody(String response) {
        logger.info(response);
    }

    @Override
    public void setAPIStart() {
        timeAPI = System.currentTimeMillis();
    }

    @Override
    public void setAPIEnd(HttpServletRequest request, ErrorCode errorCode, String systemId) {
        timeAPI = System.currentTimeMillis() - timeAPI;
        logger.info("Req: {} {} {}, Res: {}, Duration API: {} ms DB: {} ms", systemId,
                request.getMethod(), request.getRequestURI(), errorCode, timeAPI, timeDB);
        resetDurations();
    }

    @Override
    public void setDBStart() {
        dbStartTime = System.currentTimeMillis();
    }

    @Override
    public void setDBEnd() {
        long duration = System.currentTimeMillis() - dbStartTime;
        timeDB += duration;
    }

    private void resetDurations() {
        timeAPI = 0L;
        timeDB = 0L;
        dbStartTime = 0L;
    }
}
