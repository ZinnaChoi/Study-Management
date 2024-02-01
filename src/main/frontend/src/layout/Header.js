import React from "react";

import {
  AppBar,
  Box,
  Toolbar,
  IconButton,
  Typography,
  Menu,
  Container,
  Avatar,
  Button,
  Tooltip,
  MenuItem,
} from "@mui/material";
import { Link } from "react-router-dom";
import { menuTree } from "../constants/constants";

function ResponsiveAppBar() {
  const [anchorElStudy, setAnchorElStudy] = React.useState(null);
  const [anchorElUser, setAnchorElUser] = React.useState(null);

  const handleOpenStudySubMenu = (event) => {
    setAnchorElStudy(event.currentTarget);
  };

  const handleCloseStudySubMenu = () => {
    setAnchorElStudy(null);
  };

  const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  return (
    <AppBar position="static" color="inherit" sx={{ boxShadow: "none", mb: 2 }}>
      <Container maxWidth="lg">
        <Toolbar disableGutters>
          {/* 스터디 로고 */}
          <Link
            to={menuTree.main.path}
            style={{ textDecoration: "none", color: "inherit" }}
          >
            <img
              alt="Logo"
              sx={{ display: "flex", mr: 1, width: 32, height: 32 }}
            />
          </Link>
          <Box
            sx={{
              gap: 3,
              flexGrow: 1,
              display: "flex",
              justifyContent: "center",
            }}
          >
            {/* 부재일정 메뉴 버튼 */}
            <Link
              to={menuTree.absentSchedule.path}
              style={{ textDecoration: "none", color: "inherit" }}
            >
              <Button
                key={menuTree.absentSchedule.path}
                sx={{ color: "black", display: "block" }}
              >
                {menuTree.absentSchedule.name}
              </Button>
            </Link>
            {/* 스터디 메뉴 버튼 */}
            <Button
              key={menuTree.study.name}
              onClick={handleOpenStudySubMenu}
              sx={{ color: "black", display: "block" }}
            >
              {menuTree.study.name}
            </Button>
            {/* 게시판 메뉴 버튼 */}
            <Link
              to={menuTree.noticeBoard.path}
              style={{ textDecoration: "none", color: "inherit" }}
            >
              <Button
                key={menuTree.noticeBoard.path}
                sx={{ color: "black", display: "block" }}
              >
                {menuTree.noticeBoard.name}
              </Button>
            </Link>
            {/* 스터디 서브 메뉴 */}
            <Menu
              sx={{
                mt: "45px",
                display: "flex",
                flexDirection: "row",
                "& .MuiMenu-paper": {
                  backgroundColor: "black",
                  color: "white",
                },
              }}
              id="sub-menu-appbar"
              anchorEl={anchorElStudy}
              anchorOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              open={Boolean(anchorElStudy)}
              onClose={handleCloseStudySubMenu}
            >
              {/* 스터디원 정보 메뉴 */}
              <MenuItem
                key={menuTree.study.membersInfo.path}
                onClick={handleCloseStudySubMenu}
              >
                <Typography textAlign="center">
                  <Link
                    to={menuTree.study.membersInfo.path}
                    style={{ textDecoration: "none", color: "inherit" }}
                  >
                    {menuTree.study.membersInfo.name}
                  </Link>
                </Typography>
              </MenuItem>
              {/* 출석 통계 메뉴 */}
              <MenuItem
                key={menuTree.study.attendanceStatistics.path}
                onClick={handleCloseStudySubMenu}
              >
                <Typography textAlign="center">
                  <Link
                    to={menuTree.study.attendanceStatistics.path}
                    style={{ textDecoration: "none", color: "inherit" }}
                  >
                    {menuTree.study.attendanceStatistics.name}
                  </Link>
                </Typography>
              </MenuItem>
              {/* 기상시간 성공률 메뉴 */}
              <MenuItem
                key={menuTree.study.wakeupSuccessRate.path}
                onClick={handleCloseStudySubMenu}
              >
                <Typography textAlign="center">
                  <Link
                    to={menuTree.study.wakeupSuccessRate.path}
                    style={{ textDecoration: "none", color: "inherit" }}
                  >
                    {menuTree.study.wakeupSuccessRate.name}
                  </Link>
                </Typography>
              </MenuItem>
              {/* 스터디 관리(관리자 계정만 접근 가능) 메뉴 */}
              <MenuItem
                key={menuTree.study.management.path}
                onClick={handleCloseStudySubMenu}
                disabled={true}
              >
                <Typography textAlign="center">
                  <Link
                    to={menuTree.study.management.name}
                    style={{ textDecoration: "none", color: "inherit" }}
                  >
                    {menuTree.study.management.name}
                  </Link>
                </Typography>
              </MenuItem>
            </Menu>
          </Box>

          <Box sx={{ flexGrow: 0 }}>
            <Tooltip title="내 계정">
              {/* 사용자 아이콘 버튼 */}
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar sx={{ width: 32, height: 32 }}></Avatar>
              </IconButton>
            </Tooltip>
            <Menu
              sx={{
                mt: "45px",
                flexDirection: "row",
                "& .MuiMenu-paper": {
                  backgroundColor: "black",
                  color: "white",
                },
              }}
              id="menu-appbar"
              anchorEl={anchorElUser}
              anchorOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              open={Boolean(anchorElUser)}
              onClose={handleCloseUserMenu}
            >
              {/* 내 정보 메뉴 버튼 */}
              <MenuItem
                key={menuTree.myPage.path}
                onClick={handleCloseUserMenu}
              >
                <Typography textAlign="center">
                  <Link
                    to={menuTree.myPage.path}
                    style={{ textDecoration: "none", color: "inherit" }}
                  >
                    {menuTree.myPage.name}
                  </Link>
                </Typography>
              </MenuItem>
              {/* 로그아웃 메뉴 버튼 */}
              <MenuItem key={"로그아웃"} onClick={handleCloseUserMenu}>
                <Typography textAlign="center">{"로그아웃"}</Typography>
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}
export default ResponsiveAppBar;
