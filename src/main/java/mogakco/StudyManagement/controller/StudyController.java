package mogakco.StudyManagement.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.StudyInfoRes;
import mogakco.StudyManagement.dto.StudyReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.study.StudyService;

@Tag(name = "관리자", description = "스터디 등록 및 일정 등록")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class StudyController extends CommonController {

    private final StudyService studyService;

    public StudyController(StudyService studyService, LoggingService lo) {
        super(lo);
        this.studyService = studyService;
    }

    @Operation(summary = "스터디 정보 조회", description = "등록 스터디 정보 조회")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping(value = "/study")
    public StudyInfoRes getStudy(HttpServletRequest request) {
        StudyInfoRes result = new StudyInfoRes();

        try {
            startAPI(lo, null);
            result = studyService.getStudy(lo);
        } catch (Exception e) {
            result = new StudyInfoRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage(), null, null, null, null);
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }

        return result;
    }

    @Operation(summary = "스터디 등록", description = "새 스터디 추가")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping(value = "/study", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public CommonRes createStudy(HttpServletRequest request,
            @Schema(name = "req", description = """
                        스터디 추가 Request Body(json)
                        studyName*(String): 추가할 스터디 이름
                        schedules*(Array): 스케줄 리스트(HTML)
                        schedules.scheduleName(String): 스케줄 이름
                        schedules.startTime(String): 스케줄 시작 시간
                        schedules.endTime(String): 스케줄 종료 시간
                    """, required = true, example = """
                        {
                            "studyName": "모각코 스터디",
                            "schedules": [
                              {
                                "scheduleName": "AM2",
                                "startTime": "1300",
                                "endTime": "1400"
                              },
                              {
                                "scheduleName": "AM3",
                                "startTime": "1500",
                                "endTime": "1600"
                              }
                            ]
                          }

                    """) @RequestPart(name = "req") String req,
            @Parameter(description = "이미지 파일") @RequestParam(name = "logo file", required = false) MultipartFile imageFile) {
        CommonRes result = new CommonRes();

        try {
            StudyReq studyReq = mapper.readValue(req, StudyReq.class);
            startAPI(lo, studyReq);
            result = studyService.createStudy(studyReq, imageFile, lo);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }

        return result;
    }

    @Operation(summary = "스터디 정보 수정", description = "스터디 정보(스터디 이름, 로고, 스케줄) 수정")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping(value = "/study", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public CommonRes updateStudy(HttpServletRequest request,
            @Schema(name = "req", description = """
                        스터디 추가 Request Body(json)
                        studyName*(String): 현재 스터디 이름
                        updateStudyName*(String): 변경할 스터디 이름
                        schedules*(Array): 스케줄 리스트(HTML)
                        schedules.scheduleName(String): 스케줄 이름
                        schedules.startTime(String): 스케줄 시작 시간
                        schedules.endTime(String): 스케줄 종료 시간
                    """, required = true, example = """
                        {
                            "studyName": "현재 스터디 이름",
                            "updateStudyName": "변경할 스터디 이름",
                            "schedules": [
                              {
                                "scheduleName": "AM2",
                                "startTime": "1300",
                                "endTime": "1400"
                              },
                              {
                                "scheduleName": "AM3",
                                "startTime": "1500",
                                "endTime": "1600"
                              }
                            ]
                          }

                    """) @RequestPart(name = "req") String req,
            @Parameter(description = "이미지 파일") @RequestParam(name = "logo file", required = false) MultipartFile imageFile) {
        CommonRes result = new CommonRes();

        try {
            StudyReq studyReq = mapper.readValue(req, StudyReq.class);
            startAPI(lo, studyReq);
            result = studyService.updateStudy(studyReq, imageFile, lo);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }

        return result;
    }

    @Operation(summary = "스터디 정보 삭제", description = "스터디 정보(스터디 이름, 로고, 스케줄) 삭제")
    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping(value = "/study/{studyid}")
    public CommonRes deleteStudy(HttpServletRequest request,
            @PathVariable(name = "studyid", required = true) Long studyId) {
        CommonRes result = new CommonRes();

        try {
            startAPI(lo, null);
            result = studyService.deleteStudy(studyId, lo);
        } catch (Exception e) {
            result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, findErrorCodeByCode(result.getRetCode()), lo, result);
        }

        return result;
    }

}
