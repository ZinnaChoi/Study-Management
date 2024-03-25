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
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.StudyInfoRes;
import mogakco.StudyManagement.dto.StudyReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.service.study.StudyService;

@Tag(name = "관리자", description = "스터디 등록 및 일정 등록")
@SecurityRequirement(name = "bearer-key")
@RequestMapping(path = "/api/v1")
@RestController
public class StudyController extends CommonController {

  private final StudyService studyService;

  public StudyController(StudyService studyService) {
    this.studyService = studyService;
  }

  @Operation(summary = "스터디 정보 조회", description = "등록 스터디 정보 조회")
  @SecurityRequirement(name = "bearer-key")
  @GetMapping(value = "/study")
  public StudyInfoRes getStudy() {
    StudyInfoRes result = new StudyInfoRes();

    try {
      result = studyService.getStudy();
    } catch (Exception e) {
      result = new StudyInfoRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
          ErrorCode.INTERNAL_ERROR.getMessage(), null, null, null, null);
    }

    return result;
  }

  @Operation(summary = "스터디 등록", description = "새 스터디 추가")
  @SecurityRequirement(name = "bearer-key")
  @PostMapping(value = "/study", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
  public CommonRes createStudy(
      @Schema(name = "req", description = """
              [예시]
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
      result = studyService.createStudy(studyReq, imageFile);
    } catch (Exception e) {
      result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
          ErrorCode.INTERNAL_ERROR.getMessage());
    }

    return result;
  }

  @Operation(summary = "스터디 정보 수정", description = "스터디 정보(스터디 이름, 로고, 스케줄) 수정")
  @SecurityRequirement(name = "bearer-key")
  @PutMapping(value = "/study", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
  public CommonRes updateStudy(
      @Schema(name = "req", description = """
              [예시]
              {
                  "studyName": "현재 스터디 이름",
                  "updateStudyName": "변경할 스터디 이름",
                  "useCurrentLogo": "현재 등록된 로고 사용 여부",
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
          """, required = true, example = """
              {
                  "studyName": "현재 스터디 이름",
                  "updateStudyName": "변경할 스터디 이름",
                  "useCurrentLogo": "true",
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
      result = studyService.updateStudy(studyReq, imageFile);
    } catch (Exception e) {
      result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
          ErrorCode.INTERNAL_ERROR.getMessage());
    }
    return result;
  }

  @Operation(summary = "스터디 정보 삭제", description = "스터디 정보(스터디 이름, 로고, 스케줄) 삭제")
  @SecurityRequirement(name = "bearer-key")
  @DeleteMapping(value = "/study/{studyid}")
  public CommonRes deleteStudy(@PathVariable(name = "studyid", required = true) Long studyId) {
    CommonRes result = new CommonRes();

    try {
      result = studyService.deleteStudy(studyId);
    } catch (Exception e) {
      result = new CommonRes(systemId, ErrorCode.INTERNAL_ERROR.getCode(),
          ErrorCode.INTERNAL_ERROR.getMessage());
    }

    return result;
  }

}
