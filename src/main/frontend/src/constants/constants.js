export const menuTree = {
  main: { path: "/", name: "main" },
  absentSchedule: { path: "/absent-schedule", name: "부재 일정" },
  myPage: { path: "/my-page", name: "내 정보" },
  noticeBoard: { path: "/notice-board", name: "게시판" },
  study: {
    name: "스터디",
    attendanceStatistics: { path: "/attendance-statistics", name: "출석 통계" },
    management: { path: "/management", name: "스터디 관리" },
    membersInfo: { path: "/members-info", name: "스터디원 정보" },
    wakeupSuccessRate: {
      path: "/wakeup-success-rate",
      name: "기상시간 성공률",
    },
  },
  notFound: { path: "*", name: "not found" },
};

export const copyRights = "@모각코. All rights reserved.";
