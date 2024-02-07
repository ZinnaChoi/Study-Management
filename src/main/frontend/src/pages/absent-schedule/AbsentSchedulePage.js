import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import MemberCheckbox from "../../components/MemberCheckbox";
import AbsentCalendar from "./AbsentCalendar";
import AbsentDetailPopup from "./AbsentDetailPopup";

const AbsentSchedule = () => {
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [showDetailPopup, setShowDetailPopup] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null);

  const handleDateClick = (date) => {
    setShowDetailPopup(true);
    setSelectedDate(date);
  };

  const containerStyle = {
    display: "flex",
    flexDirection: "row",
  };

  const checkboxContainerStyle = {
    flex: 1,
    borderRight: "1px solid #ccc",
    paddingRight: "20px",
    marginRight: "20px",
  };

  const calendarContainerStyle = {
    flex: 5,
    height: "100vh",
    maxWidth: "100%",
    padding: 0,
    margin: 0,
    fontSize: "1.0em",
  };

  return (
    <div style={containerStyle}>
      <div style={checkboxContainerStyle}>
        <button>부재일정 추가</button> <h3>스터디원 선택</h3>
        <MemberCheckbox
          authClient={authClient}
          selectedMembers={selectedMembers}
          setSelectedMembers={setSelectedMembers}
        />
      </div>
      <div style={calendarContainerStyle}>
        <AbsentCalendar
          authClient={authClient}
          selectedMembers={selectedMembers}
          onDateClick={handleDateClick}
        />
      </div>
      {showDetailPopup && (
        <AbsentDetailPopup
          selectedDate={selectedDate}
          onClose={() => setShowDetailPopup(false)}
        />
      )}
    </div>
  );
};

export default AbsentSchedule;
