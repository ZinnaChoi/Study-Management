import React, { useState } from "react";
import { authClient } from "../../services/APIService";
import CommonDialog from "../../components/CommonDialog";

const PostAddPopup = ({ open, onClose, onRefresh }) => {
  const handleCreatePost = (formJson) => {
    console.log(formJson);
    authClient
      .post("/posts", formJson)
      .then((response) => {
        alert(response.data.retMsg);
        onClose();
        onRefresh();
      })
      .catch((error) => {
        alert(
          "게시글 생성 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  return (
    <CommonDialog
      open={open}
      title="게시글 추가"
      onClose={onClose}
      acceptStr="생성"
      cancleStr="취소"
      names={["title", "content"]}
      isRequireds={[true, true]}
      descriptions={["제목을 입력하세요", "내용을 입력하세요"]}
      inputTypes={["text", "text"]}
      showButton={false}
      submitEvt={handleCreatePost}
    ></CommonDialog>
  );
};

export default PostAddPopup;
