import React, { useState, useEffect } from "react";
import { Tooltip, Chip, Paper, Button } from "@mui/material";
import { authClient, authFormClient } from "../../services/APIService";
import { useNavigate } from "react-router-dom";
import { menuTree } from "../../constants/constants";
import HomeIcon from "@mui/icons-material/Home";
import "../../styles/Button.css";
import "../../styles/MyPage.css";
import "../../styles/StudyManagement.css";
import StudyRegistPopup from "./StudyRegistPopup";

export default function StudyManagement() {
  const navigate = useNavigate();
  const [logo, setLogo] = useState(null);
  const [studyInfo, setStudyInfo] = useState({ id: "", name: "" });
  const [schedules, setSchedules] = useState([]);
  const [editDialogOpen, setEditDialogOpen] = useState(false);

  useEffect(() => {
    getStudyInfo();
  }, []);

  function getStudyInfo() {
    authClient
      .get("/study")
      .then(function (response) {
        if (response.data?.retCode === 200) {
          const studyInfo = {
            id: response.data?.studyId,
            name: response.data?.studyName,
          };
          setLogo(response.data?.logo);
          setStudyInfo((prev) => {
            return { ...prev, ...studyInfo };
          });
          setSchedules(response.data?.schedules);
        } else {
          alert(response.data?.retMsg);
        }
      })
      .catch(function (error) {
        alert(
          "스터디 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  }

  const doStudyEdit = (data, addedSchedules) => {
    if (addedSchedules.length === 0) {
      alert("스케줄을 추가해주세요.");
      return;
    }
    const updateStudyName = data["스터디 이름"];
    const logoFile =
      data["로고"].name === "" || data["로고"].size === 0 ? null : data["로고"];
    const newAddedSchedules = addedSchedules.map(
      ({ scheduleId, ...rest }) => rest
    );
    const schedules = newAddedSchedules;
    const reqBody = {
      studyName: studyInfo.name,
      updateStudyName: updateStudyName,
      schedules: schedules,
    };

    const formData = new FormData();
    formData.append("req", JSON.stringify(reqBody));
    formData.append("logo file", logoFile);
    authFormClient
      .put("/study", formData)
      .then(function (response) {
        if (response.data?.retCode === 200) {
          alert("스터디 정보가 성공적으로 수정되었습니다.");
          window.location.reload();
        } else {
          alert(response.data?.retMsg);
        }
      })
      .catch(function (error) {
        alert(
          "스터디 정보 수정 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const doStudyDelete = () => {
    const isConfirm = window.confirm("정말 스터디를 삭제하시겠습니까?");
    if (isConfirm) {
      authClient
        .delete("/study/" + studyInfo.id)
        .then(function (response) {
          if (response.data?.retCode === 200) {
            alert("스터디 정보가 성공적으로 삭제되었습니다.");
            navigate(menuTree.login.path);
          } else {
            alert(response.data?.retMsg);
          }
        })
        .catch(function (error) {
          alert(
            "스터디 삭제 실패: " +
              (error.response?.data.retMsg || "Unknown error")
          );
        });
    }
  };

  return (
    <React.Fragment>
      <Paper elevation={8}>
        <h3 className="paper-title">스터디 관리</h3>
        <hr />
        <br />
        <div className="study-paper-content">
          <div className="study-btn-align">
            <Button
              onClick={() => setEditDialogOpen(true)}
              className="accept-btn study-btn-space"
            >
              정보 변경
            </Button>
            <StudyRegistPopup
              open={editDialogOpen}
              useCancel={true}
              schedules={schedules}
              doAction={doStudyEdit}
              onClose={() => setEditDialogOpen(false)}
            />
            <Button className="cancel-btn" onClick={doStudyDelete}>
              스터디 삭제
            </Button>
          </div>
          <br />
          <div className="study-logo-container">
            {logo !== null ? (
              <img
                src={"data:image/png;base64," + logo}
                alt="Logo"
                width="320"
                height="320"
              />
            ) : (
              <HomeIcon sx={{ fontSize: 320 }} />
            )}
          </div>
          <div>
            <h3>스터디명</h3>
            <span>{studyInfo.name}</span>
          </div>
          <div>
            <h3>스케줄</h3>
            {schedules.map((schedule, index) => (
              <Tooltip
                key={index}
                title={`${schedule.startTime.slice(
                  0,
                  2
                )}:${schedule.startTime.slice(2)} ~ ${schedule.endTime.slice(
                  0,
                  2
                )}:${schedule.endTime.slice(2)}`}
              >
                <Chip
                  className="study-manage-chip-space"
                  label={schedule.scheduleName}
                ></Chip>
              </Tooltip>
            ))}
          </div>
        </div>
        <br />
      </Paper>
      <br />
    </React.Fragment>
  );
}
