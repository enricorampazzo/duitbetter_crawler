package ai.getuseful.duitbetter.crawler;

import ai.getuseful.duitbetter.crawler.dto.WebPage;
import ai.getuseful.duitbetter.crawler.entities.WebPageNode;
import ai.getuseful.duitbetter.crawler.repository.WebPageNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Component
public class Main implements CommandLineRunner {

    @Autowired
    private WebPageNodeRepository webPageNodeRepository;

    @Override
    public void run(String... args) throws Exception {
        URL pageUrl = new URL("https://www.du.ae/personal/support");
        WebPage support = WebPage.buildPage(webPageNodeRepository, pageUrl, "DU support", true);
        webPageNodeRepository.savePage(support.url().toString(), support.title(), support.text(), support.links());
        List<WebPageNode> leafs = webPageNodeRepository.getPagesWithoutSource();
        while(!leafs.isEmpty()) {
            for (WebPageNode wpn : leafs) {
                URL linkedPageUrl = Utils.createURL(wpn.getUrl(), pageUrl.getProtocol(), pageUrl.getHost());
                WebPage linkedPage = WebPage.buildPage(webPageNodeRepository, linkedPageUrl, wpn.getTitle(), true);
                webPageNodeRepository.savePage(linkedPage.url().toString(), linkedPage.title(), linkedPage.text(), linkedPage.links());
            }
            leafs = webPageNodeRepository.getPagesWithoutSource();
        }
    }
}
