package mogakco.StudyManagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.ScheduleReq;
import mogakco.StudyManagement.dto.StudyReq;
import mogakco.StudyManagement.service.external.SendEmailService;
import mogakco.StudyManagement.service.study.StudyService;
import mogakco.StudyManagement.util.TestUtil;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class StudyControllerTest {

        @Mock
        private StudyService studyService;

        @Mock
        private SendEmailService sendEmailService;

        @InjectMocks
        private StudyController studyController;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        private static final String STUDY_API_URL = "/api/v1/study";

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("스터디 정보 조회 성공")
        @Sql("/study/StudySetup.sql")
        public void getStudyInfo_Success() throws Exception {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(STUDY_API_URL);

                MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
                System.out.println(result.getResponse().getContentAsString());
                assertEquals(200, result.getResponse().getStatus());
        }

        @Test
        @WithMockUser(username = "user1", authorities = { "USER" })
        @DisplayName("스터디 정보 조회 성공_USER 권한도 가능")
        @Sql("/study/StudySetup.sql")
        public void getStudyInfo_AuthenticationSuccess() throws Exception {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(STUDY_API_URL);

                MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, null);
                assertEquals(200, result.getResponse().getStatus());
        }

        /////////////////////////////////////////////////////////////////

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 생성 성공")
        public void createStudySuccess() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleReq scheduleCreateReq = new ScheduleReq("NotExistScheduleName", "13:00", "14:00");
                List<ScheduleReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyReq studyReq = new StudyReq(
                                "NotExistStudyName", "10.10.10.110", "admin", "password", schedules);

                String requestBodyJson = objectMapper.writeValueAsString(studyReq);

                MockPart reqPart = new MockPart("req", requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file);
                List<MockPart> parts = Lists.list(reqPart);
                TestUtil.performFileRequest(mockMvc, STUDY_API_URL, HttpMethod.POST, files, parts, 200, 200);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 생성 실패_존재하는 스터디 이름")
        @Sql("/study/StudySetup.sql")
        public void createStudyFailureExistStudyName() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleReq scheduleCreateReq = new ScheduleReq("NotExistScheduleName", "13:00", "14:00");
                List<ScheduleReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyReq studyReq = new StudyReq(
                                "NotExistStudyName", "10.10.10.110", "admin", "password", schedules);

                String requestBodyJson = objectMapper.writeValueAsString(studyReq);

                MockPart reqPart = new MockPart("req", requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file);
                List<MockPart> parts = Lists.list(reqPart);
                TestUtil.performFileRequest(mockMvc, STUDY_API_URL, HttpMethod.POST, files, parts, 200, 400);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 생성 실패_존재하는 스케줄 이름")
        @Sql("/study/StudySetup.sql")
        public void createStudyFailureExistScheduleName() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleReq scheduleCreateReq = new ScheduleReq("NotExistScheduleName", "13:00", "14:00");
                List<ScheduleReq> schedules = Collections.singletonList(scheduleCreateReq);

                StudyReq studyReq = new StudyReq(
                                "NotExistStudyName", "10.10.10.110", "admin", "password", schedules);

                String requestBodyJson = objectMapper.writeValueAsString(studyReq);

                MockPart reqPart = new MockPart("req", requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file);
                List<MockPart> parts = Lists.list(reqPart);
                TestUtil.performFileRequest(mockMvc, STUDY_API_URL, HttpMethod.POST, files, parts, 200, 400);
        }

        /////////////////////////////////////////////////////////////////

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 정보 수정 성공")
        @Sql("/study/StudySetup.sql")
        public void createStudyInfoUpdate_Success() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleReq scheduleUpdateReq = new ScheduleReq("NotExistScheduleName", "13:00", "14:00");
                List<ScheduleReq> schedules = Collections.singletonList(scheduleUpdateReq);

                StudyReq studyReq = new StudyReq(
                                getStudyName(), "10.10.10.110", "admin", "password", schedules);

                String requestBodyJson = objectMapper.writeValueAsString(studyReq);

                MockPart reqPart = new MockPart("req", requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file);
                List<MockPart> parts = Lists.list(reqPart);
                TestUtil.performFileRequest(mockMvc, STUDY_API_URL, HttpMethod.PUT, files, parts, 200, 200);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 정보 수정 실패(등록된 스터디 정보 없음)")
        public void createStudyInfoUpdate_NotFoundStudy() throws Exception {

                MockMultipartFile file = new MockMultipartFile("logo file", "logo.png", "image/png",
                                "yourImageData".getBytes());
                ScheduleReq scheduleUpdateReq = new ScheduleReq("NotExistScheduleName", "13:00", "14:00");
                List<ScheduleReq> schedules = Collections.singletonList(scheduleUpdateReq);

                StudyReq studyReq = new StudyReq(
                                "NotExistStudyName", "10.10.10.110", "admin", "password", schedules);

                String requestBodyJson = objectMapper.writeValueAsString(studyReq);

                MockPart reqPart = new MockPart("req", requestBodyJson.getBytes());

                List<MockMultipartFile> files = Lists.list(file);
                List<MockPart> parts = Lists.list(reqPart);
                TestUtil.performFileRequest(mockMvc, STUDY_API_URL, HttpMethod.PUT, files, parts, 200, 404);
        }

        /////////////////////////////////////////////////////////////////

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 삭제 성공")
        @Sql("/study/StudySetup.sql")
        public void deleteStudyInfoUpdate_Success() throws Exception {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromUriString(STUDY_API_URL).path("/" + getStudyIdById());

                TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "DELETE", 200, 200);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        @DisplayName("관리자 스터디 삭제 실패_없는 스터디 이름 삭제 시도")
        public void deleteStudyInfoUpdate_NoExistStudyNameTried() throws Exception {

                Long wrongId = 99982L;
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromUriString(STUDY_API_URL).path("/" + wrongId);

                TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "DELETE", 200, 404);
        }

        @Test
        @WithMockUser(username = "user1", authorities = { "USER" })
        @DisplayName("관리자 스터디 삭제 실패_USER 권한 API 요청 시도")
        @Sql("/study/StudySetup.sql")
        public void deleteStudyInfoUpdate_AuthenticationFail() throws Exception {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromUriString(STUDY_API_URL).path("/" + getStudyIdById());

                TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "DELETE", 403, null);
        }

        private Long getStudyIdById() {
                return jdbcTemplate.queryForObject(
                                "SELECT study_id FROM study_info LIMIT 1",
                                Long.class);
        }

        private String getStudyName() {
                return jdbcTemplate.queryForObject(
                                "SELECT study_name FROM study_info LIMIT 1",
                                String.class);
        }

        /////////////////////////////////////////////////////////////////
}