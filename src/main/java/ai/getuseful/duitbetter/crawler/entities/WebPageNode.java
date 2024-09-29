package ai.getuseful.duitbetter.crawler.entities;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class WebPageNode {

    @Id
    private String url;
    @Property
    private String title;
    @Property
    private String text;
    @EqualsAndHashCode.Exclude
    @Relationship(type="LINKS_TO", direction = Relationship.Direction.OUTGOING)
    private List<String> links;
}
