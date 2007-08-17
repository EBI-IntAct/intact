/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: ReportTopic.java,v 1.15 2006/04/13 12:37:16 skerrien Exp $
 */
/**
 * Describes a Report topic.
 */
public enum ReportTopic {


    EXPERIMENT_TO_CORRECT("Experiment(s) to review"),
    /**
     * Report topics
     */
    //
    // C V O B J E C T
    //


    HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_DATABASE_AC_IN_XREF("This/these Cvs are annotated as hidden or obsolete but are actualy in used in  a xref as database"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_QUALIFIER_AC_IN_XREF("This/these Cvs are annotated as hidden or obsolete but are actualy in used in a xref as Reference Qualifier"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_IDENTIFICATION_AC_IN_FEATURE("This/these Cvs are annotated as hidden or obsolete but are actualy in used in a feature as CvFeatureIdentification"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_FEATURETYPE_AC_IN_FEATURE("This/these Cvs are annotated as hidden or obsolete but are actualy in used in a feature as CvFeatureType"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_FROMFUZZYTYPE_AC_IN_RANGE("This/these Cvs are annotated as hidden or obsolete but are actualy in used in a range as fromfuzzytype_ac"),

    HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_TOFUZZYTYPE_AC_IN_RANGE("This/these Cvs are annotated as hidden or obsolete but are actualy in used in a range as tofuzzytype_ac"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_ROLE_IN_COMPONENT("This/these Cvs are annotated as hidden or obsolete but are actualy in used as role of a component"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_INTERACTORTYPE_IN_INTERACTOR("This/these Cvs are annotated as hidden or obsolete but are actualy in used for a Protein as Interactor Type"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_INTERACTIONTYPE_IN_INTERACTOR("This/these Cvs are annotated as hidden or obsolete but are actualy in used for an Interaction as Interaction Type"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_PROTEINFORM_IN_INTERACTOR("This/these Cvs are annotated as hidden or obsolete but are actualy in used as Protein Form Type for a Protein"),

    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_TISSUEAC_IN_BIOSOURCE("This/these Cvs are annotated as hidden or obsolete but are actualy in used as a Tissue for This/these Biosource(s)"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_CELLTYPEAC_IN_BIOSOURCE("This/these Cvs are annotated as hidden or obsolete but are actualy in used as a Celltype for This/these Biosource(s)"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_DETECTMETHODAC_IN_EXPERIMENT("This/these Cvs are annotated as hidden or obsolete but are actualy in used as a Detection Method for This/these Experiment(s)"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_IDENTMETHODAC_IN_EXPERIMENT("This/these Cvs are annotated as hidden or obsolete but are actualy in used as a Identification Method for This/these Experiment(s)"),
    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_TOPICAC_IN_ANNOTATION("This/these object(s) have annotation using CvTopic which are hidden or obsolete Cvs"),

    HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_ALIASTYPEAC_IN_ALIAS("This/these alias have an alias type corresponding to a hidden or obsolete Cv"),

    CVINTERACTION_WITHOUT_ANNOTATION_UNIPROT_DR_EXPORT("This/these CvInteraction have no uniprot-dr-export annotation"),

    //
    // F E A T U R E
    //
    FEATURE_WITHOUT_A_RANGE("This/these Features(s) are not associated to a Range"),

    //
    // R A N G E
    //


    //special format
    RANGE_SEQUENCE_NOT_EQUAL_TO_PROTEIN_SEQ("This/these Range(s) are associated to a sequence which does not corresponds to the protein sequence. And the Range Sequence couldn't be remapped automatically"),
    //special format
    RANGE_SEQUENCE_SAVED_BY_ADDING_THE_M("This/these Range(s) were created when the first Methionine was not there, since then the Methionine had been added to the Protein Sequence. The Range Sequence has been remapped. "),
    //special format
    RANGE_SEQUENCE_SAVED_BY_SUPPRESSING_THE_M("This/these Range(s) were created when the first Methionine was there, since then the Methionine had been remooved from the Protein Sequence. The Range Sequence has been remapped. "),
    //special format
    DELETION_INTERVAL_TO_LONG_TO_BE_CARACTERIZED_BY_DELETION_ANALYSIS_FEATURE_TYPE(" Features characterizing deletion of more then 2 amino-acid and having CvFeatureIdentification set to \"Deletion Analysis\""),

    //
    // A N N O T A T I O N
    //
    //special format
    URL_NOT_VALID("This/these Url(s) is/are not valid"),
    //special format
    TOPICAC_NOT_VALID("This topic ac shouldn't be use to annotate this kind of object"),

    //
    // B I O S O U R C E
    //
    BIOSOURCE_WITH_NO_TAXID("BioSource having no taxId set"),
    BIOSOURCE_WITH_NO_NEWT_XREF("BioSource having no Newt xref with Reference Qualifier equal to identity"),

    //
    // E X P E R I M E N T S
    //
    EXPERIMENT_WITHOUT_INTERACTIONS("Experiments with no Interactions"),
    EXPERIMENT_WITHOUT_PUBMED("Experiments with no pubmed id"),
    EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE("Experiments with no pubmed id (with 'primary-reference' as qualifier)"),
    EXPERIMENT_WITHOUT_ORGANISM("Experiments with no organism"),
    EXPERIMENT_WITHOUT_CVIDENTIFICATION("Experiments with no CvIdentification"),
    EXPERIMENT_WITHOUT_CVINTERACTION("Experiments with no CvInteraction"),
    EXPERIMENT_TO_BE_REVIEWED("Experiments having an annotation to-be-reviewed"),
    EXPERIMENT_ON_HOLD("Experiments having an annotation on-hold"),
    EXPERIMENT_NOT_ACCEPTED_NOT_TO_BE_REVIEWED("Experiment(s) without annotation 'to-be-reviewed' or 'accepted'"),

    //
    // I N T E R A C T I O  N S
    //
    INTERACTION_WITH_NO_EXPERIMENT("Interactions with no Experiment"),
    INTERACTION_WITH_NO_CVINTERACTIONTYPE("Interactions with no CvInteractionType"),
    INTERACTION_WITH_NO_ORGANISM("Interactions with no Organism"),
    INTERACTION_WITH_NO_CATEGORIES("Interactions with no categories (bait-prey, neutral, self, unspecified)"),
    INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES("Interactions with mixed categories (bait-prey, enzyme-enzymeTarget, neutral, complex, self, unspecified)"),
    INTERACTION_WITH_NO_BAIT("Interactions with no bait"),
    INTERACTION_WITH_NO_PREY("Interactions with no prey"),
    INTERACTION_WITH_NO_FLUOROPHORE_ACCEPTOR("Interactions with fluorophore acceptor"),
    INTERACTION_WITH_NO_FLUOROPHORE_DONOR("Interactions with fluorophore donor"),
    INTERACTION_WITH_NO_ELECTRON_ACCEPTOR("Interactions with electron acceptor"),
    INTERACTION_WITH_NO_ELECTRON_DONOR("Interactions with electron donor"),
    INTERACTION_WITH_NO_ENZYME_TARGET("Interactions with no enzymeTarget"),
    INTERACTION_WITH_NO_ENZYME("Interactions with no enzyme"),
    INTERACTION_WITH_NO_INHIBITED("Interactions with no inhibited"),
    INTERACTION_WITH_NO_INHIBITOR("Interactions with no inhibitor"),
    INTERACTION_WITH_ONLY_ONE_NEUTRAL("Interactions with only one neutral component and stoichiometry 1"),
    INTERACTION_WITH_PROTEIN_COUNT_LOWER_THAN_2("Interactions with less than 2 proteins (Role = complex)"),
    INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2("Interactions with protein having their role set to self and its stoichiometry lower than 2.0"),
    INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN("Interactions with more than one protein having their role set to self"),
    SINGLE_PROTEIN_CHECK("Interactions with only One Protein"),
    NO_PROTEIN_CHECK("Interactions with No Components"),
    //special format
    PROTEIN_SEQUENCE_AND_RANGE_SEQUENCE_NOT_EQUAL("Sequence associated with the Range differs from the Protein sequence"),
    RANGE_HAS_NO_SEQUENCE_WHEN_PROTEIN_HAS_A_SEQUENCE("Range has no sequence but Protein got one"),
    //RANGE_HAS_A_SEQUENCE_BUT_THE_PROTEIN_DOES_NOT_HAVE_ONE = new ReportTopic("Range has a sequence but Protein does not have one");
    //INTERACTION_ASSOCIATED_TO_A_RANGE_BUT_PROTEIN_DOES_NOT_HAVE_SEQUENCE = new ReportTopic("Interaction assiciated to a range when the protein has no related sequence");
    //FUZZY_TYPE_NOT_APPROPRIATE = new ReportTopic("As the protein is not associated to any sequence, the fuzzy type must be either n-terminal, c-terminal or undetermined and numeric feature range should not be given");
    //INTERVAL_VALUE_NOT_APPROPRIATE = new ReportTopic("Interval values not appropriate for the FromCvFuzzyType. When FromCvFuzzyType is n-terminal, c-terminal or undetermined, all interval values should be equal to zero.");

    //special format ==> addMessage( ReportTopic rt, InteractionBean ib, List experimentBeans)
    INTERACTION_LINKED_TO_MORE_THEN_ONE_EXPERIMENT("Interaction linked to more then one experiment"),

    //
    // P R O T E I N S
    //
    INTERACTOR_WITH_MULTIPLE_IDENTITY("Interactor with multiple identity xreferences."),
    NON_UNIPROT_PROTEIN_WITH_NO_UNIPROT_IDENTITY("proteins (non uniprot) with no Xref with XrefQualifier(identity)"),
    PROTEIN_WITH_NO_UNIPROT_IDENTITY("Interactor with no Xref with XrefQualifier(identity) to and CvDatabase UniprotKb or ddbj-embl-genbank"),
    PROTEIN_WITH_MORE_THAN_ONE_DDBJ_IDENTITY("Nucleic Acid with more than one Xref with XrefQualifier(identity) and CvDatabase(ddbj-embl-genbank)"),
    PROTEIN_WITH_IDENTITY_XREF_TO_DDBJ_AND_UNIPROT("Interactor having one ore more xref identity to Uniprot and one or more xref identity to ddbj-embl-genbank"),
    PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY("proteins with more than one Xref with XrefQualifier(identity) and CvDatabase(uniprot)"),
    PROTEIN_WITH_WRONG_CRC64("proteins Crc64 stored in the database does not correspond to the Crc64 calculated from the sequence"),
    PROTEIN_WITHOUT_A_SEQUENCE_BUT_WITH_AN_CRC64("proteins does not have a sequence but have a Crc64"),
    //special format
    DUPLICATED_PROTEIN("Those proteins are duplicated"),
    DUPLICATED_SPLICE_VARIANT("Those splice variant are duplicated (with the same parent protein)"),

    //
    // X R E F
    //
    //special format
    XREF_WITH_NON_VALID_PRIMARYID("Xref having non valid primaryId");

    private static final String NEW_LINE = "<br>";// System.getProperty( "line.separator"),

    private String title;

    private ReportTopic( String title ) {

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
