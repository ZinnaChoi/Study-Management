import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import ReactDOM from "react-dom/client";
import Header from "./layout/Header";
import Footer from "./layout/Footer";
import NotFound from "./NotFound";
import AbsentSchedule from "./pages/absent-schedule/AbsentSchedule";
import MyPage from "./pages/my-page/MyPage";
import NoticeBoard from "./pages/notice-board/NoticeBoard";
import AttendanceStatistics from "./pages/study/AttendanceStatistics";
import Management from "./pages/study/Management";
import MembersInfo from "./pages/home/MembersInfo";
import WakeUpSuccessRate from "./pages/study/WakeUpSuccessRate";
import Login from "./pages/login/login";
import Join from "./pages/join/join";
import CssBaseline from "@mui/material/CssBaseline";
import Container from "@mui/material/Container";
import { menuTree } from "./constants/constants";

const root = ReactDOM.createRoot(document.getElementById("root"));

const token = localStorage.getItem("token");

root.render(
  <React.Fragment>
    <CssBaseline />
    <React.StrictMode>
      <BrowserRouter>
        {/* 로그인 페이지를 위한 라우트 */}
        <Routes>
          <Route path={menuTree.join.path} element={<Join />} />
          <Route path={menuTree.login.path} element={<Login />} />
          {/* 기존의 메인 페이지 라우트들 */}
          <Route
            path="*"
            element={
              token ? (
                <React.Fragment>
                  <Header />
                  <Container
                    maxWidth="lg"
                    style={{
                      minHeight: "80vh",
                    }}
                  >
                    <Routes>
                      <Route
                        path={menuTree.main.path}
                        element={<MembersInfo />}
                      ></Route>
                      <Route
                        path={menuTree.absentSchedule.path}
                        element={<AbsentSchedule />}
                      ></Route>
                      <Route
                        path={menuTree.myPage.path}
                        element={<MyPage />}
                      ></Route>
                      <Route
                        path={menuTree.noticeBoard.path}
                        element={<NoticeBoard />}
                      ></Route>
                      <Route
                        path={menuTree.statistics.attendanceStatistics.path}
                        element={<AttendanceStatistics />}
                      ></Route>
                      <Route
                        path={menuTree.statistics.wakeupSuccessRate.path}
                        element={<WakeUpSuccessRate />}
                      ></Route>
                      <Route
                        path={menuTree.management.path}
                        element={<Management />}
                      ></Route>
                      {/* 상단에 위치하는 라우트들의 규칙을 모두 확인, 일치하는 라우트가 없는경우 처리 */}
                      <Route
                        path={menuTree.notFound.path}
                        element={<NotFound />}
                      ></Route>
                    </Routes>
                  </Container>
                  <Footer />
                </React.Fragment>
              ) : (
                <Navigate to={menuTree.login.path} replace />
              )
            }
          />
        </Routes>
      </BrowserRouter>
    </React.StrictMode>
  </React.Fragment>
);
