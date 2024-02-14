import React, { useEffect } from "react";

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
import { Link, useNavigate } from "react-router-dom";
import { menuTree } from "../constants/constants";
import { authClient } from "../services/APIService";
import HomeIcon from "@mui/icons-material/Home";

function Header() {
  const navigate = useNavigate();
  const [anchorElStudy, setAnchorElStudy] = React.useState(null);
  const [anchorElUser, setAnchorElUser] = React.useState(null);
  const [logo, setLogo] = React.useState(null);

  const handleOpenStatisticsSubMenu = (event) => {
    setAnchorElStudy(event.currentTarget);
  };

  const handleClostStatisticsSubMenu = () => {
    setAnchorElStudy(null);
  };

  const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const doLogout = () => {
    navigate(menuTree.login.path);
    localStorage.removeItem("token");
    alert("로그아웃 되었습니다.");
  };

  // 통계 내 서브메뉴
  const SubMenuItem = ({ menuItem, onCloseSubMenu }) => {
    return (
      <MenuItem key={menuItem.path} onClick={onCloseSubMenu}>
        <Typography textAlign="center">
          <Link
            to={menuItem.path}
            style={{ textDecoration: "none", color: "inherit" }}
          >
            {menuItem.name}
          </Link>
        </Typography>
      </MenuItem>
    );
  };

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

  useEffect(() => {
    setLogoImg();
  });

  return (
    <AppBar position="static" color="inherit" sx={{ boxShadow: "none", mb: 2 }}>
      <Container maxWidth="lg">
        <Toolbar disableGutters>
          {/* 스터디 로고 */}
          <Link
            to={menuTree.main.path}
            style={{ textDecoration: "none", color: "inherit" }}
          >
            {/* 조건부 로고 표출 */}
            {logo !== null ? (
              <img
                src={"data:image/png;base64," + logo}
                alt="Logo"
                width="32"
                height="32"
              />
            ) : (
              <HomeIcon fontSize="large" />
            )}
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
            {/* 통계 메뉴 버튼 */}
            <Button
              key={menuTree.statistics.name}
              onClick={handleOpenStatisticsSubMenu}
              sx={{ color: "black", display: "block" }}
            >
              {menuTree.statistics.name}
            </Button>
            {/* 통계 서브 메뉴 */}
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
              onClose={handleClostStatisticsSubMenu}
            >
              {Object.values(menuTree.statistics).map((menuItem) =>
                menuItem !== "통계" ? (
                  <SubMenuItem
                    key={menuItem.path}
                    menuItem={menuItem}
                    onCloseSubMenu={handleClostStatisticsSubMenu}
                  />
                ) : null
              )}
            </Menu>
            {/* 스터디 관리 메뉴 버튼 */}
            <Link
              to={menuTree.management.path}
              style={{
                textDecoration: "none",
                color: "inherit",
                pointerEvents: menuTree.management.disabled ? "none" : "",
              }}
            >
              <Button
                key={menuTree.management.path}
                sx={{ color: "black", display: "block" }}
                disabled={menuTree.management.disabled}
              >
                {menuTree.management.name}
              </Button>
            </Link>
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
              <MenuItem key={"로그아웃"} onClick={doLogout}>
                <Typography textAlign="center">{"로그아웃"}</Typography>
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}
export default Header;
