import React from "react";
import { authClient } from "../../services/APIService";
import CommonDialog from "../../components/CommonDialog";
import { TextField } from "@mui/material";

const WakeupPopup = ({ open, onClose }) => {
  const fetchWakeupLog = () => {
    authClient
      .post("/stat/wakeup")
      .then((response) => {
        alert(response.data.retMsg);
        onClose();
      })
      .catch((error) => {
        alert(
          "기상 체크 실패: " + (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const getCurrentTime = () => {
    const currentTime = new Date().toLocaleTimeString();
    return (
      <TextField
        key="current-time"
        label="현재 시간"
        value={currentTime}
        InputProps={{
          readOnly: true,
        }}
        variant="outlined"
        fullWidth
      />
    );
  };
  const extraComponents = <> {getCurrentTime()} 기상 체크를 하시겠습니까?</>;

  return (
    <CommonDialog
      open={open}
      title="기상 성공 체크 "
      onClose={onClose}
      acceptStr="예"
      cancleStr="아니요"
      isRequireds={true}
      showButton={false}
      submitEvt={fetchWakeupLog}
      extraComponents={extraComponents}
    />
  );
};

export default WakeupPopup;
