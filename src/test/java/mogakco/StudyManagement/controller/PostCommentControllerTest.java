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

import mogakco.StudyManagement.dto.PostCommentReq;
import mogakco.StudyManagement.service.external.SendEmailService;
import mogakco.StudyManagement.service.post.PostCommentService;
import mogakco.StudyManagement.util.TestUtil;

import com.fasterxml.jackson.databind.JsonNode;
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
        private SendEmailService sendEmailService;

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

        private static final String POST_COMMENT_CREATE_API_URL = "/api/v1/posts/{postId}/comments";
        private static final String POST_COMMENT_UPDATE_DELETE_API_URL = "/api/v1/posts/{postId}/comments/{commentId}";
        private static final String POST_COMMENT_REPLY_API_URL = "/api/v1/posts/{postId}/comments/{commentId}/replies";

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글 등록 성공")
        public void registerPostCommentSuccess() throws Exception {
                String requestBodyJson = objectMapper
                                .writeValueAsString(new PostCommentReq(
                                                "Thank you"));
                String url = POST_COMMENT_CREATE_API_URL.replace("{postId}",
                                getLatestPostIdByMemberId("PostUser").toString());
                TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 201);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글 등록 실패 - 존재하지 않는 게시글 번호")
        public void registerPostCommentFailInvalidPostId() throws Exception {
                String requestBodyJson = objectMapper
                                .writeValueAsString(new PostCommentReq(
                                                "Thank you"));
                String url = POST_COMMENT_CREATE_API_URL.replace("{postId}", "-1");
                TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 404);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글 등록 실패 - 빈 댓글 요청")
        public void registerPostCommentFailEmptyContent() throws Exception {
                String url = POST_COMMENT_CREATE_API_URL.replace("{postId}",
                                getLatestPostIdByMemberId("PostUser").toString());
                TestUtil.performRequest(mockMvc, url, null, "POST", 400, 400);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 답글 등록 성공")
        public void registerPostCommentReplySuccess() throws Exception {
                String requestBodyJson = objectMapper
                                .writeValueAsString(
                                                new PostCommentReq("Good~!"));
                String url = POST_COMMENT_REPLY_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 201);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 답글 등록 실패 - 존재하지 않는 게시글")
        public void registerPostCommentReplyFailPostNotFound() throws Exception {
                String requestBodyJson = objectMapper
                                .writeValueAsString(
                                                new PostCommentReq("Good~!"));
                String url = POST_COMMENT_REPLY_API_URL.replace("{postId}", "-1")
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 404);

        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 답글 등록 실패 - 존재하지 않는 게시판 댓글")
        public void registerPostCommentReplyFailPostCommentNotFound() throws Exception {
                String requestBodyJson = objectMapper
                                .writeValueAsString(
                                                new PostCommentReq("Good~!"));
                String url = POST_COMMENT_REPLY_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", "-1");

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 404);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 답글 등록 실패 - 답글에 답글 생성 요청")
        public void registerPostCommentReplyFailInvalidRequest() throws Exception {
                String requestBodyJson = objectMapper
                                .writeValueAsString(
                                                new PostCommentReq("Good~!"));
                String url = POST_COMMENT_REPLY_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("reply1", "PostUser")
                                                .toString());

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "POST", 200, 400);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @DisplayName("게시판 답글 조회 성공")
        public void getPostCommentReplySuccess() throws Exception {
                String url = POST_COMMENT_REPLY_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());
                MvcResult result = TestUtil.performRequest(mockMvc, url, null, "GET", 200, 200);

                JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

                int replyCnt = responseBody.path("postCommentReplies").size();
                assertTrue(replyCnt == 1);

        }

        @DisplayName("게시판 답글 조회 실패 - 존재하지 않는 게시글 번호")
        public void getPostCommentReplyFailPostNotFound() throws Exception {
                String url = POST_COMMENT_REPLY_API_URL
                                .replace("{postId}", "-1")
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());
                TestUtil.performRequest(mockMvc, url, null, "GET", 200, 404);
        }

        @DisplayName("게시판 답글 조회 실패 - 존재하지 않는 댓글 번호")
        public void getPostCommentReplyFailCommentNotFound() throws Exception {
                String url = POST_COMMENT_REPLY_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", "-1");
                TestUtil.performRequest(mockMvc, url, null, "GET", 200, 404);
        }

        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 답글 조회 실패 - 답글의 답글 조회 요청")
        public void getPostCommentReplyFailInvalidComment() throws Exception {
                String url = POST_COMMENT_REPLY_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("reply1", "PostUser")
                                                .toString());
                TestUtil.performRequest(mockMvc, url, null, "GET", 200, 400);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 수정 성공")
        public void updatePostCommentSuccess() throws Exception {
                String requestBodyJson = objectMapper.writeValueAsString(
                                new PostCommentReq("Updated Content"));

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "PATCH", 200, 200);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 수정 실패 - 존재하지 않는 게시글")
        public void updatePostCommentFailPostNotFound() throws Exception {
                String requestBodyJson = objectMapper.writeValueAsString(
                                new PostCommentReq("Updated Content"));

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", "-1")
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "PATCH", 200, 404);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 수정 실패 - 존재하지 않는 댓글")
        public void updatePostCommentFailCommentNotFound() throws Exception {
                String requestBodyJson = objectMapper.writeValueAsString(
                                new PostCommentReq("Updated Content"));

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", "-1");

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "PATCH", 200, 404);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser2", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 수정 실패 - 작성하지 않은 댓글(답글)")
        public void updatePostCommentFailInvalidMember() throws Exception {
                String requestBodyJson = objectMapper.writeValueAsString(
                                new PostCommentReq("Updated Content"));

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());

                TestUtil.performRequest(mockMvc, url, requestBodyJson, "PATCH", 200, 400);

        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 삭제 성공")
        public void deletePostCommentSuccess() throws Exception {

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());
                TestUtil.performRequest(mockMvc, url, null, "DELETE", 200, 204);

        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 삭제 실패 - 존재하지 않는 게시글 번호")
        public void deletePostCommentFailPostNotFound() throws Exception {

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", "-1")
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());
                TestUtil.performRequest(mockMvc, url, null, "DELETE", 200, 404);

        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 삭제 실패 - 존재하지 않는 댓글(답글) 번호")
        public void deletePostCommentFailCommentNotFound() throws Exception {

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", "-1");
                TestUtil.performRequest(mockMvc, url, null, "DELETE", 200, 404);
        }

        @Test
        @Sql("/post/PostCommentSetup.sql")
        @WithMockUser(username = "PostUser2", authorities = { "USER" })
        @DisplayName("게시판 댓글(답글) 삭제 실패 - 잘못된 사용자 ")
        public void deletePostCommentFailInvalidMember() throws Exception {

                String url = POST_COMMENT_UPDATE_DELETE_API_URL
                                .replace("{postId}", getLatestPostIdByMemberId("PostUser").toString())
                                .replace("{commentId}", getLatestCommentIdByContentAndMemberId("comment1", "PostUser")
                                                .toString());
                TestUtil.performRequest(mockMvc, url, null, "DELETE", 200, 400);

        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public Long getLatestPostIdByMemberId(String memberId) {
                return jdbcTemplate.queryForObject(
                                "SELECT post_id FROM post WHERE member_id = (SELECT member_id FROM member WHERE id = ?) ORDER BY created_at DESC LIMIT 1",
                                Long.class,
                                memberId);
        }

        public Long getLatestCommentIdByContentAndMemberId(String content, String memberId) {
                return jdbcTemplate.queryForObject(
                                "SELECT comment_id FROM study.post_comment " +
                                                "WHERE content = ? AND member_id = (SELECT member_id FROM study.member WHERE id = ?) "
                                                +
                                                "ORDER BY created_at DESC LIMIT 1",
                                Long.class,
                                content, memberId);
        }

}
