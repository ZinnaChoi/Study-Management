import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";
import Table from "../../components/Table";
import Pagination from "../../components/Pagination";

const NoticeBoard = () => {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("MEMBER");
  const [searchKeyword, setSearchKeyword] = useState("");

  const fetchPosts = (
    currentPage = page,
    currentSize = size,
    currentSearchType = searchType,
    currentSearchKeyword = searchKeyword
  ) => {
    const params = {
      page: currentPage,
      size: currentSize,
      sort: "postId,desc",
      searchType: currentSearchType,
      searchKeyWord: currentSearchKeyword,
    };

    authClient
      .get("/posts", { params })
      .then((response) => {
        if (response && response.data) {
          setPosts(response.data.content);
          setPage(response.data.pageable.page);
          setSize(response.data.pageable.size);
          setTotalPages(response.data.pageable.totalPages);
        }
      })
      .catch((error) => {
        alert(
          "게시판 목록 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  const handleSearch = () => {
    fetchPosts(page, size, searchType, searchKeyword);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      fetchPosts(page, size, searchType, searchKeyword);
    }
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
    fetchPosts(newPage, size, searchType, searchKeyword);
  };

  const containerStyle = {
    padding: "30px",
    minHeight: "100vh",
    justifyContent: "center",
  };

  const selectAndInputBaseStyle = {
    marginRight: "5px",
    padding: "5px 10px",
    borderRadius: "5px",
  };

  const searchAndTableContainerStyle = {
    display: "flex",
    justifyContent: "space-between",
    width: "100%",
    minHeight: "50px",
    marginBottom: "10px",
  };

  const selectStyle = {
    flex: "1",
    ...selectAndInputBaseStyle,
  };

  const inputStyle = {
    flex: "8",
    ...selectAndInputBaseStyle,
  };

  const buttonStyle = {
    margin: "0 5px",
    padding: "5px 10px",
    border: "none",
    borderRadius: "5px",
    backgroundColor: "#375582",
    color: "white",
    cursor: "pointer",
  };

  const searchButtonStyle = {
    flex: "0.5",
    ...buttonStyle,
  };

  const addButtonStyle = {
    flex: "0.5",
    ...buttonStyle,
  };

  const columns = [
    {
      Header: "좋아요 수",
      accessor: "likes",
    },
    {
      Header: "제목",
      accessor: "title",
    },
    {
      Header: "작성자",
      accessor: "memberName",
    },
    {
      Header: "댓글 수",
      accessor: "commentCnt",
    },
    {
      Header: "작성 시간",
      accessor: "createdAt",
      Cell: (content) => parseDate(content.createdAt).toLocaleString(),
    },
    {
      Header: "업데이트 시간",
      accessor: "updatedAt",
      Cell: (content) => parseDate(content.updatedAt).toLocaleString(),
    },
  ];

  return (
    <div style={containerStyle}>
      <div style={searchAndTableContainerStyle}>
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          style={selectStyle}
        >
          <option value="MEMBER">작성자</option>
          <option value="TITLE">제목</option>
        </select>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          onKeyDown={handleKeyDown}
          style={inputStyle}
        />
        <button onClick={handleSearch} style={searchButtonStyle}>
          검색
        </button>
        <button style={addButtonStyle}>추가</button>
      </div>
      <Table columns={columns} contents={posts} />
      <Pagination
        totalPages={totalPages}
        currentPage={page}
        onPageChange={handlePageChange}
      />
    </div>
  );
};

export default NoticeBoard;
