package service.ticket.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, String>{
	
	List<Ticket> findByConfirmationId(Long confirmationId);

}
