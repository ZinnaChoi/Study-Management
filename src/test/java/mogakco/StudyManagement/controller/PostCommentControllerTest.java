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

import mogakco.StudyManagement.dto.PostCommentReq;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.post.PostCommentService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.TestUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DisplayName("게시판 댓글 테스트")
public class PostCommentControllerTest {

    @Mock
    private PostCommentService postCommentService;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private PostCommentController postCommentController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${study.systemId}")
    private String systemId;

    private static final String POST_COMMENT_API_URL = "/api/v1/posts/{postId}/comments";

    @Test
    @Sql("/post/PostCommentSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시판 댓글 등록 성공")
    public void registerPostCommentSuccess() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostCommentReq(DateUtil.getCurrentDateTime(), systemId, "Thank you"));
        String url = POST_COMMENT_API_URL.replace("{postId}", getLatestPostIdByMemberId("PostUser").toString());
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 201);
    }

    @Test
    @Sql("/post/PostCommentSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시판 댓글 등록 실패 - 존재하지 않는 게시글 번호")
    public void registerPostCommentFailInvalidPostId() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostCommentReq(DateUtil.getCurrentDateTime(), systemId, "Thank you"));
        String url = POST_COMMENT_API_URL.replace("{postId}", "-1");
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 404);
    }

    @Test
    @Sql("/post/PostCommentSetup.sql")
    @WithMockUser(username = "PostUser", authorities = { "USER" })
    @DisplayName("게시판 댓글 등록 실패 - 빈 댓글 요청")
    public void registerPostCommentFailEmptyContent() throws Exception {
        String requestBodyJson = objectMapper
                .writeValueAsString(new PostCommentReq(DateUtil.getCurrentDateTime(), systemId, ""));
        String url = POST_COMMENT_API_URL.replace("{postId}", getLatestPostIdByMemberId("PostUser").toString());
        TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 400, 400);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Long getLatestPostIdByMemberId(String memberId) {
        return jdbcTemplate.queryForObject(
                "SELECT post_id FROM post WHERE member_id = (SELECT member_id FROM member WHERE id = ?) ORDER BY created_at DESC LIMIT 1",
                Long.class,
                memberId);
    }

}
