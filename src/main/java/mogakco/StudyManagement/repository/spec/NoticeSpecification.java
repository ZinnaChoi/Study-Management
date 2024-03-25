package mogakco.StudyManagement.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import mogakco.StudyManagement.domain.Notice;
import mogakco.StudyManagement.enums.MessageType;

public class NoticeSpecification {

    public static Specification<Notice> matchMessageType(MessageType messageType) {
        return (root, query, cb) -> {
            switch (messageType) {
                case ABSENT:
                    return cb.isTrue(root.get("absent"));
                case NEW_POST:
                    return cb.isTrue(root.get("newPost"));
                case WAKE_UP:
                    return cb.isTrue(root.get("wakeup"));
                default:
                    throw new IllegalArgumentException("Unsupported MessageType: " + messageType);
            }
        };
    }

}
