package mogakco.StudyManagement.service.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.enums.MessageType;

@Service
public class SendEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.username}")
    private String fromEmail;

    public void sendEmail(String memberName, MessageType type, String toAddress) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toAddress);
        message.setSubject("안녕하세요. 모각코입니다");
        message.setText(createContent(memberName, type));

        try {
            javaMailSender.send(message);
            System.out.println("알림 이메일이 전송되었습니다.");
        } catch (Exception e) {
            System.out.println("알림 이메일 전송에 실패하였습니다. " + e.getMessage());
        }
    }

    public String createContent(String memberName, MessageType messageType) {
        switch (messageType) {
            case GENERAL:
                return String.format(
                        "안녕하세요 %s님! \n \n 10분 뒤 시작할 스터디를 위해 구글 미트 화상 회의를 생성해 멤버들에게 공유해주세요. \n https://meet.google.com/iai-womn-bcb?ec=asw-meet-hero-startmeeting \n \n 참가자가 2인 이상인 경우, 한 시간 뒤 회의가 종료되오니 새 회의를 만들어주세요.",
                        memberName);
            case NEW_POST:
                return String.format("%s님의 새 글이 등록되었습니다.", memberName);
            case ABSENT:
                return String.format("%s님의 부재 일정이 등록되었습니다.", memberName);
            case WAKE_UP:
                return String.format("%s님의 기상 성공 여부가 등록되었습니다.", memberName);
            default:
                return "";
        }
    }
}
