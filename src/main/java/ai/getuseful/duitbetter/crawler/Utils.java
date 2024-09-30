package ai.getuseful.duitbetter.crawler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {

    public static URL createURL(String urlString, String defaultProtocol, String defaultHost){
        URL url;
        try {
            url = new URI(urlString).toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            try {
                url = new URI(String.format("%s%s%s", defaultProtocol, defaultHost, urlString)).toURL();
            } catch (URISyntaxException | MalformedURLException | IllegalArgumentException ex) {
                System.out.format("Unable to build a proper URL from %s, skipping", urlString);
                url = null;
            }
        }
        return url;
    }
}
