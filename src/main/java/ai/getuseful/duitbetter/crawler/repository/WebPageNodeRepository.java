package ai.getuseful.duitbetter.crawler.repository;

import ai.getuseful.duitbetter.crawler.entities.WebPageNode;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface WebPageNodeRepository extends CrudRepository<WebPageNode, UUID> {

    @Query(value = """
            MERGE (wp:WebPage {url:$url})
            ON CREATE SET
              wp.title = $title, wp.text = $text,
              wp.cleaned_text = $cleanedText
            ON MATCH SET 
              wp.text = coalesce(wp.text, $text),
              wp.cleaned_text = coalesce(wp.cleaned_text, $cleanedText) 
            WITH wp
              UNWIND keys($links) AS link
                MERGE (linkedPage:WebPage {url: link}) ON CREATE SET linkedPage.title = $links[link]
            WITH wp, linkedPage
              MERGE (wp)-[:LINKS_TO]->(linkedPage)
            """)
    WebPageNode savePage(String url, String title, String text, Map<String, String> links, String cleanedText);
    @Query(value = """
    MATCH(wp:WebPage {url:$url}) RETURN wp
    """)
    WebPageNode getByUrl(String url);

    @Query(value= """
      MATCH (wp:WebPage) WHERE wp.url in $urls return wp.url
    """)
    List<String> getExistingUrls(Set<String> urls);

    @Query(value="""
    MATCH (wp:WebPage) where wp.text is null return wp.url as url, wp.title as title
    """)
    List<WebPageNode> getPagesWithoutSource();

    @Query(value= """
    MATCH (wp:WebPage) where wp.cleanedHtml is null return wp.url as url, wp.text as text LIMIT 500
   """)
    List<WebPageNode> getPagesWithHtmlToClean();

    @Query(value= """
    MATCH (wp:WebPage {url:$url}) SET wp.cleanedHtml = true, wp.text = $cleanedHtml
   """)
    void setCleanHtmlForPage(String url, String cleanedHtml);

    @Query(value= """
    MATCH (wp:WebPage) WHERE wp.cleaned_text is null return wp limit 400
    """)
    List<WebPageNode> getPagesWithoutExtractedText();

    @Query(value= """
    MATCH (wp:WebPage {url:$url}) set wp.cleaned_text = $cleanedText
    """)
    void setPageCleanedText(String url, String cleanedText);




}

