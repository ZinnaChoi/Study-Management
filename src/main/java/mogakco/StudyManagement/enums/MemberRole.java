package mogakco.StudyManagement.enums;

public enum MemberRole {
  ADMIN,
  USER;

  public static MemberRole fromString(String role) {
    for (MemberRole memberRole : MemberRole.values()) {
      if (memberRole.name().equalsIgnoreCase(role)) {
        return memberRole;
      }
    }
    throw new IllegalArgumentException("No constant with role " + role + " found");
  }
}
