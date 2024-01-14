package mogakco.StudyManagement.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

    // 성공 케이스
    @Test
    @WithMockUser // 보안 무시 , 설정안하면 401 에러
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
    void divideByZeroShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/calculator/divide/{a}/{b}", 10, 0))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Division by zero is not allowed"));
    }

}
