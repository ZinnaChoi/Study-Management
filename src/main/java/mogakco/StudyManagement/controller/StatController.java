package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.service.stat.StatService;

@Tag(name = "통계", description = "통계 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class StatController extends CommonController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @Operation(summary = "통계 조회", description = "출석률 통계 조회, 기상률 조회")
    @GetMapping("/stat")
    public StatGetRes getStat(@RequestParam("type") LogType type,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        StatGetRes result = new StatGetRes();
        try {
            result = statService.getStat(type, startDate, endDate);
            if (result.getContent() == null || result.getContent().isEmpty()) {
                result = new StatGetRes(systemId, ErrorCode.NOT_FOUND.getCode(),
                        ErrorCode.NOT_FOUND.getMessage("content"), result.getContent(), result.getAttendanceMaxScore());
            }
        } catch (Exception e) {
            result = new StatGetRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        }

        return result;
    }

    @Operation(summary = "부재 로그 저장", description = "통계 생성을 위한 부재 로그 일일 업데이트")
    @PostMapping("/stat/absent")
    @Hidden
    // AbsentScheduleBatch.java 내 스케줄링의 기능 확인용 이므로 Hidden처리함.
    public CommonRes createAbsentLog() {
        CommonRes result = new CommonRes();
        try {
            result = statService.createAbsentLog();
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

    @Operation(summary = "기상 로그 저장", description = "통계 생성을 위한 기상 로그 업데이트")
    @PostMapping("/stat/wakeup")
    public CommonRes createWakeUpLog() {

        CommonRes result = new CommonRes();
        try {
            result = statService.createWakeUpLog();
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

}
