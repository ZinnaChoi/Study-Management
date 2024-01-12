package mogakco.StudyManagement.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.core.JsonProcessingException;

import mogakco.StudyManagement.controller.CommonController;
import mogakco.StudyManagement.dto.DTOResCommon;
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
    public ResponseEntity<DTOResCommon> handleValidationExceptions(MethodArgumentNotValidException ex)
            throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String errorString = mapper.writeValueAsString(errors);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(), errorString));

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DTOResCommon> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        String errorString = ex.getLocalizedMessage();
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(), errorString));
    }

}