package service.ticket;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=TicketServiceApplication.class)
@WebIntegrationTest
public abstract class TicketServiceApplicationTests {

	protected RestTemplate restTemplate = new TestRestTemplate();

	protected MockMvc mockMvc;
	
	protected String base_url = "http://localhost:8080/tickets";
	
	@Autowired
    private WebApplicationContext wac;
	
	@org.junit.Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
	

}
