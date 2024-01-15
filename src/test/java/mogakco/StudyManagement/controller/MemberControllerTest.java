package mogakco.StudyManagement.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.service.member.impl.MemberServiceImpl;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithMockUser;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
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

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 성공")
    void loginSuccess() throws Exception {
        MemberLoginReq req = new MemberLoginReq(DateUtil.getCurrentDateTime(), "SYS_01", adminId, password);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, LOGIN_URL, requestBodyJson, 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_not include sendDate")
    void loginFail_NotIncludeSendDate() throws Exception {
        MemberLoginReq req = new MemberLoginReq(null, "SYS_01", adminId, password);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, LOGIN_URL, requestBodyJson, 200, 404);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect ID")
    void loginFail_IncorrectId() throws Exception {
        String wrongId = "asdkawqwrjajsd12312";
        MemberLoginReq req = new MemberLoginReq(DateUtil.getCurrentDateTime(), "SYS_01", wrongId, password);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, LOGIN_URL, requestBodyJson, 200, 404);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect PWD")
    void loginFail_IncorrectPwd() throws Exception {
        String wrongPwd = "asdkawqwrjajsd12312";
        MemberLoginReq req = new MemberLoginReq(DateUtil.getCurrentDateTime(), "SYS_01", adminId, wrongPwd);
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, LOGIN_URL, requestBodyJson, 200, 400);
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 성공")
    @Sql("/ScheduleSetup.sql")
    void joinSuccess() throws Exception {
        MemberJoinReq req = new MemberJoinReq("user1", "password123!", "HongGilDong", "01011112222", "모각코 스터디", "AM1",
                "1530");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, JOIN_URL, requestBodyJson, 200, 200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_not include sendDate")
    void joinFail_NotIncludeSendDate() throws Exception {
        MemberJoinReq req = new MemberJoinReq("user1", "password123!", "HongGilDong", "01011112222", "모각코 스터디", "AM1",
                "1530");
        req.setSendDate(null);
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, JOIN_URL, requestBodyJson, 200, 404);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_invaild ID")
    void joinFail_InvalidId() throws Exception {
        String invaildId = "";
        MemberJoinReq req = new MemberJoinReq(invaildId, "password123!", "HongGilDong", "01011112222", "모각코 스터디", "AM1",
                "1530");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, JOIN_URL, requestBodyJson, 400, 400);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_invaild PWD")
    void joinFail_InvalidPwd() throws Exception {
        String invaildPwd = "pwd";
        MemberJoinReq req = new MemberJoinReq("user1", invaildPwd, "HongGilDong", "01011112222", "모각코 스터디", "AM1",
                "1530");
        req.setSendDate(DateUtil.getCurrentDateTime());
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, JOIN_URL, requestBodyJson, 400, 400);
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
        String expression = "duplicated";

        TestUtil.performPostRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, 200, true, expression);
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
        String expression = "duplicated";

        TestUtil.performPostRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, 200, false, expression);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 실패_not include sendDate")
    void idDuplicatedFail_NotIncludeSendDate() throws Exception {
        MemberIdDuplReq req = new MemberIdDuplReq(adminId);
        req.setSendDate(null);
        req.setSystemId("SYS_01");
        String requestBodyJson = objectMapper.writeValueAsString(req);

        TestUtil.performPostRequest(mockMvc, ID_DUPLICATED_CHECK_URL, requestBodyJson, 200, 404);
    }

}
