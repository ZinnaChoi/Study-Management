package mogakco.StudyManagement.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class TestUtil {

    public static void performPostRequest(MockMvc mockMvc, String url, String requestBodyJson, int expectedStatus,
            Integer expectedRetCode)
            throws Exception {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson))
                .andExpect(status().is(expectedStatus))
                .andExpect(expectedRetCode != null ? jsonPath("$.retCode").value(expectedRetCode) : null);
    }

}