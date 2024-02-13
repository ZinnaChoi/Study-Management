import React, { useState } from "react";
import { parseDate } from "../../util/DateUtil";
import "../../styles/Button.css";

const Comment = ({ comment, onSave, onDelete, loginMemberName }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState(
    comment.content.replace(/<br\s*\/?>/gi, "\n")
  );

  const handleEdit = () => setIsEditing(true);

  const handleSave = () => {
    const formattedContent = editedContent.replace(/\n/g, "<br>");
    onSave(comment.commentId, formattedContent);
    setIsEditing(false);
  };

  const handleChange = (e) => setEditedContent(e.target.value);

  const handleCancel = () => {
    setIsEditing(false);
    setEditedContent(comment.content.replace(/<br\s*\/?>/gi, "\n"));
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
          <textarea
            className="comment-input"
            value={editedContent}
            onChange={handleChange}
            rows={4} // Adjust the number of rows as needed
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
        <div
          className="comment-content"
          dangerouslySetInnerHTML={{ __html: comment.content }}
        />
      )}
      {isLoginMember && !isEditing && (
        <>
          <button onClick={handleEdit} className="comment-edit-btn edit-btn">
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
  );
};

export default Comment;
