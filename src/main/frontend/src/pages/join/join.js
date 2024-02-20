import React, { useState, useEffect } from "react";
import { Button, FormControl, Input, Paper, Typography } from "@mui/material";
import Select from "react-select";
import { useNavigate } from "react-router-dom";
import { client } from "../../services/APIService";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { menuTree } from "../../constants/constants";
import "../../styles/Button.css";
import "../../styles/Join.css";

export default function Join() {
  const navigate = useNavigate();
  const [id, setId] = useState("");
  const [pwd, setPwd] = useState({ password: "", showPassword: false });
  const [confirmPwd, setConfirmPwd] = useState({
    password: "",
    showPassword: false,
  });

  const [schedules, setSchedules] = useState([]);
  const [selectedSchedules, setSelectedSchedules] = useState([]);

  const [isDuplicateChecked, setIsDuplicateChecked] = useState(false);

  useEffect(() => {
    getSchedules();
  }, []);

  const SchedulesSelect = () => {
    return (
      <div className="input-size">
        <Select
          options={schedules}
          isMulti
          required
          value={selectedSchedules}
          onChange={setSelectedSchedules}
          placeholder="스케줄 선택"
        />
      </div>
    );
  };

  const getSchedules = () => {
    client
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

  const handleClickShowPassword = (val) => {
    if (val === "password") {
      setPwd({
        ...pwd,
        showPassword: !pwd.showPassword,
      });
    } else if (val === "password-confirm") {
      setConfirmPwd({
        ...confirmPwd,
        showPassword: !confirmPwd.showPassword,
      });
    }
  };

  const checkIdDuplicated = () => {
    if (id === null || id.length === 0) {
      alert("아이디를 입력해주세요.");
      return;
    }
    client
      .post("/join/check-id", id)
      .then(function (response) {
        if (response.data?.retCode === 200) {
          if (response.data?.duplicated) {
            alert("이미 존재하는 아이디 입니다. \n다른 아이디를 사용해주세요.");
            setId("");
          } else {
            setIsDuplicateChecked(true);
            alert("사용 가능한 아이디 입니다.");
          }
        } else {
          alert(response.data?.retMsg);
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  };

  const handleChange = (prop) => (event) => {
    if (prop === "password") {
      setPwd({ ...pwd, password: event.target.value });
    } else if (prop === "password-confirm") {
      setConfirmPwd({ ...confirmPwd, password: event.target.value });
    }
  };

  const doJoin = (event) => {
    event.preventDefault();
    if (!isDuplicateChecked) {
      alert("아이디 중복 확인을 해주세요.");
      return;
    }
    const formData = new FormData(event.currentTarget);
    let formJson = Object.fromEntries(formData.entries());
    const password = formJson["password"];
    const confirmPassword = formJson["password-confirm"];

    if (password !== confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    delete formJson["password-confirm"];
    formJson["wakeupTime"] = formJson["wakeupTime"].split(":").join("");
    formJson.scheduleNames = selectedSchedules.map(
      (schedule) => schedule.value
    );

    client
      .post("/join", formJson)
      .then(function (response) {
        if (response.data?.retCode === 200) {
          alert("회원가입이 완료 되었습니다. 로그인 페이지로 이동합니다.");
          navigate(menuTree.login.path);
        } else {
          alert(response.data?.retMsg);
        }
      })
      .catch(function (error) {
        console.log(error);
        const messages = JSON.parse(error.response?.data.retMsg);
        let message = "";
        for (let key in messages) {
          if (messages.hasOwnProperty(key)) {
            message += messages[key] + "\n";
          }
        }
        alert("회원 가입 실패: " + (message || "Unknown error"));
      });
  };

  return (
    <div className="join-layout">
      <Paper elevation={3} className="join-paper-size">
        <Typography variant="h5" className="join-title">
          회원가입
        </Typography>
        <hr />
        <form onSubmit={doJoin}>
          <div className="join-contents">
            <FormControl margin="normal" required fullWidth>
              <div className="join-content">
                <div className="join-input-key">이름</div>
                <Input className="input-size" id="name" name="name" autoFocus />
              </div>
            </FormControl>
            <FormControl margin="normal" required fullWidth>
              <div className="join-content">
                <div className="join-input-key">이메일</div>
                <Input
                  className="input-size"
                  id="email"
                  name="email"
                  type="email"
                />
              </div>
            </FormControl>
            <FormControl margin="normal" required fullWidth>
              <div className="join-content">
                <div className="join-input-key">아이디</div>
                <Input
                  className="input-size"
                  id="id"
                  name="id"
                  value={id}
                  onChange={(e) => setId(e.target.value)}
                  endAdornment={
                    <Button
                      className="accept-btn"
                      size="small"
                      onClick={checkIdDuplicated}
                    >
                      중복 확인
                    </Button>
                  }
                />
              </div>
            </FormControl>
            <FormControl margin="normal" required fullWidth>
              <div className="join-content">
                <div className="join-input-key">비밀번호</div>
                <Input
                  className="input-size"
                  id="password"
                  name="password"
                  type={pwd.showPassword ? "text" : "password"}
                  value={pwd.password}
                  onChange={handleChange("password")}
                  placeholder="8~16자 영문 대 소문자, 숫자, 특수문자"
                  autoComplete="current-password"
                  endAdornment={
                    <Button
                      size="small"
                      onClick={() => handleClickShowPassword("password")}
                    >
                      {pwd.showPassword ? (
                        <Visibility fontSize="small" />
                      ) : (
                        <VisibilityOff fontSize="small" />
                      )}
                    </Button>
                  }
                  inputProps={{
                    style: { fontSize: "0.8rem" },
                  }}
                />
              </div>
            </FormControl>
            <FormControl margin="normal" required fullWidth>
              <div className="join-content">
                <div className="join-input-key">비밀번호 확인</div>
                <Input
                  className="input-size"
                  id="password-confirm"
                  name="password-confirm"
                  type={confirmPwd.showPassword ? "text" : "password"}
                  value={confirmPwd.password}
                  onChange={handleChange("password-confirm")}
                  endAdornment={
                    <Button
                      size="small"
                      onClick={() =>
                        handleClickShowPassword("password-confirm")
                      }
                    >
                      {confirmPwd.showPassword ? (
                        <Visibility fontSize="small" />
                      ) : (
                        <VisibilityOff fontSize="small" />
                      )}
                    </Button>
                  }
                />
              </div>
            </FormControl>
            <FormControl margin="normal" required fullWidth>
              <div className="join-content">
                <div className="join-input-key">참여 스케줄</div>
                <SchedulesSelect />
              </div>
            </FormControl>
            <FormControl margin="normal" required fullWidth>
              <div className="join-content">
                <div className="join-input-key">기상 시간</div>
                <Input
                  className="input-size"
                  id="wakeupTime"
                  name="wakeupTime"
                  type="time"
                />
              </div>
            </FormControl>
          </div>
          <div className="join-row-space">
            <Button
              type="submit"
              fullWidth
              variant="contained"
              className="cancel-btn"
            >
              회원 가입
            </Button>
          </div>
        </form>
      </Paper>
    </div>
  );
}
