import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";
import Comment from "./Comment";
import "../../styles/NoticeBoard.css";
import "../../styles/Button.css";

const PostDetailPopup = ({ postId, onRefresh, setShowDetailPopup }) => {
  const [postDetail, setPostDetail] = useState({
    comments: [],
  });
  const [editMode, setEditMode] = useState(false);
  const [editedTitle, setEditedTitle] = useState("");
  const [editedContent, setEditedContent] = useState("");
  const [loginMemberName, setLoginMemberName] = useState("");
  const [newComment, setNewComment] = useState("");

  const fetchPostDetail = () => {
    authClient
      .get(`/posts/${postId}`)
      .then((response) => {
        if (response.data) {
          const detailedPost = response.data.postDetail;
          console.log("Comments:", detailedPost.comments);
          setPostDetail({
            ...detailedPost,
            content: detailedPost.content,
            likes: detailedPost.likes,
          });
          setEditedTitle(detailedPost.title);
          setEditedContent(
            (detailedPost.content || "").replace(/<br\s*\/?>/gi, "\n")
          );
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
          alert(response.data.retMsg);
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
      content: newComment.replace(/\n/g, "<br>"),
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

  const handleSaveEditedComment = (commentId, content) => {
    authClient
      .patch(`/posts/${postId}/comments/${commentId}`, { content })
      .then((response) => {
        alert(response.data.retMsg);
        fetchPostDetail();
      })
      .catch((error) => {
        alert(`댓글 수정에 실패했습니다: ${error}`);
      });
  };

  const handleDeleteComment = (commentId) => {
    const confirmDelete = window.confirm("댓글을 삭제하시겠습니까?");
    if (confirmDelete) {
      authClient
        .delete(`/posts/${postId}/comments/${commentId}`)
        .then((response) => {
          alert(response.data.retMsg);
          fetchPostDetail();
        })
        .catch((error) => {
          alert(`댓글 삭제에 실패했습니다: ${error}`);
        });
    }
  };

  const isLoginMember = (authorName) => {
    return loginMemberName === authorName;
  };

  const toggleLike = () => {
    authClient
      .post(`/posts/${postId}/likes`)
      .then((response) => {
        if (response.data.retCode == 409) {
          authClient
            .delete(`/posts/${postId}/likes`)
            .then((response) => {
              alert(response.data.retMsg);
              fetchPostDetail();
              onRefresh();
            })
            .catch((error) => {
              alert(`좋아요 취소에 실패했습니다: ${error}`);
            });
        } else {
          alert(response.data.retMsg);
          fetchPostDetail();
          onRefresh();
        }
      })
      .catch((error) => {
        alert(`좋아요 등록에 실패했습니다: ${error}`);
      });
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
          <span className={`like-icon`}>♥</span>
          {postDetail?.likes}
        </p>
        <span>조회수: {postDetail?.viewCnt}</span>
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
        <h2>댓글</h2>
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
            key={comment.commentId}
            comment={comment}
            onSave={handleSaveEditedComment}
            onDelete={handleDeleteComment}
            loginMemberName={loginMemberName}
          />
        ))}
      </div>
    </div>
  );
};

export default PostDetailPopup;
