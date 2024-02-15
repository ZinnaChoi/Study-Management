import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import Table from "../../components/Table";
import Pagination from "../../components/Pagination";
import "../../styles/Button.css";
import "../../styles/NoticeBoard.css";
import "../../styles/Home.css";

// 스케줄 별 스터디원
export default function MemberBySchedule() {
  const [searchType, setSearchType] = useState("ALL");
  const [schedules, setSchedules] = useState([]);
  const [memberInfoBySchedule, setMemberInfoBySchedule] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [size, setSize] = useState(10);

  const columns = [
    {
      Header: "아이디",
      accessor: "id",
    },
    {
      Header: "이름",
      accessor: "name",
    },
    {
      Header: "스케줄 목록",
      accessor: "scheduleNames",
    },
  ];

  const getSchedules = () => {
    authClient
      .get("/schedules")
      .then((response) => {
        if (response && response.data) {
          setSchedules(response.data?.registedSchedules);
        }
      })
      .catch((error) => {
        alert(
          "스케줄 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const getMemberInfoBySchedule = () => {
    const params = {
      page: page,
      size: size,
      sort: "memberId,desc",
      schedule: searchType === "ALL" ? null : searchType,
    };
    authClient
      .get("/members/schedule-name", { params })
      .then((response) => {
        if (response && response.data) {
          const members = response.data.members;
          members.forEach((m, index) => {
            members[index].scheduleNames = m.scheduleNames.toString();
          });
          setMemberInfoBySchedule(response.data.members);
          setPage(response.data.pageable.page);
          setSize(response.data.pageable.size);
          setTotalPages(response.data.pageable.totalPages);
        }
      })
      .catch((error) => {
        alert(
          "스케줄 별 스터디원 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  // 최초 렌더링 시 한번만 실행
  useEffect(() => {
    getSchedules();
  }, []);

  // searchType 변경시마다 실행
  useEffect(() => {
    getMemberInfoBySchedule();
  }, [searchType, page]);

  const handleSchedulePageChange = (newPage) => {
    console.log(newPage);
    setPage(newPage);
  };

  return (
    <React.Fragment>
      <div className="member-info-layout">
        <h5 className="between-field-and-select between-filter-and-table">
          스케줄 이름
        </h5>
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className="selectAndInputBase select select-width"
        >
          <option value="ALL">전체</option>
          {schedules.map((schedule) => (
            <option key={schedule.scheduleId} value={schedule.scheduleName}>
              {schedule.scheduleName} (
              {`${schedule.startTime.slice(0, 2)}:${schedule.startTime.slice(
                2
              )}`}
              ~{`${schedule.endTime.slice(0, 2)}:${schedule.endTime.slice(2)}`})
            </option>
          ))}
        </select>
      </div>
      <Table columns={columns} contents={memberInfoBySchedule} />
      <Pagination
        totalPages={totalPages}
        currentPage={page}
        onPageChange={handleSchedulePageChange}
      />
    </React.Fragment>
  );
}
