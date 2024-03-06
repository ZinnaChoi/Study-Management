import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import Table from "../../components/Table";
import Pagination from "../../components/Pagination";
import "../../styles/Button.css";
import "../../styles/NoticeBoard.css";
import "../../styles/Home.css";

// 스터디원 테이블
export default function StudyMemberTable() {
  const [searchType, setSearchType] = useState("PARTICIPATION");
  const [membersInfo, setMembersInfo] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [size, setSize] = useState(10);

  const columns = [
    {
      Header: "스터디원 이름",
      accessor: "name",
    },
    {
      Header: "목표 기상 시간",
      accessor: "wakeupTime",
    },
    {
      Header: "참여 스케줄",
      accessor: "scheduleNames",
    },
  ];

  const getMembersInfo = () => {
    const params = {
      page: page,
      size: size,
      sort: "name,asc",
      searchType: searchType,
      searchKeyWord: searchKeyword,
    };

    authClient
      .get("/members-info", { params })
      .then((response) => {
        if (response && response.data) {
          const modifiedData = response.data?.content.map((item) => ({
            ...item,
            scheduleNames: item.scheduleNames.toString(),
            wakeupTime:
              item.wakeupTime.slice(0, 2) + ":" + item.wakeupTime.slice(2),
          }));
          setMembersInfo(modifiedData);
          setPage(response.data.pageable.page);
          setSize(response.data.pageable.size);
          setTotalPages(response.data.pageable.totalPages);
        }
      })
      .catch((error) => {
        alert(
          "스터디원 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  // 최초 렌더링 시 한번만 실행
  useEffect(() => {
    getMembersInfo();
  }, []);

  // page 변경시마다 실행
  useEffect(() => {
    getMembersInfo();
  }, [page]);

  const handleSchedulePageChange = (newPage) => {
    setPage(newPage);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      getMembersInfo();
    }
  };

  return (
    <React.Fragment>
      <div className="member-info-layout">
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className="selectAndInputBase select"
        >
          <option value="PARTICIPATION">참여 스케줄</option>
          <option value="WAKEUP">기상 시간</option>
        </select>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          onKeyDown={handleKeyDown}
          className="selectAndInputBase input"
        />
        <button onClick={getMembersInfo} className="searchButton search-btn">
          검색
        </button>
      </div>
      <Table columns={columns} contents={membersInfo} />
      <Pagination
        totalPages={totalPages}
        currentPage={page}
        onPageChange={handleSchedulePageChange}
      />
    </React.Fragment>
  );
}
