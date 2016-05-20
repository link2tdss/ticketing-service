package service.ticket.cache;

import java.io.Serializable;

public class AvailableTicket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3281844888671286511L;
	private String ticketId;
	private String price;
	private String seatNo;
	private String rowNo;
	private int levelId; 

	/**
	 * @return the ticketId
	 */
	public String getTicketId() {
		return ticketId;
	}

	/**
	 * @param ticketId
	 *            the ticketId to set
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the seatNo
	 */
	public String getSeatNo() {
		return seatNo;
	}

	/**
	 * @param seatNo
	 *            the seatNo to set
	 */
	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}

	/**
	 * @return the rowNo
	 */
	public String getRowNo() {
		return rowNo;
	}

	/**
	 * @param rowNo
	 *            the rowNo to set
	 */
	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}

	/**
	 * @return the levelId
	 */
	public int getLevelId() {
		return levelId;
	}

	/**
	 * @param levelId the levelId to set
	 */
	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	
}
