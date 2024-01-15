package mogakco.StudyManagement.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;

import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.service.member.impl.MemberServiceImpl;

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

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 성공")
    void loginSuccess() throws Exception {
        MemberLoginReq req = new MemberLoginReq();

        req.setSendDate("string");
        req.setSystemId("string");
        req.setId(adminId);
        req.setPassword(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_not include sendDate")
    void loginFail_NotIncludeSendDate() throws Exception {
        MemberLoginReq req = new MemberLoginReq();

        req.setSendDate(null);
        req.setSystemId("string");
        req.setId(adminId);
        req.setPassword(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.retCode").value(404));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect ID")
    void loginFail_IncorrectId() throws Exception {
        MemberLoginReq req = new MemberLoginReq();

        String wrongId = "asdkawqwrjajsd12312";
        req.setSendDate("string");
        req.setSystemId("string");
        req.setId(wrongId);
        req.setPassword(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.retCode").value(404));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("로그인 API 실패_incorrect PWD")
    void loginFail_IncorrectPwd() throws Exception {
        MemberLoginReq req = new MemberLoginReq();

        String wrongPwd = "asdkawqwrjajsd12312";
        req.setSendDate("string");
        req.setSystemId("string");
        req.setId(adminId);
        req.setPassword(wrongPwd);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.retCode").value(400));
    }

    /////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 성공")
    @Sql("/ScheduleSetup.sql")
    void joinSuccess() throws Exception {
        MemberJoinReq req = new MemberJoinReq();

        req.setSendDate("string");
        req.setSystemId("string");
        req.setId("user1");
        req.setPassword("password123!");
        req.setName("HongGilDong");
        req.setContact("01011112222");
        req.setStudyName("모각코 스터디");
        req.setEventName("AM1");
        req.setWakeupTime("1530");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/join")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.retCode").value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_not include sendDate")
    void joinFail_NotIncludeSendDate() throws Exception {
        MemberJoinReq req = new MemberJoinReq();

        req.setSendDate(null);
        req.setSystemId("string");
        req.setId("user1");
        req.setPassword("password123!");
        req.setName("HongGilDong");
        req.setContact("01011112222");
        req.setStudyName("모각코 스터디");
        req.setEventName("AM1");
        req.setWakeupTime("1530");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/join")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.retCode").value(404));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_invaild ID")
    void joinFail_InvalidId() throws Exception {
        MemberJoinReq req = new MemberJoinReq();
        String invaildId = "";

        req.setSendDate(null);
        req.setSystemId("string");
        req.setId(invaildId);
        req.setPassword("password123!");
        req.setName("HongGilDong");
        req.setContact("01011112222");
        req.setStudyName("모각코 스터디");
        req.setEventName("AM1");
        req.setWakeupTime("1530");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/join")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.retCode").value(400));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("회원가입 API 실패_invaild PWD")
    void joinFail_InvalidPwd() throws Exception {
        MemberJoinReq req = new MemberJoinReq();
        String invaildPwd = "pwd";

        req.setSendDate(null);
        req.setSystemId("string");
        req.setId("user99199");
        req.setPassword(invaildPwd);
        req.setName("HongGilDong");
        req.setContact("01011112222");
        req.setStudyName("모각코 스터디");
        req.setEventName("AM1");
        req.setWakeupTime("1530");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/join")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.retCode").value(400));
    }

    /////////////////////////////////////////////////////////////////
    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 성공_중복")
    void idDuplicatedSuccess_dupl() throws Exception {
        MemberIdDuplReq req = new MemberIdDuplReq();
        req.setSendDate("string");
        req.setSystemId("string");
        req.setId(adminId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/id-duplicated")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.duplicated").value(true));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 성공_미중복")
    void idDuplicatedSuccess_notDupl() throws Exception {
        MemberIdDuplReq req = new MemberIdDuplReq();

        String wrongId = "aawadowajdqjdqwdas";
        req.setSendDate("string");
        req.setSystemId("string");
        req.setId(wrongId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/id-duplicated")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.duplicated").value(false));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("아이디 중복 API 실패_not include sendDate")
    void idDuplicatedFail_NotIncludeSendDate() throws Exception {
        MemberIdDuplReq req = new MemberIdDuplReq();

        req.setSendDate(null);
        req.setSystemId("string");
        req.setId(adminId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/id-duplicated")
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.retCode").value(404));
    }

}
