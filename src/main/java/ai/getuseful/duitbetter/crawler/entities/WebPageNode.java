package ai.getuseful.duitbetter.crawler.entities;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class WebPageNode {

    @Id
    @Generated
    private UUID id;
    @Property
    private String url;
    @Property
    private String title;
    @Property
    private String text;
    @Property
    private boolean cleanedHtml;
    @EqualsAndHashCode.Exclude
    @Relationship(type="LINKS_TO", direction = Relationship.Direction.OUTGOING)
    private List<WebPageNode> links;
}
