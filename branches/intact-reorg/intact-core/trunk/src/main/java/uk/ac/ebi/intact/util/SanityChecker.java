package uk.ac.ebi.intact.util;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import javax.mail.MessagingException;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;





/**
 * Utility class to perform some sanity checks on the DB. Mainly for use by curators. A allUsersReport of anomolies
 * detected (as per the list of checks) is sent via email to the appropriate people.
 *
 * @author Samuel Kerrien, Chris Lewington
 * @version $Id$
 */
public class SanityChecker {

    public static class SanityCheckerException extends Exception {

        public SanityCheckerException( String message ) {
            super( message );
        }
    }

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    public static final String TIME;

    /**
     * Mapping user -> mail adress Map( lowercase(username), email )
     */
    private static Map usersEmails = new HashMap();

    /**
     * List of user name that can't be mapped to a mail adress
     */
    private static Set unknownUsers = new HashSet();

    /**
     * List of admin mail adress
     */
    private static Collection adminsEmails = new HashSet();

    /**
     * Configuration file from which we get the lists of curators and admins.
     */
    public static final String SANITY_CHECK_CONFIG_FILE = "/config/sanityCheck.properties";

    /**
     * Prefix of the curator key from the properties file.
     */
    public static final String CURATOR = "curator.";

    /**
     * Prefix of the admin key from the properties file.
     */
    public static final String ADMIN = "admin.";

    static {
        Properties props = PropertyLoader.load( SANITY_CHECK_CONFIG_FILE );
        if ( props != null ) {
            int index;
            for ( Iterator iterator = props.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();


                index = key.indexOf( CURATOR );
                if ( index != -1 ) {
                    String userstamp = key.substring( index + CURATOR.length() );
                    String curatorMail = (String) props.get( key );
                    usersEmails.put( userstamp, curatorMail );
                } else {
                    // is it an admin then ?
                    index = key.indexOf( "admin." );
                    if ( index != -1 ) {
                        // store it
                        String adminMail = (String) props.get( key );
                        adminsEmails.add( adminMail );
                    }
                }
            } // keys
        } else {

            System.err.println( "Unable to open the properties file: " + SANITY_CHECK_CONFIG_FILE );
        }

        // format the current time
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd@HH.mm" );
        TIME = formatter.format( date );
    }


    /**
     * Service termination hook (gets called when the JVM terminates from a signal). eg.
     * <pre>
     * IntactHelper helper = new IntactHelper();
     * DatabaseConnexionShutdownHook dcsh = new DatabaseConnexionShutdownHook( helper );
     * Runtime.getRuntime().addShutdownHook( sh );
     * </pre>
     */
    private static class DatabaseConnexionShutdownHook extends Thread {

        private IntactHelper helper;

        public DatabaseConnexionShutdownHook( IntactHelper helper ) {
            super();
            this.helper = helper;
        }

        public void run() {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                    System.out.println( "Connexion to the database closed." );
                } catch ( IntactException e ) {
                    System.err.println( "Could not close the connexion to the database." );
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Describes a Report topic.
     */
    private class ReportTopic {

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


    /**
     * Report topics
     */
    //
    // A N N O T A T I O N
    //
    public final ReportTopic URL_NOT_VALID = new ReportTopic ("This/those Url(s) is/are not valid");


    //
    // B I O S O U R C E
    //
    public final ReportTopic BIOSOURCE_WITH_NO_TAXID = new ReportTopic( "BioSource having no taxId set" );
    public final ReportTopic BIOSOURCE_WITH_NO_NEWT_XREF = new ReportTopic( "BioSource having no Newt xref with Reference Qualifier equal to identity" );

    //
    // E X P E R I M E N T S
    //
    public final ReportTopic EXPERIMENT_WITHOUT_INTERACTIONS = new ReportTopic( "Experiments with no Interactions" );
    public final ReportTopic EXPERIMENT_WITHOUT_PUBMED = new ReportTopic( "Experiments with no pubmed id" );
    public final ReportTopic EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE = new ReportTopic( "Experiments with no pubmed id (with 'primary-reference' as qualifier)" );
    public final ReportTopic EXPERIMENT_WITHOUT_ORGANISM = new ReportTopic( "Experiments with no organism" );
    public final ReportTopic EXPERIMENT_WITHOUT_CVIDENTIFICATION = new ReportTopic( "Experiments with no CvIdentification" );
    public final ReportTopic EXPERIMENT_WITHOUT_CVINTERACTION = new ReportTopic( "Experiments with no CvInteraction" );
    public final ReportTopic EXPERIMENT_TO_BE_REVIEWED = new ReportTopic( "Experiments to be reviewed" );

    //
    // I N T E R A C T I O  N S
    //
    public final ReportTopic INTERACTION_WITH_NO_EXPERIMENT = new ReportTopic( "Interactions with no Experiment" );
    public final ReportTopic INTERACTION_WITH_NO_CVINTERACTIONTYPE = new ReportTopic( "Interactions with no CvInteractionType" );
    public final ReportTopic INTERACTION_WITH_NO_ORGANISM = new ReportTopic( "Interactions with no Organism" );
    public final ReportTopic INTERACTION_WITH_NO_CATEGORIES = new ReportTopic( "Interactions with no categories (bait-prey, neutral, self, unspecified)" );
    public final ReportTopic INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES = new ReportTopic( "Interactions with mixed categories (bait-prey, enzyme-enzymeTarget, neutral, complex, self, unspecified)" );
    public final ReportTopic INTERACTION_WITH_NO_BAIT = new ReportTopic( "Interactions with no bait" );
    public final ReportTopic INTERACTION_WITH_NO_PREY = new ReportTopic( "Interactions with no prey" );
    public final ReportTopic INTERACTION_WITH_NO_ENZYME_TARGET = new ReportTopic( "Interactions with no enzymeTarget" );
    public final ReportTopic INTERACTION_WITH_NO_ENZYME = new ReportTopic( "Interactions with no enzyme" );
    public final ReportTopic INTERACTION_WITH_ONLY_ONE_NEUTRAL = new ReportTopic( "Interactions with only one neutral component" );
    public final ReportTopic INTERACTION_WITH_PROTEIN_COUNT_LOWER_THAN_2 = new ReportTopic( "Interactions with less than 2 proteins (Role = complex)" );
    public final ReportTopic INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2 = new ReportTopic( "Interactions with protein having their role set to self and its stoichiometry lower than 1.0" );
    public final ReportTopic INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN = new ReportTopic( "Interactions with more than one protein having their role set to self" );
    public final ReportTopic SINGLE_PROTEIN_CHECK = new ReportTopic( "Interactions with only One Protein" );
    public final ReportTopic NO_PROTEIN_CHECK = new ReportTopic( "Interactions with No Components" );
    public final ReportTopic PROTEIN_SEQUENCE_AND_RANGE_SEQUENCE_NOT_EQUAL = new ReportTopic ("Sequence associated with the Range differs from the Protein sequence");
    public final ReportTopic RANGE_HAS_NO_SEQUENCE_WHEN_PROTEIN_HAS_A_SEQUENCE = new ReportTopic("Range has no sequence but Protein got one");
    public final ReportTopic RANGE_HAS_A_SEQUENCE_BUT_THE_PROTEIN_DOES_NOT_HAVE_ONE = new ReportTopic("Range has a sequence but Protein does not have one");
    public final ReportTopic INTERACTION_ASSOCIATED_TO_A_RANGE_BUT_PROTEIN_DOES_NOT_HAVE_SEQUENCE = new ReportTopic("Interaction assiciated to a range when the protein has no related sequence");
    public final ReportTopic FUZZY_TYPE_NOT_APPROPRIATE = new ReportTopic("As the protein is not associated to any sequence, the fuzzy type must be either n-terminal, c-terminal or undetermined and numeric feature range should not be given");
    public final ReportTopic INTERVAL_VALUE_NOT_APPROPRIATE = new ReportTopic("Interval values not appropriate for the FromCvFuzzyType. When FromCvFuzzyType is n-terminal, c-terminal or undetermined, all interval values should be equal to zero.");
    //
    // P R O T E I N S
    //
    public final ReportTopic NON_UNIPROT_PROTEIN_WITH_NO_UNIPROT_IDENTITY = new ReportTopic( "proteins (non uniprot) with no Xref with XrefQualifier(identity)" );
    public final ReportTopic PROTEIN_WITH_NO_UNIPROT_IDENTITY = new ReportTopic( "proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot)" );
    public final ReportTopic PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY = new ReportTopic( "proteins with more than one Xref with XrefQualifier(identity) and CvDatabase(uniprot)" );
    public final ReportTopic PROTEIN_WITH_WRONG_CRC64 = new ReportTopic( "proteins Crc64 stored in the database does not correspond to the Crc64 calculated from the sequence");
    public final ReportTopic PROTEIN_WITHOUT_A_SEQUENCE_BUT_WITH_AN_CRC64 = new ReportTopic( "proteins does not have a sequence but have a Crc64");


    //Holds the statements for finding userstamps in various tables
    private PreparedStatement experimentStatement;
    private PreparedStatement interationStatement;
    private PreparedStatement proteinStatement;
    private PreparedStatement bioSourceStatement;
    private PreparedStatement rangeStatement;
    private PreparedStatement annotationStatement;
    private PreparedStatement interactorStatement;
    private PreparedStatement featureStatement;
    private PreparedStatement cvStatement;

    private PreparedStatement exp2AnnotStatement; //statement to retrieve the ac experiment linked to a specific annotation
    private PreparedStatement bs2AnnotStatement;
    private PreparedStatement int2AnnotStatement;
    private PreparedStatement cv2AnnotStatement;
    private PreparedStatement feature2AnnotStatement;
    /**
     * Contains individual errors of curators as Map( user, Map( topic, Collection( message ) ) )
     */
    private Map allUsersReport = new HashMap();

    /**
     * Contains all error for admin as Map( Topic, Collection(Message) )
     */
    private Map adminReport = new HashMap();

    private IntactHelper helper;


    ///////////////////////////////////
    // Needed Controlled Vocabularies

    /**
     * Xref databases
     */
    private static CvDatabase uniprotDatabase;
    private static CvDatabase pubmedDatabase;

    /**
     * Describe wether an Xref is related the primary SPTR AC (identityCrefQualifier) or not (secondaryXrefQualifier)
     */
    private static CvXrefQualifier identityXrefQualifier;
    private static CvXrefQualifier primaryReferenceXrefQualifier;

    private CvComponentRole neutral;
    private CvComponentRole bait;
    private CvComponentRole prey;
    private CvComponentRole enzyme;
    private CvComponentRole enzymeTarget;
    private CvComponentRole self;
    private CvComponentRole unspecified;

    private CvTopic onHoldCvTopic;


    public SanityChecker( IntactHelper helper ) throws IntactException, SQLException, SanityCheckerException {

        //set up statements to get user info...
        //NB remember the Connection belongs to the helper - don't close it anywhere but
        //let the helper do it at the end!!
        this.helper = helper;
        Connection conn = helper.getJDBCConnection();
        bioSourceStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_biosource WHERE ac=?" );
        proteinStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_interactor WHERE ac=?" );
        interationStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_interactor WHERE ac=?" );
        experimentStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_experiment WHERE ac=?" );
        rangeStatement=conn.prepareStatement("SELECT userstamp,timestamp FROM ia_experiment WHERE ac=?");
        annotationStatement = conn.prepareStatement("SELECT userstamp, timestamp FROM ia_annotation WHERE ac=?");
        interactorStatement = conn.prepareStatement("SELECT userstamp, timestamp FROM ia_interactor WHERE ac=?");
        featureStatement = conn.prepareStatement("SELECT userstamp, timestamp FROM ia_feature WHERE ac=?");
        cvStatement = conn.prepareStatement("SELECT userstamp, timestamp FROM ia_controlledvocab WHERE ac=?");


        exp2AnnotStatement = conn.prepareStatement("SELECT experiment_ac FROM ia_exp2annot WHERE annotation_ac=?");
        bs2AnnotStatement = conn.prepareStatement("SELECT biosource_ac FROM ia_biosource2annot WHERE annotation_ac=?");
        int2AnnotStatement = conn.prepareStatement("SELECT interactor_ac FROM ia_int2annot WHERE annotation_ac=?");
        cv2AnnotStatement = conn.prepareStatement("SELECT cvobject_ac FROM ia_cvobject2annot WHERE annotation_ac=?");
        feature2AnnotStatement = conn.prepareStatement("SELECT feature_ac FROM ia_feature2annot WHERE annotation_ac=?");

        ////////////////////////////////////////////////
        // Collecting required Controlled Vocabularies
        uniprotDatabase = (CvDatabase) getCvObjectViaMI( CvDatabase.class, "MI:0486" );
        pubmedDatabase = (CvDatabase) getCvObjectViaMI( CvDatabase.class, "MI:0446" );

        neutral = (CvComponentRole) getCvObjectViaMI( CvComponentRole.class, "MI:0497" );
        bait = (CvComponentRole) getCvObjectViaMI( CvComponentRole.class, "MI:0496" );
        prey = (CvComponentRole) getCvObjectViaMI( CvComponentRole.class, "MI:0498" );
        enzyme = (CvComponentRole) getCvObjectViaMI( CvComponentRole.class, "MI:0501" );
        enzymeTarget = (CvComponentRole) getCvObjectViaMI( CvComponentRole.class, "MI:0502" );
        self = (CvComponentRole) getCvObjectViaMI( CvComponentRole.class, "MI:0503" );
        unspecified = (CvComponentRole) getCvObjectViaMI( CvComponentRole.class, "MI:0499" );

        identityXrefQualifier = (CvXrefQualifier) getCvObjectViaMI( CvXrefQualifier.class, "MI:0356" );
        primaryReferenceXrefQualifier = (CvXrefQualifier) getCvObjectViaMI( CvXrefQualifier.class, "MI:0358" );

        // CvTopix still don't have MI reference
        onHoldCvTopic = (CvTopic) helper.getObjectByLabel( CvTopic.class, CvTopic.ON_HOLD );
        if ( onHoldCvTopic == null ) {
            throw new SanityCheckerException( "Your IntAct node doesn't contain the required: CvTopic( on-hold )." );
        }
    }

    /**
     * Get a CvObject based on its class name and its shortlabel.
     *
     * @param clazz the Class we are looking for
     * @param miRef the PSI-MI reference of the object we are looking for
     *
     * @return the CvObject of type <code>clazz</code> and having the PSI-MI reference.
     *
     * @throws IntactException        if the search failed
     * @throws SanityCheckerException if the object is not found.
     */
    private CvObject getCvObjectViaMI( Class clazz, String miRef ) throws IntactException,
                                                                          SanityCheckerException {

        CvObject cv = (CvObject) helper.getObjectByXref( clazz, miRef );

        if ( cv == null ) {
            StringBuffer sb = new StringBuffer( 128 );
            sb.append( "Could not find " );
            sb.append( miRef );
            sb.append( ' ' );
            sb.append( clazz.getName() );
            sb.append( " in your IntAct node" );

            throw new SanityCheckerException( sb.toString() );
        }

        return cv;
    }

    /**
     * Checks We have so far: -----------------------
     * <p/>
     * 1.  Any Experiment lacking a PubMed ID
     * 2.  Any PubMed ID in Experiment DBXref without qualifier=Primary-reference
     * 3.  Any Interaction containing a bait but not a prey protein
     * 4.  Any Interaction containing a prey but not a bait protein
     * 5.  Any interaction with no protein attached
     * 6.  Any interaction with 1 protein attached, stoichiometry=1
     * 7.  Any Interaction missing a link to an Experiment
     * 8.  Any experiment (not on hold) with no Interaction linked to it
     * 9.  Any interaction missing CvInteractionType
     * 10. Any interaction missing Organism
     * 11. Any experiment (not on hold) missing Organism
     * 12. Any experiment (not on hold) missing CvInteraction
     * 13. Any experiment (not on hold) missing CvIdentification
     * 14. Any proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot), unless it is not a UniProt
     *     Protein in which case it must have at least one XrefQualifier(identity)
     * 15. Any BioSource with a NULL or empty taxid.
     * 16. Any proteins with more than one Xref with XrefQualifier(identity) and CvDatabase(uniprot), only for uniprot
     *     proteins
     * <p/>
     * To perform these checks we need to enhance the Helper/persistence code to handle more complex queries, ie to be
     * able to build Criteria and Query objects probably used in OJB (easiest to do). This is going to be needed anyway
     * so that we can handle more complex search queries later....
     */
    public void checkBioSource( Collection bioSources ) throws IntactException, SQLException {

        System.out.println( "Checking on BioSource (rule 15) ..." );

        for ( Iterator it = bioSources.iterator(); it.hasNext(); ) {
            BioSource bioSource = (BioSource) it.next();

            //check 15
            if ( bioSource.getTaxId() == null || "".equals( bioSource.getTaxId() ) ) {
                addMessage( BIOSOURCE_WITH_NO_TAXID, bioSource, false );
            }
        }
    }

    /**
     * Performs checks on Experiments.
     *
     * @throws IntactException Thrown if there was a Helper problem
     * @throws SQLException    Thrown if there was a DB access problem
     */
    public void checkExperiments( Collection experiments ) throws IntactException, SQLException {

        System.out.println( "Checking on Experiment (rules 8, 11, 12, 13) ..." );

        for ( Iterator it = experiments.iterator(); it.hasNext(); ) {
            Experiment exp = (Experiment) it.next();

            if ( !isOnHold( exp ) ) {

                //check 8
                if ( exp.getInteractions().size() < 1 ) {
                    //record it.....
                    addMessage( EXPERIMENT_WITHOUT_INTERACTIONS, exp, false );
                }

                //check 11
                if ( exp.getBioSource() == null ) {
                    addMessage( EXPERIMENT_WITHOUT_ORGANISM, exp, false );
                }

                //check 12
                if ( exp.getCvInteraction() == null ) {
                    addMessage( EXPERIMENT_WITHOUT_CVINTERACTION, exp, false );
                }

                //check 13
                if ( exp.getCvIdentification() == null ) {
                    addMessage( EXPERIMENT_WITHOUT_CVIDENTIFICATION, exp, false );
                }
            } // if
        } // for
    }

    /**
     * Performs checks on Experiments.
     *
     * @throws IntactException Thrown if there was a Helper problem
     * @throws SQLException    Thrown if there was a DB access problem
     */
    public void checkExperimentsPubmedIds( Collection experiments ) throws IntactException, SQLException {

        System.out.println( "Checking on Experiment and their pubmed IDs (rules 1 and 2) ..." );

        //check 1 and 2
        for ( Iterator it = experiments.iterator(); it.hasNext(); ) {
            Experiment exp = (Experiment) it.next();

            if ( !isOnHold( exp ) ) {
                int pubmedCount = 0;
                int pubmedPrimaryCount = 0;
                Collection Xrefs = exp.getXrefs();
                for ( Iterator iterator = Xrefs.iterator(); iterator.hasNext(); ) {
                    Xref xref = (Xref) iterator.next();
                    if ( pubmedDatabase.equals( xref.getCvDatabase() ) ) {
                        pubmedCount++;

                        if ( primaryReferenceXrefQualifier.equals( xref.getCvXrefQualifier() ) ) {
                            pubmedPrimaryCount++;
                        }
                    }
                }

                if ( pubmedCount == 0 ) {
                    //record it.....
                    addMessage( EXPERIMENT_WITHOUT_PUBMED, exp, false );
                }

                if ( pubmedPrimaryCount < 1 ) {
                    //record it.....
                    addMessage( EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE, exp, false );
                }
            } // if not on hold
        } // experiments
    }

    /**
     * Performs Interaction checks.
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there was a search problem
     */
    public void checkInteractions( Collection interactions ) throws IntactException, SQLException {

        System.out.println( "Checking on Interactions (rule 7) ..." );

        for ( Iterator it = interactions.iterator(); it.hasNext(); ) {
            Interaction interaction = (Interaction) it.next();

            if ( !isOnHold( interaction ) ) {

                //check 7
                if ( interaction.getExperiments().size() < 1 ) {
                    //record it.....
                    addMessage( INTERACTION_WITH_NO_EXPERIMENT, interaction, false );
                }

                //check 9
                if ( interaction.getCvInteractionType() == null ) {
                    addMessage( INTERACTION_WITH_NO_CVINTERACTIONTYPE, interaction, false );
                }

                //check 10
                // 2005-04-14: on-hold ... might be re-introduced later.
//                if( interaction.getBioSource() == null ) {
//                    addMessage( INTERACTION_WITH_NO_ORGANISM, interaction );
//                }
            } // if not on hold
        } // interactions
    }

    /**
     * Performs Interaction checks.
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there was a search problem
     */
    public void checkInteractionsBaitAndPrey( Collection interactions ) throws IntactException, SQLException {

        System.out.println( "Checking on Interactions (rule 6) ..." );

        //check 7
        for ( Iterator it = interactions.iterator(); it.hasNext(); ) {
            Interaction interaction = (Interaction) it.next();

            if ( !isOnHold( interaction ) ) {
                Collection components = interaction.getComponents();
                int preyCount = 0,
                        baitCount = 0,
                        enzymeCount = 0,
                        enzymeTargetCount = 0,
                        neutralCount = 0,
                        selfCount = 0,
                        unspecifiedCount = 0;
                float selfStoichiometry = 0;
                float neutralStoichiometry = 0;

                for ( Iterator iterator = components.iterator(); iterator.hasNext(); ) {
                    Component component = (Component) iterator.next();
                    //record it.....


                    if ( bait.equals( component.getCvComponentRole() ) ) {
                        baitCount++;
                    } else if ( prey.equals( component.getCvComponentRole() ) ) {
                        preyCount++;
                    } else if ( enzyme.equals( component.getCvComponentRole() ) ) {
                        enzymeCount++;
                    } else if ( enzymeTarget.equals( component.getCvComponentRole() ) ) {
                        enzymeTargetCount++;
                    } else if ( neutral.equals( component.getCvComponentRole() ) ) {
                        neutralCount++;
                        neutralStoichiometry = component.getStoichiometry();
                    } else if ( self.equals( component.getCvComponentRole() ) ) {
                        selfCount++;
                        selfStoichiometry = component.getStoichiometry();
                    } else if ( unspecified.equals( component.getCvComponentRole() ) ) {
                        unspecifiedCount++;
                    }
                }


                /**
                 * We have to consider Components as 3 distinct groups: bait-prey, agent-target and neutral
                 * We are not allowed to mix categories,
                 * if you have a bait you must have at least one prey
                 * if you have a enzyme you must have exactly one enzymeTarget
                 * if you have neutral component you must have at least 2
                 * if you have self you must have only one protein with Stochiometry >= 2
                 */

                int baitPrey = ( baitCount + preyCount > 0 ? 1 : 0 );
                int enzymeTarget = ( enzymeCount + enzymeTargetCount > 0 ? 1 : 0 );
                int neutral = ( neutralCount > 0 ? 1 : 0 );
                int self = ( selfCount > 0 ? 1 : 0 );
                int unspecified = ( unspecifiedCount > 0 ? 1 : 0 );

                // count the number of categories used.
                int categoryCount = baitPrey + neutral + enzymeTarget + self + unspecified;

                switch ( categoryCount ) {
                    case 0:
                        // none of those categories
                        addMessage( INTERACTION_WITH_NO_CATEGORIES, interaction, false );
                        break;

                    case 1:
                        // exactly 1 category
                        if ( baitPrey == 1 ) {
                            // bait-prey
                            if ( baitCount == 0 ) {
                                addMessage( INTERACTION_WITH_NO_BAIT, interaction, false );
                            } else if ( preyCount == 0 ) {
                                addMessage( INTERACTION_WITH_NO_PREY, interaction, false );
                            }

                        } else if ( enzymeTarget == 1 ) {
                            // enzyme - enzymeTarget
                            if ( enzymeCount == 0 ) {
                                addMessage( INTERACTION_WITH_NO_ENZYME, interaction, false );
                            } else if ( enzymeTargetCount == 0 ) {
                                addMessage( INTERACTION_WITH_NO_ENZYME_TARGET, interaction, false );
                            }

                        } else if ( self == 1 ) {
                            // it has to be > 1
                            if ( selfCount > 1 ) {
                                addMessage( INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN, interaction, false );
                            } else { // = 1
                                if ( selfStoichiometry < 1F ) {
                                    addMessage( INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2, interaction, false );
                                }
                            }

                        } else {
                            // neutral
                            if ( neutralCount == 1 ) {
                                if ( neutralStoichiometry < 2 ) {
                                    addMessage( INTERACTION_WITH_ONLY_ONE_NEUTRAL, interaction, false );
                                }
                            }
                        }
                        break;

                    default:
                        // > 1 : mixed up categories !
                        addMessage( INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES, interaction, false );

                } // switch

                // What about the unknown category ?
            } // if not on hold
        } // interactions
    }

    /**
     * Performs checks against Proteins.
     *
     * @throws IntactException Thrown if there were Helper problems
     * @throws SQLException    thrown if there were DB access problems
     */
    public void checkComponentOfInteractions( Collection interactions ) throws IntactException, SQLException {

        System.out.println( "Checking on Components (rules 5 and 6) ..." );

        //checks 5 and 6 (easier if done together)
        for ( Iterator it = interactions.iterator(); it.hasNext(); ) {
            Interaction interaction = (Interaction) it.next();

            if ( !isOnHold( interaction ) ) {
                Collection components = interaction.getComponents();
                int originalSize = components.size();
                int matchCount = 0;
                Protein proteinToCheck = null;
                if ( components.size() > 0 ) {
                    Component firstOne = (Component) components.iterator().next();

                    if ( firstOne.getInteractor() instanceof Protein ) {
                        proteinToCheck = (Protein) firstOne.getInteractor();
                        //components.remove( firstOne ); //don't check it twice!!
                    } else {
                        //not interested (for now) in Interactions that have
                        //interactors other than Proteins (for now)...
                        return;
                    }

                    for ( Iterator iter = components.iterator(); iter.hasNext(); ) {
                        Component comp = (Component) iter.next();
                        Interactor interactor = comp.getInteractor();
                        if ( interactor.equals( proteinToCheck ) ) {
                            //check it against the first one..
                            matchCount++;
                        }
                    }
                    //now compare the count and the original - if they are the
                    //same then we have found one that needs to be flagged..
                    if ( matchCount == originalSize ) {
                        addMessage( SINGLE_PROTEIN_CHECK, interaction, false );
                    }

                } else {
                    //Interaction has no Components!! This is in fact test 5...
                    addMessage( NO_PROTEIN_CHECK, interaction, false );
                }
            } // if not on hold
        } // interactions
    }

    /**
     * Checks if the protein has been annotated with the no-uniprot-update CvTopic, if so, return false,
     * otherwise true. That flag is added to a protein when created via the editor. As some protein may
     * have a UniProt ID as identity we don't want those to be overwitten.
     *
     * @param protein the protein to check
     *
     * @return false if no Annotation having CvTopic( no-uniprot-update ), otherwise true.
     */
    private boolean needsUniprotUpdate( final Protein protein ) {

        // TODO Move to IntAct model

        boolean needsUpdate = true;

        for ( Iterator iterator = protein.getAnnotations().iterator(); iterator.hasNext() && true == needsUpdate; ) {
            Annotation annotation = (Annotation) iterator.next();

            if( CvTopic.NON_UNIPROT.equals( annotation.getCvTopic().getShortLabel() ) ) {
                needsUpdate = false;
            }
        }

        return needsUpdate;
    }

    public void checkProteins( Collection proteins ) throws SQLException, IntactException {

        System.out.println( "Checking on Proteins (rules 14 and 16) ..." );

        //checks 14
        for ( Iterator it = proteins.iterator(); it.hasNext(); ) {
            Protein protein = (Protein) it.next();

            Collection xrefs = protein.getXrefs();
            int count = 0;

            if( needsUniprotUpdate( protein ) ) {

                for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
                    Xref xref = (Xref) iterator.next();

                    if ( uniprotDatabase.equals( xref.getCvDatabase() ) && identityXrefQualifier.equals( xref.getCvXrefQualifier() ) ) {
                        count++;
                    }
                } // xrefs

                if ( count == 0 ) {
                    addMessage( PROTEIN_WITH_NO_UNIPROT_IDENTITY, protein, false );
                } else if ( count > 1 ) {
                    addMessage( PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY, protein, false );
                }

            } else {

                // this is not a UniProt Protein.
                for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
                    Xref xref = (Xref) iterator.next();

                    if ( identityXrefQualifier.equals( xref.getCvXrefQualifier() ) ) {
                        count++;
                    }
                } // xrefs

                if ( count == 0 ) {
                    addMessage( NON_UNIPROT_PROTEIN_WITH_NO_UNIPROT_IDENTITY, protein, false );
                }
            }
        } // proteins
    }

    /**
     * tidies up the DB statements.
     */
    public void cleanUp() {

        try {
            if ( experimentStatement != null ) {
                experimentStatement.close();
            }
            if ( interationStatement != null ) {
                interationStatement.close();
            }
            if ( proteinStatement != null ) {
                proteinStatement.close();
            }
            if ( bioSourceStatement != null ) {
                bioSourceStatement.close();
            }
            if (rangeStatement != null) {
                rangeStatement.close();
            }
            if(annotationStatement != null){
                annotationStatement.close();
            }
            if(cvStatement != null){
                cvStatement.close();
            }
            if (interactorStatement != null){
                interactorStatement.close();
            }
            if (featureStatement != null){
                featureStatement.close();
            }
            if(exp2AnnotStatement != null){
                exp2AnnotStatement.close();
            }
            if(bs2AnnotStatement!= null){
                bs2AnnotStatement.close();
            }
            if(int2AnnotStatement!= null){
                int2AnnotStatement.close();
            }
            if(cv2AnnotStatement != null){
                cv2AnnotStatement.close();
            }
            if(feature2AnnotStatement != null){
                feature2AnnotStatement.close();
            }
        } catch ( SQLException se ) {
            System.out.println( "failed to close statement!!" );
            se.printStackTrace();
        }
    }

    //--------------------------- private methods ------------------------------------------

    /**
     * Check if an Experiment as been annotated as on-hold.
     *
     * @param experiment the experiment to be checked
     *
     * @return true if the experiment is on-hold, else false.
     */
    private boolean isOnHold( Experiment experiment ) {

        boolean onHold = false;

        for ( Iterator iterator = experiment.getAnnotations().iterator(); iterator.hasNext() && !onHold; ) {
            Annotation annotation = (Annotation) iterator.next();

            if ( onHoldCvTopic.equals( annotation.getCvTopic() ) ) {
                onHold = true;
            }
        }

        return onHold;
    }

    /**
     * Check if an interaction or one of its experiments as been annotated as on-hold.
     *
     * @param interaction the interaction to be checked
     *
     * @return true if the interaction or one of its experiments is on-hold, else false.
     */
    private boolean isOnHold( Interaction interaction ) {

        boolean onHold = false;

        for ( Iterator iterator = interaction.getAnnotations().iterator(); iterator.hasNext() && !onHold; ) {
            Annotation annotation = (Annotation) iterator.next();

            if ( onHoldCvTopic.equals( annotation.getCvTopic() ) ) {
                onHold = true;
            }
        }

        if ( !onHold ) {
            // check experiemnts
            for ( Iterator iterator = interaction.getExperiments().iterator(); iterator.hasNext() && !onHold; ) {
                Experiment experiment = (Experiment) iterator.next();
                onHold = isOnHold( experiment );
            }
        }

        return onHold;
    }

    /**
     * Helper method to obtain userstamp info from a given record, and then if it has any to append the details to a
     * result buffer.
     *
     * @param topic the type of error we have dicovered for the given AnnotatedObject.
     * @param obj   The Intact object that user info is required for.
     *
     * @throws SQLException thrown if there were DB problems
     */
    private void addMessage( ReportTopic topic, BasicObject obj, boolean isCheckReviewed/*AnnotatedObject obj*/ ) throws SQLException, IntactException {

        String user = null;
        Timestamp date = null;
        ResultSet results = null;

        if(isCheckReviewed==false){
            if ( obj instanceof Experiment ) {
                experimentStatement.setString( 1, obj.getAc() );
                results = experimentStatement.executeQuery();

            } else if ( obj instanceof Interaction ) {
                interationStatement.setString( 1, obj.getAc() );
                results = interationStatement.executeQuery();

            } else if ( obj instanceof Protein ) {
                proteinStatement.setString( 1, obj.getAc() );
                results = proteinStatement.executeQuery();

            } else if ( obj instanceof BioSource ) {
                bioSourceStatement.setString( 1, obj.getAc() );
                results = bioSourceStatement.executeQuery();
            }else if (obj instanceof Annotation ) {
                annotationStatement.setString(1,obj.getAc());
                results = annotationStatement.executeQuery();
            }


            if ( results.next() ) {
                user = results.getString( "userstamp" );
                date = results.getTimestamp( "timestamp" );
            }
            results.close();
        }
        else{
            HashMap map = retrieveTimeUserFromAudit(obj.getAc());
            user = (String) map.get("userstamp");
            date =  (Timestamp) map.get("timestamp");
        }


        String userMessageReport="";
        String adminMessageReport="";
        // Build users report
        if(obj instanceof Annotation){
            Annotation annotation = (Annotation) obj;
            userMessageReport = "AC: " + annotation.getAc() +
                                   "\t URL: " + annotation.getAnnotationText() +
                                   "\t When: " + date;
            adminMessageReport = "AC: " + obj.getAc() +
                                 "\t URL: " + annotation.getAnnotationText() +
                                 "\t User: " + user +
                                 "\t When: " + date;

        }
        if(obj instanceof AnnotatedObject){
            AnnotatedObject annotObj = (AnnotatedObject) obj;
            userMessageReport = "AC: " + annotObj.getAc() +
                                "\t Shortlabel: " + annotObj.getShortLabel() +
                                "\t When: " + date;
            adminMessageReport = "AC: " + annotObj.getAc() +
                                 "\t Shortlabel: " + annotObj.getShortLabel() +
                                 "\t User: " + user +
                                 "\t When: " + date;


        }

        if ( user != null && !( user.trim().length() == 0 ) ) {

            // add new message to the user
            Map userReport = (Map) allUsersReport.get( user );
            if ( userReport == null ) {
                userReport = new HashMap();
            }

            Collection topicMessages = (Collection) userReport.get( topic );
            if ( topicMessages == null ) {
                topicMessages = new ArrayList();

                // add the messages to the topic
                userReport.put( topic, topicMessages );
            }

            // add the message to the topic
            topicMessages.add( userMessageReport );

            // add the user's messages
            allUsersReport.put( user, userReport );
        } else {

            System.err.println( "No user found for object: " + userMessageReport );
        }


      /*  // build admin admin report
        String adminMessageReport = "AC: " + obj.getAc() +
                                    "\t Shortlabel: " + obj.getShortLabel() +
                                    "\t User: " + user +
                                    "\t When: " + date;
        */
        Collection topicMessages = (Collection) adminReport.get( topic );
        if ( topicMessages == null ) {
            topicMessages = new ArrayList();

            // add the messages to the topic
            adminReport.put( topic, topicMessages );
        }

        // add the message to the topic
        topicMessages.add( adminMessageReport );
    }


    /**
     * post emails to the curators (their individual errors) and to the administrator (global list of errors)
     *
     * @throws MessagingException
     */
    public void postEmails() throws MessagingException {

        MailSender mailer = new MailSender();

        // send individual mail to curators
        for ( Iterator iterator = allUsersReport.keySet().iterator(); iterator.hasNext(); ) {
            String user = (String) iterator.next();

            Map reportMessages = (Map) allUsersReport.get( user );
            StringBuffer fullReport = new StringBuffer( 256 );
            int errorCount = 0;

            for ( Iterator iterator1 = reportMessages.keySet().iterator(); iterator1.hasNext(); ) {
                ReportTopic topic = (ReportTopic) iterator1.next();

                fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
                Collection messages = (Collection) reportMessages.get( topic );

                // write individual messages of that topic.
                for ( Iterator iterator2 = messages.iterator(); iterator2.hasNext(); ) {
                    String message = (String) iterator2.next();

                    fullReport.append( message ).append( NEW_LINE );
                    errorCount++;
                } // messages in the topic

                fullReport.append( NEW_LINE );
            } // topics

            // don't send mail to curator if no errors
            if ( errorCount > 0 ) {

                System.out.println( "Send individual report to " + user + "( " + user + ")" );
                String email = (String) usersEmails.get( user.toLowerCase() );

                if ( email != null ) {
                    String[] recipients = new String[ 1 ];
                    recipients[ 0 ] = email;

                    // send mail
                    mailer.postMail( recipients,
                                     "SANITY CHECK - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
                                     fullReport.toString(),
                                     "cleroy@ebi.ac.uk" );
                    System.out.println("FULL REPORT for User : " + fullReport.toString());
                } else {

                    // keep track of unknown users
                    unknownUsers.add( user.toLowerCase() );

                    System.err.println( "Could not find that user, here is the content of his report:" );
                    System.err.println( fullReport.toString() );

                }
            }

        } // users

        // send summary of all individual mail to admin
        StringBuffer fullReport = new StringBuffer( 256 );

        try {
            fullReport.append( "Instance name: " + helper.getDbName() );
            fullReport.append( NEW_LINE ).append( NEW_LINE );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        // generate error message is some users have not been found
        if ( !unknownUsers.isEmpty() ) {
            if ( unknownUsers.size() == 1 ) {

                fullReport.append( "Could not find an email adress for user: " + unknownUsers.iterator().next() );

            } else {
                // more than one, then generate a list
                fullReport.append( "Could not find email adress for the following list of users: " ).append( NEW_LINE );
                for ( Iterator iterator = unknownUsers.iterator(); iterator.hasNext(); ) {
                    String user = (String) iterator.next();
                    fullReport.append( user ).append( NEW_LINE );
                }
            }

            fullReport.append( NEW_LINE ).append( NEW_LINE );
            System.out.println("FULL REPORT for User : " + fullReport.toString());
        }

        // generate full report
        int errorCount = 0;
        if ( !adminReport.isEmpty() ) {

            for ( Iterator iterator = adminReport.keySet().iterator(); iterator.hasNext(); ) {
                ReportTopic topic = (ReportTopic) iterator.next();

                Collection messages = (Collection) adminReport.get( topic );
                fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
                for ( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
                    String message = (String) iterator1.next();
                    fullReport.append( message ).append( NEW_LINE );
                    errorCount++;
                } // messages

                fullReport.append( NEW_LINE );
            } // topics

        } else {

            fullReport.append( "No curation error to report." );

        }

        // Send mail to the administrator
        String[] recipients = new String[ adminsEmails.size() ];
        int i = 0;
        for ( Iterator iterator = adminsEmails.iterator(); iterator.hasNext(); ) {
            String email = (String) iterator.next();
            recipients[ i++ ] = email;
        }

        // always send mail to admin, even if no errors
        mailer.postMail( recipients,
                         "SANITY CHECK (ADMIN) - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
                         fullReport.toString(),
                         "cleroy@ebi.ac.uk" );
        System.out.println("FULL REPORT for Admin : " + fullReport.toString());

    }

    private String getFullReportOutput() {

        StringBuffer fullReport = new StringBuffer( 256 );

        for ( Iterator iterator = adminReport.keySet().iterator(); iterator.hasNext(); ) {
            ReportTopic topic = (ReportTopic) iterator.next();

            Collection messages = (Collection) adminReport.get( topic );
            fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
            for ( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
                String message = (String) iterator1.next();
                fullReport.append( message ).append( NEW_LINE );
            } // messages

            fullReport.append( NEW_LINE );
        } // topics

        return fullReport.toString();
    }

    public static void main( String[] args ) throws Exception {

        IntactHelper helper = null;
        SanityChecker checker = null;

        try {
            helper = new IntactHelper();

            // Install termination hook, that allows to close cleanly the db connexion if the user hits CTRL+C.
            Runtime.getRuntime().addShutdownHook( new DatabaseConnexionShutdownHook( helper ) );

            System.out.println( "Helper created (User: " + helper.getDbUserName() + " " +
                                "Database: " + helper.getDbName() + ")" );
            System.out.print( "checking data integrity..." );
            checker = new SanityChecker( helper );

            long start = System.currentTimeMillis();
            //do checks here.....


            //get the Annotation corresponding to url (i.e. having topic_ac equal to EBI-18
            Collection annotations = helper.search( Annotation.class.getName(), "topic_ac", "EBI-18" );
            System.out.println(annotations.size() + "annotation loaded.");
            checker.checkURL(annotations);
            annotations = null;
            Runtime.getRuntime().gc();


            //get the Experiment and Interaction info from the DB for later use.
            Collection bioSources = helper.search( BioSource.class.getName(), "ac", "*" );
            System.out.println( bioSources.size() + " biosources loaded." );
            checker.checkBioSource( bioSources );
            checker.checkNewt(bioSources);
            bioSources = null;
            Runtime.getRuntime().gc(); // free memory before to carry on.

            //get the Experiment and Interaction info from the DB for later use.
            Collection experiments = helper.search( Experiment.class.getName(), "ac", "*" );
            System.out.println( experiments.size() + " experiments loaded." );
            checker.checkExperiments( experiments );
            checker.checkReviewed(experiments);
            checker.checkExperimentsPubmedIds( experiments );
            experiments = null;
            Runtime.getRuntime().gc(); // free memory before to carry on.
             
            //System.out.println("Something is working");
            Collection interactions = helper.search( Interaction.class.getName(), "ac", "EBI-1*" );
            System.out.println( interactions.size() + " interactions loaded." );
            //System.out.println("After, helper.search() I am going to sleep 30 seconds");
            //Thread.sleep(30000);
            //checker.catherine(interactions);
            checker.checkInteractions( interactions );
            checker.checkInteractionsBaitAndPrey( interactions );
            checker.checkComponentOfInteractions( interactions );
            interactions = null;
            Runtime.getRuntime().gc(); // free memory before to carry on.

            Collection proteins = helper.search( Protein.class.getName(), "ac", "*" );
            System.out.println( proteins.size() + " proteins loaded." );
            checker.checkProteins( proteins );

            checker.checkCrc64(proteins);

            long end = System.currentTimeMillis();
            long total = end - start;
            System.out.println( "....Done. " );
            System.out.println();
            System.out.println( "Total time to perform checks: " + total / 1000 + "s" );

            // try to send emails
            try {
                checker.postEmails();

            } catch ( MessagingException e ) {
                // scould not send emails, then how error ...
                e.printStackTrace();

                // ... and save the full report on hard disk instead.
                String filename = null;
                if ( args.length != 1 ) {

                    filename = "sanityCheck-" + TIME + ".txt";

                    System.out.println( "Usage: javaRun.sh SanityChecker <filename>" );
                    System.out.println( "<filename> automatically set to: " + filename );
                } else {
                    filename = args[ 0 ];
                }

                File file = new File( filename );
                System.out.println( "results filename: " + filename );
                System.out.println( "Output will be written in: " + file.getAbsolutePath() );
                PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( file ) ) );

                out.println( "Checks against Database " + helper.getDbName() );
                out.println( "----------------------------------" );
                out.println();

                out.write( checker.getFullReportOutput() );
                out.flush();
                out.close();
            }

        } catch ( IntactException e ) {

            e.printStackTrace();
            if ( e.getRootCause() != null ) {
                e.getRootCause().printStackTrace();
            }
            System.exit( 1 );
        } catch ( SQLException sqe ) {

            System.out.println( "DB error!" );
            sqe.printStackTrace();
            System.exit( 1 );
        } catch ( OutOfMemoryError aome ) {

            aome.printStackTrace();
            System.err.println( "" );
            System.err.println( "SanityChecker ran out of memory." );
            System.err.println( "Please run it again and change the JVM configuration." );
            System.err.println( "Here are some the options: http://java.sun.com/docs/hotspot/VMOptions.html" );
            System.err.println( "Hint: You can use -Xms -Xmx to specify respectively the minimum and maximum" );
            System.err.println( "      amount of memory (heap size) that the JVM is allowed to allocate." );
            System.err.println( "      eg. java -Xms128m -Xmx640m <className>" );

            System.exit( 1 );
        } catch ( Exception e ) {

            e.printStackTrace();
            System.exit( 1 );
        } finally {

            if ( checker != null ) {
                checker.cleanUp();
            }
        }
        System.exit( 0 );
    }



   /**
     * This method check whether the url contained in the annotation are valide. To do so, it uses the HttpClient
     * library (commons-httpclient-3.0-rc2.ja, http://jakarta.apache.org/commons/httpclient). This library depends on
     * two other libraries ( commons-codec-1.4-dev.jar and junit.jar).
     * @param annotations : collection of Annotation having corresponding to URL (i.e. having for topic_ac : EBI-18)
     * @throws SQLException
     */

    public void checkURL(Collection annotations) throws SQLException {//Collection annotations){

        for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
            Annotation annotation = (Annotation) iterator.next();
            String urlString=annotation.getAnnotationText();
            HttpURL httpUrl= null;


            //Creating the httpUrl object corresponding to the the url string contained in the annotation
            try {
                httpUrl = new HttpURL(urlString);
            } catch (URIException e) {
               // e.printStackTrace();
                retrieveObject(annotation);
            }

            // If httpUrl is not null, get the method corresponding to the uri, execute if and analyze the
            // status code to know whether the url is valide or not.
            if(httpUrl!=null){
                HttpClient client = new HttpClient();
                HttpMethod method=null;
                try{
                    method = new GetMethod(urlString);
                }catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                    retrieveObject(annotation);
                }
                int statusCode = -1;
                if(method!=null){
                    try {
                        statusCode = client.executeMethod(method);
                    } catch (IOException e) {
                        //e.printStackTrace();
                        retrieveObject(annotation);
                    }

                    if(statusCode!=-1){
                        String statusText=HttpStatus.getStatusText(statusCode);
                        if(statusCode >= 300 && statusCode <400) {
                            retrieveObject(annotation);
                        }
                        else
                            if(statusCode >= 400 && statusCode<600){
                                retrieveObject(annotation);
                            }
                        }
                    }
                }
            }

    }

   /**
    * This method execute several statements to retrieve the object ac that are associated with the annotation given
    * in parameter. Those object ac can correspond to Interactor, Experiment, BioSource, CvObject or Feature. The ResultSet
    * containing this or those ac is givent to the AnnotationMessage method which build the message.
    *
    * @param annotation
    */

    public void retrieveObject(Annotation annotation) {

        ResultSet resultsExp = null;
        ResultSet resultsBs = null;
        ResultSet resultsInt = null;
        ResultSet resultsCv = null;
        ResultSet resultsFeature = null;

        try {
            exp2AnnotStatement.setString(1,annotation.getAc());
            bs2AnnotStatement.setString(1,annotation.getAc());
            int2AnnotStatement.setString(1,annotation.getAc());
            cv2AnnotStatement.setString(1,annotation.getAc());
            feature2AnnotStatement.setString(1,annotation.getAc());
        } catch (SQLException e) {
        //    e.printStackTrace();
        }


        try {
            resultsExp = exp2AnnotStatement.executeQuery();
        } catch (SQLException e) {
        //    e.printStackTrace();
        }
        try {
            resultsBs =  bs2AnnotStatement.executeQuery();
        } catch (SQLException e) {
        //    e.printStackTrace();
        }
        try {
            resultsInt = int2AnnotStatement.executeQuery();
        } catch (SQLException e) {
        //    e.printStackTrace();
        }
        try {
            resultsCv = cv2AnnotStatement.executeQuery();
        } catch (SQLException e) {
        //    e.printStackTrace();
        }
        try {
            resultsFeature = feature2AnnotStatement.executeQuery();
        } catch (SQLException e) {
        //    e.printStackTrace();
        }

        if(resultsExp != null ){
            annotationMessage(resultsExp,Experiment.class,annotation);
        }
        if(resultsBs != null ){
            annotationMessage(resultsBs,BioSource.class,annotation);
        }
        if(resultsInt != null ){
            annotationMessage(resultsInt,Interactor.class,annotation);
        }
        if(resultsCv != null){
            annotationMessage(resultsCv,CvObject.class,annotation);
        }
        if(resultsFeature != null ){
            annotationMessage(resultsFeature,Feature.class,annotation);
        }
    }

    /**
     *   This method create the adminReportMessage and the userReportMessage for a not valide URI.
     *
     * @param results ResultSet containing the ac of object annotated by the annotation given in parameter
     * @param clazz   The class of the object annotated by the annotation (can be : Interactor, Experiment, BioSource, CvObject or Feature)
     * @param annotation
     */
    public void annotationMessage (ResultSet results, Class clazz, Annotation annotation){

        String relatedObjectAcName = null;
        String user = null;
        Timestamp date = null;
        PreparedStatement statement = null;
        String userMessageReport = null;
        String adminMessageReport = null;

        if(Experiment.class.equals(clazz)){
            relatedObjectAcName="experiment_ac";
            statement=experimentStatement;
        } else if(BioSource.class.equals(clazz)){
            relatedObjectAcName="biosource_ac";
            statement=bioSourceStatement;
        } else if(Interactor.class.equals(clazz)){
            relatedObjectAcName="interactor_ac";
            statement=interactorStatement;
        } else if (Feature.class.equals(clazz)){
            relatedObjectAcName="feature_ac";
            statement=featureStatement;
        } else if (CvObject.class.equals(clazz)){
            relatedObjectAcName="cvobject_ac";
            statement=cvStatement;
        }

        try{
            if(results.next()){
                String relatedObjectAc=null;
                //if(results.next()) {
                relatedObjectAc = results.getString( relatedObjectAcName );

                //}
                //else System.out.println("For annotation "+annotation.getAc() + "next() didn't go well");
                System.out.println("relatedObjectAc : " + relatedObjectAc);
                statement.setString(1,relatedObjectAc);
                ResultSet relatedObjectResults = statement.executeQuery();
                if( relatedObjectResults.next()){
                    user = relatedObjectResults.getString( "userstamp" );
                    date = relatedObjectResults.getTimestamp( "timestamp" );
                    System.out.println("user : "+ user);
                }
                relatedObjectResults.close();

                userMessageReport =     "Annotation Description (URI): " + annotation.getAnnotationText()+
                                        "\t Annotation AC: " + annotation.getAc()+
                                        "\t This uri is used to annotate the following object :" + relatedObjectAcName + ": " + relatedObjectAc +
                                        "\t When: " + date +
                                        "\n";

                adminMessageReport =     "Annotation Description (URI): " + annotation.getAnnotationText()+
                                         "\t Annotation AC: " + annotation.getAc()+
                                         "\t This uri is used to annotate the following object :" + relatedObjectAcName + ": " + relatedObjectAc +
                                         "\t User: " + user +
                                         "\t When: " + date +
                                         "\n";


                if ( user != null && !( user.trim().length() == 0 ) ) {

                    // add new message to the user
                    Map userReport = (Map) allUsersReport.get( user );
                    if ( userReport == null ) {
                        userReport = new HashMap();
                    }

                    Collection topicMessages = (Collection) userReport.get( URL_NOT_VALID );
                    if ( topicMessages == null ) {
                        topicMessages = new ArrayList();

                        // add the messages to the topic
                        userReport.put( URL_NOT_VALID, topicMessages );
                    }

                    // add the message to the topic
                    topicMessages.add( userMessageReport );

                    // add the user's messages
                    allUsersReport.put( user, userReport );
                } else {

                    System.err.println( "No user found for object: " + userMessageReport );
                }


                Collection topicMessages = (Collection) adminReport.get( URL_NOT_VALID );
                if ( topicMessages == null ) {
                    topicMessages = new ArrayList();

                    // add the messages to the topic
                    adminReport.put( URL_NOT_VALID, topicMessages );
                }

                // add the message to the topic
                topicMessages.add( adminMessageReport );

            }
        }catch(SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void checkCrc64(Collection proteins) throws SQLException, IntactException {
        for (Iterator iterator = proteins.iterator(); iterator.hasNext();) {
            Protein protein = (Protein) iterator.next();

            String crc64Stored=protein.getCrc64();
            String sequence=protein.getSequence();
            if(sequence!=null && false==sequence.trim().equals("")){
                String crc64Calculated=Crc64.getCrc64(sequence);
                if(!crc64Calculated.equals(crc64Stored)){
                    addMessage(PROTEIN_WITH_WRONG_CRC64, protein, false);
                }
            }
            else{
                if(crc64Stored!=null){
                    addMessage(PROTEIN_WITHOUT_A_SEQUENCE_BUT_WITH_AN_CRC64,protein, false);
                    System.out.println("Sequence"+sequence);
                }
            }
        }
    }

    public void checkReviewed(Collection experiments) throws SQLException, IntactException {

        for (Iterator iterator = experiments.iterator(); iterator.hasNext();) {
            Experiment experiment =  (Experiment) iterator.next();
            Collection annotations=experiment.getAnnotations();
            for (Iterator iterator1 = annotations.iterator(); iterator1.hasNext();) {
                Annotation annotation =  (Annotation) iterator1.next();
                CvTopic cvTopic = annotation.getCvTopic();
                if("to-be-reviewed".equals(cvTopic.getShortLabel())){
                    addMessage( EXPERIMENT_TO_BE_REVIEWED,experiment, true);
                }
            }
        }
    }
    /**
     * This method check that all biosource have at least one xref with a "Newt" taxid AND
     * with the xref qualifier equal to "identity"
     * It will not send any message when the taxid is < 0 because those are particular biosources
     * to tell that those experiments were done "in vitro" or that it was a "chemical synthesis"
     * (EBI-1318 or EBI-350168)
     *
     * @param bioSources A collection containing bioSources
     * @throws IntactException
     * @throws SQLException
     */
    public void checkNewt(Collection bioSources) throws IntactException, SQLException {

        System.out.println("Checking bioSource (existing newt identity xref) :");

        for (Iterator iterator = bioSources.iterator(); iterator.hasNext();) {
            boolean hasNewtXref=false;
            BioSource bioSource = (BioSource) iterator.next();
            Collection xrefs = bioSource.getXrefs();
            for (Iterator iterator1 = xrefs.iterator(); iterator1.hasNext();) {
                Xref xref =  (Xref) iterator1.next();
                String xrefQualifier = xref.getCvXrefQualifier().getShortLabel();
                CvDatabase cvDatabase= xref.getCvDatabase();
                String cvDatabaseAc =cvDatabase.getAc();

                if(cvDatabaseAc!=null){
                    Collection cvObjects = helper.search(CvObject.class.getName(), "shortlabel", "newt");
                    for (Iterator iterator2 = cvObjects.iterator(); iterator2.hasNext();) {
                        CvObject cvObject =  (CvObject) iterator2.next();
                        if(cvDatabaseAc.equals(cvObject.getAc()) && "identity".equals(xrefQualifier)){
                            hasNewtXref=true;
                        }
                    }
                }
            }
            if(false==hasNewtXref){
                int taxid = Integer.parseInt(bioSource.getTaxId());
                if(taxid>=1){
                    addMessage(BIOSOURCE_WITH_NO_NEWT_XREF,bioSource, false);
                }
            }
        }
    }

    public void catherine( Collection interactions ) throws IntactException, SQLException, InterruptedException {

        for ( Iterator it = interactions.iterator(); it.hasNext(); ) {
            Interaction interaction = (Interaction) it.next();
            if ( !isOnHold( interaction ) ) {
                Collection components = interaction.getComponents();
                for ( Iterator itComp = components.iterator();itComp.hasNext();){
                    Component component = (Component) itComp.next();


                    if ( component.getInteractor() instanceof Protein) {
                        //search for the sequence of the protein search if the
                        //componant is linked to a feature with a range and
                        //if the sequence are the same
                        //System.out.println("Component number " +j + " is a protein");

                        Protein protein =(Protein)component.getInteractor();
                        String protSeq = protein.getSequence();

                        System.out.println("Prot Ac : "+ protein.getAc());
                        //System.out.println("Seq Prot : "+ protSeq );

                        // todo ""
                        if(protSeq!=null && false==protSeq.trim().equals("")){ // the protein seq is not null
                            Collection features = component.getBindingDomains();

                            for( Iterator itFeat = features.iterator();itFeat.hasNext();){
                                Feature feature = (Feature)itFeat.next();
                                if(feature!=null){
                                    Collection ranges = feature.getRanges();

                                    for(Iterator itRange = ranges.iterator();itRange.hasNext();){
                                        Range range = (Range)itRange.next();
                                        String rangeSeq = range.getSequence();
                                        int fromStart = range.getFromIntervalStart();
                                        //int maxSequenceSize=Range.getMaxSequenceSize();
                                        //     if(fromStart!=0 && false==("M".equals(protein.getSequence().substring(0,1)))){
                                        //         fromStart=fromStart-1;
                                        //maxSequenceSize=maxSequenceSize-1;
                                        //     }

                                        if(rangeSeq!=null && false==rangeSeq.trim().equals("") ){ //range got a sequence
                                            //verify if for intact the first letter in a sequence is the
                                            //letter number 0 or it it is the number 1 , do the same for
                                            // string.

                                            String protSubSeq=null;
                                            CvFuzzyType fuzzyType = range.getFromCvFuzzyType();
                                            if (fuzzyType!=null){
                                                //System.out.println("Cv Fuzzy type = "+fuzzyType.getShortLabel());
                                                String fuzzyTypeLabel = fuzzyType.getShortLabel();
                                                if(CvFuzzyType.C_TERMINAL.equals(fuzzyTypeLabel)){
                                                    if(protSeq.length()<Range.getMaxSequenceSize()){
                                                        protSubSeq=protSeq.substring(0, protSeq.length());
                                                    }
                                                    else{
                                                        protSubSeq=protSeq.substring(protSeq.length()-Range.getMaxSequenceSize(),protSeq.length());
                                                    }
                                                }else{
                                                    if(CvFuzzyType.N_TERMINAL.equals(fuzzyTypeLabel)){
                                                        if(protSeq.length()<Range.getMaxSequenceSize()){
                                                            protSubSeq=protSeq.substring(0,protSeq.length());
                                                        }
                                                        else{
                                                            protSubSeq=protSeq.substring(0,Range.getMaxSequenceSize());
                                                        }
                                                    }
                                                    else{
                                                        if(protSeq.length()<fromStart+Range.getMaxSequenceSize()){
                                                            //if(protSeq.length()<range.getFromIntervalStart()+Range.getMaxSequenceSize()){
                                                            //protSubSeq=protSeq.substring(range.getFromIntervalStart(), protSeq.length());
                                                            protSubSeq=protSeq.substring(fromStart, protSeq.length());

                                                        }
                                                        else{
                                                            //protSubSeq=protSeq.substring(range.getFromIntervalStart(), range.getFromIntervalStart()+Range.getMaxSequenceSize());
                                                            protSubSeq=protSeq.substring(fromStart, fromStart+Range.getMaxSequenceSize());
                                                            //System.out.println("Taking subseq form : "+fromStart+" to "+ fromStart+Range.getMaxSequenceSize());
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                if(protSeq.length()<fromStart+Range.getMaxSequenceSize()){
                                                    protSubSeq=protSeq.substring(fromStart, protSeq.length());
                                                }
                                                else{
                                                    //System.out.println("Taking subseq form : "+fromStart+" to "+ fromStart+Range.getMaxSequenceSize());
                                                    protSubSeq=protSeq.substring(fromStart, fromStart+Range.getMaxSequenceSize());
                                                }
                                            }

                                            //System.out.println("Sub seq of prot "+j+" is "+protSubSeq);

                                            if(!rangeSeq.equals(protSubSeq)){
                                                //error : the sequence in the range and the subsequence of the
                                                // protein are not equal
                                                System.out.println("NOT EQUAL");
                                                addRangeMessage( PROTEIN_SEQUENCE_AND_RANGE_SEQUENCE_NOT_EQUAL, interaction, protein,range );
                                                System.out.println(rangeSeq);
                                                System.out.println(protSubSeq);
                                                System.out.println();

                                                String rangeSeqM = "M"+rangeSeq.substring(0,rangeSeq.length()-1);


                                                String protSubSeqM = "M"+protSubSeq.substring(0,protSubSeq.length()-1);
                                                if(rangeSeqM.equals(protSubSeq)){
                                                    System.out.println("The seq were not equal but, adding M in front of rangeSeq...");
                                                }
                                                else if(protSubSeqM.equals(rangeSeq)){
                                                    System.out.println("The seq were not equal but, adding M in front of protSeq...");

                                                }
                                                rangeSeqM=null;
                                                protSubSeqM=null;
                                            }
                                            else{
                                                System.out.println("Both sequence are equals");

                                            }
                                            rangeSeq=null;
                                            protSubSeq=null;
                                        }
                                        /* else{  //range does not have a seq but protein got one
                                        System.out.println("To protein " + j + " corresponds a range with no sequence.");

                                        addRangeMessage (RANGE_HAS_NO_SEQUENCE_WHEN_PROTEIN_HAS_A_SEQUENCE, interaction, protein, range);
                                        }*/
                                    }

                                }
                            }
                       /* }else{ //the protein seq is null
                            Collection features = component.getBindingDomains();
                            for (Iterator iterator = features.iterator(); iterator.hasNext();) {
                                Feature feature =(Feature)  iterator.next();
                                Collection ranges = feature.getRanges();

                                for (Iterator iterator1 = ranges.iterator(); iterator1.hasNext();) {
                                    Range range = (Range) iterator1.next();

                                    if(range.getSequence()!=null){
                                        //THE RANGE HAS A SEQUENCE BUT THE PROTEIN HAS NO SEQUENCE
                                        addRangeMessage (RANGE_HAS_A_SEQUENCE_BUT_THE_PROTEIN_DOES_NOT_HAVE_ONE,interaction, protein, range);
                                    }

                                    CvFuzzyType fuzzyType = range.getFromCvFuzzyType();
                                    String fuzzyTypeLabel;
                                    if(fuzzyType!=null)
                                        fuzzyTypeLabel=null;
                                    else
                                        fuzzyTypeLabel = fuzzyType.getShortLabel();
                                    if(false == (CvFuzzyType.N_TERMINAL.equals(fuzzyTypeLabel) ||
                                            CvFuzzyType.C_TERMINAL.equals(fuzzyTypeLabel) ||
                                            CvFuzzyType.UNDETERMINED.equals(fuzzyTypeLabel))){
                                        addRangeMessage(FUZZY_TYPE_NOT_APPROPRIATE,interaction,protein,range);
                                    }
                                    else if (range.getFromIntervalStart()!=0 || range.getFromIntervalEnd()!=0 || range.getToIntervalEnd()!=0 || range.getToIntervalStart()!=0){
                                        addRangeMessage(INTERVAL_VALUE_NOT_APPROPRIATE, interaction,protein,range);
                                    }


                                }
                            } */
                        }
                    }
                    System.out.println();
                    System.out.println();
                } //if interactor is a protein
            } // if not on hold

        } // interactions
    }





    private void addRangeMessage( ReportTopic topic, Interaction interaction, Protein protein, Range range ) throws SQLException {

            String interactionUser = null;
            Timestamp interactionDate = null;

            String proteinUser = null;
            Timestamp proteinDate = null;

            String rangeUser=null;
            Timestamp rangeDate=null;

            ResultSet interactionResults = null;
            ResultSet proteinResults=null;
            ResultSet rangeResults=null;


            interationStatement.setString( 1, interaction.getAc() );
            interactionResults = interationStatement.executeQuery();

            proteinStatement.setString( 1, protein.getAc() );
            proteinResults = proteinStatement.executeQuery();

            rangeStatement.setString( 1, range.getAc());
            rangeResults = rangeStatement.executeQuery();

            if ( interactionResults.next() ) {
                interactionUser = interactionResults.getString( "userstamp" );
                interactionDate = interactionResults.getTimestamp( "timestamp" );
            }
            interactionResults.close();

            if ( proteinResults.next() ) {
                proteinUser = proteinResults.getString( "userstamp" );
                proteinDate = proteinResults.getTimestamp( "timestamp" );
            }
            proteinResults.close();

            if ( rangeResults.next() ) {
                rangeUser = rangeResults.getString( "userstamp" );
                rangeDate = rangeResults.getTimestamp( "timestamp" );
            }
            rangeResults.close();

            // Build users report
            String userMessageReport = "INTERACTION AC: " + interaction.getAc() +
                                       "\t Shortlabel: " + interaction.getShortLabel() +
                                       "\t When: " + interactionDate +
                                       "\n PROTEIN AC: " + protein.getAc()+
                                       "\t Shortlabel: " + protein.getShortLabel()+
                                       "\t When :" + proteinDate +
                                       "\n RANGE AC: " + range.getAc()+
                                       "\t When :" + rangeDate;

            if ( rangeUser != null && !( rangeUser.trim().length() == 0 ) ) {

                // add new message to the user
                Map userReport = (Map) allUsersReport.get( rangeUser );
                if ( userReport == null ) {
                    userReport = new HashMap();
                }

                Collection topicMessages = (Collection) userReport.get( topic );
                if ( topicMessages == null ) {
                    topicMessages = new ArrayList();

                    // add the messages to the topic
                    userReport.put( topic, topicMessages );
                }

                // add the message to the topic
                topicMessages.add( userMessageReport );

                // add the user's messages
                allUsersReport.put( rangeUser, userReport );
            } else {

                System.err.println( "No user found for object: " + userMessageReport );
            }



            // build admin admin report
            String adminMessageReport = "INTERACTION AC: " + interaction.getAc() +
                                       "\tShortlabel: " + interaction.getShortLabel() +
                                       "\tWhen: " + interactionDate +
                                       "\nPROTEIN AC: " + protein.getAc()+
                                       "\tShortlabel: " + protein.getShortLabel()+
                                       "\tWhen :" + proteinDate +
                                       "\nRANGE AC: " + range.getAc()+
                                       "\tWhen :" + rangeDate+
                                       "\n";

            Collection topicMessages = (Collection) adminReport.get( topic );
            if ( topicMessages == null ) {
                topicMessages = new ArrayList();

                // add the messages to the topic
                adminReport.put( topic, topicMessages );
            }

            // add the message to the topic
            topicMessages.add( adminMessageReport );
        }

    public HashMap retrieveTimeUserFromAudit(String ac) throws IntactException, SQLException {
        String userstamp="";
        Timestamp timestamp=null;
        IntactHelper helper =  new IntactHelper();
        Connection conn = helper.getJDBCConnection();

        ResultSet results=null;
        PreparedStatement getCuratorName;

        getCuratorName = conn.prepareStatement("select userstamp, timestamp from ia_experiment_audit where ac=? order by updated");
        getCuratorName.setString( 1, ac );
        results = getCuratorName.executeQuery();
        if(results.next()){
            userstamp = results.getString( "userstamp" );
            timestamp = results.getTimestamp( "timestamp" );
        }
        HashMap map = new HashMap();
        map.put("userstamp",userstamp);
        System.out.println("The name found is : "+userstamp);
        map.put("timestamp", timestamp);
        return map;
    }



}

