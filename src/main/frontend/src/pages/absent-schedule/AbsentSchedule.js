import React, { useState, useEffect } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { authClient } from "../../services/APIService";
import { getCurrentDateTime } from "../../util/DateUtil";

// 부재 일정 화면
const AbsentSchedule = () => {
  const [events, setEvents] = useState([]);
  const [currentYearMonth, setCurrentYearMonth] = useState("");
  const [prevYearMonth, setPrevYearMonth] = useState("");
  const [colorMap, setColorMap] = useState({});

  const getRandomPastelColor = () =>
    `hsl(${360 * Math.random()}, ${60 + 40 * Math.random()}%, ${
      70 + 30 * Math.random()
    }%)`;

  const assignColorToScheduleName = (scheduleName) => {
    if (!colorMap[scheduleName]) {
      setColorMap((prevColorMap) => ({
        ...prevColorMap,
        [scheduleName]: getRandomPastelColor(),
      }));
    }
  };

  const getCurrentYearMonth = (dateInfo) => {
    const start = new Date(dateInfo.start);
    let year = start.getFullYear();
    let month =
      start.getDate() > 20 ? start.getMonth() + 2 : start.getMonth() + 1;
    if (month === 13) {
      month = 1;
      year++;
    }
    const formattedMonth = month < 10 ? `0${month}` : `${month}`;
    setCurrentYearMonth(`${year}${formattedMonth}`);
  };

  useEffect(() => {
    if (currentYearMonth !== prevYearMonth) {
      const requestBody = {
        sendDate: getCurrentDateTime(),
        systemId: "STUDY_0001",
        yearMonth: currentYearMonth,
      };

      authClient
        .get("/absent/calendar", { params: requestBody })
        .then((absentResponse) => {
          const newColorMap = { ...colorMap };
          absentResponse.data.content.forEach((absentInfo) => {
            const { scheduleName } = absentInfo;
            if (!newColorMap[scheduleName]) {
              newColorMap[scheduleName] = getRandomPastelColor();
            }
          });

          setColorMap(newColorMap);

          const newEvents = absentResponse.data.content.map((absentInfo) => {
            return {
              title: `${
                absentInfo.scheduleName
              }: ${absentInfo.memberNameList.join(", ")}`,
              start: absentInfo.absentDate,
              backgroundColor: newColorMap[absentInfo.scheduleName],
              borderColor: newColorMap[absentInfo.scheduleName],
              display: "block",
            };
          });
          setEvents(newEvents);
          setPrevYearMonth(currentYearMonth);
        })
        .catch((error) => {
          console.error("데이터 조회 실패:", error);
        });
    }
  }, [currentYearMonth, prevYearMonth, colorMap]);

  const calendarStyle = {
    height: "100vh",
    maxWidth: "100%",
    padding: 0,
    margin: 0,
    fontSize: "1.2em",
  };

  return (
    <div style={calendarStyle}>
      <FullCalendar
        plugins={[dayGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        events={events}
        datesSet={getCurrentYearMonth}
        eventContent={renderEventContent}
      />
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
