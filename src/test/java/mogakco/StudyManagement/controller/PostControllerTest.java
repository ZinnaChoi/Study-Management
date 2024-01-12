package mogakco.StudyManagement.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import mogakco.StudyManagement.dto.PostCreateReq;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private PostController postController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String CREATE_POST_API_URL = "/api/v1/posts";

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createPostSuccess() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostCreateReq(DateUtil.getCurrentDateTime(), "SYS_01", "2024 2월 개발 뉴스 공유드립니다",
                        "chatGPT 5.0 도입"));
        TestUtil.performPostRequest(mockMvc, CREATE_POST_API_URL, requestBodyJson, 200,
                200);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createPostFailEmptyTitle() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostCreateReq(DateUtil.getCurrentDateTime(), "SYS_01", null, "chatGPT 5.0 도입"));
        TestUtil.performPostRequest(mockMvc, CREATE_POST_API_URL, requestBodyJson, 400, 400);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createPostFailInvalidJson() throws Exception {
        String invalidJson = "{\n" +
                "  \"sendDate\": \"20240112132910401\",\n" +
                "  \"systemId\": \"SYS_01\",\n" +
                "  \"title\": \"2024 2월 개발 뉴스 공유드립니다\",\n" +
                "  \"content\": \"chatGPT 5.0 도입\"\n"; // Bad Request (Missing closing brace)

        TestUtil.performPostRequest(mockMvc, CREATE_POST_API_URL, invalidJson, 400, 400);
    }

}
