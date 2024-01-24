package mogakco.StudyManagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.enums.MemberUpdateType;
import mogakco.StudyManagement.service.member.impl.MemberServiceImpl;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithMockUser;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("계정 및 권한 테스트")
public class MemberControllerTest {

    @Mock
    private MemberServiceImpl MemberServiceImpl;

    @InjectMocks
    private MemberController memberController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Value("${account.admin.id}")
    private String adminId;

    @Value("${account.admin.password}")
    private String password;

    private static final String LOGIN_URL = "/api/v1/login";
    private static final String JOIN_URL = "/api/v1/join";
    private static final String ID_DUPLICATED_CHECK_URL = "/api/v1/id-duplicated";
    private static final String MEMBER_INFO_URL = "/api/v1/member";
    private static final String MEMBERS_INFO_BY_SCHEDULE_URL = "/api/v1/members/schedule-name";
    private static final String MEMBERS_INFO_BY_WAKEUP_URL = "/api/v1/members/wakeup-time";

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 성공")
    void loginSuccess() throws Exception {
        MemberLoginReq req = new MemberLoginReq(DateUtil.getCurrentDateTime(), "SYS_01", adminId, password);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGIN_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_not include sendDate")
    void loginFail_NotIncludeSendDate() throws Exception {
        MemberLoginReq req = new MemberLoginReq(null, "SYS_01", adminId, password);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGIN_URL, requestBodyJson, "POST", 400, 400);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect ID")
    void loginFail_IncorrectId() throws Exception {
        String wrongId = "asdkawqwrjajsd12312";
        MemberLoginReq req = new MemberLoginReq(DateUtil.getCurrentDateTime(), "SYS_01", wrongId, password);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGIN_URL, requestBodyJson, "POST", 200, 404);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect PWD")
    void loginFail_IncorrectPwd() throws Exception {
        String wrongPwd = "asdkawqwrjajsd12312";
        MemberLoginReq req = new MemberLoginReq(DateUtil.getCurrentDateTime(), "SYS_01", adminId, wrongPwd);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGIN_URL, requestBodyJson, "POST", 200, 400);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 성공")
    @Transactional
    @Sql("/member/MemberSetup.sql")
    void joinSuccess() throws Exception {
        MemberJoinReq req = new MemberJoinReq("user90919239", "password123!", "HongGilDong", "01011112222", "모각코 스터디",
                Arrays.asList("AM1", "AM2"),
                "1530");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, JOIN_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_not include sendDate")
    void joinFail_NotIncludeSendDate() throws Exception {
        MemberJoinReq req = new MemberJoinReq("user1", "password123!", "HongGilDong", "01011112222", "모각코 스터디",
                Arrays.asList("AM1", "AM2"),
                "1530");
        req.setSendDate(null);
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, JOIN_URL, requestBodyJson, "POST", 400, 400);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_invaild ID")
    void joinFail_InvalidId() throws Exception {
        String invaildId = "";
        MemberJoinReq req = new MemberJoinReq(invaildId, "password123!", "HongGilDong", "01011112222", "모각코 스터디",
                Arrays.asList("AM1", "AM2"),
                "1530");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, JOIN_URL, requestBodyJson, "POST", 400, 400);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_invaild PWD")
    void joinFail_InvalidPwd() throws Exception {
        String invaildPwd = "pwd";
        MemberJoinReq req = new MemberJoinReq("user1", invaildPwd, "HongGilDong", "01011112222", "모각코 스터디",
                Arrays.asList("AM1", "AM2"),
                "1530");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, JOIN_URL, requestBodyJson, "POST", 400, 400);
    }

    /////////////////////////////////////////////////////////////////
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 성공_중복")
    void idDuplicatedSuccess_dupl() throws Exception {
        MemberIdDuplReq req = new MemberIdDuplReq(adminId);
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 성공_미중복")
    void idDuplicatedSuccess_notDupl() throws Exception {
        String wrongId = "aawadowajdqjdqwdas";
        MemberIdDuplReq req = new MemberIdDuplReq(wrongId);
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 실패_not include sendDate")
    void idDuplicatedFail_NotIncludeSendDate() throws Exception {
        MemberIdDuplReq req = new MemberIdDuplReq(adminId);
        req.setSendDate(null);
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, "POST", 400, 400);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("단일 회원정보 조회 성공")
    public void getMemberInfo_Success() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBER_INFO_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        System.out.println(result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("단일 회원정보 조회 실패_not include sendDate")
    public void getMemberInfo_NotIncludeSendDate() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBER_INFO_URL);

        uriBuilder.queryParam("systemId", "SYS_01");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
        assertEquals(400, result.getResponse().getStatus());
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "user1", authorities = { "USER" })
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @DisplayName("MyPage 회원 정보변경 성공")
    void setMemberInfo_Success() throws Exception {
        MemberInfoUpdateReq req = new MemberInfoUpdateReq(MemberUpdateType.SCHEDULE_NAMES, "아무개", "010-1111-1111",
                Arrays.asList("AM1", "AM2", "AM3", "AM4"), "1930", "password123!");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, MEMBER_INFO_URL, requestBodyJson, "PATCH", 200, 200);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "USER" })
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @DisplayName("MyPage 회원 정보변경 실패_이름 빈 값")
    // 이름, 스케줄 이름, 비밀번호, 기상 시간 빈 값등은 다 똑같은 실패 케이스로 케이스마다 테스트 코드 추가하지는 않았음
    void setMemberInfo_EmptyName() throws Exception {
        MemberInfoUpdateReq req = new MemberInfoUpdateReq(MemberUpdateType.NAME, "", "010-1111-1111",
                Arrays.asList("AM1", "AM2", "AM3", "AM4"), "1930", "password123!");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, MEMBER_INFO_URL, requestBodyJson, "PATCH", 200, 400);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "USER" })
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @DisplayName("MyPage 회원 정보변경 실패_잘못된 비밀번호 형식")
    void setMemberInfo_WrongPwd() throws Exception {
        String wrongPwd = "p1";
        MemberInfoUpdateReq req = new MemberInfoUpdateReq(MemberUpdateType.PASSWORD, "", "010-1111-1111",
                Arrays.asList("AM1"), "1930", wrongPwd);
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, MEMBER_INFO_URL, requestBodyJson, "PATCH", 400, 400);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @WithMockUser(username = "user1", authorities = { "USER" })
    @DisplayName("운영 타입별 다수 회원 이름, 아이디 조회 성공")
    public void getMembersBySchedule_Success() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBERS_INFO_BY_SCHEDULE_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01")
                .queryParam("schedule", "AM1")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "memberId,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
    }

    @Test
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @WithMockUser(username = "user1", authorities = { "USER" })
    @DisplayName("운영 타입별 다수 회원 이름, 아이디 조회 실패_not include sendDate")
    public void getMembersBySchedule__NotIncludeSendDate() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBERS_INFO_BY_SCHEDULE_URL);

        uriBuilder.queryParam("systemId", "SYS_01")
                .queryParam("schedule", "AM1")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "memberId,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @WithMockUser(username = "user1", authorities = { "USER" })
    @DisplayName("기상 시간별 다수 회원 이름, 아이디 조회 성공")
    public void getMembersByWakeup_Success() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBERS_INFO_BY_WAKEUP_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01")
                .queryParam("time", "1530")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "memberId,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
    }

    @Test
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @WithMockUser(username = "user1", authorities = { "USER" })
    @DisplayName("기상 시간별 다수 회원 이름, 아이디 조회 실패_not include sendDate")
    public void getMembersByWakeup_NotIncludeSendDate() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBERS_INFO_BY_WAKEUP_URL);

        uriBuilder.queryParam("systemId", "SYS_01")
                .queryParam("time", "1530")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "memberId,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }
}
