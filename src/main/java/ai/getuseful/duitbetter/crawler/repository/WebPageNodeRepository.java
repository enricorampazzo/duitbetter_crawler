package ai.getuseful.duitbetter.crawler.repository;

import ai.getuseful.duitbetter.crawler.entities.WebPageNode;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WebPageNodeRepository extends CrudRepository<String, WebPageNode> {

    @Query(value = """
            MERGE (wp:WebPage {url:$url}) ON CREATE SET title = $title, text = $text RETURN wp
            """)
    public WebPageNode savePage(String url, String title, String text, List<String> labels);
}
