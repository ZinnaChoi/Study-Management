import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";
import "../../styles/NoticeBoard.css";
import "../../styles/Button.css";

const PostDetailPopup = ({ postId, onRefresh }) => {
  const [postDetail, setPostDetail] = useState({
    comments: [],
    isLiked: false,
  });
  const [editMode, setEditMode] = useState(false);
  const [editedTitle, setEditedTitle] = useState("");
  const [editedContent, setEditedContent] = useState("");
  const [loginMemberName, setLoginMemberName] = useState("");
  const [newComment, setNewComment] = useState("");

  useEffect(() => {
    authClient
      .get(`/posts/${postId}`)
      .then((response) => {
        if (response.data) {
          const detailedPost = response.data.postDetail;
          setPostDetail({
            ...detailedPost,
            content: detailedPost.content,
          });
          setEditedTitle(detailedPost.title);
          setEditedContent(detailedPost.content.replace(/<br\s*\/?>/gi, "\n"));
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

  const handleEditPost = () => {
    setEditMode(true);
  };

  const handleSavePost = () => {
    const formattedContent = editedContent.replace(/\n/g, "<br>");
    const updatedPost = {
      title: editedTitle,
      content: formattedContent,
    };

    authClient
      .patch(`/posts/${postId}`, updatedPost)
      .then((response) => {
        alert(response.data.retMsg);
        setEditMode(false);
        setPostDetail((prev) => ({
          ...prev,
          title: editedTitle,
          content: formattedContent,
        }));
        onRefresh();
      })
      .catch((error) => {
        console.error("게시글 수정 실패:", error);
      });
  };

  const createMarkup = (htmlContent) => {
    return { __html: htmlContent };
  };

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

  const editUI = (
    <>
      <label htmlFor="post-title">제목:</label>
      <input
        id="post-title"
        type="text"
        className="post-edit"
        value={editedTitle}
        onChange={(e) => setEditedTitle(e.target.value)}
      />
      <label htmlFor="post-content">내용:</label>
      <textarea
        id="post-content"
        className="post-edit"
        value={editedContent}
        onChange={(e) => setEditedContent(e.target.value)}
      />
      <div className="edit-actions">
        <button className="accept-btn" onClick={handleSavePost}>
          저장
        </button>
        <button className="cancel-btn" onClick={() => setEditMode(false)}>
          취소
        </button>
      </div>
    </>
  );

  const viewUI = (
    <>
      <h1>{postDetail?.title}</h1>
      <div className="post-author">
        <p onClick={toggleLike}>
          <span className={`like-icon ${postDetail.isLiked ? "liked" : ""}`}>
            ♥
          </span>
          {postDetail?.likes}
        </p>
        <span>작성자: {postDetail?.memberName}</span>
        <span>
          작성 일시:{" "}
          {postDetail?.createdAt &&
            parseDate(postDetail.createdAt).toLocaleString()}
        </span>
        <span>
          수정 일시:{" "}
          {postDetail?.updatedAt &&
            parseDate(postDetail.updatedAt).toLocaleString()}
        </span>
      </div>
      <div
        className="post-detail-content"
        dangerouslySetInnerHTML={createMarkup(postDetail?.content)}
      />
    </>
  );

  return (
    <div className="post-detail-fullscreen">
      <div className="post-detail-header">
        {isLoginMember(postDetail?.memberName) && !editMode && (
          <button className="edit-btn" onClick={handleEditPost}>
            수정
          </button>
        )}
        {isLoginMember(postDetail?.memberName) && (
          <button className="delete-btn" onClick={handleDeletePost}>
            삭제
          </button>
        )}
      </div>
      <div className="post-detail-container">{editMode ? editUI : viewUI}</div>
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
