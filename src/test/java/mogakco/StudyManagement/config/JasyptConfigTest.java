package mogakco.StudyManagement.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("암복호화 테스트.")
@SpringBootTest
public class JasyptConfigTest {

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Test
    void stringEncryptor() {
        String dbUrl = "dbUrl";
        String dbUsername = "dbUsername";
        String dbPassword = "dbPassword";
        String secretKey = "secretKey";
        String adminId = "adminId";
        String adminPassword = "adminPassword";
        String mailUsername = "mailUsername";
        String mailPassword = "mailPassword";

        System.out.println("En_dbUrl : " + jasyptEncoding(dbUrl));
        System.out.println("En_dbUsername : " + jasyptEncoding(dbUsername));
        System.out.println("En_dbPassword : " + jasyptEncoding(dbPassword));
        System.out.println("En_secretKey : " + jasyptEncoding(secretKey));
        System.out.println("En_adminId : " + jasyptEncoding(adminId));
        System.out.println("En_adminPassword : " + jasyptEncoding(adminPassword));
        System.out.println("En_mailUsername : " + jasyptEncoding(mailUsername));
        System.out.println("En_mailPassword : " + jasyptEncoding(mailPassword));
    }

    @Test
    void stringDecryptor() {
        String dbUrl = "gQS6Big/3WlqTjFY4FGrxg=="; // dbUrl
        String dbUsername = "gMs64oreSLpvxiF2Oebv0DmS7+1vhfTQ"; // dbUsername
        String dbPassword = "lGmWk51pN3awpHLazWK68jihvfKxV8Ro"; // dbPassword
        String secretKey = "fQilxnWt3Un46+b/Ir92UlSCrc4G8JSu"; // secretKey
        String adminId = "nyhunuGSFeKvK5hKK8D0dQ=="; // adminId
        String adminPassword = "UJXZ1c5ujFKk1BqMBoL1K65zjrez1zKh"; // adminPassword
        String mailUsername = "QUHo96L7Ot0dlkh9XMHIZ/+OkPW3YTTd"; // mailUsername
        String mailPassword = "oguHT+kCzg39p6FjITfrRTyStrh2J8lK"; // mailPassword

        System.out.println("DE_dbUrl : " + jasyptDecoding(dbUrl));
        System.out.println("DE_dbUsername : " + jasyptDecoding(dbUsername));
        System.out.println("DE_dbPassword : " + jasyptDecoding(dbPassword));
        System.out.println("DE_secretKey : " + jasyptDecoding(secretKey));
        System.out.println("DE_adminId : " + jasyptDecoding(adminId));
        System.out.println("DE_adminPassword : " + jasyptDecoding(adminPassword));
        System.out.println("DE_mailUsername : " + jasyptDecoding(mailUsername));
        System.out.println("DE_mailPassword : " + jasyptDecoding(mailPassword));

    }

    private String jasyptEncoding(String value) {
        return jasyptProcessor(true, value);
    }

    private String jasyptDecoding(String value) {
        return jasyptProcessor(false, value);
    }

    private String jasyptProcessor(boolean encrypt, String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(password);
        return encrypt ? pbeEnc.encrypt(value) : pbeEnc.decrypt(value);
    }
}
