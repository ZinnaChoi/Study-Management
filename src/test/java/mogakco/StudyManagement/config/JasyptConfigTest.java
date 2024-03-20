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
        String dbPassword = "dbPassword!";
        String secretKey = "secretKey";
        String adminId = "adminId";
        String adminPassword = "adminPassword";

        System.out.println("En_dbUrl : " + jasyptEncoding(dbUrl));
        System.out.println("En_dbUsername : " + jasyptEncoding(dbUsername));
        System.out.println("En_dbPassword : " + jasyptEncoding(dbPassword));
        System.out.println("En_secretKey : " + jasyptEncoding(secretKey));
        System.out.println("En_adminId : " + jasyptEncoding(adminId));
        System.out.println("En_adminPassword : " + jasyptEncoding(adminPassword));
    }

    @Test
    void stringDecryptor() {
        String dbUrl = "dbUrl";
        String dbUsername = "dbUsername";
        String dbPassword = "dbPassword!";
        String secretKey = "secretKey";
        String adminId = "adminId";
        String adminPassword = "adminPassword";

        System.out.println("DE_dbUrl : " + jasyptDecoding(dbUrl));
        System.out.println("DE_dbUsername : " + jasyptDecoding(dbUsername));
        System.out.println("DE_dbPassword : " + jasyptDecoding(dbPassword));
        System.out.println("DE_secretKey : " + jasyptDecoding(secretKey));
        System.out.println("DE_adminId : " + jasyptDecoding(adminId));
        System.out.println("DE_adminPassword : " + jasyptDecoding(adminPassword));
    }

    public String jasyptEncoding(String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(password);
        return pbeEnc.encrypt(value);
    }

    public String jasyptDecoding(String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(password);
        return pbeEnc.decrypt(value);
    }

}
