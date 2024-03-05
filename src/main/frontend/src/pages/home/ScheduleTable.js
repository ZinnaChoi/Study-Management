import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import Table from "../../components/Table";
import "../../styles/Button.css";
import "../../styles/NoticeBoard.css";
import "../../styles/Home.css";

// 전체 스터디 시간(스케줄) 테이블
export default function ScheduleTable() {
  const [schedules, setSchedules] = useState([]);

  const columns = [
    {
      Header: "스케줄",
      accessor: "scheduleName",
    },
    {
      Header: "시작 시간",
      accessor: "startTime",
    },
    {
      Header: "종료 시간",
      accessor: "endTime",
    },
  ];

  const getSchedules = () => {
    authClient
      .get("/schedules")
      .then((response) => {
        if (response && response.data) {
          const modifiedData = response.data?.registedSchedules.map((item) => ({
            ...item,
            startTime:
              item.startTime.slice(0, 2) + ":" + item.startTime.slice(2),
            endTime: item.endTime.slice(0, 2) + ":" + item.endTime.slice(2),
          }));
          setSchedules(modifiedData);
        }
      })
      .catch((error) => {
        alert(
          "스케줄 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  // 최초 렌더링 시 한번만 실행
  useEffect(() => {
    getSchedules();
  }, []);

  return (
    <React.Fragment>
      <Table columns={columns} contents={schedules} />
    </React.Fragment>
  );
}
