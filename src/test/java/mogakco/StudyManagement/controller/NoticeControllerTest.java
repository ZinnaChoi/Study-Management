package mogakco.StudyManagement.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.notice.NoticeService;
import mogakco.StudyManagement.util.TestUtil;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class NoticeControllerTest {

    @Mock
    private NoticeService noticeService;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private NoticeController noticeController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_NOTICE_API_URL = "/api/v1/notice";

    @Test
    @Sql("/notice/NoticeListSetup.sql")
    @WithMockUser(username = "noticeUser1", authorities = { "USER" })
    @DisplayName("알림 상태 조회 성공")
    public void getNoticeListSuccess() throws Exception {

        MvcResult result = TestUtil.performRequest(mockMvc,
                GET_NOTICE_API_URL + "/" + getMemberIdByMemberName("noticeUser1"), null, "GET", 200, 200);

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 1);

    }

    @Test
    @Sql("/notice/NoticeListSetup.sql")
    @WithMockUser(username = "noticeUser2", authorities = { "USER" })
    @DisplayName("알림 상태 조회 실패 - 존재하지 않는 사용자 ")
    public void getNoticeListFailForNonExistingUser() throws Exception {

        TestUtil.performRequest(mockMvc, GET_NOTICE_API_URL + "/" + 5,
                null, "GET", 200, 404);

    }

    public Long getMemberIdByMemberName(String Id) {
        return jdbcTemplate.queryForObject(
                "SELECT member_id FROM member WHERE id = ?",
                Long.class,
                Id);
    }
}
