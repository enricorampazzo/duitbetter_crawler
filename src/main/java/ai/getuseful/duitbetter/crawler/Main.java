package ai.getuseful.duitbetter.crawler;

import ai.getuseful.duitbetter.cleaner.CleanDuWebPage;
import ai.getuseful.duitbetter.crawler.dto.WebPage;
import ai.getuseful.duitbetter.crawler.entities.WebPageNode;
import ai.getuseful.duitbetter.crawler.repository.WebPageNodeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Component
public class Main implements CommandLineRunner {

    @Autowired
    private WebPageNodeRepository webPageNodeRepository;

    @Override
    public void run(String... args) throws Exception {
        removeScriptAndStyleTags();
    }

    private void crawlWebsite() throws MalformedURLException {
        URL pageUrl = new URL("https://www.du.ae/personal/support");
        WebPage support = WebPage.buildPage(webPageNodeRepository, pageUrl, "DU support", true, true);
        webPageNodeRepository.savePage(support.url().toString(), support.title(), support.html(), support.links(),
                new CleanDuWebPage(pageUrl, support.html()).clean());
        List<WebPageNode> leafs = webPageNodeRepository.getPagesWithoutSource();
        while(!leafs.isEmpty()) {
            for (WebPageNode wpn : leafs) {
                URL linkedPageUrl = Utils.createURL(wpn.getUrl(), pageUrl.getProtocol(), pageUrl.getHost());
                WebPage linkedPage = WebPage.buildPage(webPageNodeRepository, linkedPageUrl, wpn.getTitle(), true, true);
                webPageNodeRepository.savePage(linkedPage.url().toString(), linkedPage.title(), linkedPage.html(), linkedPage.links(), new CleanDuWebPage(linkedPageUrl, linkedPage.html()).clean());
            }
           leafs = webPageNodeRepository.getPagesWithoutSource();
        }
    }
    private void removeScriptAndStyleTags(){
        List<WebPageNode> webPagesToClean = webPageNodeRepository.getPagesWithHtmlToClean();
        while(webPagesToClean.size() > 0) {
            for (WebPageNode wptc : webPagesToClean) {
                Document d = Jsoup.parse(wptc.getText());
                int textBeforeClean = wptc.getText().length();
                d.getElementsByTag("script").iterator().forEachRemaining(Node::remove);
                d.getElementsByTag("style").iterator().forEachRemaining(Node::remove);
                d.getElementsByTag("link").iterator().forEachRemaining(Node::remove);
                d.getElementsByTag("meta").iterator().forEachRemaining(Node::remove);
                d.getElementsByTag("iframe").iterator().forEachRemaining(Node::remove);
                int textAfterClean = d.html().length();
                System.out.format("Before clean html length: %d; after clean html length: %d; difference %d\n",
                        textBeforeClean, textAfterClean, textBeforeClean - textAfterClean);
                webPageNodeRepository.setCleanHtmlForPage(wptc.getUrl(), d.html());
            }
            webPagesToClean = webPageNodeRepository.getPagesWithHtmlToClean();
        }
    }

    private void retryToCleanText(){

    }
}
