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

        mockMvc.perform(MockMvcRequestBuilders.get("/calculator/add/{a}/{b}", 2, 3))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("5"));
    }

    // 실패 케이스
    @Test
    @WithMockUser
    void addShouldNotReturnIncorrectSum() throws Exception {
        when(calculatorService.add(anyInt(), anyInt())).thenReturn(5);

        mockMvc.perform(MockMvcRequestBuilders.get("/calculator/add/{a}/{b}", 2, 3))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("6")); // 올바른 값인지 확인
    }
}
