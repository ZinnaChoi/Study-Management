import React, { useState } from "react";
import { parseDate } from "../../util/DateUtil";
import "../../styles/Button.css";

const Comment = ({ comment, onSave, onDelete, loginMemberName }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState(comment.content);

  const handleEdit = () => setIsEditing(true);

  const handleSave = () => {
    onSave(comment.commentId, editedContent);
    setIsEditing(false);
  };

  const handleChange = (e) => setEditedContent(e.target.value);

  const handleCancel = () => {
    setIsEditing(false);
    setEditedContent(comment.content);
  };

  const isLoginMember = comment.memberName === loginMemberName;

  return (
    <div className="comment">
      <span className="comment-author">{comment.memberName}</span>
      <span className="comment-date">
        {parseDate(comment.updatedAt).toLocaleString()}
      </span>
      {isEditing ? (
        <>
          <input
            type="text"
            value={editedContent}
            onChange={handleChange}
            className="comment-input"
          />
          {isLoginMember && (
            <>
              <button
                onClick={handleSave}
                className="comment-save-btn accept-btn"
              >
                저장
              </button>
              <button
                onClick={handleCancel}
                className="comment-cancel-btn cancel-btn"
              >
                취소
              </button>
            </>
          )}
        </>
      ) : (
        <>
          <span className="comment-content">{comment.content}</span>
          {isLoginMember && (
            <>
              <button
                onClick={handleEdit}
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
        </>
      )}
    </div>
  );
};

export default Comment;
