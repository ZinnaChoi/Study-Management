import React, { useState } from "react";
import { authClient } from "../../services/APIService";
import MemberCheckbox from "../../components/MemberCheckbox";
import AbsentCalendar from "./AbsentCalendar";
import AbsentDetailPopup from "./AbsentDetailPopup";
import AbsentAddPopup from "./AbsentAddPopup";
import "../../styles/AbsentSchedule.css";

const AbsentSchedule = () => {
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [showDetailPopup, setShowDetailPopup] = useState(false);
  const [showAddPopup, setShowAddPopup] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null);
  const [refreshKey, setRefreshKey] = useState(Date.now());

  const handleDateClick = (date) => {
    setSelectedDate(date);
    setShowDetailPopup(true);
  };

  const handleCloseDetailPopup = () => {
    setShowDetailPopup(false);
  };

  const handleCloseAddPopup = () => {
    setShowAddPopup(false);
  };

  const refreshCalendar = () => {
    setRefreshKey(Date.now());
  };

  return (
    <div className="container">
      <div className="checkbox">
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
      <div className="calendar">
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
          onClose={handleCloseDetailPopup}
          onRefresh={refreshCalendar}
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
