import React, { useState, useEffect } from "react";
import { authClient } from "../../services/APIService";

// 스터디 관리 화면
export default function Management() {
  const [isStudyInfoRegisted, setIsStudyInfoRegisted] = useState(false);
  useEffect(() => {
    getStudyInfo();
  });

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

  return (
    <React.Fragment>
      {isStudyInfoRegisted ? (
        <h1>관리자 - 스터디 관리 화면</h1>
      ) : (
        <h1>관리자 - 스터디 등록 화면</h1>
      )}
    </React.Fragment>
  );
}
