import React, { useState, useEffect } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { authClient } from "../../services/APIService";
import { getCurrentDateTime } from "../../util/DateUtil";

// 부재 일정 화면
const AbsentSchedule = () => {
  const [events, setEvents] = useState([]);
  const [schedules, setSchedules] = useState([]);
  const [scheduleColors, setScheduleColors] = useState({});
  const [currentYearMonth, setCurrentYearMonth] = useState("");

  const getRandomPastelColor = () => {
    return `hsl(${360 * Math.random()}, 70%, 80%)`;
  };

  const getCurrentYearMonth = (dateInfo) => {
    const start = new Date(dateInfo.start);
    let year = start.getFullYear();
    let month;

    if (start.getDate() > 20) {
      month = start.getMonth() + 2;
      if (month === 13) {
        month = 1;
        year++;
      }
    } else {
      month = start.getMonth() + 1;
    }
    const formattedMonth = month < 10 ? `0${month}` : month;
    console.log(year);
    console.log(formattedMonth);
    setCurrentYearMonth(`${year}${formattedMonth}`);
  };

  useEffect(() => {
    const requestBody = {
      sendDate: getCurrentDateTime(),
      systemId: "STUDY_0001",
    };

    const absentRequestBody = {
      sendDate: getCurrentDateTime(),
      systemId: "STUDY_0001",
      yearMonth: currentYearMonth,
    };

    Promise.all([
      authClient.get("/schedules", { params: requestBody }),
      authClient.get("/absent/calendar", { params: absentRequestBody }),
    ])
      .then(([schedulesResponse, absentResponse]) => {
        const sortedSchedules = schedulesResponse.data.registedSchedules.sort(
          (a, b) => a.startTime.localeCompare(b.startTime)
        );
        setSchedules(sortedSchedules);

        const newScheduleColors = { ...scheduleColors };
        sortedSchedules.forEach((schedule) => {
          if (!newScheduleColors[schedule.scheduleName]) {
            newScheduleColors[schedule.scheduleName] = getRandomPastelColor();
          }
        });
        setScheduleColors(newScheduleColors);

        const newEvents = absentResponse.data.content.map((absentInfo) => ({
          title: absentInfo.memberNameList.join(", "),
          start: absentInfo.absentDate,
          backgroundColor: newScheduleColors[absentInfo.scheduleName],
          borderColor: newScheduleColors[absentInfo.scheduleName],
          display: "block",
        }));
        setEvents(newEvents);
      })
      .catch((error) => {
        console.error("데이터 조회 실패:", error);
      });
  }, [currentYearMonth, scheduleColors]);

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

  const getScheduleListItemStyle = (scheduleName) => ({
    backgroundColor: scheduleColors[scheduleName],
    padding: "3px",
    borderRadius: "3px",
    height: "25px",
    marginBottom: "3px",
  });

  return (
    <div>
      <div style={containerStyle}>
        <div style={scheduleStyle}>
          <h3>스터디 시간</h3>
          <ul>
            {schedules.map((schedule) => (
              <li
                key={schedule.scheduleId}
                style={getScheduleListItemStyle(schedule.scheduleName)}
              >
                {schedule.scheduleName}
              </li>
            ))}
          </ul>
        </div>
        <div style={calendarStyle}>
          <FullCalendar
            plugins={[dayGridPlugin, interactionPlugin]}
            initialView="dayGridMonth"
            events={events}
            datesSet={getCurrentYearMonth}
            eventContent={renderEventContent}
          />
        </div>
      </div>
    </div>
  );
};

function renderEventContent(eventInfo) {
  return (
    <>
      <div style={{ color: "black" }}>{eventInfo.event.title}</div>
    </>
  );
}

export default AbsentSchedule;
