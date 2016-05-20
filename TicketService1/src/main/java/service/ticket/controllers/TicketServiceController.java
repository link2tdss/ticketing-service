package service.ticket.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.IMap;

import service.ticket.ServiceConstants;
import service.ticket.TicketServiceImpl;
import service.ticket.cache.AvailableTicket;
import service.ticket.cache.HeldTicket;
import service.ticket.data.Ticket;
import ticket.data.dao.SeatHold;

@RestController
@RequestMapping(value = "/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketServiceController{
	
	@Autowired
	private TicketServiceImpl ticketService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/available")
	public List<AvailableTicket> numSeatsAvailable(@RequestParam(name="venueLevel", required=false) Integer venueLevel) {
		Optional<Integer> level = Optional.ofNullable(venueLevel);
		return ticketService.numSeatsAvailable(level);
	}

	@RequestMapping(value = "/reserve/{numSeats}")
	public SeatHold findAndHoldSeats(@PathVariable Integer numSeats, 
			@RequestParam(name="minLevel", required=false) Integer minLevel, @RequestParam(name="maxLevel", required=false) Integer maxLevel,
			@RequestParam(name="customerEmail", required=true) String customerEmail) {
		
		Optional<Integer> min = Optional.ofNullable(minLevel);
		Optional<Integer> max = Optional.ofNullable(maxLevel);
		return ticketService.findAndHoldSeats(numSeats, min, max, customerEmail);
	}
	

	@Transactional
	@RequestMapping(value = "/confirm/{seatHoldId}")
	public String reserveSeats(@PathVariable Long seatHoldId,
			@RequestParam(name="customerEmail", required=true) String customerEmail) {
		// TODO Auto-generated method stub
		return ticketService.reserveSeats(seatHoldId, customerEmail);
	}
	
	@RequestMapping(value = "/view/{confirmationId}")
	public List<Ticket> viewSeats(@PathVariable String confirmationId) {
		// TODO Auto-generated method stub
		return ticketService.viewReservation(confirmationId);
	}
	

	
}
