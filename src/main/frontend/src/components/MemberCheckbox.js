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
          .filter((member) => member.id !== "admin")
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

  return (
    <div>
      {membersList.map((memberName) => (
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
      ))}
    </div>
  );
};

export default MemberCheckbox;
