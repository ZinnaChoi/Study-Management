package mogakco.StudyManagement.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
@DisplayName("게시글 테스트")
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${study.systemId}")
    private String systemId;

    private static final String POST_API_URL = "/api/v1/posts";

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 등록 성공")
    public void createPostSuccess() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostReq(DateUtil.getCurrentDateTime(), systemId, "2024 2월 개발 뉴스 공유드립니다",
                        "chatGPT 5.0 도입"));
        TestUtil.performRequest(mockMvc, POST_API_URL, requestBodyJson, "POST", 200, 200);
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 등록 실패 - 빈 제목")
    public void createPostFailEmptyTitle() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostReq(DateUtil.getCurrentDateTime(), systemId, null, "chatGPT 5.0 도입"));
        TestUtil.performRequest(mockMvc, POST_API_URL, requestBodyJson, "POST", 400, 400);
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 등록 실패 - 잘못된 요청 JSON 형식")
    public void createPostFailInvalidJson() throws Exception {
        String invalidJson = "{\n" +
                "  \"sendDate\": \"20240112132910401\",\n" +
                "  \"systemId\": \"ABC\",\n" +
                "  \"title\": \"2024 2월 개발 뉴스 공유드립니다\",\n" +
                "  \"content\": \"chatGPT 5.0 도입\"\n"; // Bad Request (Missing closing brace)

        TestUtil.performRequest(mockMvc, POST_API_URL, invalidJson, "POST", 400, 400);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 목록 조회 성공 - Page 0 Size 3")
    public void getPostListSuccessPage0Size3() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(POST_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("searchKeyWord", "post")
                .queryParam("searchType", PostSearchType.TITLE)
                .queryParam("page", 0)
                .queryParam("size", 3)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 3);

        for (JsonNode element : responseBody.path("content")) {
            String title = element.path("title").asText();
            assertTrue(title.startsWith("post4"));
            break;
        }
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 목록 조회 성공 - 작성자 이름으로 조회")
    public void getPostListSuccessSearchMember() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(POST_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("searchKeyWord", "PostUser")
                .queryParam("searchType", PostSearchType.MEMBER)
                .queryParam("page", 0)
                .queryParam("size", 4)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 4);
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 목록 조회 성공 - Page 1 Size 2")
    public void getPostListSuccessPage1Size2() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(POST_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("searchKeyWord", "post")
                .queryParam("searchType", PostSearchType.TITLE)
                .queryParam("page", 1)
                .queryParam("size", 2)
                .queryParam("sort", "title,desc");

        MvcResult result = TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 200, 200);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        int contentCount = responseBody.path("content").size();
        assertTrue(contentCount == 2);

        for (JsonNode element : responseBody.path("content")) {
            String title = element.path("title").asText();
            assertTrue(title.startsWith("post2"));
            break;
        }
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 목록 조회 실패 - 잘못된 검색 타입")
    public void getPostListFailInvalidSearchType() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(POST_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("searchKeyWord", "post")
                .queryParam("searchType", "INVALID_TYPE") // Invalid SearchType
                .queryParam("page", 0)
                .queryParam("size", 3)
                .queryParam("sort", "title,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 목록 조회 실패 - 빈 검색어")
    public void getPostListFailEmptySearchKeyWord() throws Exception {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(POST_API_URL);

        uriBuilder.queryParam("sendDate", DateUtil.getCurrentDateTime())
                .queryParam("systemId", systemId)
                .queryParam("searchKeyWord", "")
                .queryParam("searchType", PostSearchType.TITLE)
                .queryParam("page", 0)
                .queryParam("size", 3)
                .queryParam("sort", "title,desc");

        TestUtil.performRequest(mockMvc, uriBuilder.toUriString(), null, "GET", 400, 400);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 수정 성공")
    public void updatePostSuccess() throws Exception {

        String requestBodyJson = objectMapper.writeValueAsString(
                new PostReq(DateUtil.getCurrentDateTime(), systemId, "Updated Title", "Updated Content"));

        MvcResult result = TestUtil.performRequest(mockMvc,
                POST_API_URL + "/" + getLatestPostIdByMemberId("PostUser"), requestBodyJson, "PATCH", 200, 200);
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

        for (JsonNode element : responseBody.path("content")) {
            String title = element.path("title").asText();
            assertTrue(title.startsWith("Updated Content"));
            break;
        }
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 수정 실패 - 잘못된 게시글 번호")
    public void updatePostFailPostNotFound() throws Exception {
        String requestBodyJson = objectMapper.writeValueAsString(
                new PostReq(DateUtil.getCurrentDateTime(), systemId, "Updated Title", "Updated Content"));

        // Not Exist postId -1
        TestUtil.performRequest(mockMvc,
                POST_API_URL + "/" + -1, requestBodyJson, "PATCH", 200, 404);
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser2", authorities = { "USER" })
    @DisplayName("게시글 수정 실패 - 잘못된 사용자")
    public void updatePostFailPostDiffMember() throws Exception {

        String requestBodyJson = objectMapper.writeValueAsString(
                new PostReq(DateUtil.getCurrentDateTime(), systemId, "Updated Title", "Updated Content"));

        TestUtil.performRequest(mockMvc,
                POST_API_URL + "/" + getLatestPostIdByMemberId("PostUser"), requestBodyJson, "PATCH", 200, 400);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 삭제 성공")
    public void deletePostSuccess() throws Exception {

        TestUtil.performRequest(mockMvc,
                POST_API_URL + "/" + getLatestPostIdByMemberId("PostUser"), null, "DELETE", 200, 204);
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 삭제 실패 - 잘못된 게시글 번호")
    public void deletePostFailNotFound() throws Exception {
        TestUtil.performRequest(mockMvc,
                POST_API_URL + "/" + -1, null, "DELETE", 200, 404); // Not Exist PostId
    }

    @Test
    @Sql("/post/PostListSetup.sql")
    @WithMockUser(username = "PostUser2", authorities = { "USER" })
    @DisplayName("게시글 삭제 실패 - 잘못된 사용자")
    public void deletePostFailDiffMember() throws Exception {
        TestUtil.performRequest(mockMvc,
                POST_API_URL + "/" + getLatestPostIdByMemberId("PostUser"), null, "DELETE", 200, 400);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Long getLatestPostIdByMemberId(String memberId) {
        return jdbcTemplate.queryForObject(
                "SELECT post_id FROM post WHERE member_id = (SELECT member_id FROM member WHERE id = ?) ORDER BY created_at DESC LIMIT 1",
                Long.class,
                memberId);
    }

}
