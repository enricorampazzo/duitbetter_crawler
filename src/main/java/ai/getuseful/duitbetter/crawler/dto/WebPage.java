package ai.getuseful.duitbetter.crawler.dto;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record WebPage(String url, String title, String text, Map<String, String> links){

    public static WebPage buildPage(String url, String title){
        var co = new ChromeOptions();
        co.setImplicitWaitTimeout(Duration.of(10, ChronoUnit.SECONDS));
        co.setPageLoadTimeout(Duration.of(10, ChronoUnit.SECONDS));
        WebDriver driver = new ChromeDriver(co);
        String source = null;
        try {
            driver.get(url);
        } catch (TimeoutException ex) {
            source = driver.getPageSource();
        }
        Document d = Jsoup.parse(source);
        List<Element> links = d.getElementsByTag("a").stream().filter(e -> {
            String href = e.attribute("href").getValue().strip();
            return !href.isEmpty() && !href.contains("#") && !href.toLowerCase().startsWith("javascript");
        }).toList();
        Map<String, String> linksMap = new HashMap<>();
        for (Element link : links) {
            String key = link.attribute("href").getValue();
            if (linksMap.containsKey(key)) {
                continue;
            }
            String label = link.ownText();
            if (label.isBlank()) {
                label = link.text();
            }
            linksMap.put(key, label);
        }
        driver.close();
        return new WebPage(url, title, d.text(), linksMap);

    }
}
