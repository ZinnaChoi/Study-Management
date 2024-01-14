package mogakco.StudyManagement.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

@Component
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

    public static MvcResult performGetRequest(MockMvc mockMvc, String url, String queryString, int expectedStatus)
            throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);

        if (queryString != null && !queryString.isEmpty()) {
            uriBuilder.queryParam("query", queryString);
        }

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(uriBuilder.toUriString())
                .contentType(MediaType.APPLICATION_JSON));

        if (expectedStatus == 200) {
            resultActions.andExpect(status().isOk());
        } else {
            resultActions.andExpect(status().is(expectedStatus));
        }

        return resultActions.andReturn();
    }

}