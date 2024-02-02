package mogakco.StudyManagement.service.external;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@RequiredArgsConstructor
@Component
public class EmailService {

    public void sendEamil(String memberName, String type, String toAddress) {

        try {

            final String username = "wowdayeon@gmail.com";
            final String password = "itmytkxjlgxfltgu";

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

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String createContent(String memberName, String messageType) {
        switch (messageType) {
            case "general":
                return String.format(
                        "안녕하세요 %s님! \n10분 뒤 시작할 스터디를 위해 구글 미트를 생성해주세요. \n https://meet.google.com/iai-womn-bcb?ec=asw-meet-hero-startmeeting",
                        memberName);
            case "newpost":
                return String.format("%s님의 새 글이 등록되었습니다.", memberName);
            case "absent":
                return String.format("%s님의 부재 일정이 등록되었습니다.", memberName);
            case "wakeup":
                return String.format("%s님의 기상 성공 여부가 등록되었습니다.", memberName);
            default:
                return "";
        }
    }

}
