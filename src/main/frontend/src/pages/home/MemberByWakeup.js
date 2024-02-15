import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import Table from "../../components/Table";
import Pagination from "../../components/Pagination";
import "../../styles/Button.css";
import "../../styles/NoticeBoard.css";
import "../../styles/Home.css";

// 기상 시간 별 스터디원
export default function MemberByWakeup() {
  const [searchType, setSearchType] = useState("ALL");
  const [wakeupTimes, setWakeupTimes] = useState([]);
  const [memberInfoByWakeupTime, setMemberInfoByWakeupTime] = useState([]);
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
      Header: "기상 시간",
      accessor: "wakeupTime",
    },
  ];

  const getWakeupTimes = () => {
    authClient
      .get("/wakeup-times")
      .then((response) => {
        if (response && response.data) {
          setWakeupTimes(response.data?.registedWakeups);
        }
      })
      .catch((error) => {
        alert(
          "기상시간 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const getMemberInfoByWakeupTime = () => {
    const params = {
      page: page,
      size: size,
      sort: "memberId,desc",
      time: searchType === "ALL" ? null : searchType,
    };
    authClient
      .get("/members/wakeup-time", { params })
      .then((response) => {
        if (response && response.data) {
          const members = response.data.members;
          members.forEach((m, index) => {
            members[index].wakeupTime = `${m.wakeupTime.slice(
              0,
              2
            )}:${m.wakeupTime.slice(2)}`;
          });
          setMemberInfoByWakeupTime(members);
          setPage(response.data.pageable.page);
          setSize(response.data.pageable.size);
          setTotalPages(response.data.pageable.totalPages);
        }
      })
      .catch((error) => {
        alert(
          "기상 시간 별 스터디원 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  // 최초 렌더링 시 한번만 실행
  useEffect(() => {
    getWakeupTimes();
  }, []);

  // searchType 변경시마다 실행
  useEffect(() => {
    getMemberInfoByWakeupTime();
  }, [searchType, page]);

  const handleWakeupPageChange = (newPage) => {
    setPage(newPage);
  };

  return (
    <React.Fragment>
      <div className="member-info-layout">
        <h5 className="between-field-and-select  between-filter-and-table">
          기상 시간
        </h5>
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className="selectAndInputBase select select-width"
        >
          <option value="ALL">전체</option>
          {wakeupTimes.map((wakeupTime, index) => (
            <option key={index} value={wakeupTime}>
              {`${wakeupTime.slice(0, 2)}:${wakeupTime.slice(2)}`}
            </option>
          ))}
        </select>
      </div>
      <Table columns={columns} contents={memberInfoByWakeupTime} />
      <Pagination
        totalPages={totalPages}
        currentPage={page}
        onPageChange={handleWakeupPageChange}
      />
    </React.Fragment>
  );
}
