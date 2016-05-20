package service.ticket.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.MultiMap;

import service.ticket.ServiceConstants;
import service.ticket.data.VenueLevel;
import ticket.data.dao.SeatHold;

/**
 * Service class used for interaction with Hazelcast, to facilitate adding and removing data from the cache.
 * @author tsodhi
 *
 */
@Component
public class CacheService {

	@Autowired
	private HazelcastInstance cache;
	
	@Value("${ticket.holdTime}")
	private Integer holdTime;
	
	public List<AvailableTicket> findAllAvailableTickets(){
		List<AvailableTicket> tickets = new ArrayList<AvailableTicket>();
		IList<VenueLevel> levels = cache.getList(ServiceConstants.VENUE_LEVELS);
		for(VenueLevel level : levels){
			tickets.addAll(findAllAvailableTicketsbyVenueLevel(level.getLevelId()));
		}
		return tickets;
	}
	
	/*public IMap<Integer,List<AvailableTicket>> getAvailableTicketsMap(){
		return cache.getMap(ServiceConstants.AVAILABLE_TICKETS);
	}*/
	
	public MultiMap<Integer,AvailableTicket> getAvailableTicketsMap(){
		return cache.getMultiMap(ServiceConstants.AVAILABLE_TICKETS);
	}
	
	public List<AvailableTicket> findAllAvailableTicketsbyVenueLevel(Integer levelId){
		List<AvailableTicket> tickets = new ArrayList<AvailableTicket>();
		/*Object obj = cache.getMap(ServiceConstants.AVAILABLE_TICKETS).get(levelId);
		if(obj != null){
			tickets.addAll((List<AvailableTicket>)obj);
		}*/
		
		MultiMap<Integer,AvailableTicket> ticketsMap = cache.getMultiMap(ServiceConstants.AVAILABLE_TICKETS);
		tickets.addAll(ticketsMap.get(levelId));
		return tickets;
	}
	
	public List<VenueLevel> findAllVenueLevels(){
		IList<VenueLevel> levels = cache.getList(ServiceConstants.VENUE_LEVELS);
		return levels;
	}
	
	public void putNewHeldTicket(Long seatHoldId, SeatHold seatHold){
		cache.getMap(ServiceConstants.HELD_TICKETS).put(seatHoldId, seatHold, holdTime, TimeUnit.SECONDS);
	}
	
	public Optional<SeatHold> findAndLockHeldTicket(Long seatHoldId){
		IMap<Long, SeatHold> heldTickets =  cache.getMap(ServiceConstants.HELD_TICKETS);
		SeatHold seatHold = heldTickets.get(seatHoldId);
		if(seatHold != null){
			heldTickets.lock(seatHoldId);
		}
		return Optional.ofNullable(heldTickets.get(seatHoldId));
	}
	
	public void removeAndUnlockHeldTicket(Long seatHoldId){
		IMap<Long, SeatHold> heldTickets =  cache.getMap(ServiceConstants.HELD_TICKETS);
		heldTickets.remove(seatHoldId);
		heldTickets.unlock(seatHoldId);
		
	}
	
	public void putNewAvailableTicket(Integer levelId, AvailableTicket ticket){
		/*IMap<Integer,List<AvailableTicket>> availableTickets = cache.getMap(ServiceConstants.AVAILABLE_TICKETS);
		List<AvailableTicket> tickets = availableTickets.get(levelId);
		tickets.add(ticket);
		availableTickets.put(levelId, tickets);*/
		MultiMap<Integer,AvailableTicket> ticketsMap = cache.getMultiMap(ServiceConstants.AVAILABLE_TICKETS);
		ticketsMap.put(levelId, ticket);
	}
	
	public long getNextTicketId(){
		return cache.getIdGenerator(ServiceConstants.HELD_TICKETS).newId();
	}
	
	public long getNextReservationNumber(){
		return cache.getIdGenerator(ServiceConstants.RESERVATION).newId();
	}
}
