package mogakco.StudyManagement.controller;

import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.ScheduleCUReq;
import mogakco.StudyManagement.dto.StudyCUReq;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.study.StudyService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

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
        private static final String UPDATE_STUDY_API_URL = "/api/v1/studyinfo";

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 생성 성공")
        public void createStudySuccess() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleCUReq scheduleCreateReq = new ScheduleCUReq("없는 스케줄 이름123123", "13:00", "14:00");
                List<ScheduleCUReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyCUReq studyCUReq = new StudyCUReq(
                                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
                studyCUReq.setSendDate(DateUtil.getCurrentDateTime());
                studyCUReq.setSystemId("SYS_01");
                String requestBodyJson = objectMapper.writeValueAsString(studyCUReq);

                MockMultipartFile jsonFile = new MockMultipartFile("studyCUReq", "", "application/json",
                                requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file, jsonFile);
                TestUtil.performFileRequest(mockMvc, CREATE_STUDY_API_URL, files, 200, 200);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 생성 실패_존재하는 스터디 이름")
        @Sql("/study/StudySetup.sql")
        public void createStudyFailureExistStudyName() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleCUReq scheduleCreateReq = new ScheduleCUReq("없는 스케줄 이름123123", "13:00", "14:00");
                List<ScheduleCUReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyCUReq studyCUReq = new StudyCUReq(
                                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
                studyCUReq.setSendDate(DateUtil.getCurrentDateTime());
                studyCUReq.setSystemId("SYS_01");
                String requestBodyJson = objectMapper.writeValueAsString(studyCUReq);

                MockMultipartFile jsonFile = new MockMultipartFile("studyCUReq", "", "application/json",
                                requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file, jsonFile);
                TestUtil.performFileRequest(mockMvc, CREATE_STUDY_API_URL, files, 200, 400);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 생성 실패_존재하는 스케줄 이름")
        @Sql("/study/StudySetup.sql")
        public void createStudyFailureExistScheduleName() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleCUReq scheduleCreateReq = new ScheduleCUReq("없는 스케줄 이름123123", "13:00", "14:00");
                List<ScheduleCUReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyCUReq studyCUReq = new StudyCUReq(
                                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
                studyCUReq.setSendDate(DateUtil.getCurrentDateTime());
                studyCUReq.setSystemId("SYS_01");
                String requestBodyJson = objectMapper.writeValueAsString(studyCUReq);

                MockMultipartFile jsonFile = new MockMultipartFile("studyCUReq", "", "application/json",
                                requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file, jsonFile);
                TestUtil.performFileRequest(mockMvc, CREATE_STUDY_API_URL, files, 200, 400);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 정보 수정 성공")
        @Sql("/study/StudySetup.sql")
        public void createStudyInfoUpdate_Success() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleCUReq scheduleCreateReq = new ScheduleCUReq("없는 스케줄 이름123123", "13:00", "14:00");
                List<ScheduleCUReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyCUReq studyCUReq = new StudyCUReq(
                                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
                studyCUReq.setSendDate(DateUtil.getCurrentDateTime());
                studyCUReq.setSystemId("SYS_01");
                String requestBodyJson = objectMapper.writeValueAsString(studyCUReq);

                MockMultipartFile jsonFile = new MockMultipartFile("studyCUReq", "", "application/json",
                                requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file, jsonFile);
                TestUtil.performFileRequest(mockMvc, UPDATE_STUDY_API_URL, files, 200, 200);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 정보 수정 실패(등록된 스터디 정보 없음)")
        public void createStudyInfoUpdate_NotFoundStudy() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleCUReq scheduleCreateReq = new ScheduleCUReq("없는 스케줄 이름123123", "13:00", "14:00");
                List<ScheduleCUReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyCUReq studyCUReq = new StudyCUReq(
                                "없는 스터디 이름123123", "10.10.10.110", "admin", "password", schedules);
                studyCUReq.setSendDate(DateUtil.getCurrentDateTime());
                studyCUReq.setSystemId("SYS_01");
                String requestBodyJson = objectMapper.writeValueAsString(studyCUReq);

                MockMultipartFile jsonFile = new MockMultipartFile("studyCUReq", "", "application/json",
                                requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file, jsonFile);
                TestUtil.performFileRequest(mockMvc, UPDATE_STUDY_API_URL, files, 200, 404);
        }
}