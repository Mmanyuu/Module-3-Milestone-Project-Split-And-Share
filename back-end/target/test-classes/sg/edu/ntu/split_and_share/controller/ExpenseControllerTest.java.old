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

import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.repository.ExpenseRepository;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ExpenseRepository mockRepo;

	// Add code here
	@Test
	public void testGetCatalogueById() throws Exception {
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
	public void testInvalidExpenseId() throws Exception {
		// Create customer with invalid fields
		Expense invalidExpense = new Expense();

		// Convert object to JSON
		String invalidExpenseAsJson = objectMapper.writeValueAsString(invalidExpense);

		// Build the request
		RequestBuilder request = MockMvcRequestBuilders.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidExpenseAsJson);

		// Perform request
		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

	}
	
}
