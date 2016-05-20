package service.ticket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.MultiMap;

import service.ticket.cache.AvailableTicket;
import service.ticket.cache.CacheService;
import service.ticket.cache.HeldTicket;
import service.ticket.data.Ticket;
import service.ticket.data.TicketRepository;
import service.ticket.data.VenueLevel;
import ticket.data.dao.SeatHold;

@Component
public class TicketServiceImpl implements TicketService {

	@Autowired
	private CacheService cacheService;

	@Autowired
	private TicketRepository ticketRepository;

	@SuppressWarnings("unchecked")
	@Override
	public List<AvailableTicket> numSeatsAvailable(Optional<Integer> venueLevel) {
		if (venueLevel.isPresent()) {
			return cacheService.findAllAvailableTicketsbyVenueLevel(venueLevel.get());
		} else {
			return cacheService.findAllAvailableTickets();
		}
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {
		SeatHold seatHold = new SeatHold(cacheService.getNextTicketId(), new ArrayList<HeldTicket>());

		if (minLevel.isPresent() && maxLevel.isPresent()) {
			for (VenueLevel venueLevel : cacheService.findAllVenueLevels()) {
				if (venueLevel.getLevelId() >= maxLevel.get() && venueLevel.getLevelId() <= minLevel.get()) {
					List<HeldTicket> tickets = attemptReserveTicektsAtLevel(numSeats, venueLevel.getLevelId(),
							customerEmail);
					seatHold.getTickets().addAll(tickets);
					numSeats = numSeats - tickets.size();
					if (numSeats == 0)
						break;
				}
			}
		} else if (maxLevel.isPresent() && !minLevel.isPresent()) {
			for (VenueLevel venueLevel : cacheService.findAllVenueLevels()) {
				if(venueLevel.getLevelId() < maxLevel.get())
					continue;
				List<HeldTicket> tickets = attemptReserveTicektsAtLevel(numSeats, venueLevel.getLevelId(),
						customerEmail);
				seatHold.getTickets().addAll(tickets);
				numSeats = numSeats - tickets.size();
				if (numSeats == 0)
					break;
			}
		} else if (!maxLevel.isPresent() && minLevel.isPresent()) {
			for (VenueLevel venueLevel : cacheService.findAllVenueLevels()) {
				if(venueLevel.getLevelId() > minLevel.get())
					break;
				List<HeldTicket> tickets = attemptReserveTicektsAtLevel(numSeats, venueLevel.getLevelId(),
						customerEmail);
				seatHold.getTickets().addAll(tickets);
				numSeats = numSeats - tickets.size();
				if (numSeats == 0)
					break;
			}
		}else {
			for (VenueLevel venueLevel : cacheService.findAllVenueLevels()) {
				List<HeldTicket> tickets = attemptReserveTicektsAtLevel(numSeats, venueLevel.getLevelId(),
						customerEmail);
				seatHold.getTickets().addAll(tickets);
				numSeats = numSeats - tickets.size();
				if (numSeats == 0)
					break;
			}
		}
		cacheService.putNewHeldTicket(seatHold.getSeatHoldId(), seatHold);
		return seatHold;
	}

	@Override
	public String reserveSeats(Long seatHoldId, String customerEmail) {
		Long confirmationId = cacheService.getNextReservationNumber();
		Optional<SeatHold> obj = cacheService.findAndLockHeldTicket(seatHoldId);
		if (obj.isPresent()) {
			try {

				SeatHold seatHold = obj.get();
				List<Ticket> tickets = new ArrayList<Ticket>();
				for (HeldTicket heldTicket : seatHold.getTickets()) {
					Ticket ticket = new Ticket();
					ticket.setCustomerEmail(heldTicket.getCustomerEmail());
					ticket.setLevelId(heldTicket.getLevelId());
					/*
					 * TODO : Populate Venue Name
					 */
					ticket.setLevelName("");
					ticket.setPrice(heldTicket.getPrice());
					ticket.setRow(heldTicket.getRowNo());
					ticket.setSeatNo(heldTicket.getSeatNo());
					ticket.setTicketId(heldTicket.getTicketId());
					ticket.setConfirmationId(confirmationId);
					tickets.add(ticket);
				}
				ticketRepository.save(tickets);
				return String.valueOf(confirmationId);
				
				/*
				 * TODO : Subtract the tickets from the VenueLevel and reconcile with the cache. Needed when reloading with existing data.
				 */

			} finally {
				cacheService.removeAndUnlockHeldTicket(seatHoldId);
			}
		}
		return "-1";
	}

	/*
	 * private List<HeldTicket> attemptReserveTicektsAtLevel(Integer numSeats,
	 * Integer venueLevel, String customerEmail) {
	 * 
	 * IMap<Integer, List<AvailableTicket>> avaialableTicketsMap =
	 * cacheService.getAvailableTicketsMap(); List<AvailableTicket>
	 * avaialableTickets = avaialableTicketsMap.get(venueLevel);
	 * List<HeldTicket> heldTickets = new ArrayList<HeldTicket>();
	 * avaialableTicketsMap.lock(venueLevel); try { int ticketCount =
	 * avaialableTickets.size() > numSeats ? numSeats :
	 * avaialableTickets.size();
	 * 
	 * for (int i = 1; i <= ticketCount; i++) { AvailableTicket ticket =
	 * avaialableTickets.get(0); HeldTicket heldTicket = new HeldTicket();
	 * heldTicket.setCustomerEmail(customerEmail);
	 * heldTicket.setPrice(ticket.getPrice());
	 * heldTicket.setRowNo(ticket.getRowNo());
	 * heldTicket.setSeatNo(ticket.getSeatNo());
	 * heldTicket.setTicketId(ticket.getTicketId());
	 * heldTicket.setLevelId(venueLevel); heldTickets.add(heldTicket);
	 * avaialableTickets.remove(ticket); }
	 * 
	 * avaialableTicketsMap.put(venueLevel, avaialableTickets); } finally {
	 * avaialableTicketsMap.unlock(venueLevel); }
	 * 
	 * return heldTickets;
	 * 
	 * }
	 */

	private List<HeldTicket> attemptReserveTicektsAtLevel(Integer numSeats, Integer venueLevel, String customerEmail) {

		MultiMap<Integer, AvailableTicket> avaialableTicketsMap = cacheService.getAvailableTicketsMap();
		Collection<AvailableTicket> avaialableTickets = avaialableTicketsMap.get(venueLevel);

		List<HeldTicket> heldTickets = new ArrayList<HeldTicket>();
		avaialableTicketsMap.lock(venueLevel);
		try {
			int ticketCount = avaialableTickets.size() > numSeats ? numSeats : avaialableTickets.size();
			Iterator<AvailableTicket> tickets = avaialableTickets.iterator();
			int counter = ticketCount;
			while (tickets.hasNext() && counter > 0) {
				AvailableTicket ticket = tickets.next();
				HeldTicket heldTicket = new HeldTicket();
				heldTicket.setCustomerEmail(customerEmail);
				heldTicket.setPrice(ticket.getPrice());
				heldTicket.setRowNo(ticket.getRowNo());
				heldTicket.setSeatNo(ticket.getSeatNo());
				heldTicket.setTicketId(ticket.getTicketId());
				heldTicket.setLevelId(venueLevel);
				heldTickets.add(heldTicket);
				avaialableTicketsMap.remove(venueLevel, ticket);
				counter--;
			}

		} finally {
			avaialableTicketsMap.unlock(venueLevel);
		}

		return heldTickets;

	}

	@Override
	public List<Ticket> viewReservation(String confirmationId) {
		return ticketRepository.findByConfirmationId(Long.parseLong(confirmationId));
	}

}
