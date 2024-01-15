package mogakco.StudyManagement.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.ScheduleCreateReq;
import mogakco.StudyManagement.dto.StudyCreateReq;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.study.StudyService;
import mogakco.StudyManagement.util.TestUtil;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
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
    public void createStudySuccess() throws Exception {
        ScheduleCreateReq scheduleCreateReq = new ScheduleCreateReq("AM3333332tt13443", "13:00", "14:00");
        List<ScheduleCreateReq> schedules = Collections.singletonList(scheduleCreateReq);

        StudyCreateReq studyCreateReq = new StudyCreateReq(
                "모각코스터디312t33344",
                "studylogo".getBytes(), "10.10.10.110", "admin", "password", schedules);

        String requestBodyJson = objectMapper.writeValueAsString(studyCreateReq);

        TestUtil.performPostRequest(mockMvc, CREATE_STUDY_API_URL, requestBodyJson, 200, 200);
    }

    // studyname null
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void createStudyFailureEmptyStudyName() throws Exception {
        ScheduleCreateReq scheduleCreateReq = new ScheduleCreateReq("AM1", "13:00", "14:00");
        List<ScheduleCreateReq> schedules = Collections.singletonList(scheduleCreateReq);

        StudyCreateReq studyCreateReq = new StudyCreateReq(null,
                "studylogo".getBytes(), "10.10.10.110", "admin", "password", schedules);

        String requestBodyJson = objectMapper.writeValueAsString(studyCreateReq);

        TestUtil.performPostRequest(mockMvc, CREATE_STUDY_API_URL, requestBodyJson, 400, 400);

    }

    // Bad Request
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void createStudyFailInvalidJson() throws Exception {
        String invalidJson = "{\n" +
                " \"studyName\": \"모각코\",\n" + //
                " \"studyLogo\": \"studylogo\",\n" +
                " \"dbUrl\": \"10.10.10.110\",\n" +
                " \"dbUser\": \"admin\",\n" +
                " \"dbPassword\": \"password\",\n" +
                " \"schedules\": [\n" +
                "   {\n" +
                "     \"eventName\": \"AM1\",\n" +
                "     \"startTime\": \"13:00\",\n" +
                "     \"endTime\": \"14:00\"\n" +
                "   }\n" +
                " ]\n";
        TestUtil.performPostRequest(mockMvc, CREATE_STUDY_API_URL, invalidJson, 400, 400);
    }
}