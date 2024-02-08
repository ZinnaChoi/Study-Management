package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import mogakco.StudyManagement.dto.AbsentDetailReq;
import mogakco.StudyManagement.dto.AbsentDetailRes;
import mogakco.StudyManagement.dto.AbsentCalendarReq;
import mogakco.StudyManagement.dto.AbsentCalendarRes;
import mogakco.StudyManagement.dto.AbsentReq;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.absent.AbsentService;
import mogakco.StudyManagement.service.common.LoggingService;

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
    public CommonRes registerAbsentSchedule(HttpServletRequest request,
            @RequestBody @Valid AbsentReq absentReq) {

        CommonRes result = new CommonRes();
        try {
            startAPI(lo, absentReq);
            result = absentService.registerAbsentSchedule(absentReq, lo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "부재일정 캘린더 조회", description = "부재일정 월별 캘린더 조회")
    @GetMapping("/absent/calendar")
    public AbsentCalendarRes getAbsentScheduleByMonth(HttpServletRequest request,
            @ModelAttribute @Valid AbsentCalendarReq absentCalendarReq) {

        AbsentCalendarRes result = new AbsentCalendarRes();
        try {
            startAPI(lo, absentCalendarReq);
            result = absentService.getAbsentScheduleByMonth(absentCalendarReq, lo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new AbsentCalendarRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "부재일정 상세 조회", description = "부재일정 일별 상세 조회")
    @GetMapping("/absent/detail")
    public AbsentDetailRes getAbsentScheduleDetail(HttpServletRequest request,
            @ModelAttribute @Valid AbsentDetailReq absentDetailReq) {

        AbsentDetailRes result = new AbsentDetailRes();
        try {
            startAPI(lo, absentDetailReq);
            result = absentService.getAbsentScheduleDetail(absentDetailReq, lo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new AbsentDetailRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

    @Operation(summary = "부재일정 수정", description = "부재일정 상세 수정")
    @PatchMapping("/absent")
    public CommonRes updateAbsentSchedule(HttpServletRequest request,
            @RequestBody @Valid AbsentReq absentReq) {

        CommonRes result = new CommonRes();
        try {
            startAPI(lo, absentReq);
            result = absentService.updateAbsentSchedule(absentReq, lo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;

    }

    @Operation(summary = "부재일정 삭제", description = "부재일정 삭제")
    @DeleteMapping("/absent")
    public CommonRes deleteAbsentSchedule(HttpServletRequest request,
            @RequestParam(name = "absentDate") @Pattern(regexp = "^[0-9]{8}$") String absentDate) {
        CommonRes result = new CommonRes();

        try {
            startAPI(lo, null);
            result = absentService.deleteAbsentSchedule(absentDate, lo);
            result.setSystemId(systemId);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }
        return result;
    }

}
