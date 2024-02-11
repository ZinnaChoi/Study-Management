package mogakco.StudyManagement.controller;

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
import org.springframework.transaction.annotation.Transactional;

import mogakco.StudyManagement.service.external.SendEmailService;
import mogakco.StudyManagement.service.post.PostLikeService;
import mogakco.StudyManagement.util.TestUtil;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DisplayName("게시판 좋아요 테스트")
public class PostLikeControllerTest {

    @Mock
    private PostLikeService postLikeService;

    @Mock
    private SendEmailService sendEmailService;

    @InjectMocks
    private PostLikeController postLikeController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${study.systemId}")
    private String systemId;

    private static final String POST_LIKE_API_URL = "/api/v1/posts/{postId}/likes";

    @Test
    @Sql("/post/PostLikeSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시글 좋아요 등록 성공")
    public void registerPostLikeSuccess() throws Exception {
        String requestBodyJson = "{}";
        String url = POST_LIKE_API_URL.replace("{postId}", getLatestPostIdByMemberId("PostUser").toString());
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 201);
    }

    @Test
    @Sql("/post/PostLikeSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시판 좋아요 등록 실패 - 존재하지 않는 게시글 번호")
    public void registerPostLikeFailInvalidPostId() throws Exception {
        String requestBodyJson = "{}";
        String url = POST_LIKE_API_URL.replace("{postId}", "-1");
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 404);
    }

    @Test
    @Sql("/post/PostLikeSetup.sql")
    @WithMockUser(username = "PostUser2", authorities = { "USER" })
    @DisplayName("게시판 좋아요 등록 실패 - 이미 좋아요한 게시글")
    public void registerPostLikeFailAlreadyLiked() throws Exception {
        String requestBodyJson = "{}";
        String url = POST_LIKE_API_URL.replace("{postId}", getLatestPostIdByMemberId("PostUser").toString());
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 409);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    @Sql("/post/PostLikeSetup.sql")
    @WithMockUser(username = "PostUser2", authorities = { "USER" })
    @DisplayName("게시글 좋아요 취소 성공")
    public void cancelPostLikeSuccess() throws Exception {
        String requestBodyJson = "{}";
        String url = POST_LIKE_API_URL.replace("{postId}", getLatestPostIdByMemberId("PostUser").toString());
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "DELETE", 200, 204);
    }

    @Test
    @Sql("/post/PostLikeSetup.sql")
    @WithMockUser(username = "PostUser1", authorities = { "USER" })
    @DisplayName("게시글 좋아요 취소 실패 - 존재하지 않는 좋아요")
    public void cancelPostLikeFailLikeNotFound() throws Exception {
        String requestBodyJson = "{}";
        String url = POST_LIKE_API_URL.replace("{postId}", getLatestPostIdByMemberId("PostUser").toString());
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "DELETE", 200, 404);
    }

    @Test
    @Sql("/post/PostLikeSetup.sql")
    @WithMockUser(username = "PostUser1", authorities = { "USER" })
    @DisplayName("게시글 좋아요 취소 실패 - 존재하지 않는 게시글")
    public void cancelPostLikeFailPostNotFound() throws Exception {
        String requestBodyJson = "{}";
        String url = POST_LIKE_API_URL.replace("{postId}", "-1");
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "DELETE", 200, 404);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Long getLatestPostIdByMemberId(String memberId) {
        return jdbcTemplate.queryForObject(
                "SELECT post_id FROM post WHERE member_id = (SELECT member_id FROM member WHERE id = ?) AND title = 'post1'",
                Long.class,
                memberId);
    }

}
