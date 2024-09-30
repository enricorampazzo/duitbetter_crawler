package ai.getuseful.duitbetter.crawler.dto;


import ai.getuseful.duitbetter.crawler.Utils;
import ai.getuseful.duitbetter.crawler.repository.WebPageNodeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record WebPage(URL url, String title, String text, Map<String, String> links){

    public static WebPage buildPage(WebPageNodeRepository repository, URL url, String title, boolean keepOnlyInternalLinks){
        List<Integer> pageLoadTimeouts = List.of(8);
        String source = null;
        ChromeDriver driver = null;
        for (int plt: pageLoadTimeouts)
            try {
                ChromeOptions co = new ChromeOptions();
                co.setPageLoadTimeout(Duration.of(plt, ChronoUnit.SECONDS));
                co.setImplicitWaitTimeout(Duration.of(plt, ChronoUnit.SECONDS));
                driver = new ChromeDriver(co);
                driver.get(url.toString());
                source = driver.getPageSource();
                driver.close();
                break;
            } catch (TimeoutException ex) {
                source = driver.getPageSource();
                if(source != null){
                    driver.close();
                    break;
                }
            }
        Document d = Jsoup.parse(source);
        List<Element> links = d.getElementsByTag("a").stream().filter(e -> {
            Attribute href = e.attribute("href");
            return href != null && !href.getValue().isEmpty() && !href.getValue().contains("#") && !href.getValue().toLowerCase().startsWith("javascript");
        }).toList();
        Map<String, String> linksMap = new HashMap<>();
        for (Element link : links) {
            URL key = Utils.createURL(link.attribute("href").getValue(), url.getProtocol(), url.getHost());
            if (key != null) {
                if (repository.getByUrl(key.toString()) != null) {
                    continue;
                }
                String label = link.ownText();
                if (label.isBlank()) {
                    label = link.text();
                }
                if (key.getHost().equals(url.getHost()) || !keepOnlyInternalLinks)
                    linksMap.put(key.toString(), label);
            }
        }
        return new WebPage(url, title, d.text(), linksMap);

    }
}
