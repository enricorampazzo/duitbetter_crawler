package ai.getuseful.duitbetter.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootTest
class CrawlerApplicationTests {

	@Test
	void contextLoads() throws MalformedURLException {
		URL url = new URL("https://du.ae/support");
		System.out.println(url.getHost());
	}

}
