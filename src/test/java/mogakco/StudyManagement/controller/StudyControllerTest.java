package mogakco.StudyManagement.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.ScheduleCreateReq;
import mogakco.StudyManagement.dto.StudyCreateReq;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.study.StudyService;
import mogakco.StudyManagement.util.DateUtil;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class StudyControllerTest {

    @Mock
    private StudyService studyService;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private StudyController studyController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String CREATE_STUDY_API_URL = "/api/v1/study";

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("관리자 스터디 생성 성공")
    public void createStudySuccess() throws Exception {

        MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                "yourImageData".getBytes());
        ScheduleCreateReq scheduleCreateReq = new ScheduleCreateReq("없는 이벤트 이름123123", "13:00", "14:00");
        List<ScheduleCreateReq> schedules = Collections.singletonList(scheduleCreateReq);

        StudyCreateReq studyCreateReq = new StudyCreateReq(
                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
        studyCreateReq.setSendDate(DateUtil.getCurrentDateTime());
        studyCreateReq.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(studyCreateReq);

        MockMultipartFile jsonFile = new MockMultipartFile("studyCreateReq", "", "application/json",
                requestBodyJson.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(CREATE_STUDY_API_URL)
                .file(file) // 이미지(로고) 파일
                .file(jsonFile) // 바이너리 studyCreateReq 객체
                .contentType(MediaType.MULTIPART_FORM_DATA)) // Content-Type 설정
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.retCode").value(200));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("관리자 스터디 생성 실패_존재하는 스터디 이름")
    @Sql("/ExistStudyNameSetup.sql")
    public void createStudyFailureExistStudyName() throws Exception {

        MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                "yourImageData".getBytes());
        ScheduleCreateReq scheduleCreateReq = new ScheduleCreateReq("없는 이벤트 이름123123", "13:00", "14:00");
        List<ScheduleCreateReq> schedules = Collections.singletonList(scheduleCreateReq);

        StudyCreateReq studyCreateReq = new StudyCreateReq(
                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
        studyCreateReq.setSendDate(DateUtil.getCurrentDateTime());
        studyCreateReq.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(studyCreateReq);

        MockMultipartFile jsonFile = new MockMultipartFile("studyCreateReq", "", "application/json",
                requestBodyJson.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(CREATE_STUDY_API_URL)
                .file(file) // 이미지(로고) 파일
                .file(jsonFile) // 바이너리 studyCreateReq 객체
                .contentType(MediaType.MULTIPART_FORM_DATA)) // Content-Type 설정
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.retCode").value(400));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("관리자 스터디 생성 실패_존재하는 이벤트 이름")
    @Sql("/ExistEventNameSetup.sql")
    public void createStudyFailureExistEventName() throws Exception {

        MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                "yourImageData".getBytes());
        ScheduleCreateReq scheduleCreateReq = new ScheduleCreateReq("없는 이벤트 이름123123", "13:00", "14:00");
        List<ScheduleCreateReq> schedules = Collections.singletonList(scheduleCreateReq);

        StudyCreateReq studyCreateReq = new StudyCreateReq(
                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
        studyCreateReq.setSendDate(DateUtil.getCurrentDateTime());
        studyCreateReq.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(studyCreateReq);

        MockMultipartFile jsonFile = new MockMultipartFile("studyCreateReq", "", "application/json",
                requestBodyJson.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(CREATE_STUDY_API_URL)
                .file(file) // 이미지(로고) 파일
                .file(jsonFile) // 바이너리 studyCreateReq 객체
                .contentType(MediaType.MULTIPART_FORM_DATA)) // Content-Type 설정
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.retCode").value(400));
    }
}