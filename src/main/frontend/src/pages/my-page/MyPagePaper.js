import React from "react";
import { Paper, Divider, Grid, Switch, Tooltip } from "@mui/material";
import CommonDialog from "../../components/CommonDialog";
import "../../styles/Button.css";
import "../../styles/MyPage.css";

export default function MyPagePaper(props) {
  return (
    <React.Fragment>
      <div className="paper-align">
        <Paper elevation={8} style={{ width: "70%" }}>
          <h3 className="paper-title">{props.paperTitle}</h3>
          <hr />
          <br />
          {props.valueTitle.map((title, index) => (
            <React.Fragment key={index}>
              <Grid container className="grid-container">
                <Grid item xs={1}>
                  {props.keys[index] && props.iconComponents[index] && (
                    <Tooltip title={props.keys[index]}>
                      {props.iconComponents[index]}
                    </Tooltip>
                  )}
                </Grid>
                <Grid item xs={7}>
                  <div className="value-box">
                    <span className="value-title">{title}</span>
                    <span className="value-description">
                      {props.valueDescription[index]}
                    </span>
                  </div>
                </Grid>
                <Grid item xs={1}>
                  {props.useSwitch ? (
                    <Switch
                      onChange={(e) =>
                        props.switchClicked(props.keys[index], e.target.checked)
                      }
                      checked={
                        props.useSwitch
                          ? props.switchStates[props.keys[index]]
                          : false
                      }
                    />
                  ) : (
                    props.useBtn[index] &&
                    localStorage.getItem("role") !== "ADMIN" && (
                      <CommonDialog
                        btnTitle={"수정"}
                        btnClass={"edit-btn"}
                        title={props.dialogTitles[index] + " 수정"}
                        names={props.dialogInputNames[index]}
                        descriptions={props.dialogInputDescriptions}
                        isRequireds={props.dialogInputRequireds}
                        acceptStr={"확인"}
                        cancleStr={"취소"}
                        submitEvt={(formJson) => {
                          props.editClicked(formJson, props.keys[index]);
                        }}
                        showButton={true}
                        inputTypes={
                          props.keys[index] === "WAKEUP"
                            ? ["time"]
                            : props.keys[index] === "PASSWORD"
                            ? ["password", "password", "password"]
                            : props.keys[index] === "EMAIL"
                            ? ["email"]
                            : null
                        }
                        extraComponents={
                          props.keys[index] === "SCHEDULE_NAMES"
                            ? props.extraComponents
                            : null
                        }
                      ></CommonDialog>
                    )
                  )}
                </Grid>
              </Grid>
              <Divider className="divide-line" />
              <div className="content-space"></div>
            </React.Fragment>
          ))}
          <br />
        </Paper>
      </div>
      <br />
    </React.Fragment>
  );
}
