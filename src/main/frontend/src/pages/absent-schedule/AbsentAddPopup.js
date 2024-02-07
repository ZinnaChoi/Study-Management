import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import { getCurrentDateTime, formatDateToYYYYMMDD } from "../../util/DateUtil";
import Select from "react-select";

const AbsentAddPopup = ({ onClose }) => {
  const [schedules, setSchedules] = useState([]);
  const [selectedSchedules, setSelectedSchedules] = useState([]);
  const [description, setDescription] = useState("");
  const [absentDate, setAbsentDate] = useState(
    formatDateToYYYYMMDD(new Date())
  );
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    authClient
      .get("/schedules", {
        params: {
          sendDate: getCurrentDateTime(),
          systemId: "STUDY_0001",
        },
      })
      .then((response) => {
        const scheduleOptions = response.data.registedSchedules.map(
          (schedule) => ({
            value: schedule.scheduleName,
            label: schedule.scheduleName,
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

  const handleAddAbsent = () => {
    const newAbsentData = {
      sendDate: getCurrentDateTime(),
      systemId: "STUDY_0001",
      absentDate: formatDateToYYYYMMDD(absentDate),
      description: description,
      scheduleNameList: selectedSchedules.map((schedule) => schedule.value),
    };

    setIsSubmitting(true);
    authClient
      .post("/absent", newAbsentData)
      .then((response) => {
        alert(response.data.retMsg);
        setIsSubmitting(false);
        onClose();
      })
      .catch((error) => {
        alert(
          "부재일정 등록 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
        setIsSubmitting(false);
      });
  };

  const popupContainerStyle = {
    position: "fixed",
    top: "0",
    left: "0",
    width: "100%",
    height: "100%",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0, 0, 0, 0.4)",
    zIndex: "4",
  };

  const popupStyle = {
    width: "auto",
    maxHeight: "80vh",
    overflowY: "auto",
    background: "#fff",
    padding: "20px",
    borderRadius: "10px",
    boxShadow: "0 2px 10px rgba(0, 0, 0, 0.3)",
    zIndex: "5",
  };

  const formStyle = {
    display: "flex",
    flexDirection: "column",
    marginBottom: "20px",
  };

  const buttonGroupStyle = {
    display: "flex",
    justifyContent: "center",
    gap: "10px",
  };

  const customStyles = {
    control: (base) => ({
      ...base,
      marginBottom: "20px",
    }),
  };

  const inputStyle = {
    marginBottom: "20px",
    width: "100%",
  };

  return (
    <div style={popupContainerStyle}>
      <div style={popupStyle}>
        <h3>부재 일정 등록</h3>
        <input
          type="date"
          value={absentDate}
          onChange={(e) => setAbsentDate(e.target.value)}
          style={inputStyle}
        />
        <div style={formStyle}>
          <Select
            options={schedules}
            isMulti
            onChange={setSelectedSchedules}
            placeholder="부재 스케줄 선택"
            styles={customStyles}
          />
          <input
            type="text"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="부재 사유 입력"
            style={inputStyle}
          />
        </div>
        <div style={buttonGroupStyle}>
          <button onClick={handleAddAbsent} disabled={isSubmitting}>
            등록
          </button>
          <button onClick={onClose}>취소</button>
        </div>
      </div>
    </div>
  );
};

export default AbsentAddPopup;
