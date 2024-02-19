import React, { useState, useEffect } from "react";
import Select from "react-select";
import { authClient } from "../../services/APIService";
import PersonIcon from "@mui/icons-material/Person";
import EmailIcon from "@mui/icons-material/Email";
import KeyIcon from "@mui/icons-material/Key";
import GroupsIcon from "@mui/icons-material/Groups";
import ScheduleIcon from "@mui/icons-material/Schedule";
import WbSunnyIcon from "@mui/icons-material/WbSunny";
import LockIcon from "@mui/icons-material/Lock";
import MyPagePaper from "./MyPagePaper";
import "../../styles/Button.css";
import "../../styles/MyPage.css";

// 내 정보 화면
export default function MyPage() {
  const [myProfileValueTitles, setMyProfileValueTitles] = useState([]);
  const [myProfileValueDescription, setMyProfileValueDescription] = useState(
    []
  );

  const [schedules, setSchedules] = useState([]);
  const [selectedSchedules, setSelectedSchedules] = useState([]);

  const SchedulesSelect = () => {
    return (
      <div className="dialog-content">
        <div className="mypage-select-title">스케줄</div>
        <Select
          options={schedules}
          isMulti
          required
          value={selectedSchedules}
          onChange={setSelectedSchedules}
          placeholder="스케줄 선택"
          styles={{
            container: (provided) => ({
              ...provided,
              width: "100%",
            }),
          }}
        />
      </div>
    );
  };

  const myProfileProps = {
    paperTitle: "내 프로필",
    keys: ["NAME", "EMAIL", "ROLE", "STUDY_NAME", "SCHEDULE_NAMES", "WAKEUP"],
    dialogTitles: ["이름", "이메일", "", "", "스케줄", "기상 시간"],
    dialogInputNames: [["새 이름"], ["변경 이메일"], [], [], [], ["기상 시간"]],
    dialogInputRequireds: [true, true, true, true, false, true],
    dialogInputDescriptions: [" "],
    iconComponents: [
      <PersonIcon color="action" />,
      <EmailIcon color="action" />,
      <KeyIcon color="action" />,
      <GroupsIcon color="action" />,
      <ScheduleIcon color="action" />,
      <WbSunnyIcon color="action" />,
    ],
    multiSelectComponent: <SchedulesSelect />,
    valueTitle: myProfileValueTitles,
    valueDescription: myProfileValueDescription,
    useBtn: [true, true, false, false, true, true],
  };

  const securityConfigProps = {
    paperTitle: "보안 설정",
    keys: ["PASSWORD"],
    dialogTitles: ["비밀번호"],
    dialogInputNames: [["현재 비밀번호", "새 비밀번호", "새 비밀번호 확인"]],
    dialogInputRequireds: [true, true, true],
    dialogInputDescriptions: [
      " ",
      "8~16자 영문 대 소문자, 숫자, 특수문자",
      " ",
    ],
    iconComponents: [<LockIcon color="action" />],
    valueTitle: ["비밀번호"],
    valueDescription: [],
    useBtn: [true],
  };

  const alarmConfigProps = {
    paperTitle: "알람 설정",
    keys: ["ALL", "GOOGLE_MEET", "ABSENT", "WAKEUP_SUCCESS", "NEW_POST"],
    iconComponents: [],
    valueTitle: [
      "전체",
      "Google Meet 생성",
      "부재 일정",
      "목표 기상 시간 달성",
      "새 글",
    ],
    valueDescription: [],
  };

  useEffect(() => {
    getMemberInfo();
    getSchedules();
  }, []);

  // myProfileValueTitles 상태가 변경된 후에 실행될 로직 처리
  useEffect(() => {
    const currentUserSchedules = myProfileValueTitles[4];
    if (currentUserSchedules) {
      const obj = currentUserSchedules.split(",").map((schedule) => {
        const foundSchedule = schedules.find((ele) => ele.value === schedule);
        return {
          value: schedule,
          label: foundSchedule.label,
        };
      });
      setSelectedSchedules(obj);
    }
  }, [myProfileValueTitles]);

  const getSchedules = () => {
    authClient
      .get("/schedules")
      .then((response) => {
        if (response && response.data) {
          const scheduleOptions = response.data?.registedSchedules.map(
            (schedule) => ({
              value: schedule.scheduleName,
              label: `${schedule.scheduleName} (${schedule.startTime.slice(
                0,
                2
              )}:${schedule.startTime.slice(2)}~${schedule.endTime.slice(
                0,
                2
              )}:${schedule.endTime.slice(2)})`,
            })
          );
          setSchedules(scheduleOptions);
        }
      })
      .catch((error) => {
        alert(
          "스케줄 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const getMemberInfo = () => {
    authClient
      .get("/member")
      .then((response) => {
        if (response && response.data) {
          const data = response.data;
          setMyProfileValueTitles([
            data.name,
            data.email,
            data.role === "USER" ? "스터디원" : "관리자",
            data.studyName,
            data.scheduleName.toString(),
            data.wakeupTime !== null
              ? `${data.wakeupTime.slice(0, 2)}:${data.wakeupTime.slice(2)}`
              : "",
          ]);
          setMyProfileValueDescription([data.id]);
        }
      })
      .catch((error) => {
        console.log(error);
        alert(
          "회원 정보 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const handleEdit = (formJson, key) => {
    let successStr = "";
    const reqBody = {
      type: key,
      name: myProfileValueTitles[0],
      email: myProfileValueTitles[1],
      scheduleName: myProfileValueTitles[4].split(","),
      wakeupTime: myProfileValueTitles[5].split(":").join(""),
    };

    switch (key) {
      case "NAME":
        successStr = "이름";
        reqBody.name = formJson["새 이름"];
        break;
      case "EMAIL":
        successStr = "이메일";
        reqBody.email = formJson["변경 이메일"];
        break;
      case "SCHEDULE_NAMES":
        successStr = "스케줄";
        const scheduleNames = selectedSchedules.map(
          (schedule) => schedule.value
        );
        reqBody.scheduleName = scheduleNames;
        // reqBody.scheduleName = formJson["스케줄"].split(",");
        break;
      case "WAKEUP":
        successStr = "기상 시간";
        reqBody.wakeupTime = formJson["기상 시간"].split(":").join("");
        break;
      case "PASSWORD":
        if (formJson["새 비밀번호"] !== formJson["새 비밀번호 확인"]) {
          alert("새 비밀번호가 일치하지 않습니다.");
          return;
        }
        successStr = "비밀번호";
        reqBody.prePassword = formJson["현재 비밀번호"];
        reqBody.password = formJson["새 비밀번호"];
        break;
      default:
        break;
    }

    authClient
      .patch("/member", reqBody)
      .then(function (response) {
        if (response.data?.retCode === 200) {
          getMemberInfo();
          alert(successStr + " 변경 완료");
        } else {
          alert(response.data?.retMsg);
        }
      })
      .catch(function (error) {
        console.log(error);
        alert(error.response?.data.retMsg);
      });
  };

  return (
    <React.Fragment>
      <MyPagePaper
        paperTitle={myProfileProps.paperTitle}
        keys={myProfileProps.keys}
        iconComponents={myProfileProps.iconComponents}
        valueTitle={myProfileProps.valueTitle}
        valueDescription={myProfileProps.valueDescription}
        useBtn={myProfileProps.useBtn}
        dialogTitles={myProfileProps.dialogTitles}
        dialogInputNames={myProfileProps.dialogInputNames}
        dialogInputRequireds={myProfileProps.dialogInputRequireds}
        dialogInputDescriptions={myProfileProps.dialogInputDescriptions}
        editClicked={handleEdit}
        extraComponents={myProfileProps.multiSelectComponent}
      />
      <MyPagePaper
        paperTitle={securityConfigProps.paperTitle}
        keys={securityConfigProps.keys}
        iconComponents={securityConfigProps.iconComponents}
        valueTitle={securityConfigProps.valueTitle}
        valueDescription={securityConfigProps.valueDescription}
        useBtn={securityConfigProps.useBtn}
        dialogTitles={securityConfigProps.dialogTitles}
        dialogInputNames={securityConfigProps.dialogInputNames}
        dialogInputRequireds={securityConfigProps.dialogInputRequireds}
        dialogInputDescriptions={securityConfigProps.dialogInputDescriptions}
        editClicked={handleEdit}
      />
      <MyPagePaper
        paperTitle={alarmConfigProps.paperTitle}
        keys={alarmConfigProps.keys}
        iconComponents={alarmConfigProps.iconComponents}
        valueTitle={alarmConfigProps.valueTitle}
        valueDescription={alarmConfigProps.valueDescription}
        switchClicked={handleSwitchChange}
        useSwitch={true}
      />
    </React.Fragment>
  );
}

/**
 * To 다연님
 * 스위치 버튼 클릭 시 아래 함수 실행됨
 * 함수만 만들어 놓았으며, 전체 버튼 on/off 시 다른 스위치 버튼 동작 처리,
 * 기본 설정값 반영등은 직접 구현해야함
 * */
const handleSwitchChange = (key, isChecked) => {
  alert(key + " 스위치 버튼 " + isChecked);
};
