import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";
import "../../styles/NoticeBoard.css";
import "../../styles/Button.css";

const PostDetailPopup = ({ postId }) => {
  const [postDetail, setPostDetail] = useState({
    comments: [],
    isLiked: false,
  });
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

  const toggleLike = () => {
    setPostDetail((prevState) => ({
      ...prevState,
      isLiked: !prevState.isLiked,
    }));
  };

  return (
    <div className="post-detail-fullscreen">
      {isLoginMember(postDetail?.memberName) && (
        <div className="post-detail-header">
          <button className="edit-btn" onClick={handleEditPost}>
            수정
          </button>
          <button className="delete-btn" onClick={handleDeletePost}>
            삭제
          </button>
        </div>
      )}
      <div className="post-detail-container">
        <div className="post-detail-info">
          <h1>{postDetail?.title}</h1>
          <p onClick={toggleLike}>
            <span className={`like-icon ${postDetail.isLiked ? "liked" : ""}`}>
              ♥
            </span>
            {postDetail?.likes}
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
        <h2> 댓글 </h2>
        <div className="comment-button-group">
          <button className="accept-btn" onClick={handlePostComment}>
            작성
          </button>
          <button className="cancel-btn" onClick={() => setNewComment("")}>
            취소
          </button>
        </div>
        <textarea
          value={newComment}
          onChange={handleNewCommentChange}
          placeholder="댓글을 입력하세요"
          className="text-area"
        ></textarea>
        {postDetail?.comments?.map((comment) => (
          <div>
            <div key={comment.commentId} className="comment">
              <div className="comment-info">
                <span>{comment.memberName}</span>
                <span>{parseDate(comment.updatedAt).toLocaleString()}</span>
              </div>
              {isLoginMember(comment.memberName) && (
                <div className="comment-actions">
                  <button
                    className="edit-btn"
                    onClick={() => handleEditComment(comment.commentId)}
                  >
                    수정
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => handleDeleteComment(comment.commentId)}
                  >
                    삭제
                  </button>
                </div>
              )}
            </div>
            <div className="comment-content">{comment.content}</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PostDetailPopup;
