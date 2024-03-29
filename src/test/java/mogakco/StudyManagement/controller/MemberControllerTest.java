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
import mogakco.StudyManagement.service.external.SendEmailService;
import mogakco.StudyManagement.service.member.MemberService;
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
    private MemberService MemberService;

    @Mock
    private SendEmailService sendEmailService;

    @InjectMocks
    private MemberController memberController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Value("${account.admin.id}")
    private String adminId;

    @Value("${account.admin.password}")
    private String adminPassword;

    private static final String LOGIN_URL = "/api/v1/login";
    private static final String LOGOUT_URL = "/api/v1/logout";
    private static final String JOIN_URL = "/api/v1/join";
    private static final String WITHDRAW_URL = "/api/v1/withdraw";
    private static final String ID_DUPLICATED_CHECK_URL = "/api/v1/join/check-id";
    private static final String MEMBER_INFO_URL = "/api/v1/member";
    private static final String MEMEBER_LIST_URL = "/api/v1/members";
    private static final String MEMBERS_INFO_BY_SCHEDULE_URL = "/api/v1/members/schedule-name";
    private static final String MEMBERS_INFO_BY_WAKEUP_URL = "/api/v1/members/wakeup-time";
    private static final String GET_SCHEDULES_URL = "/api/v1/schedules";
    private static final String GET_WAKEUPS_URL = "/api/v1/wakeup-times";

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 성공")
    void loginSuccess() throws Exception {

        MemberLoginReq req = new MemberLoginReq(adminId, adminPassword);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGIN_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect ID")
    void loginFail_IncorrectId() throws Exception {
        String wrongId = "asdkawqwrjajsd12312";
        MemberLoginReq req = new MemberLoginReq(wrongId, adminPassword);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGIN_URL, requestBodyJson, "POST", 200, 404);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect PWD")
    void loginFail_IncorrectPwd() throws Exception {
        String wrongPwd = "asdkawqwrjajsd12312";
        MemberLoginReq req = new MemberLoginReq(adminId, wrongPwd);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGIN_URL, requestBodyJson, "POST", 200, 400);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그아웃 API 성공")
    void logoutSuccess() throws Exception {
        MemberLoginReq req = new MemberLoginReq(adminId, adminPassword);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, LOGOUT_URL, requestBodyJson, "POST", 200, 200);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 성공")
    @Transactional
    @Sql("/member/MemberSetup.sql")
    void joinSuccess() throws Exception {
        MemberJoinReq req = new MemberJoinReq("user90919239", "password123!", "HongGilDong", "example123@mail.com",
                "모각코 스터디",
                Arrays.asList("AM1", "AM2"),
                "1530");

        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, JOIN_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_invaild ID")
    void joinFail_InvalidId() throws Exception {
        String invaildId = "";
        MemberJoinReq req = new MemberJoinReq(invaildId, "password123!", "HongGilDong", "01011112222", "모각코 스터디",
                Arrays.asList("AM1", "AM2"),
                "1530");

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

        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, JOIN_URL, requestBodyJson, "POST", 400, 400);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "user1", authorities = { "USER" })
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @DisplayName("회원탈퇴 API 성공")
    void withdrawSuccess() throws Exception {
        TestUtil.performRequest(mockMvc, WITHDRAW_URL, null, "DELETE", 200, 200);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @DisplayName("회원탈퇴 API 실패_관리자 권한")
    void withdrawFail_roleAdmin() throws Exception {
        TestUtil.performRequest(mockMvc, WITHDRAW_URL, null, "DELETE", 403, null);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "sajdwqe81236234", authorities = { "USER" })
    @DisplayName("회원탈퇴 API 실패_존재하지 않는 회원 탈퇴")
    void withdrawFail_InvalidId() throws Exception {
        TestUtil.performRequest(mockMvc, WITHDRAW_URL, null, "DELETE", 200, 400);
    }

    /////////////////////////////////////////////////////////////////
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 성공_중복")
    void idDuplicatedSuccess_dupl() throws Exception {
        MemberIdDuplReq req = new MemberIdDuplReq(adminId);

        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 성공_미중복")
    void idDuplicatedSuccess_notDupl() throws Exception {
        String wrongId = "aawadowajdqjdqwdas";
        MemberIdDuplReq req = new MemberIdDuplReq(wrongId);

        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, "POST", 200, 200);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("단일 회원정보 조회 성공")
    public void getMemberInfo_Success() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBER_INFO_URL);

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        System.out.println(result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("회원 목록 조회 성공")
    public void getMemberList_Success() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMEBER_LIST_URL);

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
    }
    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "user1", authorities = { "USER" })
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @DisplayName("MyPage 회원 정보변경 성공")
    void setMemberInfo_Success() throws Exception {
        MemberInfoUpdateReq req = new MemberInfoUpdateReq(MemberUpdateType.SCHEDULE_NAMES, "아무개", "example123@mail.com",
                Arrays.asList("AM1", "AM2", "AM3", "AM4"), "1930", "password123!", "");

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
        MemberInfoUpdateReq req = new MemberInfoUpdateReq(MemberUpdateType.NAME, "", "example123@mail.com",
                Arrays.asList("AM1", "AM2", "AM3", "AM4"), "1930", "password123!", "");

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
        MemberInfoUpdateReq req = new MemberInfoUpdateReq(MemberUpdateType.PASSWORD, "", "example123@mail.com",
                Arrays.asList("AM1"), "1930", wrongPwd, "");

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

        uriBuilder
                .queryParam("schedule", "AM1")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "memberId,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @Transactional
    @Sql("/member/MemberSetup.sql")
    @WithMockUser(username = "user1", authorities = { "USER" })
    @DisplayName("기상 시간별 다수 회원 이름, 아이디 조회 성공")
    public void getMembersByWakeup_Success() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MEMBERS_INFO_BY_WAKEUP_URL);

        uriBuilder
                .queryParam("time", "1530")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "memberId,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
    }

    ////////////////////////////////////////////////////////////////////
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("등록 스케줄 조회 성공")
    public void getRegistedSchedules_Success() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_SCHEDULES_URL);

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        System.out.println(result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    ////////////////////////////////////////////////////////////////////
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("등록 기상 시간 조회 성공")
    public void getRegistedWakeup_Success() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_WAKEUPS_URL);

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        System.out.println(result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

}
