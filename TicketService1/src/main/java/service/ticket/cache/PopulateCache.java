package service.ticket.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

import service.ticket.ServiceConstants;
import service.ticket.data.VenueLevel;
import service.ticket.data.VenueLevelRepository;

/**
 * Application startup listener, that ties into Spring application startup and populates the Hazelcast cache with data from the DB.
 * @author tsodhi
 *
 */
@Component
public class PopulateCache implements ApplicationListener<ContextRefreshedEvent>{
 
	@Autowired
	private HazelcastInstance cache;
 
	@Autowired
	private HeldTicketEntryListener heldTicketListener;
	
	@Autowired
	private VenueLevelRepository venueRepository;
   
 
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("Loading cache");
        List<VenueLevel> venueLevels = getVenueLevels();
        cache.getList(ServiceConstants.VENUE_LEVELS).addAll(venueLevels);
        //IMap<Integer,List<AvailableTicket>> venueLevelTickets =  cache.getMap(ServiceConstants.AVAILABLE_TICKETS);
        MultiMap<Integer, AvailableTicket> venueLevelTickets1 = cache.getMultiMap(ServiceConstants.AVAILABLE_TICKETS);
        
        for(VenueLevel venueLevel : venueRepository.findAll()){
        	List<AvailableTicket> tickets = constructTickets(venueLevel);
        	//venueLevelTickets.put(venueLevel.getLevelId(), tickets);
        	for(AvailableTicket ticket : tickets){
        		venueLevelTickets1.put(venueLevel.getLevelId(), ticket);
        	}
        	
        }
        
        cache.getMap(ServiceConstants.HELD_TICKETS).addEntryListener(heldTicketListener, true);
        
        cache.getIdGenerator(ServiceConstants.HELD_TICKETS).init(1);
        cache.getIdGenerator(ServiceConstants.RESERVATION).init(1);
        
        
    }
    
    private List<VenueLevel> getVenueLevels(){
    	List<VenueLevel> levels  = new ArrayList<VenueLevel>();
    	for(VenueLevel level : venueRepository.findAll()){
    		levels.add(level);
    	}
    	return levels;
    }
    
  


    private List<AvailableTicket> constructTickets(VenueLevel venueLevel){
    	List<AvailableTicket> tickets = new ArrayList<AvailableTicket>();
    	for (int i = 1 ; i <= venueLevel.getLevelRow(); i++ ){
    		for(int j = 1; j <= venueLevel.getLevelSeatsPerRow(); j++){
    			AvailableTicket ticket = new AvailableTicket();
        		ticket.setPrice(venueLevel.getLevelPrice());
        		ticket.setRowNo(i+"");
        		ticket.setSeatNo(j+"");
        		ticket.setTicketId(venueLevel.getLevelId() + "" + i + "" + j);
        		ticket.setLevelId(venueLevel.getLevelId());
        		tickets.add(ticket);
    		}
    	}
    	return tickets;
    }

}
