package ai.getuseful.duitbetter.crawler.repository;

import ai.getuseful.duitbetter.crawler.entities.WebPageNode;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WebPageNodeRepository extends CrudRepository<WebPageNode, UUID> {

    @Query(value = """
            MERGE (wp:WebPage {url:$url}) ON CREATE SET wp.title = $title, wp.text = $text ON MATCH SET wp.text = coalesce(wp.text, $text) WITH wp
            UNWIND keys($links) AS link
            MERGE (linkedPage:WebPage {url: link}) ON CREATE SET linkedPage.title = $links[link]
            WITH wp, linkedPage
            MERGE (wp)-[:LINKS_TO]->(linkedPage)
            """)
    WebPageNode savePage(String url, String title, String text, Map<String, String> links);
    @Query(value = """
    MATCH(wp:WebPage {url:$url}) RETURN wp
    """)
    WebPageNode getByUrl(String url);

    @Query(value="""
    MATCH (wp:WebPage) where wp.text is null return wp.url as url, wp.title as title
    """)
    List<WebPageNode> getPagesWithoutSource();

}

