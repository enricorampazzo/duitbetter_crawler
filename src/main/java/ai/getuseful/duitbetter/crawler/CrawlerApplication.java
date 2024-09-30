package ai.getuseful.duitbetter.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CrawlerApplication {

	public static void main(String[] args) {
//		SpringApplication.run(CrawlerApplication.class, args);
		new SpringApplicationBuilder(CrawlerApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

}
