import React, { useState, useEffect } from "react";
import { Tooltip, Chip, Button } from "@mui/material";
import CommonDialog from "../../components/CommonDialog";
import "../../styles/Dialog.css";
import "../../styles/StudyManagement.css";

export default function StudyRegistPopup(props) {
  const [addedSchedules, setAddedSchedules] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    if (props.schedules) {
      setAddedSchedules(props.schedules);
    }
  }, [props.schedules]);

  const handleChipDelete = (target) => {
    setAddedSchedules((prevSchedules) => {
      return prevSchedules.filter(
        (schedule) => schedule.scheduleName !== target.scheduleName
      );
    });
  };

  const addSchedule = (data, event) => {
    event.stopPropagation();
    if (!isValidTime(data["시작 시간"], data["종료 시간"])) {
      alert("종료 시간은 시작 시간보다 이르거나 같을 수 없습니다");
      return;
    }
    const schedule = {
      scheduleName: data["스케줄 이름"],
      startTime: data["시작 시간"].replace(":", ""),
      endTime: data["종료 시간"].replace(":", ""),
    };
    setAddedSchedules((prevSchedules) => [...prevSchedules, schedule]);
    setDialogOpen(false);
  };

  function isValidTime(startTime, endTime) {
    const [sHours, sMinutes] = startTime.split(":").map(Number);
    const [eHours, eMinutes] = endTime.split(":").map(Number);

    const startDate = new Date();
    startDate.setHours(sHours);
    startDate.setMinutes(sMinutes);

    const endDate = new Date();
    endDate.setHours(eHours);
    endDate.setMinutes(eMinutes);

    if (endDate <= startDate) {
      return false;
    }

    return true;
  }

  const handleOpen = () => {
    setDialogOpen(true);
  };

  const SchedulesAdd = () => {
    return (
      <div>
        <div className="dialog-content fullwidth-content">
          <div className="study-select-title">스케줄</div>
          <Button variant="outlined" onClick={handleOpen}>
            추가
          </Button>
          <CommonDialog
            open={dialogOpen}
            closeDialog={false}
            btnTitle={"추가"}
            title={"스케줄 등록"}
            names={["스케줄 이름", "시작 시간", "종료 시간"]}
            isRequireds={[true, true, true]}
            inputTypes={["", "time", "time"]}
            acceptStr={"추가"}
            cancleStr={"취소"}
            onClose={() => setDialogOpen(false)}
            submitEvt={addSchedule}
            showButton={false}
          ></CommonDialog>
          {addedSchedules.map((as, index) => (
            <Tooltip
              key={index}
              title={`${as.startTime.slice(0, 2)}:${as.startTime.slice(
                2
              )} ~ ${as.endTime.slice(0, 2)}:${as.endTime.slice(2)}`}
            >
              <Chip
                className={`study-regist-chip-space ${
                  index === 0 ? "first-chip" : ""
                }`}
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
        cancleStr={props.useCancel && "취소"}
        defaultValues={props.studyName && [props.studyName, ""]}
        onClose={props.onClose && props.onClose}
        submitEvt={(data) => props.doAction(data, addedSchedules)}
        showButton={false}
        useCheckbox={props.currentLogoCheckbox && props.currentLogoCheckbox}
        checkboxLabel={props.logoCheckboxLabel}
        extraComponents={<SchedulesAdd />}
      ></CommonDialog>
    </div>
  );
}
