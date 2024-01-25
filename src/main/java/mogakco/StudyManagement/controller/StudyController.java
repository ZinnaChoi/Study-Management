package mogakco.StudyManagement.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mogakco.StudyManagement.dto.DTOResCommon;
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

    @Operation(summary = "스터디 등록", description = "새 스터디 추가")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping(value = "/study", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DTOResCommon createStudy(HttpServletRequest request,
            @Valid @RequestPart(value = "studyReq") @Parameter(schema = @Schema(type = "string", format = "binary")) StudyReq studyReq,
            @Parameter(description = "이미지 파일") @RequestPart(name = "logo file", required = false) MultipartFile imageFile) {
        DTOResCommon result = new DTOResCommon();

        try {
            startAPI(lo, studyReq);
            result = studyService.createStudy(studyReq, imageFile, lo);
        } catch (Exception e) {
            e.printStackTrace();
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, ErrorCode.OK, lo, result);
        }

        return result;
    }

    @Operation(summary = "스터디 정보 수정", description = "스터디 정보(스터디 이름, 로고, 스케줄) 수정")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping(value = "/study", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DTOResCommon updateStudy(HttpServletRequest request,
            @Valid @RequestPart(value = "studyReq") @Parameter(schema = @Schema(type = "string", format = "binary")) StudyReq studyReq,
            @Parameter(description = "이미지 파일") @RequestPart(name = "logo file", required = false) MultipartFile imageFile) {
        DTOResCommon result = new DTOResCommon();

        try {
            startAPI(lo, studyReq);
            result = studyService.updateStudy(studyReq, imageFile, lo);
        } catch (Exception e) {
            e.printStackTrace();
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, ErrorCode.OK, lo, result);
        }

        return result;
    }

    @Operation(summary = "스터디 정보 수정", description = "스터디 정보(스터디 이름, 로고, 스케줄) 수정")
    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping(value = "/study/{studyname}")
    public DTOResCommon deleteStudy(HttpServletRequest request,
            @PathVariable(name = "studyname", required = true) String studyName) {
        DTOResCommon result = new DTOResCommon();

        try {
            startAPI(lo, null);
            result = studyService.deleteStudy(studyName, lo);
        } catch (Exception e) {
            e.printStackTrace();
            result = new DTOResCommon(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());
        } finally {
            endAPI(request, ErrorCode.OK, lo, result);
        }

        return result;
    }

}
