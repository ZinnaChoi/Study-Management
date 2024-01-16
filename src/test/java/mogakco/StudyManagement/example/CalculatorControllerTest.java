package mogakco.StudyManagement.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import mogakco.StudyManagement.controller.CalculatorController;
import mogakco.StudyManagement.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class CalculatorControllerTest {

    @Mock
    private CalculatorService calculatorService;

    @InjectMocks
    private CalculatorController calculatorController;

    @Autowired
    private MockMvc mockMvc;

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

    // 성공 케이스
    @Test
    @WithMockUser // 보안 무시 , 설정안하면 401 에러
    @Order(1) // 메소드 실행순서 지정
    void addShouldReturnCorrectSum() throws Exception {
        when(calculatorService.add(anyInt(), anyInt())).thenReturn(5);
        // "/calculator/add/{a}/{b}" 엔드포인트로 GET 요청을 수행하고, 예상되는 결과를 검증합니다.(테스트하는 메소드 기입)
        mockMvc.perform(MockMvcRequestBuilders.get("/calculator/add/{a}/{b}", 2, 3))
                .andExpect(MockMvcResultMatchers.status().isOk()) // HTTP 상태가 OK(200) 여부 확인
                .andExpect(MockMvcResultMatchers.content().string("5")); // 응답 본문이 "5"인지 확인
    }

    // 실패 케이스
    @Test
    @WithMockUser
    @Order(2)
    void divideByZeroShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/calculator/divide/{a}/{b}", 10, 0))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Division by zero is not allowed"));
    }

}
