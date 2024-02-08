import React, { useState, useEffect } from "react";
import CommonDialog from "../../components/CommonDialog";
import { authClient } from "../../services/APIService";
import { formatDateToYYYYMMDD } from "../../util/DateUtil";
import Select from "react-select";
import "../../styles/AbsentSchedule.css";

const AbsentAddPopup = ({ onClose, onRefresh }) => {
  const [schedules, setSchedules] = useState([]);
  const [selectedSchedules, setSelectedSchedules] = useState([]);

  useEffect(() => {
    authClient
      .get("/member")
      .then((response) => {
        const scheduleOptions = response.data.scheduleName.map(
          (scheduleName) => ({
            value: scheduleName,
            label: scheduleName,
          })
        );
        setSchedules(scheduleOptions);
      })
      .catch((error) => {
        alert(
          "스케줄 목록 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  }, []);

  const handleAddAbsent = (formJson) => {
    const dateValue = new Date(formJson["부재 일자"]);
    const formattedAbsentDate = formatDateToYYYYMMDD(dateValue);
    const selectedScheduleValues = selectedSchedules.map(
      (schedule) => schedule.value
    );

    const newAbsentData = {
      absentDate: formattedAbsentDate,
      description: formJson["부재 사유"],
      scheduleNameList: selectedScheduleValues,
    };
    authClient
      .post("/absent", newAbsentData)
      .then((response) => {
        alert(response.data.retMsg);
        onRefresh();
        onClose();
      })
      .catch((error) => {
        alert(
          "부재일정 등록 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  return (
    <CommonDialog
      btnTitle="부재일정 추가"
      title="부재일정 추가"
      submitEvt={handleAddAbsent}
      acceptStr="등록"
      cancleStr="취소"
      names={["부재 일자", "부재 사유"]}
      isRequireds={[true, false]}
      descriptions={["Choose a date", "Enter a description"]}
      inputTypes={["date", "text"]}
      showButton={true}
      extraComponents={
        <div className="absent-select">
          <div className="absent-select-title">부재 스케줄</div>
          <Select
            options={schedules}
            isMulti
            value={selectedSchedules}
            onChange={setSelectedSchedules}
            placeholder="스케줄 선택"
            styles={{
              container: (provided) => ({
                ...provided,
                width: "65%",
              }),
            }}
          />
        </div>
      }
    />
  );
};

export default AbsentAddPopup;
