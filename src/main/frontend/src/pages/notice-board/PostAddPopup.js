import React, { useState } from "react";
import { authClient } from "../../services/APIService";
import CommonDialog from "../../components/CommonDialog";
import { TextField } from "@mui/material";

const PostAddPopup = ({ open, onClose, onRefresh }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const handleCreatePost = () => {
    const formattedContent = content.replace(/\n/g, "<br>");

    const postPayload = {
      title: title,
      content: formattedContent,
    };

    authClient
      .post("/posts", postPayload)
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

  const extraComponents = (
    <>
      <TextField
        required
        margin="dense"
        id="title"
        name="title"
        label="제목을 입력하세요"
        type="text"
        fullWidth
        value={title}
        onChange={(e) => setTitle(e.target.value)}
      />
      <TextField
        required
        margin="dense"
        id="content"
        name="content"
        label="내용을 입력하세요"
        type="text"
        fullWidth
        multiline
        rows={4}
        value={content}
        onChange={(e) => setContent(e.target.value)}
      />
    </>
  );

  return (
    <CommonDialog
      open={open}
      title="게시글 추가"
      onClose={onClose}
      acceptStr="생성"
      cancleStr="취소"
      isRequireds={true}
      showButton={false}
      submitEvt={handleCreatePost}
      extraComponents={extraComponents}
    />
  );
};

export default PostAddPopup;
