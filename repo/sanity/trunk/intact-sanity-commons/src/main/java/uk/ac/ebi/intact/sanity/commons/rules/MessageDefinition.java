package uk.ac.ebi.intact.sanity.commons.rules;

import static uk.ac.ebi.intact.sanity.commons.rules.KeyPrefix.*;

/**
 * Definition of the messages.
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since 2.0.0
 */
public enum MessageDefinition {

    ANNOTATION_WITH_WRONG_TOPIC( ANNOTATED_OBJECT, 1, "Objects with annotation using hidden or obsolete CvTopic",
                                 "");

    // Bruno


    ////////////////////////
    // Instance variable

    String key;
    String description;
    String suggestion;

    //////////////////
    // Constructors

    MessageDefinition(String keyPrefix, int id, String description, String suggestion) {
        this( keyPrefix, id, description );
        this.suggestion = suggestion;
    }

    MessageDefinition(String keyPrefix, int id, String description ) {
        if( keyPrefix == null || keyPrefix.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non null key prefix" );
        }
        if( id <= 0 ) {
            throw new IllegalArgumentException( "You must give a positive identifier" );
        }
        if( description == null || description.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non null description" );
        }
        this.key = keyPrefix + String.valueOf( id );
        this.description = description;
    }

    ///////////////
    // Getters

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
