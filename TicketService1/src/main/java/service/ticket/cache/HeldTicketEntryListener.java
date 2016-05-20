package service.ticket.cache;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryEvictedListener;

import ticket.data.dao.SeatHold;

@Component
public class HeldTicketEntryListener implements Serializable, EntryEvictedListener<Long, SeatHold>{

	@Autowired
	private CacheService cacheService;
	
	@Override
	public void entryEvicted(EntryEvent<Long, SeatHold> event) {
		SeatHold seatHold = event.getOldValue();
		for(HeldTicket heldTicket : seatHold.getTickets()){
			AvailableTicket ticket = new AvailableTicket();
    		ticket.setPrice(heldTicket.getPrice());
    		ticket.setRowNo(heldTicket.getRowNo());
    		ticket.setSeatNo(heldTicket.getSeatNo());
    		ticket.setTicketId(heldTicket.getTicketId());
    		cacheService.putNewAvailableTicket(heldTicket.getLevelId(), ticket);
		}
		
	}

}
