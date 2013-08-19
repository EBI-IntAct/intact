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

    public TermAnnotation(String topic, String topicId, String desc){
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

    @Override
    public boolean equals(Object o){

        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof TermAnnotation ) ) {
            return false;
        }

        TermAnnotation termAnnotation = (TermAnnotation) o;

        if (this.topic != null){
            if (!this.topic.equalsIgnoreCase(termAnnotation.getTopic())){
                return false;
            }
        }
        else if (termAnnotation.getTopic() != null){
             return false;
        }

        if (this.topicId != null){
            if (!this.topicId.equalsIgnoreCase(termAnnotation.getTopicId())){
                return false;
            }
        }
        else if (termAnnotation.getTopicId() != null){
             return false;
        }

        if (this.description != null){
            if (!this.description.equalsIgnoreCase(termAnnotation.getDescription())){
                return false;
            }
        }
        else if (termAnnotation.getDescription() != null){
             return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.topic != null ? this.topic.hashCode() : 0;

        result = result * 31 + this.topicId != null ? this.topicId.hashCode() : 0;
        result = result * 31 + this.description != null ? this.description.hashCode() : 0;

        return result;
    }
}
