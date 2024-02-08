import React from "react";
import "../styles/Button.css";

const Pagination = ({ totalPages, currentPage, onPageChange }) => {
  return (
    <div
      style={{ display: "flex", justifyContent: "center", margin: "20px 0" }}
    >
      {Array.from({ length: totalPages }, (_, index) => (
        <button
          key={index}
          onClick={() => onPageChange(index)}
          className={index === currentPage ? "page-btn active" : "page-btn"}
        >
          {index + 1}
        </button>
      ))}
    </div>
  );
};

export default Pagination;
