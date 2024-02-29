import React, { useState } from "react";
import CommonDialog from "../../components/CommonDialog";
import { Button, TextField } from "@mui/material";
import MemberBySchedule from "./MemberBySchedule";
import MemberByWakeup from "./MemberByWakeup";
import WakeupPopup from "./WakeupPopup";
import "../../styles/Button.css";
import "../../styles/NoticeBoard.css";

// 홈 화면
export default function MembersInfo() {
  const [openWakeupPopup, setOpenWakeupPopup] = useState(false);

  const wakeupSuccess = () => {
    setOpenWakeupPopup(true);
  };

  const closeWakeupPopup = () => {
    setOpenWakeupPopup(false);
  };

  return (
    <React.Fragment>
      <div className="wakeup-block">
        <h3>스케줄 별 스터디원</h3>
        <Button className="accept-btn" type="button" onClick={wakeupSuccess}>
          기상 체크
        </Button>
      </div>
      <hr />
      <MemberBySchedule />
      <h3>기상 시간 별 스터디원</h3>
      <hr />
      <MemberByWakeup />
      <WakeupPopup open={openWakeupPopup} onClose={closeWakeupPopup} />
    </React.Fragment>
  );
}
