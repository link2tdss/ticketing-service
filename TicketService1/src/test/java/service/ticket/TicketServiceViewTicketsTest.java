package service.ticket;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Test;

import com.jayway.jsonpath.JsonPath;


public class TicketServiceViewTicketsTest extends TicketServiceApplicationTests{


	/**
	 * Test that tickets can be checked by VenueLevel
	 * @throws Exception
	 */
	@Test
	public void testRequestAvaialableForVenueLevel2() throws Exception {
	
		mockMvc.perform(
				get(base_url + "/available?venueLevel=2"))
					.andExpect(jsonPath("$.[*].levelId",everyItem(equalTo(2))));
	}
	

	/**
	 * Test that a ticket can be requested without specifying minimum or maximum levels
	 * @throws Exception
	 */
	@Test
	public void testRequestTicketsForHoldWithNoMinMax() throws Exception {
		mockMvc.perform(post(base_url + "/reserve/12?customerEmail=test@yahoo.com")).andExpect(jsonPath("$.tickets", hasSize(12)));

	}
	
	/**
	 * Test that a ticket can be requested specifying only minimum level.
	 * @throws Exception
	 */
	@Test
	public void testRequestTicketsForHoldWithMin() throws Exception {
		mockMvc.perform(
				post(base_url + "/reserve/2?minLevel=2&customerEmail=test@yahoo.com"))
					.andExpect(jsonPath("$.tickets", hasSize(2)))
					.andExpect(jsonPath("$.tickets[*].levelId",everyItem(lessThanOrEqualTo(2))));
			
	}
	
	/**
	 * Test that a ticket can be requested specifying only maximum level.
	 * @throws Exception
	 */
	@Test
	public void testRequestTicketsForHoldWithMax() throws Exception {
		mockMvc.perform(
				post(base_url + "/reserve/12?maxLevel=3&customerEmail=test@yahoo.com"))
					.andExpect(jsonPath("$.tickets", hasSize(12)))
					.andExpect(jsonPath("$.tickets[*].levelId",everyItem(greaterThanOrEqualTo(3))));
			
	}
	
	/**
	 * Test that a ticket can be requested specifying both minimum and maximum level.
	 * @throws Exception
	 */
	@Test
	public void testRequestTicketsForHoldWithMinAndMax() throws Exception {
		mockMvc.perform(
				post(base_url + "/reserve/12?maxLevel=3&minLevel=4&customerEmail=test@yahoo.com"))
					.andExpect(jsonPath("$.tickets", hasSize(12)))
					.andExpect(jsonPath("$.tickets[*].levelId",everyItem(greaterThanOrEqualTo(3))));
			
	}
	
	/**
	 * Test that a ticket can be requested, and reserved. 
	 * Test that a confirmed ticket can be viewed via a confirmation number.
	 * @throws Exception
	 */
	@Test
	public void testRequestTicketsForHoldAndReserve() throws Exception{
		String response = mockMvc.perform(
				post(base_url + "/reserve/2?minLevel=2&customerEmail=test@yahoo.com"))
					.andExpect(jsonPath("$.tickets", hasSize(2)))
					.andExpect(jsonPath("$.tickets[*].levelId",everyItem(lessThanOrEqualTo(2))))
					.andReturn().getResponse().getContentAsString();
		Long seatHoldID = JsonPath.parse(response).read("$.seatHoldId", Long.class);
		assertTrue(seatHoldID != null &&  seatHoldID > -1);
		
		String confirmationNo = mockMvc.perform(
				post(base_url + "/confirm/" + seatHoldID +"?customerEmail=test@yahoo.com"))
					.andReturn().getResponse().getContentAsString();
		assertTrue(confirmationNo != null && !confirmationNo.isEmpty());
		mockMvc.perform(
				get(base_url + "/view/" + confirmationNo))
					.andExpect(jsonPath("$.[*]",hasSize(2)));
	}
	
}
