import React, { useState, useEffect } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { authClient } from "../../services/APIService";
import { getCurrentDateTime } from "../../util/DateUtil";

// 부재 일정 화면
const AbsentSchedule = () => {
  const [events] = useState([]);
  const [schedules, setSchedules] = useState([]);

  useEffect(() => {
    const requestBody = {
      sendDate: getCurrentDateTime(),
      systemId: "STUDY_0001",
    };

    authClient
      .get("/schedules", { params: requestBody })
      .then((response) => {
        const sortedSchedules = response.data.registedSchedules.sort((a, b) =>
          a.startTime.localeCompare(b.startTime)
        );
        setSchedules(sortedSchedules);
      })
      .catch((error) => {
        console.error("스케쥴 조회 실패:", error);
      });
  }, []);

  const containerStyle = {
    display: "flex",
  };

  const scheduleStyle = {
    flex: 1,
    marginRight: "10px",
  };

  const calendarStyle = {
    flex: 4,
    height: "100vh",
    maxWidth: "100%",
    padding: 0,
    margin: 0,
    fontSize: "1.2em",
  };

  return (
    <div>
      <div style={containerStyle}>
        <div style={scheduleStyle}>
          <h3>스터디 시간</h3>
          <ul>
            {schedules.map((schedule) => (
              <li key={schedule.scheduleId}>{schedule.scheduleName}</li>
            ))}
          </ul>
        </div>
        <div style={calendarStyle}>
          <FullCalendar
            plugins={[dayGridPlugin, interactionPlugin]}
            initialView="dayGridMonth"
            events={events}
          />
        </div>
      </div>
    </div>
  );
};

export default AbsentSchedule;
