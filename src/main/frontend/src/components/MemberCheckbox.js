import React, { useState, useEffect } from "react";
import { getCurrentDateTime } from "../util/DateUtil";
import qs from "qs";

const MemberCheckbox = ({
  authClient,
  selectedMembers,
  setSelectedMembers,
}) => {
  const [membersList, setMembersList] = useState([]);

  useEffect(() => {
    const params = {
      sendDate: getCurrentDateTime(),
      systemId: "STUDY_0001",
    };
    authClient
      .get("/members", { params: params })
      .then((response) => {
        const memberNames = response.data.content
          // .filter((member) => member.id !== "admin")  // TODO: 부재일정 추가 기능을 위한 임시 주석 처리. UI 로그인 기능 구현 후 주석 해제 필요
          .map((member) => member.name);
        setMembersList(memberNames);
        setSelectedMembers(memberNames);
      })
      .catch((error) => {
        alert(
          "스터디원 목록 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  }, [authClient]);

  const handleCheckboxChange = (memberName) => {
    setSelectedMembers((prevSelected) => {
      const newSelectedMembers = prevSelected.includes(memberName)
        ? prevSelected.filter((name) => name !== memberName)
        : [...prevSelected, memberName];
      return newSelectedMembers;
    });
  };

  return membersList.map((memberName) => (
    <div key={memberName}>
      <label>
        <input
          type="checkbox"
          checked={selectedMembers.includes(memberName)}
          onChange={() => handleCheckboxChange(memberName)}
        />
        {memberName}
      </label>
    </div>
  ));
};

export default MemberCheckbox;
