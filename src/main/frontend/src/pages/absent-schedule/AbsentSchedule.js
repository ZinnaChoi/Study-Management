import React, { useState, useEffect } from "react";
import qs from "qs";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { authClient } from "../../services/APIService";
import { getCurrentDateTime } from "../../util/DateUtil";

// 부재 일정 화면
const AbsentSchedule = () => {
  const [events, setEvents] = useState([]);
  const [membersList, setMembersList] = useState([]);
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [currentYearMonth, setCurrentYearMonth] = useState("");
  const [prevYearMonth, setPrevYearMonth] = useState("");
  const [colorMap, setColorMap] = useState({});

  useEffect(() => {
    // TODO: Call MemberList
    const memberNames = ["지나", "엠마"];
    setMembersList(memberNames);
    setSelectedMembers(memberNames);
  }, []);

  const getRandomPastelColor = () => {
    return `hsl(${360 * Math.random()}, ${25 + 70 * Math.random()}%, ${
      85 + 10 * Math.random()
    }%)`;
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

  function handleCheckboxChange(memberName) {
    setSelectedMembers((prev) => {
      const newSelectedMembers = prev.includes(memberName)
        ? prev.filter((name) => name !== memberName)
        : [...prev, memberName];
      if (newSelectedMembers.length === 0) {
        setEvents([]);
      }
      return newSelectedMembers;
    });
  }

  const renderCheckboxes = () => {
    return membersList.map((memberName) => (
      <div key={memberName}>
        <label>
          <input
            type="checkbox"
            checked={selectedMembers.includes(memberName)}
            onChange={() => handleCheckboxChange(memberName)}
          />
          {memberName}
        </label>
      </div>
    ));
  };

  useEffect(() => {
    if (
      currentYearMonth &&
      (currentYearMonth !== prevYearMonth || selectedMembers.length > 0)
    ) {
      const requestBody = {
        sendDate: getCurrentDateTime(),
        systemId: "STUDY_0001",
        yearMonth: currentYearMonth,
        memberNameList: selectedMembers,
      };

      const queryString = qs.stringify(requestBody, { arrayFormat: "repeat" });

      authClient
        .get("/absent/calendar?" + queryString)
        .then((absentResponse) => {
          const newColorMap = absentResponse.data.content.reduce(
            (map, absentInfo) => {
              const { scheduleName } = absentInfo;
              map[scheduleName] = map[scheduleName] || getRandomPastelColor();
              return map;
            },
            colorMap
          );
          setColorMap(newColorMap);

          const newEvents = absentResponse.data.content
            .filter(
              (absentInfo) =>
                selectedMembers.length === 0 ||
                absentInfo.memberNameList.some((name) =>
                  selectedMembers.includes(name)
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

          setEvents(newEvents);
          setPrevYearMonth(currentYearMonth);
        })
        .catch((error) => {
          console.error("데이터 조회 실패:", error);
        });
    }
  }, [currentYearMonth, selectedMembers]);

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
        <h3>스터디원 선택</h3>
        {renderCheckboxes()}
      </div>
      <div style={calendarContainerStyle}>
        <FullCalendar
          plugins={[dayGridPlugin, interactionPlugin]}
          initialView="dayGridMonth"
          events={events}
          datesSet={getCurrentYearMonth}
          eventContent={renderEventContent}
        />
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
