package ai.getuseful.duitbetter.crawler;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    public static URL createURL(String urlString, String defaultProtocol, String defaultHost){
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException | IllegalArgumentException e) {
            try {
                if(!urlString.startsWith("http") && !urlString.startsWith("/"))
                    urlString = "/" + urlString;
                url = new URL(String.format("%s://%s%s", defaultProtocol, defaultHost, urlString));
            } catch(MalformedURLException | IllegalArgumentException ex) {
                System.out.format("Unable to build a proper URL from %s, skipping", urlString);
                url = null;
            }
        }
        return url;
    }
}
