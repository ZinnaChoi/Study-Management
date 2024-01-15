package mogakco.StudyManagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import mogakco.StudyManagement.dto.PostReq;
import mogakco.StudyManagement.enums.PostSearchType;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
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
    private static final String GET_POSTS_API_URL = "/api/v1/posts";

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void createPostSuccess() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostReq(DateUtil.getCurrentDateTime(), "SYS_01", "2024 2월 개발 뉴스 공유드립니다",
                        "chatGPT 5.0 도입"));
        TestUtil.performPostRequest(mockMvc, CREATE_POST_API_URL, requestBodyJson, 200, 200);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void createPostFailEmptyTitle() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostReq(DateUtil.getCurrentDateTime(), "SYS_01", null, "chatGPT 5.0 도입"));
        TestUtil.performPostRequest(mockMvc, CREATE_POST_API_URL, requestBodyJson, 400, 400);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void createPostFailInvalidJson() throws Exception {
        String invalidJson = "{\n" +
                "  \"sendDate\": \"20240112132910401\",\n" +
                "  \"systemId\": \"SYS_01\",\n" +
                "  \"title\": \"2024 2월 개발 뉴스 공유드립니다\",\n" +
                "  \"content\": \"chatGPT 5.0 도입\"\n"; // Bad Request (Missing closing brace)

        TestUtil.performPostRequest(mockMvc, CREATE_POST_API_URL, invalidJson, 400, 400);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @Sql("/post/PostListSetup.sql")
    public void getPostListSuccessPage0Size3() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_POSTS_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01")
                .queryParam("searchKeyWord", "post")
                .queryParam("searchType", PostSearchType.TITLE)
                .queryParam("page", 0)
                .queryParam("size", 3)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performGetRequest(mockMvc, uriBuilder.toUriString(), 200);

        String content = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(content);

        int contentCount = rootNode.path("content").size();
        assertTrue(contentCount == 3);

        for (JsonNode element : rootNode.path("content")) {
            String title = element.path("title").asText();
            assertTrue(title.startsWith("post4"));
            break;
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @Sql("/post/PostListSetup.sql")
    public void getPostListSuccessSearchMember() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_POSTS_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01")
                .queryParam("searchKeyWord", "PostUser")
                .queryParam("searchType", PostSearchType.MEMBER)
                .queryParam("page", 0)
                .queryParam("size", 4)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performGetRequest(mockMvc, uriBuilder.toUriString(), 200);

        String content = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(content);

        int contentCount = rootNode.path("content").size();
        assertTrue(contentCount == 4);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @Sql("/post/PostListSetup.sql")
    public void getPostListSuccessPage1Size2() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_POSTS_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01")
                .queryParam("searchKeyWord", "post")
                .queryParam("searchType", PostSearchType.TITLE)
                .queryParam("page", 1)
                .queryParam("size", 2)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performGetRequest(mockMvc, uriBuilder.toUriString(), 200);

        String content = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(content);

        int contentCount = rootNode.path("content").size();
        assertTrue(contentCount == 2);

        for (JsonNode element : rootNode.path("content")) {
            String title = element.path("title").asText();
            assertTrue(title.startsWith("post2"));
            break;
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @Sql("/post/PostListSetup.sql")
    public void getPostListFailInvalidSearchType() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_POSTS_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01")
                .queryParam("searchKeyWord", "post")
                .queryParam("searchType", "INVALID_TYPE") // Invalid SearchType
                .queryParam("page", 0)
                .queryParam("size", 3)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performGetRequest(mockMvc, uriBuilder.toUriString(), 400);
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @Sql("/post/PostListSetup.sql")
    public void getPostListFailEmptySearchKeyWord() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(GET_POSTS_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", "SYS_01")
                .queryParam("searchKeyWord", "")
                .queryParam("searchType", PostSearchType.TITLE)
                .queryParam("page", 0)
                .queryParam("size", 3)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performGetRequest(mockMvc, uriBuilder.toUriString(), 400);
        assertEquals(400, result.getResponse().getStatus());
    }

}
