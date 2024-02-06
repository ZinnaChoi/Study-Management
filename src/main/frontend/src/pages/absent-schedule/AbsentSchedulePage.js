import React, { useState, useEffect } from "react";
import qs from "qs";
import { authClient } from "../../services/APIService";
import { getCurrentDateTime, getCurrentYearMonth } from "../../util/DateUtil";
import MemberCheckbox from "../../components/MemberCheckbox";
import AbsentCalendar from "./AbsentCalendar";

const AbsentSchedule = () => {
  const [events, setEvents] = useState([]);
  const [membersList, setMembersList] = useState([]);
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [currentYearMonth, setCurrentYearMonth] = useState("");
  const [prevYearMonth, setPrevYearMonth] = useState("");
  const [colorMap, setColorMap] = useState({});

  const getRandomPastelColor = () => {
    return `hsl(${360 * Math.random()}, ${25 + 70 * Math.random()}%, ${
      85 + 10 * Math.random()
    }%)`;
  };

  const getYearMonth = (dateInfo) => {
    const newYearMonth = getCurrentYearMonth(dateInfo);
    setCurrentYearMonth(newYearMonth);
  };

  useEffect(() => {
    const params = {
      sendDate: getCurrentDateTime(),
      systemId: "STUDY_0001",
    };
    authClient
      .get("/members", { params: params })
      .then((response) => {
        const memberNames = response.data.content
          .filter((member) => member.id !== "admin")
          .map((member) => member.name);

        setMembersList(memberNames);
        setSelectedMembers(memberNames);
      })
      .catch((error) => {
        alert(
          "스터디원 목록 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  }, []);

  useEffect(() => {
    if (
      currentYearMonth &&
      (currentYearMonth !== prevYearMonth || selectedMembers.length > 0)
    ) {
      const params = {
        sendDate: getCurrentDateTime(),
        systemId: "STUDY_0001",
        yearMonth: currentYearMonth,
        memberNameList: selectedMembers,
      };

      authClient
        .get(
          "/absent/calendar?" + qs.stringify(params, { arrayFormat: "repeat" })
        )
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
        <MemberCheckbox
          membersList={membersList}
          selectedMembers={selectedMembers}
          setSelectedMembers={setSelectedMembers}
        />
      </div>
      <div style={calendarContainerStyle}>
        <AbsentCalendar events={events} getYearMonth={getYearMonth} />
      </div>
    </div>
  );
};

export default AbsentSchedule;
