package mogakco.StudyManagement.util;

import org.springframework.stereotype.Component;

import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.ConflictException;
import mogakco.StudyManagement.exception.InvalidRequestException;
import mogakco.StudyManagement.exception.NotFoundException;
import mogakco.StudyManagement.exception.UnauthorizedAccessException;

@Component
public class ExceptionUtil {

    public static DTOResCommon handleException(RuntimeException e) {
        ErrorCode errorCode;
        if (e instanceof NotFoundException) {
            errorCode = ErrorCode.NOT_FOUND;
        } else if (e instanceof InvalidRequestException || e instanceof UnauthorizedAccessException) {
            errorCode = ErrorCode.BAD_REQUEST;
        } else if (e instanceof ConflictException) {
            errorCode = ErrorCode.CONFLICT;
        } else {
            errorCode = ErrorCode.INTERNAL_ERROR;
        }
        return new DTOResCommon(null, errorCode.getCode(), e.getMessage());
    }

}
