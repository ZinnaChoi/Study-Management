package mogakco.StudyManagement.enums;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum ErrorCode {

    OK(200, HttpStatus.OK, "Ok"),

    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "Bad Request:{0}"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "{0} not found"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "User unauthorized"),

    INTERNAL_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String... args) {
        return MessageFormat.format(this.message, (Object[]) args);
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }

}