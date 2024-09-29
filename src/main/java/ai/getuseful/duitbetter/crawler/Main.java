package ai.getuseful.duitbetter.crawler;

import ai.getuseful.duitbetter.crawler.dto.WebPage;
import ai.getuseful.duitbetter.crawler.repository.WebPageNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

public class Main implements CommandLineRunner {

    @Autowired
    private WebPageNodeRepository webPageNodeRepository;

    @Override
    public void run(String... args) throws Exception {
        String pageUrl = "https://www.du.ae/personal/support";
        WebPage support = WebPage.buildPage(pageUrl, "DU support");
        webPageNodeRepository.savePage(support.url(), support.title(), support.text(), null);
    }
}
