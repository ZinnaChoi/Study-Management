import React, { useEffect, useState, useRef } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import "../../styles/AbsentSchedule.css";
import { getCurrentYearMonth } from "../../util/DateUtil";
import qs from "qs";

const AbsentCalendar = ({
  authClient,
  selectedMembers,
  onDateClick,
  refreshKey,
}) => {
  const [events, setEvents] = useState([]);
  const [currentYearMonth, setCurrentYearMonth] = useState(
    getCurrentYearMonth({ start: new Date() })
  );
  const [colorMap, setColorMap] = useState({});

  const getRandomPastelColor = () => {
    return `hsl(${360 * Math.random()}, ${25 + 70 * Math.random()}%, ${
      85 + 10 * Math.random()
    }%)`;
  };

  useEffect(() => {
    if (currentYearMonth && selectedMembers.length > 0) {
      const params = {
        yearMonth: currentYearMonth,
        memberNameList: selectedMembers,
      };

      authClient
        .get(
          "/absent/calendar?" + qs.stringify(params, { arrayFormat: "repeat" })
        )
        .then((absentResponse) => {
          const newColorMap = { ...colorMap };
          absentResponse.data.content.forEach((absentInfo) => {
            if (!newColorMap[absentInfo.scheduleName]) {
              newColorMap[absentInfo.scheduleName] = getRandomPastelColor();
            }
          });

          setColorMap(newColorMap);

          const filteredEvents = absentResponse.data.content
            .filter((absentInfo) =>
              absentInfo.memberNameList.some((memberName) =>
                selectedMembers.includes(memberName)
              )
            )
            .map((absentInfo) => ({
              title: `${
                absentInfo.scheduleName
              }: ${absentInfo.memberNameList.join(", ")}`,
              start: absentInfo.absentDate,
              backgroundColor: newColorMap[absentInfo.scheduleName],
              borderColor: newColorMap[absentInfo.scheduleName],
              display: "block",
            }));

          setEvents(filteredEvents);
        })
        .catch((error) => {
          alert(
            "캘린더 부재일정 조회 실패: " +
              (error.response?.data.retMsg || "Unknown error")
          );
        });
    }
  }, [authClient, currentYearMonth, selectedMembers, refreshKey]);

  return (
    <FullCalendar
      plugins={[dayGridPlugin, interactionPlugin]}
      initialView="dayGridMonth"
      events={events}
      datesSet={(dateInfo) => {
        const newYearMonth = getCurrentYearMonth(dateInfo);
        setCurrentYearMonth(newYearMonth);
      }}
      eventContent={(eventInfo) => (
        <>
          <div style={{ color: "black" }}>{eventInfo.event.title}</div>
        </>
      )}
      dateClick={(event) => onDateClick(event.dateStr)}
    />
  );
};

export default AbsentCalendar;
