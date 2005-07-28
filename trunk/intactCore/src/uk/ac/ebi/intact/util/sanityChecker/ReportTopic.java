/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
/**
 * Describes a Report topic.
 */
public class ReportTopic {

    /**
         * Report topics
         */
        //
        // A N N O T A T I O N
        //
        public static final ReportTopic URL_NOT_VALID = new ReportTopic ("This/those Url(s) is/are not valid");


        //
        // B I O S O U R C E
        //
        public static final ReportTopic BIOSOURCE_WITH_NO_TAXID = new ReportTopic( "BioSource having no taxId set" );
        public static final ReportTopic BIOSOURCE_WITH_NO_NEWT_XREF = new ReportTopic( "BioSource having no Newt xref with Reference Qualifier equal to identity" );

        //
        // E X P E R I M E N T S
        //
        public static final ReportTopic EXPERIMENT_WITHOUT_INTERACTIONS = new ReportTopic( "Experiments with no Interactions" );
        public static final ReportTopic EXPERIMENT_WITHOUT_PUBMED = new ReportTopic( "Experiments with no pubmed id" );
        public static final ReportTopic EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE = new ReportTopic( "Experiments with no pubmed id (with 'primary-reference' as qualifier)" );
        public static final ReportTopic EXPERIMENT_WITHOUT_ORGANISM = new ReportTopic( "Experiments with no organism" );
        public static final ReportTopic EXPERIMENT_WITHOUT_CVIDENTIFICATION = new ReportTopic( "Experiments with no CvIdentification" );
        public static final ReportTopic EXPERIMENT_WITHOUT_CVINTERACTION = new ReportTopic( "Experiments with no CvInteraction" );
        public static final ReportTopic EXPERIMENT_TO_BE_REVIEWED = new ReportTopic( "Experiments to be reviewed" );
        public static final ReportTopic EXPERIMENT_ON_HOLD = new ReportTopic( "Experiments is on hold" );

        //
        // I N T E R A C T I O  N S
        //
        public static final ReportTopic INTERACTION_WITH_NO_EXPERIMENT = new ReportTopic( "Interactions with no Experiment" );
        public static final ReportTopic INTERACTION_WITH_NO_CVINTERACTIONTYPE = new ReportTopic( "Interactions with no CvInteractionType" );
        public static final ReportTopic INTERACTION_WITH_NO_ORGANISM = new ReportTopic( "Interactions with no Organism" );
        public static final ReportTopic INTERACTION_WITH_NO_CATEGORIES = new ReportTopic( "Interactions with no categories (bait-prey, neutral, self, unspecified)" );
        public static final ReportTopic INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES = new ReportTopic( "Interactions with mixed categories (bait-prey, enzyme-enzymeTarget, neutral, complex, self, unspecified)" );
        public static final ReportTopic INTERACTION_WITH_NO_BAIT = new ReportTopic( "Interactions with no bait" );
        public static final ReportTopic INTERACTION_WITH_NO_PREY = new ReportTopic( "Interactions with no prey" );
        public static final ReportTopic INTERACTION_WITH_NO_ENZYME_TARGET = new ReportTopic( "Interactions with no enzymeTarget" );
        public static final ReportTopic INTERACTION_WITH_NO_ENZYME = new ReportTopic( "Interactions with no enzyme" );
        public static final ReportTopic INTERACTION_WITH_ONLY_ONE_NEUTRAL = new ReportTopic( "Interactions with only one neutral component" );
        public static final ReportTopic INTERACTION_WITH_PROTEIN_COUNT_LOWER_THAN_2 = new ReportTopic( "Interactions with less than 2 proteins (Role = complex)" );
        public static final ReportTopic INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2 = new ReportTopic( "Interactions with protein having their role set to self and its stoichiometry lower than 2.0" );
        public static final ReportTopic INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN = new ReportTopic( "Interactions with more than one protein having their role set to self" );
        public static final ReportTopic SINGLE_PROTEIN_CHECK = new ReportTopic( "Interactions with only One Protein" );
        public static final ReportTopic NO_PROTEIN_CHECK = new ReportTopic( "Interactions with No Components" );
        public static final ReportTopic PROTEIN_SEQUENCE_AND_RANGE_SEQUENCE_NOT_EQUAL = new ReportTopic ("Sequence associated with the Range differs from the Protein sequence");
        public static final ReportTopic RANGE_HAS_NO_SEQUENCE_WHEN_PROTEIN_HAS_A_SEQUENCE = new ReportTopic("Range has no sequence but Protein got one");
        public static final ReportTopic RANGE_HAS_A_SEQUENCE_BUT_THE_PROTEIN_DOES_NOT_HAVE_ONE = new ReportTopic("Range has a sequence but Protein does not have one");
        public static final ReportTopic INTERACTION_ASSOCIATED_TO_A_RANGE_BUT_PROTEIN_DOES_NOT_HAVE_SEQUENCE = new ReportTopic("Interaction assiciated to a range when the protein has no related sequence");
        public static final ReportTopic FUZZY_TYPE_NOT_APPROPRIATE = new ReportTopic("As the protein is not associated to any sequence, the fuzzy type must be either n-terminal, c-terminal or undetermined and numeric feature range should not be given");
        public static final ReportTopic INTERVAL_VALUE_NOT_APPROPRIATE = new ReportTopic("Interval values not appropriate for the FromCvFuzzyType. When FromCvFuzzyType is n-terminal, c-terminal or undetermined, all interval values should be equal to zero.");
        //
        // P R O T E I N S
        //
        public static final ReportTopic NON_UNIPROT_PROTEIN_WITH_NO_UNIPROT_IDENTITY = new ReportTopic( "proteins (non uniprot) with no Xref with XrefQualifier(identity)" );
        public static final ReportTopic PROTEIN_WITH_NO_UNIPROT_IDENTITY = new ReportTopic( "proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot)" );
        public static final ReportTopic PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY = new ReportTopic( "proteins with more than one Xref with XrefQualifier(identity) and CvDatabase(uniprot)" );
        public static final ReportTopic PROTEIN_WITH_WRONG_CRC64 = new ReportTopic( "proteins Crc64 stored in the database does not correspond to the Crc64 calculated from the sequence");
        public static final ReportTopic PROTEIN_WITHOUT_A_SEQUENCE_BUT_WITH_AN_CRC64 = new ReportTopic( "proteins does not have a sequence but have a Crc64");
        public static final ReportTopic DUPLICATED_PROTEIN = new ReportTopic("Those proteins are duplicated");


    private static final String NEW_LINE = System.getProperty( "line.separator" );

    private String title;

    public ReportTopic( String title ) {

        if ( title == null ) {
            this.title = "";
        } else {
            this.title = title;
        }
    }

    public String getTitle() {
        return title;
    }

    /**
     * @return the title line underlined.
     */
    public String getUnderlinedTitle() {

        StringBuffer sb = new StringBuffer( ( title.length() * 2 ) + 2 );
        sb.append( title ).append( NEW_LINE );
        for ( int i = 0; i < title.length(); i++ ) {
            sb.append( '-' );
        }

        return sb.toString();
    }
}