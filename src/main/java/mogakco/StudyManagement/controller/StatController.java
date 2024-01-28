package mogakco.StudyManagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.stat.StatService;

@Tag(name = "통계", description = "통계 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class StatController extends CommonController {

    private final StatService statService;

    public StatController(LoggingService lo, StatService statService) {
        super(lo);
        this.statService = statService;

    }

    @Operation(summary = "통계 조회", description = "출석률 통계 조회, 기상률 조회")
    @GetMapping("/stat")
    public StatGetRes getStat(
            HttpServletRequest request, @RequestParam("type") LogType type,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        StatGetRes result = new StatGetRes();

        try {
            startAPI(lo, type);
            result = statService.getStat(type, lo, pageable);

            if (result == null || result.getContent() == null || result.getContent().isEmpty()) {
                result = new StatGetRes(systemId, ErrorCode.NOT_FOUND.getCode(),
                        ErrorCode.NOT_FOUND.getMessage("content"), null, null);
            }
        } catch (Exception e) {
            result = new StatGetRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }

        return result;
    }

    @Operation(summary = "부재 일정 저장", description = "통계 생성을 위한 부재 일정 일일 업데이트")
    @PostMapping("/stat/absent")
    public DTOResCommon checkAbsentSchedulesAndLog(HttpServletRequest request) {

        DTOResCommon result = new DTOResCommon();
        try {
            startAPI(lo, null);
            result = statService.checkAbsentSchedulesAndLog(lo);
        } catch (Exception e) {
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        }
        return result;
    }

}
