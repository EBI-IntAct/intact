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

    /////////////////////////
    // Annotated Objects

    ANNOTATION_WITH_WRONG_TOPIC( ANNOTATED_OBJECT, 1, "Objects with annotation using hidden or obsolete CvTopic" ),

    BROKEN_URL( ANNOTATED_OBJECT, 2, "Invalid URLs" ),

    XREF_INVALID_PRIMARYID( ANNOTATED_OBJECT, 3, "Xref primary ID not matching CvDatabase regular expression" ),

    ////////////////////////
    // BioSource

    BIOSOURCE_WITHOUT_NEWT_XREF( BIOSOURCE, 1, "BioSource without Newt Xref" ),

    ////////////////////////
    // CvObject

    INTERACTION_DETECTION( CV, 1, "Interaction detection method without annotation unniprot-de-export" ),

    ////////////////////////
    // Experiment

    EXPERIMENT_NOT_SUPER_CURATED(  EXPERIMENT, 1, "" ),

    EXPERIMENT_ON_HOLD( EXPERIMENT, 2, "" ),

    EXPERIMENT_WITHOUT_BIOSOURCE( EXPERIMENT, 3, "" ),

    EXPERIMENT_WITHOUT_INTERACTION_DETECT( EXPERIMENT, 4, "" ),

    EXPERIMENT_WITHOUT_PARTICIPANT_DETECT( EXPERIMENT, 5, "" ),

    EXPERIMENT_WITHOUT_INTERACTION( EXPERIMENT, 6, "" ),

    EXPERIMENT_TO_BE_REVIEWED( EXPERIMENT, 7, "" ),

    EXPERIMENT_WITHOUT_PUBMED( EXPERIMENT, 8, "" ),

    ////////////////////////
    // Feature

    FEATURE_WITHOUT_TYPE( FEATURE, 1, "" ),

    FEATURE_WITHOUT_RANGE( FEATURE, 2, "" ),

    ////////////////////////
    // Interaction

    INTERACTION_COMPONENT_ROLE( INTERACTION, 1, "" ),

    INTERACTION_WITHOUT_COMPONENT( INTERACTION, 1, "" ),

    INTERACTION_WITHOUT_EXPERIMENT( INTERACTION, 1, "" ),

    ////////////////////////
    // Protein

    PROTEIN_INCORRECT_CRC64( PROTEIN, 1, "Incorrect CRC64 checksum for the protein sequence", "A developer should fix it" ),

    PROTEIN_UNIPROT_NO_XREF( PROTEIN, 2, "Missing Uniprot identity xref" ),

    PROTEIN_UNIPROT_MULTIPLE_XREF( PROTEIN, 3, "More than one identity xrefs found" ),

    PROTEIN_UNIPROT_WRONG_ID ( PROTEIN, 4, "Wrong format for the Uniprot identifier" ),

    ////////////////////////
    // Nucleid Acid

    NUC_ACID_IDENTITY_INCORRECT ( NUCLEIC_ACID, 1, "Nucleic acid with wrong qualifier for identity xref" ),

    NUC_ACID_IDENTITY_MISSING ( NUCLEIC_ACID, 2, "Missing Nucleic Acid identity Xref");

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
