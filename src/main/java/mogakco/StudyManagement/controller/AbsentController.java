package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mogakco.StudyManagement.dto.AbsentDetailReq;
import mogakco.StudyManagement.dto.AbsentCalendarReq;
import mogakco.StudyManagement.dto.AbsentCalendarRes;
import mogakco.StudyManagement.dto.AbsentRgstReq;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.absent.AbsentService;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.util.DateUtil;

@Tag(name = "부재 일정", description = "부재일정 관련 API 분류")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class AbsentController extends CommonController {

    private final AbsentService absentService;

    public AbsentController(AbsentService absentService, LoggingService lo) {
        super(lo);
        this.absentService = absentService;
    }

    @Operation(summary = "부재일정 등록", description = "새 부재일정 등록")
    @PostMapping("/absent")
    public DTOResCommon registerAbsentSchedule(HttpServletRequest request,
            @RequestBody @Valid AbsentRgstReq absentRgstReq) {

        DTOResCommon result = new DTOResCommon();
        try {
            startAPI(lo, absentRgstReq);
            result = absentService.registerAbsentSchedule(absentRgstReq, lo);
            result.setSystemId(systemId);
            result.setSendDate(DateUtil.getCurrentDateTime());
        } catch (Exception e) {
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "부재일정 캘린더 조회", description = "부재일정 월별 조회")
    @GetMapping("/absent/calendar")
    public AbsentCalendarRes getAbsentScheduleByMonth(HttpServletRequest request,
            @ModelAttribute @Valid AbsentCalendarReq absentCalendarReq) {

        AbsentCalendarRes result = new AbsentCalendarRes();
        try {
            startAPI(lo, absentCalendarReq);
            result = absentService.getAbsentScheduleByMonth(absentCalendarReq, lo);
            result.setSendDate(DateUtil.getCurrentDateTime());
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new AbsentCalendarRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    // @Operation(summary = "부재일정 상세 조회", description = "부재일정 일별 조회")
    // @GetMapping("/absent/calendar/detail")
    // public AbsentCalendarRes getAbsentScheduleDetail(HttpServletRequest request,
    // @ModelAttribute @Valid AbsentDetailReq absentListReq) {

    // AbsentCalendarRes result = new AbsentCalendarRes();
    // try {
    // startAPI(lo, absentListReq);
    // // result = absentService.getAbsentSchedule(absentListReq, lo);
    // result.setSendDate(DateUtil.getCurrentDateTime());
    // result.setSystemId(systemId);
    // } catch (Exception e) {
    // result = new AbsentCalendarRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
    // ErrorCode.INTERNAL_ERROR.getMessage(), null);
    // } finally {
    // endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
    // }
    // return result;
    // }

}
