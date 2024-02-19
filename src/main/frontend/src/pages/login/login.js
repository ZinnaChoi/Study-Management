import React, { useState, useEffect } from "react";
import {
  Button,
  Avatar,
  FormControl,
  Input,
  InputLabel,
  Paper,
  Typography,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { client } from "../../services/APIService";
import LockPersonOutlinedIcon from "@mui/icons-material/LockPersonOutlined";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { menuTree } from "../../constants/constants";
import "../../styles/Button.css";
import "../../styles/Login.css";

export default function Login() {
  const navigate = useNavigate();
  const [id, setId] = useState("");
  const [pwd, setPwd] = useState({ password: "", showPassword: false });

  const handleClickShowPassword = () => {
    setPwd({
      ...pwd,
      showPassword: !pwd.showPassword,
    });
  };

  const handleChange = (prop) => (event) => {
    if (prop === "password") {
      setPwd({ ...pwd, [prop]: event.target.value });
    } else if (prop === "id") {
      setId({ ...id, [prop]: event.target.value });
    }
  };

  const doLogin = (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const formJson = Object.fromEntries(formData.entries());

    const reqBody = { id: formJson.id, password: formJson.password };
    client
      .post("/login", reqBody)
      .then(function (response) {
        if (response.data?.retCode === 200) {
          localStorage.setItem("token", "Bearer " + response?.data?.token);
          localStorage.setItem("role", response?.data?.role);
          navigate(menuTree.main.path);
          window.location.reload();
        } else {
          alert(response.data?.retMsg);
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  };

  useEffect(() => {
    // 뒤로가기 비활성화
    doBackButtonDisable();
  }, []);

  const doBackButtonDisable = () => {
    window.history.pushState(null, null, window.location.pathname);
    window.addEventListener("popstate", handleBackButtonDisable);

    return () => {
      window.removeEventListener("popstate", handleBackButtonDisable);
    };
  };

  const handleBackButtonDisable = (event) => {
    window.history.pushState(null, null, window.location.pathname);
  };

  const routeJoin = () => {
    navigate(menuTree.join.path);
  };

  return (
    <div className="root-layout">
      <Paper elevation={3} className="paper-size">
        <div className="login-title">
          <Avatar className="login-icon">
            <LockPersonOutlinedIcon color="white" />
          </Avatar>
        </div>
        <div className="login-title">
          <Typography variant="h5">로그인</Typography>
        </div>
        <form onSubmit={doLogin}>
          <FormControl margin="normal" required fullWidth>
            <InputLabel htmlFor="id">아이디</InputLabel>
            <Input id="id" name="id" onChange={handleChange("id")} autoFocus />
          </FormControl>
          <FormControl margin="normal" required fullWidth>
            <InputLabel htmlFor="password">비밀번호</InputLabel>
            <Input
              id="password"
              name="password"
              type={pwd.showPassword ? "text" : "password"}
              value={pwd.password}
              onChange={handleChange("password")}
              autoComplete="current-password"
              endAdornment={
                <Button size="small" onClick={handleClickShowPassword}>
                  {pwd.showPassword ? (
                    <Visibility fontSize="small" />
                  ) : (
                    <VisibilityOff fontSize="small" />
                  )}
                </Button>
              }
            />
          </FormControl>
          <div className="row-space">
            <Button
              type="submit"
              fullWidth
              variant="contained"
              className="accept-btn"
            >
              로그인
            </Button>
          </div>
          <Button
            onClick={routeJoin}
            fullWidth
            variant="contained"
            className="cancel-btn"
          >
            회원 가입
          </Button>
        </form>
      </Paper>
    </div>
  );
}
