import React, { useState, useEffect } from "react";
import { parseDate } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";
import Table from "../../components/Table";
import Pagination from "../../components/Pagination";
import CommonDialog from "../../components/CommonDialog";
import "../../styles/NoticeBoard.css";
import "../../styles/Button.css";
import PostDetail from "./PostDetail";

const NoticeBoard = () => {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("MEMBER");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [selectedPostId, setSelectedPostId] = useState(null);
  const [showDetailPopup, setShowDetailPopup] = useState(false);

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

  const handlePostClick = (content) => {
    setSelectedPostId(content.postId);
    setShowDetailPopup(true);
  };

  const handleCloseDetailPopup = () => {
    setShowDetailPopup(false);
  };

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
    <div>
      <div className="searchAndTableContainer">
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className="selectAndInputBase select"
        >
          <option value="MEMBER">작성자</option>
          <option value="TITLE">제목</option>
        </select>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          onKeyDown={handleKeyDown}
          className="selectAndInputBase input"
        />
        <button onClick={handleSearch} className="searchButton search-btn">
          검색
        </button>
        <button className="addButton accept-btn">추가</button>
      </div>
      <Table
        columns={columns}
        contents={posts}
        onRowClick={handlePostClick}
        clickable={true}
      />
      <Pagination
        totalPages={totalPages}
        currentPage={page}
        onPageChange={handlePageChange}
      />
      {showDetailPopup && (
        <CommonDialog
          open={showDetailPopup}
          title="게시글 상세"
          cancleStr="닫기"
          showButton={false}
          onClose={handleCloseDetailPopup}
          extraComponents={
            <PostDetail
              postId={selectedPostId}
              onClose={handleCloseDetailPopup}
            />
          }
        />
      )}
    </div>
  );
};

export default NoticeBoard;
