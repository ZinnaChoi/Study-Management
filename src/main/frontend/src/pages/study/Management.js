import React, { useState, useEffect } from "react";
import StudyRegistPopup from "./StudyRegistPopup";
import StudyManagement from "./StudyManagement";
import { authClient, authFormClient } from "../../services/APIService";
import "../../styles/Button.css";
import "../../styles/MyPage.css";

// 스터디 관리 화면
export default function Management() {
  const [isStudyInfoRegisted, setIsStudyInfoRegisted] = useState(false);

  useEffect(() => {
    getStudyInfo();
  }, []);

  function getStudyInfo() {
    authClient
      .get("/study")
      .then(function (response) {
        if (response.data?.studyName) {
          setIsStudyInfoRegisted(true);
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  }

  const doStudyRegist = (data, addedSchedules) => {
    if (addedSchedules.length === 0) {
      alert("스케줄을 추가해주세요.");
      return;
    }
    const studyName = data["스터디 이름"];
    const logoFile =
      data["로고"].name === "" || data["로고"].size === 0 ? null : data["로고"];
    const schedules = addedSchedules;
    const reqBody = { studyName: studyName, schedules: schedules };

    const formData = new FormData();
    formData.append("req", JSON.stringify(reqBody));
    formData.append("logo file", logoFile);
    authFormClient
      .post("/study", formData)
      .then(function (response) {
        if (response.data?.retCode === 200) {
          alert("스터디 정보가 성공적으로 등록되었습니다.");
          window.location.reload();
        } else {
          alert(response.data?.retMsg);
        }
      })
      .catch(function (error) {
        alert(
          "스터디 등록 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  return (
    <React.Fragment>
      {isStudyInfoRegisted ? (
        <StudyManagement />
      ) : (
        <StudyRegistPopup
          open={!isStudyInfoRegisted}
          doAction={doStudyRegist}
        />
      )}
    </React.Fragment>
  );
}
