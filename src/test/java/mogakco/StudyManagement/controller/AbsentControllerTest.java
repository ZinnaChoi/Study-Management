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

import mogakco.StudyManagement.dto.AbsentReq;
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
    private static final String ABSENT_CALENDAR_GET_URL = "/api/v1/absent/calendar";
    private static final String ABSENT_DETAIL_GET_URL = "/api/v1/absent/detail";

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 성공")
    public void registerAbsentScheduleSuccess() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
                Arrays.asList("TESTPM1", "TESTPM3"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 201);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 존재하지 않는 이벤트 이름")
    public void registerAbsentScheduleFailNotFound() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
                Arrays.asList("TESTAM1", "TESTAM3"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 404);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 참여하지 않는 스터디 타임에 부재 요청")
    public void registerAbsentScheduleFailInvalidRequest() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
                Arrays.asList("TESTPM9"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 400);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 이미 등록된 부재 일정")
    public void registerAbsentScheduleFailConflict() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "개인 사유",
                Arrays.asList("TESTPM1"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "POST", 200, 409);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 등록 실패 - 부재 일자 형식 불일치")
    public void registerAbsentScheduleFailInvalidDate() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "2024-01-17", "개인 사유",
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
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240117", "개인 사유",
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

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_CALENDAR_GET_URL);

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
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_CALENDAR_GET_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("yearMonth", "202401")
                .queryParam("memberNameList", Arrays.asList("AbsentUser", "AbsentUser2"));

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 2);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 실패 - 존재하지 않는 Member 부재일정 조회")
    public void getAbsentScheduleFailMemberNotFound() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_CALENDAR_GET_URL);

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
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_CALENDAR_GET_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("yearMonth", "2024-01")
                .queryParam("memberNameList", Arrays.asList());

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 성공 - 특정 날짜의 부재일정 상세 조회")
    public void getAbsentScheduleDetailSuccessAll() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_DETAIL_GET_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("absentDate", "20240116");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 조회 실패 - 잘못된 absentDate 형식")
    public void getAbsentScheduleDetailFailInvalidAbsentDateFormat() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(ABSENT_DETAIL_GET_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("absentDate", "2024-01-16");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 수정 성공 - 새로운 부재일정 추가")
    public void updateAbsentScheduleSuccessAddSchedule() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "가족여행",
                Arrays.asList("TESTPM1", "TESTPM7"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "PATCH", 200, 200);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser2", authorities = { "USER" })
    @DisplayName("부재일정 수정 성공 - 부재일정 일부 삭제")
    public void updateAbsentScheduleSuccessRemoveSomeSchedule() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "가족여행",
                Arrays.asList("TESTPM1"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "PATCH", 200, 200);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 수정 성공 - 부재사유 변경")
    public void updateAbsentScheduleSuccessChangeDescription() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "개인 사유",
                Arrays.asList("TESTPM1"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "PATCH", 200, 200);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 수정 안됨 - 변경사항 없음")
    public void updateAbsentScheduleFailNotChanged() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "가족여행",
                Arrays.asList("TESTPM1"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "PATCH", 200, 204);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 수정 실패 - 잘못된 absentDate 형식")
    public void updateAbsentScheduleFailInvalidAbsentDateFormat() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "2024-01-16", "가족여행",
                Arrays.asList("TESTPM1"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "PATCH", 400, 400);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 수정 실패 - 빈 스터디 시간")
    public void updateAbsentScheduleFailInvalidEmptyEventTime() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "가족여행",
                Arrays.asList());
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "PATCH", 200, 400);
    }

    @Test
    @Sql("/absent/AbsentSetup.sql")
    @WithMockUser(username = "AbsentUser", authorities = { "USER" })
    @DisplayName("부재일정 수정 실패 - 존재하지 않는 스터디 시간")
    public void updateAbsentScheduleFailNotFoundEventName() throws Exception {
        AbsentReq request = new AbsentReq(DateUtil.getCurrentDateTime(), systemId, "20240116", "가족여행",
                Arrays.asList("INVALID TESTTIME"));
        String requestBodyJson = objectMapper.writeValueAsString(request);

        TestUtil.performRequest(mockMvc, ABSENT_API_URL, requestBodyJson, "PATCH", 200, 404);
    }

}