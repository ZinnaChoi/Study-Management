import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";
import { getCurrentDateTime, formatDateToYYYYMMDD } from "../../util/DateUtil";
import Table from "../../components/Table";

const AbsentDetailPopup = ({ selectedDate, onClose }) => {
  const [absentDetail, setAbsentDetail] = useState(null);

  useEffect(() => {
    if (selectedDate) {
      authClient
        .get(`/absent/detail`, {
          params: {
            sendDate: getCurrentDateTime(),
            systemId: "STUDY_0001",
            absentDate: formatDateToYYYYMMDD(selectedDate),
          },
        })
        .then((response) => {
          setAbsentDetail(response.data.content);
        })
        .catch((error) => {
          alert(
            "부재일정 상세 정보 조회 실패: " +
              (error.response?.data.retMsg || "Unknown error")
          );
        });
    }
  }, [selectedDate]);

  const details = absentDetail || [];
  if (details.length == 0) {
    return null;
  }

  const popupContainerStyle = {
    position: "fixed",
    top: "0",
    left: "0",
    width: "100%",
    height: "100%",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0, 0, 0, 0.4)",
    zIndex: "2",
  };

  const popupStyle = {
    width: "auto",
    maxHeight: "80vh",
    overflowY: "auto",
    background: "#fff",
    padding: "20px",
    borderRadius: "10px",
    boxShadow: "0 2px 10px rgba(0, 0, 0, 0.3)",
    zIndex: "3",
  };

  const closeButtonStyle = {
    display: "flex",
    justifyContent: "center",
    marginTop: "10px",
  };

  const columns = [
    { Header: "멤버 이름", accessor: "memberName" },
    {
      Header: "부재 스케줄 리스트",
      accessor: "scheduleNameList",
      Cell: ({ scheduleNameList }) => scheduleNameList.join(", "),
    },
    { Header: "부재 사유", accessor: "description" },
    {
      Header: "액션",
      accessor: "actions",
      Cell: (detail) => (
        <>
          <button onClick={() => {}}>수정</button>
          <button onClick={() => {}}>삭제</button>
        </>
      ),
    },
  ];

  return (
    <div style={popupContainerStyle}>
      <div style={popupStyle}>
        <h3>{selectedDate + " 부재 일정 상세"}</h3>
        <Table columns={columns} contents={absentDetail} />
        <div style={closeButtonStyle}>
          <button onClick={onClose}>닫기</button>
        </div>
      </div>
    </div>
  );
};

export default AbsentDetailPopup;
