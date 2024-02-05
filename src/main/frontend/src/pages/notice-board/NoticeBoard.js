import React, { useState, useEffect } from "react";
import { getCurrentDateTime } from "../../util/DateUtil";
import { authClient } from "../../services/APIService";

const NoticeBoard = () => {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("MEMBER");
  const [searchKeyword, setSearchKeyword] = useState("");

  const fetchPosts = async (page, size, searchType, searchKeyWord) => {
    const params = {
      sendDate: getCurrentDateTime(),
      systemId: "STUDY_0001",
      page,
      size,
      sort: "postId,desc",
      searchType,
      searchKeyWord,
    };

    try {
      const response = await authClient.get("/posts", { params });
      setPosts(response.data.content);
      setPage(response.data.pageable.page);
      setSize(response.data.pageable.size);
      setTotalPages(response.data.pageable.totalPages);
    } catch (error) {
      console.error("게시판 목록 조회 실패:", error);
    }
  };

  useEffect(() => {
    fetchPosts(page, size, "", "");
  }, []);

  const handleSearch = () => {
    fetchPosts(page, size, searchType, searchKeyword);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // 인라인 스타일 선언
  const containerStyle = {
    padding: "30px",
    minHeight: "100vh",
    justifyContent: "center",
  };

  const searchAndTableContainerStyle = {
    width: "100%",
    maxWidth: "1200px",
    marginBottom: "20px",
  };

  const tableStyle = {
    width: "100%",
    borderCollapse: "collapse",
    marginTop: "20px",
  };

  const thStyle = {
    backgroundColor: "#004085",
    color: "white",
    padding: "10px",
    border: "1px solid #dee2e6",
  };

  const tdStyle = {
    padding: "10px",
    border: "1px solid #dee2e6",
    textAlign: "center",
  };

  const buttonStyle = {
    margin: "0 5px",
    padding: "5px 10px",
    border: "none",
    borderRadius: "5px",
    backgroundColor: "#004085",
    color: "white",
    cursor: "pointer",
  };

  const paginationContainerStyle = {
    display: "flex",
    justifyContent: "center",
    margin: "20px 0",
  };

  return (
    <div style={containerStyle}>
      <div style={searchAndTableContainerStyle}>
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
        >
          <option value="MEMBER">작성자</option>
          <option value="TITLE">제목</option>
        </select>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
        />
        <button onClick={handleSearch} style={buttonStyle}>
          검색
        </button>
        <button style={buttonStyle}>추가</button>
      </div>
      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>좋아요수</th>
            <th style={thStyle}>제목</th>
            <th style={thStyle}>작성자</th>
            <th style={thStyle}>댓글 수</th>
            <th style={thStyle}>작성 시간</th>
            <th style={thStyle}>업데이트 시간</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post) => (
            <tr key={post.postId}>
              <td style={tdStyle}>{post.likes}</td>
              <td style={tdStyle}>{post.title}</td>
              <td style={tdStyle}>{post.memberName}</td>
              <td style={tdStyle}>{post.commentCnt}</td>
              <td style={tdStyle}>
                {new Date(post.createdAt).toLocaleString()}
              </td>
              <td style={tdStyle}>
                {new Date(post.updatedAt).toLocaleString()}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div style={paginationContainerStyle}>
        {Array.from({ length: totalPages }, (_, index) => (
          <button
            key={index}
            onClick={() => handlePageChange(index)}
            style={
              index === page
                ? { ...buttonStyle, backgroundColor: "#004085" }
                : buttonStyle
            }
          >
            {index + 1}
          </button>
        ))}
      </div>
    </div>
  );
};

export default NoticeBoard;
