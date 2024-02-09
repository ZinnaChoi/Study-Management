import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";

const PostDetail = ({ postId, onClose }) => {
  const [postDetail, setPostDetail] = useState(null);

  useEffect(() => {
    authClient
      .get(`/posts/${postId}`)
      .then((response) => {
        if (response && response.data && response.data.postDetail) {
          setPostDetail(response.data.postDetail);
        }
      })
      .catch((error) => {
        console.error("게시글 상세 정보 조회 실패:", error);
      });
  }, [postId]);

  return (
    <div className="modal">
      <div className="modal-content">
        <span className="close" onClick={onClose}></span>
        {postDetail && (
          <div>
            <h2>{postDetail.title}</h2>
            <p>{postDetail.likes}</p>
            <p>{postDetail.memberName}</p>
            <p>{parseDate(postDetail.createdAt).toLocaleString()}</p>
            <p>{postDetail.content}</p>
            <h3>댓글</h3>
            {postDetail.comments.map((comment) => (
              <div key={comment.commentId}>
                <p>{comment.content}</p>
                <p>{comment.memberName}</p>
                <p>{parseDate(comment.createdAt).toLocaleString()}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default PostDetail;
