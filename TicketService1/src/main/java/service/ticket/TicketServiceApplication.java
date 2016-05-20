package service.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.hazelcast.config.Config;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.MultiMapConfig.ValueCollectionType;

/**
 * Bootstrap file to load the Spring application context and configure the application.
 * @author tsodhi
 *
 */
@SpringBootApplication
@EnableCaching
@EnableJpaRepositories
public class TicketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketServiceApplication.class, args);
	}

	/**
	 * Initializes and configures Hazelcast for subsequent use.
	 * @return
	 */
	@Bean
	public Config configureCache() {
		Config config = new Config();
		// config.addMapConfig(new
		// MapConfig().setName(ServiceConstants.AVAILABLE_TICKETS));
		config.addMultiMapConfig(new MultiMapConfig().setName(ServiceConstants.AVAILABLE_TICKETS).setAsyncBackupCount(1)
				.setValueCollectionType(ValueCollectionType.LIST));

		config.addMapConfig(new MapConfig().setName(ServiceConstants.HELD_TICKETS));

		config.addListConfig(new ListConfig().setName(ServiceConstants.VENUE_LEVELS));

		return config;
	}

}
