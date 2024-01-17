package mogakco.StudyManagement.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import org.springframework.web.util.UriComponentsBuilder;

import mogakco.StudyManagement.dto.AbsentRgstReq;
import mogakco.StudyManagement.service.absent.AbsentService;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DisplayName("부재일정 테스트")
public class AbsentControllerTest {

    @Mock
    private AbsentService absentService;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private AbsentController absentController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Value("${study.systemId}")
    private String systemId;

    private static final String ABSENT_API_URL = "/api/v1/absent";

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 성공")
    public void registerAbsentScheduleSuccess() throws Exception {
        AbsentRgstReq request = new AbsentRgstReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
                Arrays.asList("TESTPM1", "TESTPM3"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 존재하지 않는 이벤트 이름")
    public void registerAbsentScheduleFailNotFound() throws Exception {
        AbsentRgstReq request = new AbsentRgstReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
                Arrays.asList("TESTAM1", "TESTAM3"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 404);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 참여하지 않는 스터디 타임에 부재 요청")
    public void registerAbsentScheduleFailInvalidRequest() throws Exception {
        AbsentRgstReq request = new AbsentRgstReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
                Arrays.asList("TESTPM9"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 400);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 이미 등록된 부재 일정")
    public void registerAbsentScheduleFailConflict() throws Exception {
        AbsentRgstReq request = new AbsentRgstReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "개인 사유",
                Arrays.asList("TESTPM1"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 409);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 부재 일자 형식 불일치")
    public void registerAbsentScheduleFailInvalidDate() throws Exception {
        AbsentRgstReq request = new AbsentRgstReq(DateUtil.getCurrentDateTime(), systemId, "2024-01-17", "개인 사유",
                Arrays.asList("TESTPM1"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        MvcResult result = TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 400, 400);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        assertTrue(responseBody.path("retMsg").asText().contains("yyyyMMdd"));
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - eventNameList 형식 불일치")
    public void registerAbsentScheduleFailInvalidEventNameList() throws Exception {
        String invalidEventNameList = "\"TESTPM1\"";
        String requestBodyJson = "{\"sendDate\":\"" + DateUtil.getCurrentDateTime() + "\", \"systemId\":\"" + systemId
                + "\", \"absentDate\":\"20240117\", \"description\":\"개인 사유\", \"eventNameList\":"
                + invalidEventNameList + "}";

        MvcResult result = TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 400, 400);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        assertTrue(responseBody.path("retMsg").asText().contains("JSON parse error"));
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 빈 eventNameList 요청")
    public void registerAbsentScheduleFailEmptyEventNameList() throws Exception {
        AbsentRgstReq request = new AbsentRgstReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
                Arrays.asList());
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 400);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 성공 - 전체 member의 부재 일정 조회")
    public void getAbsentScheduleSuccessAll() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("yearMonth", "202401")
                .queryParam("memberNameList", Arrays.asList());

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);

    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 성공 - 특정 member의 부재 일정 조회")
    public void getAbsentScheduleSuccesSpecificMembers() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("yearMonth", "202401")
                .queryParam("memberNameList", Arrays.asList("AbsentUser", "AbsentUser2"));

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 3);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 실패 - 존재하지 않는 Member 부재일정 조회")
    public void getAbsentScheduleFailMemberNotFound() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("yearMonth", "202401")
                .queryParam("memberNameList", Arrays.asList("NotValidUser"));

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 404);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 실패 - 잘못된 YearMonth 형식")
    public void getAbsentScheduleFailInvalidYearMonthFormat() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("yearMonth", "2024-01")
                .queryParam("memberNameList", Arrays.asList());

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 실패 - 잘못된 memberNameList 형식")
    public void getAbsentScheduleFailInvalidmemberNameListFormat() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("yearMonth", "202401")
                .queryParam("memberNameList", "AbsentUser");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }

}
