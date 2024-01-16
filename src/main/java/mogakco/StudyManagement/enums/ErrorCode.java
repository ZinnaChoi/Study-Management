package mogakco.StudyManagement.enums;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum ErrorCode {

    OK(200, HttpStatus.OK, "성공"),
    DELETED(204, HttpStatus.NO_CONTENT, "{0}이(가) 삭제되었습니다"),
    UNCHANGED(204, HttpStatus.NO_CONTENT, "{0}은(는) 변경 사항이 없어 업데이트되지 않았습니다."),
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "잘못된 요청: {0}"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "사용자 권한이 없습니다"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "{0}을(를) 찾을 수 없습니다"),
    CONFLICT(409, HttpStatus.CONFLICT, "{0}이(가) 이미 존재합니다."),
    INTERNAL_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "내부 오류 발생");

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