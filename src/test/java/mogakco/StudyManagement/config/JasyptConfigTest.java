package mogakco.StudyManagement.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("암복호화 테스트")
public class JasyptConfigTest {

    public String key = "code123lovers!#@";

    @Test
    void stringEncryptor() {
        String secretKey = "NwkqmAFTauzXEOiJLxcMRHSbYDthgWCsPre";
        String id = "admin";
        String password = "123password!@#";

        System.out.println("En_secretKey : " + jasyptEncoding(secretKey));
        System.out.println("En_id: " + jasyptEncoding(id));
        System.out.println("En_password : " + jasyptEncoding(password));
    }

    @Test
    void stringDecryptor() {
        String secretKey = "ImYd1fl8W/X7WGcMw32y33gu2PQ+94lLkBlmD36ks6I5zeZnq93mGTR8fsEDN0oj";
        String id = "RYifAkG+MdVjdbTZY1AQDw==";
        String password = "QFyCIGUGCTk+kMxIE0Mu7kDky/HlwhEC";

        System.out.println("DE_secretKey : " + jasyptDecoding(secretKey));
        System.out.println("DE_id : " + jasyptDecoding(id));
        System.out.println("DE_password : " + jasyptDecoding(password));
    }

    public String jasyptEncoding(String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }

    public String jasyptDecoding(String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.decrypt(value);
    }

}
