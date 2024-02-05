package mogakco.StudyManagement.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import mogakco.StudyManagement.enums.MessageType;

@RequiredArgsConstructor
@Service

public class SendEmailService {

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    public void sendEmail(String memberName, MessageType type, String toAddress) {

        try {

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            Session session = Session.getInstance(props, authenticator);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject("안녕하세요. 모각코입니다");
            message.setText(createContent(memberName, type));

            Transport.send(message);

            System.out.println("알림 이메일이 전송되었습니다.");

        } catch (MessagingException e) {
            System.out.println("알림 이메일 전송에 실패하였습니다." + e.getMessage());
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
