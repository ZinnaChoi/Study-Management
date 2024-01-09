package mogakco.StudyManagement;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.BDDAssertions.then;

//ParameterizedTest template
public class JunitTest {

    @ParameterizedTest
    @ValueSource(strings = { "1", "2" })
    void ValueSource_1(String value) {
        System.out.println(value);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false, false })
    void ValueSource_2(boolean value) {
        System.out.println(value);
    }

    @ParameterizedTest
    @EnumSource(Quarter.class)
    void enumSource_1(Quarter quarter) {
        then(quarter.getValue()).isIn(1, 2, 3, 4);
    }

    enum Quarter {
        Q1(1, "1분기"),
        Q2(2, "2분기"),
        Q3(3, "3분기"),
        Q4(4, "4분기");

        private final int value;
        private final String description;

        Quarter(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }
}