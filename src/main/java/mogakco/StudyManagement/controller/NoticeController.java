package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.NoticeGetRes;
import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.notice.NoticeService;

@Tag(name = "알림", description = "알림 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class NoticeController extends CommonController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Operation(summary = "알림 상태 조회", description = "개인별 알림 수신 여부 상태 조회")
    @GetMapping("/notice")
    public NoticeGetRes getNotice(
            HttpServletRequest request) {

        NoticeGetRes result = new NoticeGetRes();

        try {
            result = noticeService.getNotice();
        } catch (Exception e) {
            result = new NoticeGetRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        }

        return result;
    }

    @Operation(summary = "알림 상태 수정", description = "개인별 알림 수신 여부 상태 수정")
    @SecurityRequirement(name = "bearer-key")
    @PatchMapping(value = "/notice")
    public CommonRes updateNotice(HttpServletRequest request, @RequestBody @Valid NoticeReq noticeReq) {
        CommonRes result = new CommonRes();

        try {
            result = noticeService.updateNotice(noticeReq);
            result.setSystemId(systemId);
        } catch (Exception e) {
            e.printStackTrace();
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

    @Operation(summary = "구글 미트 생성 알림", description = "모니터링 작업을 통해 스터디 시작 10분 전 구글 미트 생성 알림 전송")
    @PostMapping("/notice/general")
    @Hidden
    // ScheduleStartTimeMonitoring.java 내 스케줄링의 기능 확인용 이므로 Hidden처리함.
    public CommonRes createGeneralNotice(HttpServletRequest request) {

        CommonRes result = new CommonRes();
        try {
            result = noticeService.createGeneralNotice();
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

}
