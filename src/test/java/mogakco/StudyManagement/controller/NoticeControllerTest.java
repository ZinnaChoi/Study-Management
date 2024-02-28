package mogakco.StudyManagement.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.NoticeReq;
import mogakco.StudyManagement.scheduler.StartTimeMonitoringScheduler;
import mogakco.StudyManagement.service.external.SendEmailService;
import mogakco.StudyManagement.service.notice.NoticeService;
import mogakco.StudyManagement.util.TestUtil;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class NoticeControllerTest {

        @Mock
        private NoticeService noticeService;

        @Mock
        private SendEmailService sendEmailService;

        @InjectMocks
        private NoticeController noticeController;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private MockMvc mockMvc;

        @Value("${study.systemId}")
        private String systemId;

        private static final String GET_NOTICE_API_URL = "/api/v1/notice";

        @Test
        @Sql("/notice/NoticeListSetup.sql")
        @WithMockUser(username = "noticeUser1", authorities = { "USER" })
        @DisplayName("알림 상태 조회 성공")
        public void getNoticeListSuccess() throws Exception {

                MvcResult result = TestUtil.performRequest(mockMvc,
                                GET_NOTICE_API_URL, null, "GET", 200,
                                200);

                JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

                int contentCount = responseBody.path("content").size();
                assertTrue(contentCount >= 1);

        }

        @Test
        @Sql("/notice/NoticeListSetup.sql")
        @WithMockUser(username = "noticeUser1", authorities = { "USER" })
        @DisplayName("알림 상태 변경 성공")
        public void updateNoticeSuccess() throws Exception {

                String requestBodyJson = objectMapper.writeValueAsString(
                                new NoticeReq(Boolean.TRUE.booleanValue(),
                                                Boolean.FALSE,
                                                Boolean.FALSE, Boolean.TRUE.booleanValue()));

                MvcResult result = TestUtil.performRequest(mockMvc,
                                GET_NOTICE_API_URL, requestBodyJson,
                                "PATCH", 200, 200);
                JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

                int retCode = responseBody.path("retCode").asInt();

                assertEquals(200, retCode);
        }

        @Test
        @Sql("/notice/NoticeListSetup.sql")
        @WithMockUser(username = "noticeUser2", authorities = { "USER" })
        @DisplayName("알림 상태 변경 실패 - 변경 사항 없음")
        public void updateNoticeFailNoChanges() throws Exception {

                String requestBodyJson = objectMapper.writeValueAsString(
                                new NoticeReq(Boolean.TRUE, Boolean.TRUE,
                                                Boolean.TRUE, Boolean.TRUE));

                TestUtil.performRequest(mockMvc,
                                GET_NOTICE_API_URL, requestBodyJson,
                                "PATCH", 200, 204);

        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @Test
        @Sql("/notice/NoticeListSetup.sql")
        @DisplayName("구글 링크 생성 알림 모니터링 작업 성공")
        public void excuteGeneralNoti() {
                NoticeService noticeServiceMock = Mockito.mock(NoticeService.class);
                StartTimeMonitoringScheduler startTimeMonitoringScheduler = new StartTimeMonitoringScheduler(
                                noticeServiceMock);

                startTimeMonitoringScheduler.executeGeneralNotice();

                verify(noticeServiceMock, times(1)).createGeneralNotice();
        }

}