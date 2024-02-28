import React, { useState } from "react";
import { Tooltip, Chip } from "@mui/material";
import CommonDialog from "../../components/CommonDialog";
import "../../styles/Button.css";
import "../../styles/MyPage.css";

export default function StudyRegistPopup(props) {
  const [addedSchedules, setAddedSchedules] = useState([]);

  const handleChipDelete = (target) => {
    setAddedSchedules((prevSchedules) => {
      return prevSchedules.filter(
        (schedule) => schedule.scheduleName !== target.scheduleName
      );
    });
  };

  const addSchedule = (data, event) => {
    event.stopPropagation();
    const schedule = {
      scheduleName: data["스케줄 이름"],
      startTime: data["시작 시간"].replace(":", ""),
      endTime: data["종료 시간"].replace(":", ""),
    };
    setAddedSchedules((prevSchedules) => [...prevSchedules, schedule]);
  };

  const SchedulesAdd = () => {
    return (
      <div>
        <div className="dialog-content">
          <div className="mypage-select-title">스케줄</div>
          <CommonDialog
            btnTitle={"추가"}
            title={"스케줄 등록"}
            names={["스케줄 이름", "시작 시간", "종료 시간"]}
            isRequireds={[true, true, true]}
            inputTypes={["", "time", "time"]}
            acceptStr={"추가"}
            cancleStr={"취소"}
            submitEvt={addSchedule}
            showButton={true}
          ></CommonDialog>
        </div>
        <div className="dialog-content">
          <div className="mypage-select-title"></div>
          {addedSchedules.map((as, index) => (
            <Tooltip
              key={index}
              title={`${as.startTime.slice(0, 2)}:${as.startTime.slice(
                2
              )} ~ ${as.endTime.slice(0, 2)}:${as.endTime.slice(2)}`}
            >
              <Chip
                style={{ marginTop: "5px" }}
                onDelete={() => handleChipDelete(as)}
                label={as.scheduleName}
              ></Chip>
            </Tooltip>
          ))}
        </div>
      </div>
    );
  };

  return (
    <div>
      <CommonDialog
        open={props.open}
        fullScreen={true}
        title={"필수 정보"}
        names={["스터디 이름", "로고"]}
        isRequireds={[true, false]}
        inputTypes={["", "file"]}
        acceptStr={"등록"}
        submitEvt={(data) => props.doAction(data, addedSchedules)}
        showButton={false}
        extraComponents={<SchedulesAdd />}
      ></CommonDialog>
    </div>
  );
}
