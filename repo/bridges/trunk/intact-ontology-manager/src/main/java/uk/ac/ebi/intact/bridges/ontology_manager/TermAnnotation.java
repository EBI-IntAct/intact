package uk.ac.ebi.intact.bridges.ontology_manager;

/**
 * Annotations of a Cv term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class TermAnnotation {

    private String topic;
    private String topicId;
    private String description;

    public TermAnnotation(String topic, String desc){
        if (topic == null){
            throw new IllegalArgumentException("A term annotations must have a non null topic");
        }

        this.topic = topic;
        this.description = desc;
    }

        public TermAnnotation(String topicId, String topic, String desc){
        if (topicId == null){
            throw new NullPointerException("A term annotations must have a non null topic Id");
        }

        this.topic = topic;
        this.description = desc;
            this.topicId = topicId;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public String getTopicId() {
        return topicId;
    }
}
