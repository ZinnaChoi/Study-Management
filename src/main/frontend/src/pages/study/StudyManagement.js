import React, { useState, useEffect } from "react";
import { Tooltip, Chip, Paper, Button } from "@mui/material";
import CommonDialog from "../../components/CommonDialog";
import { authClient, authFormClient } from "../../services/APIService";
import "../../styles/Button.css";
import "../../styles/MyPage.css";

export default function StudyManagement(props) {
  const [logo, setLogo] = useState(null);

  useEffect(() => {
    setLogoImg();
  });

  function setLogoImg() {
    authClient
      .get("/study")
      .then(function (response) {
        setLogo(response.data?.logo);
      })
      .catch(function (error) {
        console.log(error);
      });
  }
  return (
    <React.Fragment>
      {/* <div className="paper-align"> */}
      <Paper elevation={8} style={{ width: "100%" }}>
        <h3 className="paper-title">스터디 관리</h3>
        <hr />
        <br />
        <div style={{ display: "flex", justifyContent: "end" }}>
          <Button>정보 변경</Button>
          <Button>스터디 삭제</Button>
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            margin: "auto",
            width: "320px",
            height: "320px",
            border: "1px solid black",
            borderRadius: "4px",
          }}
        >
          <img
            src={"data:image/png;base64," + logo}
            alt="Logo"
            // width="320"
            // height="320"
          />
        </div>
        <div>
          <h3>스터디명</h3>
          <p>스터디 이름</p>
        </div>
        <div>
          <h3>스케줄</h3>
          <p>스케줄 나열</p>
        </div>
        <br />
      </Paper>
      {/* </div> */}
      <br />
    </React.Fragment>
  );
}
