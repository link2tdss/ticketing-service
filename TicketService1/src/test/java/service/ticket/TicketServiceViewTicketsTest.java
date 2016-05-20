package service.ticket;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.net.URI;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;


public class TicketServiceViewTicketsTest extends TicketService1ApplicationTests{


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
		mockMvc.perform(get(base_url + "/reserve/12?customerEmail=test@yahoo.com")).andExpect(jsonPath("$.tickets", hasSize(12)));

	}
	
	/**
	 * Test that a ticket can be requested specifying only minimum level.
	 * @throws Exception
	 */
	@Test
	public void testRequestTicketsForHoldWithMin() throws Exception {
		mockMvc.perform(
				get(base_url + "/reserve/2?minLevel=2&customerEmail=test@yahoo.com"))
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
				get(base_url + "/reserve/12?maxLevel=3&customerEmail=test@yahoo.com"))
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
				get(base_url + "/reserve/12?maxLevel=3&minLevel=4&customerEmail=test@yahoo.com"))
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
				get(base_url + "/reserve/2?minLevel=2&customerEmail=test@yahoo.com"))
					.andExpect(jsonPath("$.tickets", hasSize(2)))
					.andExpect(jsonPath("$.tickets[*].levelId",everyItem(lessThanOrEqualTo(2))))
					.andReturn().getResponse().getContentAsString();
		Long seatHoldID = JsonPath.parse(response).read("$.seatHoldId", Long.class);
		assertTrue(seatHoldID != null &&  seatHoldID > -1);
		
		String confirmationNo = mockMvc.perform(
				get(base_url + "/confirm/" + seatHoldID +"?customerEmail=test@yahoo.com"))
					.andReturn().getResponse().getContentAsString();
		assertTrue(confirmationNo != null && !confirmationNo.isEmpty());

		System.out.println("Confirmation NO is " + confirmationNo);
		mockMvc.perform(
				get(base_url + "/view/" + confirmationNo))
					.andExpect(jsonPath("$.[*]",hasSize(2)));
	}
	
}
