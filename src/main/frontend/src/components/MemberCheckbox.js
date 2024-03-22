import React, { useState, useEffect } from "react";
import qs from "qs";

const MemberCheckbox = ({
  authClient,
  selectedMembers,
  setSelectedMembers,
}) => {
  const [membersList, setMembersList] = useState([]);

  useEffect(() => {
    authClient
      .get("/members")
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
