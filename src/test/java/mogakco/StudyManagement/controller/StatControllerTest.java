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
import org.springframework.web.util.UriComponentsBuilder;

import mogakco.StudyManagement.batch.AbsentScheduleBatch;
import mogakco.StudyManagement.dto.DTOReqCommon;
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.stat.StatService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class StatControllerTest {

    @Mock
    private StatService statService;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private StatController statController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Value("${study.systemId}")
    private String systemId;

    private static final String GET_STAT_API_URL = "/api/v1/stat";
    private static final String POST_ABSENT_STAT_API_URL = "/api/v1/stat/absent";
    private static final String POST_WAKEUP_STAT_API_URL = "/api/v1/stat/wakeup";

    @Test
    @Sql("/stat/StatListSetup.sql")
    @WithMockUser(username = "statUser1", authorities = { "USER" })
    @DisplayName("통계 목록 조회 성공 - Page 0 Size 1")
    public void getStatListSuccessPage0Size1() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_STAT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("type", LogType.WAKEUP)
                .queryParam("page", 0)
                .queryParam("size", 1)
                .queryParam("sort", "member");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 1);

    }

    @Test
    @Sql("/stat/StatListSetup.sql")
    @WithMockUser(username = "statUser2", authorities = { "USER" })
    @DisplayName("통계 목록 조회 성공 - Page 1 Size 2")
    public void getStatListSuccessPage1Size2() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_STAT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("type", LogType.WAKEUP)
                .queryParam("page", 1)
                .queryParam("size", 2)
                .queryParam("sort", "member");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 1);

    }

    @Test
    @Sql("/stat/StatListSetup.sql")
    @WithMockUser(username = "statUser3", authorities = { "USER" })
    @DisplayName("통계 목록 조회 실패 - content가 존재하지 않음")
    public void getStatListFailPage0Size1() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_STAT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("type", LogType.ABSENT)
                .queryParam("page", 0)
                .queryParam("size", 1)
                .queryParam("sort", "member");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 404);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 0);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @Sql("/stat/StatAbsentScheduleSetup.sql")
    @WithMockUser(username = "absentUser1", authorities = { "USER" })
    @DisplayName("부재 로그 저장 성공")
    public void createAbsentDailyLogSuccess() throws Exception {
        String requestBodyJson = objectMapper.writeValueAsString(
                new DTOReqCommon(DateUtil.getCurrentDateTime(), systemId));
        MvcResult result = TestUtil.performRequest(mockMvc,
                POST_ABSENT_STAT_API_URL, requestBodyJson, "POST", 200, 200);

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        int retCode = responseBody.path("retCode").asInt();

        assertEquals(200, retCode);

    }

    @Test
    @DisplayName("부재 로그 배치 작업 성공")
    public void executeDailyBatch() {
        StatService statServiceMock = Mockito.mock(StatService.class);

        AbsentScheduleBatch absentScheduleBatch = new AbsentScheduleBatch(statServiceMock);

        absentScheduleBatch.executeDailyBatch();

        verify(statServiceMock, times(1)).createAbsentLog(null);
    }
    ////////////////////////////////////////////////////////

    @Test
    @Sql("/stat/StatWakeupScheduleSetup.sql")
    @WithMockUser(username = "wakeupUser1", authorities = { "USER" })
    @DisplayName("기상 로그 저장 성공")
    public void createWakeupDailyLogSuccess() throws Exception {

        MvcResult result = TestUtil.performRequest(mockMvc,
                POST_WAKEUP_STAT_API_URL, "{}", "POST", 200,
                200);

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        int retCode = responseBody.path("retCode").asInt();

        assertEquals(200, retCode);

    }

    @Test
    @Sql("/stat/StatWakeupScheduleSetup.sql")
    @WithMockUser(username = "wakeupUser3", authorities = { "USER" })
    @DisplayName("기상 로그 저장 실패 - 기상 정보가 없는 사용자")
    public void createWakeupDailyLogFail() throws Exception {

        MvcResult result = TestUtil.performRequest(mockMvc,
                POST_WAKEUP_STAT_API_URL, "{}", "POST", 200,
                404);

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        int retCode = responseBody.path("retCode").asInt();

        assertEquals(404, retCode);

    }

}
