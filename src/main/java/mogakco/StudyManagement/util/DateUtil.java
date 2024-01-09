package mogakco.StudyManagement.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {
    private static final String KOREA_TIMEZONE = "Asia/Seoul";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(KOREA_TIMEZONE));
        DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone(KOREA_TIMEZONE));
    }

    public static String getCurrentDate() {
        Date date = new Date();
        return DATE_FORMAT.format(date);
    }

    public static String getCurrentDateTime() {
        Date date = new Date();
        return DATE_TIME_FORMAT.format(date);
    }

}
