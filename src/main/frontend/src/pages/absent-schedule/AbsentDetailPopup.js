import React, { useState, useEffect } from "react";
import CommonDialog from "../../components/CommonDialog";
import { authClient } from "../../services/APIService";
import { formatDateToYYYYMMDD } from "../../util/DateUtil";
import Table from "../../components/Table";

const AbsentDetailPopup = ({ selectedDate, onClose }) => {
  const [absentDetail, setAbsentDetail] = useState([]);

  useEffect(() => {
    if (selectedDate) {
      authClient
        .get(`/absent/detail`, {
          params: {
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

  const columns = [
    { Header: "멤버 이름", accessor: "memberName" },
    {
      Header: "부재 스케줄 리스트",
      accessor: "scheduleNameList",
      Cell: (content) => content.scheduleNameList.join(", "),
    },
    { Header: "부재 사유", accessor: "description" },
    {
      Header: "액션",
      accessor: "actions",
      Cell: () => (
        <>
          <button onClick={() => {}}>수정</button>
          <button onClick={() => {}}>삭제</button>
        </>
      ),
    },
  ];

  const dialogProps = {
    open: true,
    btnTitle: "부재 일정 상세보기",
    title: selectedDate + " 부재 일정 상세",
    submitEvt: () => {},
    acceptStr: "",
    cancleStr: "닫기",
    showButton: false,
    onClose: onClose,
    extraComponents: <Table columns={columns} contents={absentDetail} />,
  };

  return absentDetail.length > 0 ? <CommonDialog {...dialogProps} /> : null;
};

export default AbsentDetailPopup;
