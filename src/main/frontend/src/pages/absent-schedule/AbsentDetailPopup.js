import React, { useState, useEffect } from "react";
import CommonDialog from "../../components/CommonDialog";
import { authClient } from "../../services/APIService";
import { formatDateToYYYYMMDD } from "../../util/DateUtil";
import Table from "../../components/Table";
import Select from "react-select";
import "../../styles/Button.css";

const AbsentDetailPopup = ({ selectedDate, onClose, onRefresh }) => {
  const [absentDetail, setAbsentDetail] = useState([]);
  const [editingMembers, setEditingMembers] = useState({});
  const [schedules, setSchedules] = useState({});
  const [editFormData, setEditFormData] = useState({});
  const [loginMemberName, setLoginMemberName] = useState({});

  useEffect(() => {
    authClient
      .get("/member")
      .then((response) => {
        setLoginMemberName(response.data.name);
        const scheduleOptions = response.data.scheduleName.reduce(
          (options, scheduleName) => {
            options[scheduleName] = {
              value: scheduleName,
              label: scheduleName,
            };
            return options;
          },
          {}
        );
        setSchedules(scheduleOptions);
      })
      .catch((error) => {
        alert(
          "스터디원 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  }, []);

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
          const initialEditingMembers = response.data.content.reduce(
            (acc, detail) => {
              acc[detail.memberName] = false;
              return acc;
            },
            {}
          );
          setEditingMembers(initialEditingMembers);
        })
        .catch((error) => {
          alert(
            "부재일정 상세 정보 조회 실패: " +
              (error.response?.data.retMsg || "Unknown error")
          );
        });
    }
  }, [selectedDate]);

  const handleEditClick = (memberName) => {
    setEditingMembers((prevEditingMembers) => ({
      ...prevEditingMembers,
      [memberName]: true,
    }));

    const memberDetail = absentDetail.find(
      (detail) => detail.memberName === memberName
    );

    const selectedScheduleOptions = memberDetail.scheduleNameList.map(
      (name) => ({
        value: name,
        label: name,
      })
    );

    setEditFormData((prevEditFormData) => ({
      ...prevEditFormData,
      [memberName]: {
        scheduleNameList: selectedScheduleOptions,
        allSchedules: Object.values(schedules),
        description: memberDetail.description,
      },
    }));
  };

  const handleEditFormChange = (memberName, name, value) => {
    setEditFormData((prevEditFormData) => ({
      ...prevEditFormData,
      [memberName]: {
        ...prevEditFormData[memberName],
        [name]: value,
      },
    }));
  };

  const handleSaveClick = (memberName) => {
    const updatedData = {
      memberName: memberName,
      absentDate: formatDateToYYYYMMDD(selectedDate),
      description: editFormData[memberName].description,
      scheduleNameList: editFormData[memberName].scheduleNameList.map(
        (item) => item.value
      ),
    };

    authClient
      .patch("/absent", updatedData)
      .then((response) => {
        if (response.data.retCode == 400) {
          alert(response.data.retMsg);
        } else {
          setEditingMembers((prevEditingMembers) => ({
            ...prevEditingMembers,
            [memberName]: false,
          }));
          setAbsentDetail((prevDetails) =>
            prevDetails.map((detail) =>
              detail.memberName === memberName
                ? { ...detail, ...updatedData }
                : detail
            )
          );
          onRefresh();
        }
      })
      .catch((error) => {
        alert("수정 실패: " + (error.response?.data.retMsg || "Unknown error"));
      });
  };

  const handleDelete = (memberName) => {
    const confirmation = window.confirm(
      `${selectedDate}${memberName}'s absence will be deleted. Are you sure?`
    );
    if (confirmation) {
      authClient
        .delete(`/absent`, {
          params: {
            absentDate: formatDateToYYYYMMDD(selectedDate),
          },
        })
        .then(() => {
          onRefresh();
        })
        .catch((error) => {
          alert(
            "Failed to delete the absence: " +
              (error.response?.data.retMsg || "Unknown error")
          );
        });
    }
  };

  const tableContents = absentDetail.map((detail) => {
    const isEditing = editingMembers[detail.memberName];
    const isCurrentUser = detail.memberName === loginMemberName;

    return {
      ...detail,
      actions: (
        <div onClick={(e) => e.stopPropagation()}>
          {isCurrentUser && (
            <>
              {isEditing ? (
                <button
                  className="accept-btn"
                  onClick={() => handleSaveClick(detail.memberName)}
                >
                  저장
                </button>
              ) : (
                <>
                  <button
                    className="edit-btn"
                    style={{ marginRight: "5px" }}
                    onClick={() => handleEditClick(detail.memberName)}
                  >
                    수정
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => handleDelete(detail.memberName)}
                  >
                    삭제
                  </button>
                </>
              )}
            </>
          )}
        </div>
      ),
      scheduleNameList: editingMembers[detail.memberName] ? (
        <Select
          options={editFormData[detail.memberName]?.allSchedules}
          isMulti
          value={editFormData[detail.memberName]?.scheduleNameList}
          onChange={(value) =>
            handleEditFormChange(detail.memberName, "scheduleNameList", value)
          }
        />
      ) : (
        detail.scheduleNameList.join(", ")
      ),
      description: editingMembers[detail.memberName] ? (
        <input
          type="text"
          value={editFormData[detail.memberName]?.description || ""}
          onChange={(e) =>
            handleEditFormChange(
              detail.memberName,
              "description",
              e.target.value
            )
          }
        />
      ) : (
        detail.description
      ),
    };
  });

  const columns = [
    { Header: "멤버 이름", accessor: "memberName" },
    {
      Header: "부재 스케줄 리스트",
      accessor: "scheduleNameList",
      Cell: (content) => {
        return Array.isArray(content.scheduleNameList)
          ? content.scheduleNameList.join(", ")
          : content.scheduleNameList;
      },
    },
    { Header: "부재 사유", accessor: "description" },
    {
      Header: "액션",
      accessor: "actions",
    },
  ];

  return (
    <CommonDialog
      open={absentDetail.length > 0}
      btnTitle="부재 일정 상세보기"
      title={`${selectedDate} 부재 일정 상세`}
      submitEvt={() => {}}
      cancleStr="닫기"
      showButton={false}
      extraComponents={<Table columns={columns} contents={tableContents} />}
      onClose={onClose}
    />
  );
};

export default AbsentDetailPopup;
