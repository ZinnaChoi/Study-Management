const MemberCheckbox = ({
  membersList,
  selectedMembers,
  setSelectedMembers,
}) => {
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
