package ai.getuseful.duitbetter.cleaner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CleanDuWebPage implements ICleaner {
    private URL url;
    private String html;

    @Override
    public String clean() {
        Document d = Jsoup.parse(html);
        StringBuilder sb = new StringBuilder();
        for(String cls: List.of("du-mod-content", "align-center", "container-list-item", "summary", "copy"))
            for(Element dmc : d.getElementsByClass(cls) )
                sb.append(dmc.text()).append("\n");
        if(sb.isEmpty()){
            System.out.format("URL %s is not supported\n", this.url);
        }
        return sb.toString().replace("\n\n", "\n".strip());

    }

}
