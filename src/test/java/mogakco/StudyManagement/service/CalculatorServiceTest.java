package mogakco.StudyManagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalculatorServiceTest {

    @Autowired
    private CalculatorService calculatorService;

    // 성공케이스
    @Test
    // @Order(1) // 메소드 실행순서 지정
    void addShouldReturnCorrectSum() {
        // Arrange, Act, Assert
        assertEquals(5, calculatorService.add(2, 3), "예상결과값과 일치");
    }

    // 실패케이스
    @Test
    // @Order(2)
    void addShouldNotReturnIncorrectSum() {
        // Arrange, Act, Assert
        assertEquals(6, calculatorService.add(2, 3), "예상결과값과 실행 결과값이 다름");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("BeforeAll: 테스트 실행 전 한 번만 실행");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("BeforeEach: 각 테스트 메소드 실행 전 실행");
    }

    @AfterEach
    void afterEach() {
        System.out.println("AfterEach: 각 테스트 메소드 실행 후 실행");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("AfterAll: 모든 테스트 실행 후 한 번만 실행");
    }
}