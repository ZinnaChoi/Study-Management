import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";
import "../../styles/NoticeBoard.css";
import "../../styles/Button.css";

const PostDetailPopup = ({ postId, onRefresh, setShowDetailPopup }) => {
  const [postDetail, setPostDetail] = useState({
    comments: [],
    isLiked: false,
  });
  const [editMode, setEditMode] = useState(false);
  const [editedTitle, setEditedTitle] = useState("");
  const [editedContent, setEditedContent] = useState("");
  const [loginMemberName, setLoginMemberName] = useState("");
  const [newComment, setNewComment] = useState("");
  const [editedComments, setEditedComments] = useState({});

  const fetchPostDetail = () => {
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
        alert(
          `게시글 상세 정보 조회 실패: ${error.response?.data.retMsg || error}`
        );
      });
  };

  const fetchMemberInfo = () => {
    authClient
      .get("/member")
      .then((response) => {
        setLoginMemberName(response.data.name);
      })
      .catch((error) => {
        alert(
          `스터디원정보 조회 실패: ${error.response?.data.retMsg || error}`
        );
      });
  };

  useEffect(() => {
    fetchPostDetail();
    fetchMemberInfo();
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
        setEditMode(false);
        onRefresh();
      })
      .catch((error) => {
        alert(
          `게시글 수정에 실패했습니다: ${error.response?.data.retMsg || error}`
        );
      });
  };

  const createMarkup = (htmlContent) => {
    return { __html: htmlContent };
  };

  const handleDeletePost = () => {
    const confirmDelete = window.confirm("게시글을 삭제하시겠습니까?");
    if (confirmDelete) {
      authClient
        .delete(`/posts/${postId}`)
        .then((response) => {
          onRefresh();
          setShowDetailPopup(false);
        })
        .catch((error) => {
          alert(
            `게시글 삭제에 실패했습니다: ${
              error.response?.data.retMsg || error
            }`
          );
        });
    }
  };

  const handleNewCommentChange = (e) => {
    setNewComment(e.target.value);
  };

  const handlePostComment = () => {
    if (!newComment.trim()) {
      alert("댓글 내용을 입력해주세요.");
      return;
    }

    const commentData = {
      content: newComment,
    };

    authClient
      .post(`/posts/${postId}/comments`, commentData)
      .then((response) => {
        alert(response.data.retMsg);
        setNewComment("");
        fetchPostDetail();
      })
      .catch((error) => {
        alert(
          `댓글 등록에 실패했습니다: ${error.response?.data.retMsg || error}`
        );
      });
  };

  const handleEditComment = (comment) => {
    setEditedComments({
      ...editedComments,
      [comment.commentId]: comment.content,
    });
  };

  // Function to handle the change of comment content while editing
  const handleCommentChange = (commentId, newContent) => {
    setEditedComments({
      ...editedComments,
      [commentId]: newContent,
    });
  };

  const handleSaveEditedComment = (commentId) => {
    const content = editedComments[commentId];
    authClient
      .patch(`/posts/${postId}/comments/${commentId}`, { content })
      .then(() => {
        setPostDetail({
          ...postDetail,
          comments: postDetail.comments.map((comment) =>
            comment.commentId === commentId ? { ...comment, content } : comment
          ),
        });
        const newEditedComments = { ...editedComments };
        delete newEditedComments[commentId];
        setEditedComments(newEditedComments);
      })
      .catch((error) => {
        alert(`댓글 수정에 실패했습니다: ${error}`);
      });
  };

  const handleCancelEdit = (commentId) => {
    const newEditedComments = { ...editedComments };
    delete newEditedComments[commentId];
    setEditedComments(newEditedComments);
  };

  const handleDeleteComment = (commentId) => {};

  const isLoginMember = (authorName) => {
    return loginMemberName === authorName;
  };

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
        <button className="accept-btn" onClick={() => handleSavePost()}>
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

  const Comment = ({
    comment,
    isEditing,
    onEdit,
    onCancel,
    onSave,
    onDelete,
    onChange,
  }) => {
    return (
      <div className="comment" key={comment.commentId}>
        <span className="comment-author">{comment.memberName}</span>
        <span className="comment-date">
          {parseDate(comment.updatedAt).toLocaleString()}
        </span>
        {isEditing ? (
          <input
            type="text"
            value={comment.content}
            onChange={(e) => onChange(comment.commentId, e.target.value)}
            className="comment-input"
          />
        ) : (
          <span className="comment-content">{comment.content}</span>
        )}
        <div className="comment-actions">
          {isEditing ? (
            <>
              <button
                onClick={() => onSave(comment.commentId)}
                className="comment-save-btn accept-btn"
              >
                저장
              </button>
              <button
                onClick={() => onCancel(comment.commentId)}
                className="comment-cancel-btn cancel-btn"
              >
                취소
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => onEdit(comment)}
                className="comment-edit-btn edit-btn"
              >
                수정
              </button>
              <button
                onClick={() => onDelete(comment.commentId)}
                className="comment-delete-btn delete-btn"
              >
                삭제
              </button>
            </>
          )}
        </div>
      </div>
    );
  };

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
            저장
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
        {postDetail.comments.map((comment) => (
          <Comment
            comment={comment}
            isEditing={editedComments.hasOwnProperty(comment.commentId)}
            onEdit={handleEditComment}
            onCancel={handleCancelEdit}
            onSave={handleSaveEditedComment}
            onDelete={handleDeleteComment}
            onChange={handleCommentChange}
          />
        ))}
      </div>
    </div>
  );
};

export default PostDetailPopup;
