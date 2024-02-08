import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import MemberCheckbox from "../../components/MemberCheckbox";
import AbsentCalendar from "./AbsentCalendar";
import AbsentDetailPopup from "./AbsentDetailPopup";
import AbsentAddPopup from "./AbsentAddPopup";

const AbsentSchedule = () => {
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [showDetailPopup, setShowDetailPopup] = useState(false);
  const [showAddPopup, setShowAddPopup] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null);
  const [refreshKey, setRefreshKey] = useState(Date.now());

  const handleDateClick = (date) => {
    setShowDetailPopup(true);
    setSelectedDate(date);
  };

  const handleOpenAddPopup = () => {
    setShowAddPopup(true);
  };

  const handleCloseAddPopup = () => {
    setShowAddPopup(false);
  };

  const refreshCalendar = () => {
    setRefreshKey(Date.now());
  };

  const containerStyle = {
    display: "flex",
    flexDirection: "row",
  };

  const checkboxStyle = {
    flex: 1,
    borderRight: "1px solid #ccc",
    paddingRight: "20px",
    marginRight: "20px",
  };

  const calendarStyle = {
    flex: 5,
    height: "100vh",
    maxWidth: "100%",
    padding: 0,
    margin: 0,
    fontSize: "1.0em",
  };

  return (
    <div style={containerStyle}>
      <div style={checkboxStyle}>
        <AbsentAddPopup
          open={showAddPopup}
          onClose={handleCloseAddPopup}
          onRefresh={refreshCalendar}
        />
        <h3>스터디원 선택</h3>
        <MemberCheckbox
          authClient={authClient}
          selectedMembers={selectedMembers}
          setSelectedMembers={setSelectedMembers}
        />
      </div>
      <div style={calendarStyle}>
        <AbsentCalendar
          authClient={authClient}
          selectedMembers={selectedMembers}
          onDateClick={handleDateClick}
          refreshKey={refreshKey}
        />
      </div>
      {showDetailPopup && (
        <AbsentDetailPopup
          selectedDate={selectedDate}
          onClose={() => setShowDetailPopup(false)}
        />
      )}
      {showAddPopup && (
        <AbsentAddPopup
          selectedDate={selectedDate}
          onClose={() => {
            setShowAddPopup(false);
          }}
        />
      )}
    </div>
  );
};

export default AbsentSchedule;
