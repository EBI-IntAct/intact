package uk.ac.ebi.intact.sanity.commons.rules;

import uk.ac.ebi.intact.model.*;
import static uk.ac.ebi.intact.sanity.commons.rules.KeyPrefix.*;
import static uk.ac.ebi.intact.sanity.commons.rules.MessageLevel.*;

import java.io.PrintStream;
import java.util.*;

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

    ANNOTATION_WITH_WRONG_TOPIC( AnnotatedObject.class, ANNOTATED_OBJECT, 1, "Objects with annotation using hidden or obsolete CvTopic", WARNING ),

    BROKEN_URL( AnnotatedObject.class, ANNOTATED_OBJECT, 2, "Invalid URLs", WARNING ),

    XREF_INVALID_PRIMARYID( AnnotatedObject.class, ANNOTATED_OBJECT, 3, "Xref primary ID not matching CvDatabase regular expression", ERROR ),

    ////////////////////////
    // BioSource

    BIOSOURCE_WITHOUT_NEWT_XREF( BioSource.class, BIOSOURCE, 1, "BioSource without Newt Xref", INFO,
                                 "Add a Newt Identity Xref according to the bioSource taxid" ),

    ////////////////////////
    // CvObject

    INTERACTION_DETECTION_WITHOUT_UNIPROT_EXPORT( CvInteraction.class, CV, 1,
                                                  "Interaction detection method without annotation uniprot-de-export",
                                                  ERROR, "Add a uniprot-dr-export annotation" ),

    TOPIC_WITHOUT_USED_IN_CLASS( CvTopic.class, CV, 2, "Topic without annotation 'used-in-class'", WARNING ),


    ////////////////////////
    // Experiment

    EXPERIMENT_NOT_SUPER_CURATED( Experiment.class, EXPERIMENT, 1, "Experiment not Super-Curated", INFO ),

    EXPERIMENT_ON_HOLD( Experiment.class, EXPERIMENT, 2, "Experiment marked as 'On hold'", INFO ),

    EXPERIMENT_WITHOUT_BIOSOURCE( Experiment.class, EXPERIMENT, 3, "Experiment without host organism", WARNING ),

    EXPERIMENT_WITHOUT_INTERACTION_DETECT( Experiment.class, EXPERIMENT, 4, "Experiment without interaction detection method ", ERROR ),

    EXPERIMENT_WITHOUT_PARTICIPANT_DETECT( Experiment.class, EXPERIMENT, 5, "Experiment without participant detection method", ERROR ),

    EXPERIMENT_WITHOUT_INTERACTION( Experiment.class, EXPERIMENT, 6, "Experiment without interactions", ERROR,
                                    "Edit the experiment and add at least one interaction"),

    EXPERIMENT_TO_BE_REVIEWED( Experiment.class, EXPERIMENT, 7, "Experiment marked as 'To be reviewed'", INFO ),

    EXPERIMENT_WITHOUT_PUBMED( Experiment.class, EXPERIMENT, 8, "No Pubmed ID found for experiment", ERROR,
                               "Edit the experiment and add the primary-reference to pubmed"),

    ////////////////////////
    // Feature

    FEATURE_WITHOUT_TYPE( Feature.class, FEATURE, 1, "A feature type is mandatory and was not found", ERROR,
                          "Edit the feature and add a range"),

    FEATURE_WITHOUT_RANGE( Feature.class, FEATURE, 2, "Feature without ranges specified", ERROR ),

    ////////////////////////
    // Interaction

    INTERACTION_ROLES_NO_CATEGORY( Interaction.class, INTERACTION, 1, "Interaction without any recognized categories", ERROR ),

    INTERACTION_ROLES_MIXED_CATEGORIES( Interaction.class, INTERACTION, 2, "Interaction with mixed component roles", ERROR ),

    INTERACTION_WITHOUT_COMPONENT( Interaction.class, INTERACTION, 3, "Interaction without Components", ERROR ),

    INTERACTION_WITHOUT_EXPERIMENT( Interaction.class, INTERACTION, 4, "Interaction not associated to an Experiment", ERROR ),

    ////////////////////////
    // Protein

    PROTEIN_INCORRECT_CRC64( Protein.class, PROTEIN, 1, "Incorrect CRC64 checksum for the protein sequence", ERROR , "A developer should fix it" ),

    PROTEIN_UNIPROT_NO_XREF( Protein.class, PROTEIN, 2, "Missing Uniprot identity xref", ERROR ),

    PROTEIN_UNIPROT_MULTIPLE_XREF( Protein.class, PROTEIN, 3, "More than one identity xref found", ERROR ),

    PROTEIN_UNIPROT_WRONG_AC( Protein.class, PROTEIN, 4, "Wrong format for the Uniprot AC", ERROR ),

    ////////////////////////
    // Nucleid Acid

    NUC_ACID_IDENTITY_INCORRECT( NucleicAcid.class, NUCLEIC_ACID, 1, "Nucleic acid with wrong qualifier for identity xref", ERROR ),

    NUC_ACID_IDENTITY_MISSING( NucleicAcid.class, NUCLEIC_ACID, 2, "Missing Nucleic Acid identity Xref", ERROR );

    ////////////////////////
    // Instance variable

    private String key;
    private String description;
    private String suggestion;
    private Class<? extends IntactObject> targetClass;
    private MessageLevel level;

    //////////////////
    // Constructors

    MessageDefinition( Class<? extends IntactObject> clazz, String keyPrefix, int id, String description, MessageLevel level, String suggestion ) {
        this( clazz, keyPrefix, id, description, level );
        this.suggestion = suggestion;
    }

    MessageDefinition( Class<? extends IntactObject> clazz, String keyPrefix, int id, String description, MessageLevel level ) {

        if ( clazz == null ) {
            throw new IllegalArgumentException( "You must give a non null class" );
        }
        if ( keyPrefix == null || keyPrefix.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non null key prefix" );
        }
        if ( id <= 0 ) {
            throw new IllegalArgumentException( "You must give a positive identifier" );
        }
        if ( description == null || description.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non null description" );
        }
        if( level == null ) {
            throw new IllegalArgumentException( "You must give a non null level" );
        }

        this.targetClass = clazz;
        this.key = keyPrefix + String.valueOf( id );
        this.description = description;
        this.level = level;
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

    public Class<? extends IntactObject> getTargetClass() {
        return targetClass;
    }

    public MessageLevel getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "[" + key + "] " + level + " - " + description + ( ( suggestion == null ) ? "" : " (" + suggestion + ")" );
    }

    public static MessageDefinition[] valuesByTargetClass(Class<? extends IntactObject> clazz) {
        return valuesByTargetClass( clazz, true );
    }

    public static MessageDefinition[] valuesByTargetClass(Class<? extends IntactObject> clazz, boolean excludeSubclasses) {
        Collection<MessageDefinition> messageDefinitions = new ArrayList<MessageDefinition>();

        for (MessageDefinition messageDefinition : values()) {
            if (excludeSubclasses) {
                if (clazz.equals(messageDefinition.getTargetClass())) {
                    messageDefinitions.add(messageDefinition);
                }
            } else if (clazz.isAssignableFrom(messageDefinition.getTargetClass())) {
                messageDefinitions.add(messageDefinition);
            }
        }

        return messageDefinitions.toArray(new MessageDefinition[messageDefinitions.size()]);
    }

    public static Set<Class<? extends IntactObject>> allTargetClasses() {
        Set<Class<? extends IntactObject>> classes = new LinkedHashSet<Class<? extends IntactObject>>();

        for (MessageDefinition messageDefinition : values()) {
            classes.add(messageDefinition.getTargetClass());
        }

        return classes;
    }

    public static void printAll( PrintStream ps ) {
        for (Class<? extends IntactObject> targetClass : allTargetClasses()) {
            ps.println(targetClass.getSimpleName()+":");

            for ( MessageDefinition messageDefinition : valuesByTargetClass(targetClass, true) ) {
                ps.println( "   "+messageDefinition );
            }

            ps.println();
        }
    }

    public static void main( String[] args ) {
        MessageDefinition.printAll( System.out );
    }
}
