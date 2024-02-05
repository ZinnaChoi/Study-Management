const MemberCheckbox = ({ membersList, selectedMembers, onCheckboxChange }) => {
  return membersList.map((memberName) => (
    <div key={memberName}>
      <label>
        <input
          type="checkbox"
          checked={selectedMembers.includes(memberName)}
          onChange={() => onCheckboxChange(memberName)}
        />
        {memberName}
      </label>
    </div>
  ));
};
export default MemberCheckbox;
