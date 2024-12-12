package sg.edu.ntu.split_and_share.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
// import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;

@WebMvcTest(DashboardController.class)
public class DashboardController {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private DashboardRepository mockRepo;

	// Add code here
	@Test
	public void testGetDashboardById() throws Exception {
		// Step 1: Build a GET request to /customers/1
		RequestBuilder request = MockMvcRequestBuilders.get("/catalogues/1");

		// Step 2: Perform the request, get response and assert
		mockMvc.perform(request)
				// Assert that the status code is 200 OK
				.andExpect(status().isOk())
				// Assert that the content type is JSON
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				// Assert that the id returned is 1
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void testInvalidDashboardId() throws Exception {
		// Create customer with invalid fields
		Dashboard invalidUser = new Dashboard();

		// Convert object to JSON
		String invalidUserAsJson = objectMapper.writeValueAsString(invalidUser);

		// Build the request
		RequestBuilder request = MockMvcRequestBuilders.post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidUserAsJson);

		// Perform request
		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

	}
	
}
