package ticket.data.dao;

import java.io.Serializable;
import java.util.List;

import service.ticket.cache.HeldTicket;

public class SeatHold implements Serializable {

	private long seatHoldId;
	private List<HeldTicket> tickets;
	
	

	public SeatHold(long seatHoldId, List<HeldTicket> tickets) {
		super();
		this.seatHoldId = seatHoldId;
		this.tickets = tickets;
	}
	/**
	 * @return the seatHoldId
	 */
	public long getSeatHoldId() {
		return seatHoldId;
	}
	/**
	 * @param seatHoldId the seatHoldId to set
	 */
	public void setSeatHoldId(long seatHoldId) {
		this.seatHoldId = seatHoldId;
	}
	/**
	 * @return the tickets
	 */
	public List<HeldTicket> getTickets() {
		return tickets;
	}
	/**
	 * @param tickets the tickets to set
	 */
	public void setTickets(List<HeldTicket> tickets) {
		this.tickets = tickets;
	}
	
	
}
