export const menuTree = {
  main: { path: "/", name: "스터디원 정보" },
  absentSchedule: { path: "/absent-schedule", name: "부재 일정" },
  myPage: { path: "/my-page", name: "내 정보" },
  noticeBoard: { path: "/notice-board", name: "게시판" },
  statistics: {
    name: "통계",
    attendanceStatistics: { path: "/attendance-statistics", name: "출석 통계" },
    wakeupSuccessRate: {
      path: "/wakeup-success-rate",
      name: "기상 통계",
    },
  },
  management: { path: "/management", name: "스터디 관리" },
  login: { path: "/login", name: "로그인" },
  join: { path: "/join", name: "회원가입" },
  notFound: { path: "*", name: "not found" },
};

export const copyRights = "@모각코. All rights reserved.";
