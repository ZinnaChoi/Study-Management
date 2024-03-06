import React, { useState } from "react";
import { Button } from "@mui/material";
import StudyMemberTable from "./StudyMemberTable";
import ScheduleTable from "./ScheduleTable";
import WakeupPopup from "./WakeupPopup";
import "../../styles/Button.css";
import "../../styles/NoticeBoard.css";
import "../../styles/Home.css";

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
      <div className="wakeup-check-align">
        <Button className="accept-btn" type="button" onClick={wakeupSuccess}>
          기상 체크
        </Button>
      </div>
      <div className="wakeup-block">
        <div className="schedule-table">
          <h3>스터디 시간표</h3>
          <hr />
          <ScheduleTable />
        </div>
        <div className="study-member-table">
          <h3>스터디원</h3>
          <hr />
          <StudyMemberTable />
        </div>
      </div>
      <WakeupPopup open={openWakeupPopup} onClose={closeWakeupPopup} />
    </React.Fragment>
  );
}
