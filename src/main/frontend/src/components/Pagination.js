// Pagination.js
import React from "react";

const Pagination = ({ totalPages, currentPage, onPageChange }) => {
  const buttonStyle = {
    margin: "0 5px",
    padding: "5px 10px",
    border: "none",
    borderRadius: "5px",
    backgroundColor: "#375582",
    color: "white",
    cursor: "pointer",
  };

  return (
    <div
      style={{ display: "flex", justifyContent: "center", margin: "20px 0" }}
    >
      {Array.from({ length: totalPages }, (_, index) => (
        <button
          key={index}
          onClick={() => onPageChange(index)}
          style={
            index === currentPage
              ? { ...buttonStyle, backgroundColor: "#004085" }
              : buttonStyle
          }
        >
          {index + 1}
        </button>
      ))}
    </div>
  );
};

export default Pagination;
