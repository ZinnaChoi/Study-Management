import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";

const PostDetailPopup = ({ postId }) => {
  const [postDetail, setPostDetail] = useState({ comments: [] });
  const [loginMemberName, setLoginMemberName] = useState("");
  const [newComment, setNewComment] = useState("");

  useEffect(() => {
    authClient
      .get(`/posts/${postId}`)
      .then((response) => {
        if (response.data) {
          setPostDetail(response.data.postDetail);
        }
      })
      .catch((error) => {
        console.error("게시글 상세 정보 조회 실패:", error);
      });

    authClient
      .get("/member")
      .then((response) => {
        setLoginMemberName(response.data.name);
      })
      .catch((error) => {
        console.error("스터디원 정보 조회 실패:", error);
      });
  }, [postId]);

  const handleEditPost = () => {};

  const handleDeletePost = () => {};

  const handleNewCommentChange = (e) => {
    setNewComment(e.target.value);
  };

  const handlePostComment = () => {
    setNewComment("");
  };

  const handleEditComment = (commentId) => {};

  const isLoginMember = (authorName) => {
    return loginMemberName === authorName;
  };

  const handleDeleteComment = (commentId) => {};

  return (
    <div className="post-detail-fullscreen">
      <div className="post-detail-header">
        <div className="post-actions">
          <button onClick={handleEditPost}>수정</button>
          <button onClick={handleDeletePost}>삭제</button>
        </div>
      </div>
      <div className="post-detail-container">
        <div className="post-detail-info">
          <h1>{postDetail?.title}</h1>
          <p>
            {" "}
            <span className="like-icon">♥</span> 좋아요 수: {postDetail?.likes}
          </p>
          <div className="post-author">
            <div>작성자: {postDetail?.memberName}</div>
            <div>
              작성 일시:{" "}
              {postDetail?.createdAt &&
                parseDate(postDetail.createdAt).toLocaleString()}
            </div>
            <div>
              수정 일시:{" "}
              {postDetail?.createdAt &&
                parseDate(postDetail.updatedAt).toLocaleString()}
            </div>
          </div>
        </div>
        <div className="post-detail-content">
          <p>{postDetail?.content}</p>
        </div>
      </div>
      <div className="post-comments">
        <h3>댓글</h3>
        <textarea
          value={newComment}
          onChange={handleNewCommentChange}
          placeholder="댓글을 입력하세요"
        ></textarea>
        <button onClick={handlePostComment}>작성</button>
        <button onClick={() => setNewComment("")}>취소</button>
        {postDetail?.comments?.map((comment) => (
          <div key={comment.commentId} className="comment">
            <div className="comment-info">
              <span>{comment.memberName} </span>
              <span>{parseDate(comment.updatedAt).toLocaleString()}</span>
            </div>
            <p>{comment.content}</p>
            {isLoginMember(comment.memberName) && (
              <div className="comment-actions">
                <button onClick={() => handleEditComment(comment.commentId)}>
                  수정
                </button>
                <button onClick={() => handleDeleteComment(comment.commentId)}>
                  삭제
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default PostDetailPopup;
