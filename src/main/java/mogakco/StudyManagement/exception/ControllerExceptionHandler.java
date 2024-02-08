package mogakco.StudyManagement.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.controller.CommonController;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler extends CommonController {

    public ControllerExceptionHandler(LoggingService lo) {
        super(lo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonRes> handleValidationExceptions(HttpServletRequest request,
            MethodArgumentNotValidException ex)
            throws JsonProcessingException {
        startAPI(lo, ex.getBindingResult().getTarget());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String errorString = mapper.writeValueAsString(errors);
        CommonRes result = new CommonRes(systemId, ErrorCode.BAD_REQUEST.getCode(), errorString);
        endAPI(request, ErrorCode.BAD_REQUEST, lo, result);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(result);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonRes> handleHttpMessageNotReadableException(HttpServletRequest request,
            HttpMessageNotReadableException ex) {
        startAPI(lo, "Invalid Json Input");
        String errorString = ex.getLocalizedMessage();
        CommonRes result = new CommonRes(systemId, ErrorCode.BAD_REQUEST.getCode(), errorString);
        endAPI(request, ErrorCode.BAD_REQUEST, lo, result);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(result);

    }

}