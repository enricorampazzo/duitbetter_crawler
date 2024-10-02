package ai.getuseful.duitbetter.crawler.dto;


import ai.getuseful.duitbetter.crawler.Utils;
import ai.getuseful.duitbetter.crawler.repository.WebPageNodeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record WebPage(URL url, String title, String html, Map<String, String> links){
    public static WebPage buildPage(WebPageNodeRepository repository, URL url, String title, boolean keepOnlyInternalLinks, boolean excludenewsarticles){
        List<Integer> pageLoadTimeouts = List.of(6);
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
                if(isNewsArticle(key.toString()) && excludenewsarticles){
                    System.out.format("excluding url %s because it is a news article\n", key);
                    continue;
                }
                if(key.toString().contains("cgi-bin")){
                    System.out.format("excluding %s because it is a cgi-bin url\n", key);
                    continue;
                }
                if(key.toString().contains("/ar/")){
                    System.out.format("Excluding %s because it points to a webpage in arabic\n", key);
                    continue;
                }
                if(key.toString().contains("&lang=ar-SA")){
                    System.out.format("Excluding %s because it is a duplicate of a page ending with &lang=en-US\n", key);
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
        List<String> existingUrls = repository.getExistingUrls(linksMap.keySet());
        Map<String, String> filteredLinkMap = new HashMap<>();
        for(Map.Entry<String, String> pageUrl: linksMap.entrySet()){
            if(!existingUrls.contains(pageUrl.getKey())){
                filteredLinkMap.put(pageUrl.getKey(), pageUrl.getValue());
            }
            else {
                System.out.format("excluding url %s as a node for this url already exists\n", pageUrl.getKey());
            }
        }
        return new WebPage(url, title, d.html(), filteredLinkMap);

    }
    private static boolean isNewsArticle(String url){
        return url.contains("/media-centre/");
    }
}
