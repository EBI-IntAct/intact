package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import javax.mail.MessagingException;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class to perform some sanity checks on the DB. Mainly for use by curators.
 * A allUsersReport of anomolies detected (as per the list of checks) is sent via email to
 * the appropriate people.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SanityChecker {

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    public static final String TIME;

    /**
     * Mapping user -> mail adress
     * Map( lowercase(username), email )
     */
    private static Map usersEmails = new HashMap();

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
        if (props != null) {
            int index;
            for( Iterator iterator = props.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();



                index = key.indexOf( CURATOR );
                if( index != -1 ) {
                    String userstamp = key.substring( index + CURATOR.length() );
                    String curatorMail = (String) props.get( key );
//                    System.out.println( "Curator: " + userstamp + " ---> " + curatorMail );
                    usersEmails.put( userstamp, curatorMail );
                } else {
                    // is it an admin then ?
                    index = key.indexOf( "admin." );
                    if( index != -1 ) {
                        // store it
                        String adminMail = (String) props.get( key );
//                        System.out.println( "Admin: " + adminMail );
                        adminsEmails.add( adminMail );
                    }
                }
            } // keys
        } else {

            System.err.println ("Unable to open the properties file: " + SANITY_CHECK_CONFIG_FILE );
        }

        // format the current time
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd@HH.mm" );
        TIME = formatter.format( date );
    }


    /**
     * Service termination hook (gets called when the JVM terminates from a signal).
     * eg.
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
            if( helper != null ) {
                try {
                    helper.closeStore();
                    System.out.println( "Connexion to the database closed." );
                } catch( IntactException e ) {
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

            if( title == null ) {
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
            for( int i = 0; i < title.length() ; i++ ) {
                sb.append( '-' );
            }

            return sb.toString();
        }
    }


    /**
     * Report topics
     */

    //
    // B I O S O U R C E
    //
    public final ReportTopic BIOSOURCE_WITH_NO_TAXID = new ReportTopic( "BioSource having no taxId set" );

    //
    // E X P E R I M E N T S
    //
    public final ReportTopic EXPERIMENT_WITHOUT_INTERACTIONS = new ReportTopic( "Experiments with no Interactions" );
    public final ReportTopic EXPERIMENT_WITHOUT_PUBMED = new ReportTopic( "Experiments with no pubmed id" );
    public final ReportTopic EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE = new ReportTopic( "Experiments with no pubmed id (with 'primary-reference' as qualifier)" );
    public final ReportTopic EXPERIMENT_WITHOUT_ORGANISM = new ReportTopic( "Experiments with no organism" );
    public final ReportTopic EXPERIMENT_WITHOUT_CVIDENTIFICATION = new ReportTopic( "Experiments with no CvIdentification" );
    public final ReportTopic EXPERIMENT_WITHOUT_CVINTERACTION = new ReportTopic( "Experiments with no CvInteraction" );

    //
    // I N T E R A C T I O  N S
    //
    public final ReportTopic INTERACTION_WITH_NO_EXPERIMENT = new ReportTopic( "Interactions with no Experiment" );
    public final ReportTopic INTERACTION_WITH_NO_CVINTERACTIONTYPE = new ReportTopic( "Interactions with no CvInteractionType" );
    public final ReportTopic INTERACTION_WITH_NO_ORGANISM = new ReportTopic( "Interactions with no Organism" );
    public final ReportTopic INTERACTION_WITH_NO_CATEGORIES = new ReportTopic( "Interactions with no categories (bait-prey, target-agent, neutral, complex, self, unspecified)" );
    public final ReportTopic INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES = new ReportTopic( "Interactions with mixed categories (bait-prey, target-agent, neutral, complex, self, unspecified)" );
    public final ReportTopic INTERACTION_WITH_NO_BAIT = new ReportTopic( "Interactions with no bait" );
    public final ReportTopic INTERACTION_WITH_NO_PREY = new ReportTopic( "Interactions with no prey" );
    public final ReportTopic INTERACTION_WITH_NO_TARGET = new ReportTopic( "Interactions with no target" );
    public final ReportTopic INTERACTION_WITH_NO_AGENT = new ReportTopic( "Interactions with no agent" );
    public final ReportTopic INTERACTION_WITH_ONLY_ONE_NEUTRAL = new ReportTopic( "Interactions with only one neutral component" );
    public final ReportTopic INTERACTION_WITH_PROTEIN_COUNT_LOWER_THAN_2 = new ReportTopic( "Interactions with less than 2 proteins (Role = complex)" );
    public final ReportTopic INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2 = new ReportTopic( "Interactions with protein having their role set to self and its stoichiometry lower than 2.0" );
    public final ReportTopic INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN = new ReportTopic( "Interactions with more than one protein having their role set to self" );
    public final ReportTopic SINGLE_PROTEIN_CHECK = new ReportTopic( "Interactions with only One Protein" );
    public final ReportTopic NO_PROTEIN_CHECK = new ReportTopic( "Interactions with No Components" );

    //
    // P R O T E I N S
    //
    public final ReportTopic PROTEIN_WITH_NO_UNIPROT_IDENTITY = new ReportTopic( "proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot)" );
    public final ReportTopic PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY = new ReportTopic( "proteins with more than one Xref with XrefQualifier(identity) and CvDatabase(uniprot)" );


    //Holds the statements for finding userstamps in various tables
    private PreparedStatement experimentStatement;
    private PreparedStatement interationStatement;
    private PreparedStatement proteinStatement;
    private PreparedStatement bioSourceStatement;

    /**
     * Contains individual errors of curators
     * as Map( user, Map( topic, Collection( message ) ) )
     */
    private Map allUsersReport = new HashMap();

    /**
     * Contains all error for admin
     * as Map( Topic, Collection(Message) )
     */
    private Map adminReport = new HashMap();

    //The Experiments and Interactions - may be used in more than one test
    private CvDatabase uniprot;
    private CvXrefQualifier identity;

    // for annotations at the experiment level.
    private CvTopic onHoldCvTopic;
    private IntactHelper helper;

    public SanityChecker( IntactHelper helper ) throws IntactException, SQLException {

        //set up statements to get user info...
        //NB remember the Connection belongs to the helper - don't close it anywhere but
        //let the helper do it at the end!!
        this.helper = helper;
        Connection conn = helper.getJDBCConnection();
        bioSourceStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_biosource WHERE ac=?" );
        proteinStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_interactor WHERE ac=?" );
        interationStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_interactor WHERE ac=?" );
        experimentStatement = conn.prepareStatement( "SELECT userstamp, timestamp FROM ia_experiment WHERE ac=?" );


        uniprot = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "uniprot" );
        if( uniprot == null ) {
            throw new RuntimeException( "Your IntAct node doesn't contain the required: CvDatabase( uniprot )." );
        }

        identity = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, "identity" );
        if( uniprot == null ) {
            throw new RuntimeException( "Your IntAct node doesn't contain the required: CvXrefQualifier( identity )." );
        }

        onHoldCvTopic = (CvTopic) helper.getObjectByLabel( CvTopic.class, "on-hold" );
        if( uniprot == null ) {
            throw new RuntimeException( "Your IntAct node doesn't contain the required: CvTopic( on-hold )." );
        }
    }

    /*
     *
     * Checks We have so far:
     * -----------------------
     *
     *   1.  Any Experiment lacking a PubMed ID
     *   2.  Any PubMed ID in Experiment DBXref without qualifier=Primary-reference
     *   3.  Any Interaction containing a bait but not a prey protein
     *   4.  Any Interaction containing a prey but not a bait protein
     *   5.  Any interaction with no protein attached
     *   6.  Any interaction with 1 protein attached, stoichiometry=1
     *   7.  Any Interaction missing a link to an Experiment
     *   8.  Any experiment with no Interaction linked to it
     *   9.  Any interaction missing CvInteractionType
     *   10. Any interaction missing Organism
     *   11. Any experiment missing Organism
     *   12. Any experiment missing CvInteraction
     *   13. Any experiment missing CvIdentification
     *   14. Any proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot)
     *   15. Any BioSource with a NULL or empty taxid.
     *   16. Any proteins with more than one Xref with XrefQualifier(identity) and CvDatabase(uniprot)
     *
     * To perform these checks we need to enhance the Helper/persistence code to
     * handle more complex queries, ie to be able to build Criteria and Query objects
     * probably used in OJB (easiest to do). This is going to be needed anyway so
     * that we can handle more complex search queries later....
     *
     */

    public void checkBioSource( Collection bioSources ) throws IntactException, SQLException {

        System.out.println( "Checking on BioSource (rule 15) ..." );

        for( Iterator it = bioSources.iterator(); it.hasNext(); ) {
            BioSource bioSource = (BioSource) it.next();

            //check 15
            if( bioSource.getTaxId() == null || "".equals( bioSource.getTaxId() ) ) {
                addMessage( BIOSOURCE_WITH_NO_TAXID, bioSource );
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

        for( Iterator it = experiments.iterator(); it.hasNext(); ) {
            Experiment exp = (Experiment) it.next();

            if( !isExperimentOnHold( exp ) ) {

                //check 8
                if( exp.getInteractions().size() < 1 ) {
                    //record it.....
                    addMessage( EXPERIMENT_WITHOUT_INTERACTIONS, exp );
                }

                //check 11
                if( exp.getBioSource() == null ) {
                    addMessage( EXPERIMENT_WITHOUT_ORGANISM, exp );
                }

                //check 12
                if( exp.getCvInteraction() == null ) {
                    addMessage( EXPERIMENT_WITHOUT_CVINTERACTION, exp );
                }

                //check 13
                if( exp.getCvIdentification() == null ) {
                    addMessage( EXPERIMENT_WITHOUT_CVIDENTIFICATION, exp );
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
        for( Iterator it = experiments.iterator(); it.hasNext(); ) {
            Experiment exp = (Experiment) it.next();

            if( !isExperimentOnHold( exp ) ) {
                int pubmedCount = 0;
                int pubmedPrimaryCount = 0;
                Collection Xrefs = exp.getXrefs();
                for( Iterator iterator = Xrefs.iterator(); iterator.hasNext(); ) {
                    Xref xref = (Xref) iterator.next();
                    if( xref.getCvDatabase().getShortLabel().equals( "pubmed" ) ) {
                        pubmedCount++;
                        if( xref.getCvXrefQualifier().getShortLabel().equals( "primary-reference" ) ) {
                            pubmedPrimaryCount++;
                        }
                    }
                }

                if( pubmedCount == 0 ) {
                    //record it.....
                    addMessage( EXPERIMENT_WITHOUT_PUBMED, exp );
                }

                if( pubmedPrimaryCount < 1 ) {
                    //record it.....
                    addMessage( EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE, exp );
                }
            } // if not on hold
        } // for
    }

    /**
     * Performs Interaction checks.
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there was a search problem
     */
    public void checkInteractions( Collection interactions ) throws IntactException, SQLException {

        System.out.println( "Checking on Interactions (rule 7) ..." );

        for( Iterator it = interactions.iterator(); it.hasNext(); ) {
            Interaction interaction = (Interaction) it.next();

            //check 7
            if( interaction.getExperiments().size() < 1 ) {
                //record it.....
                addMessage( INTERACTION_WITH_NO_EXPERIMENT, interaction );
            }

            //check 9
            if( interaction.getCvInteractionType() == null ) {
                addMessage( INTERACTION_WITH_NO_CVINTERACTIONTYPE, interaction );
            }

            //check 10
            if( interaction.getBioSource() == null ) {
                addMessage( INTERACTION_WITH_NO_ORGANISM, interaction );
            }
        }
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
        for( Iterator it = interactions.iterator(); it.hasNext(); ) {
            Interaction interaction = (Interaction) it.next();

            Collection components = interaction.getComponents();
            int preyCount = 0,
                    baitCount = 0,
                    agentCount = 0,
                    targetCount = 0,
                    neutralCount = 0,
                    selfCount = 0,
                    complexCount = 0,
                    unspecifiedCount = 0;
            float selfStoichiometry = 0;
            float neutralStoichiometry = 0;

            for( Iterator iterator = components.iterator(); iterator.hasNext(); ) {
                Component component = (Component) iterator.next();
                //record it.....

                if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "bait" ) ) {
                    baitCount++;
                } else if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "prey" ) ) {
                    preyCount++;
                } else if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "target" ) ) {
                    targetCount++;
                } else if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "agent" ) ) {
                    agentCount++;
                } else if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "neutral" ) ) {
                    neutralCount++;
                    neutralStoichiometry = component.getStoichiometry();
                } else if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "self" ) ) {
                    selfCount++;
                    selfStoichiometry = component.getStoichiometry();
                } else if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "complex" ) ) {
                    complexCount++;
                } else if( component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "unspecified" ) ) {
                    unspecifiedCount++;
                }
            }


            /**
             * We have to consider Components as 3 distinct groups: bait-prey, agent-target and neutral
             * We are not allowed to mix categories,
             * if you have a bait you must have at least one prey
             * if you have a target you must have at least one agent ----- NOT DONE YET
             * if you have neutral component you must have at least 2
             * if you have complex you must have at least 2
             * if you have self you must have only one protein with Stochiometry >= 2
             */

            int baitPrey = ( baitCount + preyCount > 0 ? 1 : 0 );
            int targetAgent = ( targetCount + agentCount > 0 ? 1 : 0 );
            int neutral = ( neutralCount > 0 ? 1 : 0 );
            int self = ( selfCount > 0 ? 1 : 0 );
            int complex = ( complexCount > 0 ? 1 : 0 );
            int unspecified = ( unspecifiedCount > 0 ? 1 : 0 );

            int categoryCount = baitPrey + targetAgent + neutral + self + complex + unspecified;

            switch( categoryCount ) {
                case 0:
                    // none of those categories
                    addMessage( INTERACTION_WITH_NO_CATEGORIES, interaction );
                    break;

                case 1:
                    // exactly 1 category
                    if( baitPrey == 1 ) {
                        // bait-prey
                        if( baitCount == 0 ) {
                            addMessage( INTERACTION_WITH_NO_BAIT, interaction );
                        } else if( preyCount == 0 ) {
                            addMessage( INTERACTION_WITH_NO_PREY, interaction );
                        }

                    } else if( targetAgent == 1 ) {
                        // target-agent
                        if( targetCount == 0 ) {
                            addMessage( INTERACTION_WITH_NO_TARGET, interaction );
                        } else if( agentCount == 0 ) {
                            addMessage( INTERACTION_WITH_NO_AGENT, interaction );
                        }

                    } else if( self == 1 ) {
                        // it has to be > 1
                        if( selfCount > 1 ) {
                            addMessage( INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN, interaction );
                        } else { // = 1
                            if( selfStoichiometry < 2F ) {
                                addMessage( INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2, interaction );
                            }
                        }

                    } else if( complex == 1 ) {
                        // it has to be > 1
                        if( complexCount < 2 ) {
                            addMessage( INTERACTION_WITH_PROTEIN_COUNT_LOWER_THAN_2, interaction );
                        }

                    } else {
                        // neutral
                        if( neutralCount == 1 ) {
                            if( neutralStoichiometry < 2 ) {
                                addMessage( INTERACTION_WITH_ONLY_ONE_NEUTRAL, interaction );
                            }
                        }
                    }
                    break;

                default:
                    // > 1 : mixed up categories !
                    addMessage( INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES, interaction );

            } // switch

            // What about self or the unknown category ?
        }
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
        for( Iterator it = interactions.iterator(); it.hasNext(); ) {

            Interaction interaction = (Interaction) it.next();
            Collection components = interaction.getComponents();
            int originalSize = components.size();
            int matchCount = 0;
            Protein proteinToCheck = null;
            if( components.size() > 0 ) {
                Component firstOne = (Component) components.iterator().next();

                if( firstOne.getInteractor() instanceof Protein ) {
                    proteinToCheck = (Protein) firstOne.getInteractor();
                    components.remove( firstOne ); //don't check it twice!!
                } else {
                    //not interested (for now) in Interactions that have
                    //interactors other than Proteins (for now)...
                    return;
                }

                for( Iterator iter = components.iterator(); iter.hasNext(); ) {
                    Component comp = (Component) iter.next();
                    Interactor interactor = comp.getInteractor();
                    if( interactor.equals( proteinToCheck ) ) {
                        //check it against the first one..
                        matchCount++;
                    }
                }
                //now compare the count and the original - if they are the
                //same then we have found one that needs to be flagged..
                if( matchCount == originalSize ) {
                    addMessage( SINGLE_PROTEIN_CHECK, interaction );
                }

            } else {
                //Interaction has no Components!! This is in fact test 5...
                addMessage( NO_PROTEIN_CHECK, interaction );
            }
        }
    }

    public void checkProteins( Collection proteins ) throws SQLException {

        System.out.println( "Checking on Proteins (rules 14 and 16) ..." );

        //checks 14
        for( Iterator it = proteins.iterator(); it.hasNext(); ) {
            Protein protein = (Protein) it.next();

            Collection xrefs = protein.getXrefs();
            int count = 0;
            for( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
                Xref xref = (Xref) iterator.next();

                if( uniprot.equals( xref.getCvDatabase() ) && identity.equals( xref.getCvXrefQualifier() ) ) {
                    count++;
                }
            } // xrefs

            if( count == 0 ) {
                addMessage( PROTEIN_WITH_NO_UNIPROT_IDENTITY, protein );
            } else if( count > 1 ) {
                addMessage( PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY, protein );
            }
        } // proteins
    }

    /**
     * tidies up the DB statements.
     */
    public void cleanUp() {

        try {
            if( experimentStatement != null ) {
                experimentStatement.close();
            }
            if( interationStatement != null ) {
                interationStatement.close();
            }
            if( proteinStatement != null ) {
                proteinStatement.close();
            }
            if( bioSourceStatement != null ) {
                bioSourceStatement.close();
            }
        } catch( SQLException se ) {
            System.out.println( "failed to close statement!!" );
            se.printStackTrace();
        }
    }

    //--------------------------- private methods ------------------------------------------

    /**
     * Check if an Experiment as been annotated as on-hold.
     *
     * @param experiment the experiment to be checked
     * @return true if the experiment is on-hold, else false.
     */
    private boolean isExperimentOnHold( Experiment experiment ) {

        boolean onHold = false;

        for( Iterator iterator = experiment.getAnnotations().iterator(); iterator.hasNext() && !onHold; ) {
            Annotation annotation = (Annotation) iterator.next();

            if( onHoldCvTopic.equals( annotation.getCvTopic() ) ) {
                onHold = true;
            }
        }

        return onHold;
    }

    /**
     * Helper method to obtain userstamp info from a given record, and
     * then if it has any to append the details to a result buffer.
     *
     * @param topic the type of error we have dicovered for the given AnnotatedObject.
     * @param obj   The Intact object that user info is required for.
     * @throws SQLException thrown if there were DB problems
     */
    private void addMessage( ReportTopic topic, AnnotatedObject obj ) throws SQLException {

        String user = null;
        Timestamp date = null;
        ResultSet results = null;

        if( obj instanceof Experiment ) {
            experimentStatement.setString( 1, obj.getAc() );
            results = experimentStatement.executeQuery();

        } else if( obj instanceof Interaction ) {
            interationStatement.setString( 1, obj.getAc() );
            results = interationStatement.executeQuery();

        } else if( obj instanceof Protein ) {
            proteinStatement.setString( 1, obj.getAc() );
            results = proteinStatement.executeQuery();

        } else if( obj instanceof BioSource ) {
            bioSourceStatement.setString( 1, obj.getAc() );
            results = bioSourceStatement.executeQuery();
        }

        if( results.next() ) {
            user = results.getString( "userstamp" );
            date = results.getTimestamp( "timestamp" );
        }

        results.close();

        // Build users report
        String userMessageReport = "AC: " + obj.getAc() +
                                   "\t Shortlabel: " + obj.getShortLabel() +
                                   "\t When: " + date;

        if( user != null && !( user.trim().length() == 0 ) ) {

            // add new message to the user
            Map userReport = (Map) allUsersReport.get( user );
            if( userReport == null ) {
                userReport = new HashMap();
            }

            Collection topicMessages = (Collection) userReport.get( topic );
            if( topicMessages == null ) {
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


        // build admin admin report
        String adminMessageReport = "AC: " + obj.getAc() +
                                    "\t Shortlabel: " + obj.getShortLabel() +
                                    "\t User: " + user +
                                    "\t When: " + date;

        Collection topicMessages = (Collection) adminReport.get( topic );
        if( topicMessages == null ) {
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
        for( Iterator iterator = allUsersReport.keySet().iterator(); iterator.hasNext(); ) {
            String user = (String) iterator.next();

            Map reportMessages = (Map) allUsersReport.get( user );
            StringBuffer fullReport = new StringBuffer( 256 );
            int errorCount = 0;

            for( Iterator iterator1 = reportMessages.keySet().iterator(); iterator1.hasNext(); ) {
                ReportTopic topic = (ReportTopic) iterator1.next();

                fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
                Collection messages = (Collection) reportMessages.get( topic );

                // write individual messages of that topic.
                for( Iterator iterator2 = messages.iterator(); iterator2.hasNext(); ) {
                    String message = (String) iterator2.next();

                    fullReport.append( message ).append( NEW_LINE );
                    errorCount++;
                } // messages in the topic

                fullReport.append( NEW_LINE );
            } // topics

            // don't send mail to curator if no errors
            if( errorCount > 0 ) {

//                System.out.println( "Send individual report to "+ user + "( "+ email + )" );
                String email = (String) usersEmails.get( user.toLowerCase() );
                String[] recipients = new String[ 1 ];
                recipients[ 0 ] = email;

                // send mail
                mailer.postMail( recipients,
                                 "SANITY CHECK - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
                                 fullReport.toString(),
                                 "skerrien@ebi.ac.uk" );
            }
        } // users

        // send summary of all individual mail to admin
        StringBuffer fullReport = new StringBuffer( 256 );
        int errorCount = 0;
        if( adminReport.isEmpty() ) {

        } else {
            for( Iterator iterator = adminReport.keySet().iterator(); iterator.hasNext(); ) {
                ReportTopic topic = (ReportTopic) iterator.next();

                Collection messages = (Collection) adminReport.get( topic );
                fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
                for( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
                    String message = (String) iterator1.next();
                    fullReport.append( message ).append( NEW_LINE );
                    errorCount++;
                } // messages

                fullReport.append( NEW_LINE );
            } // topics

            String[] recipients = new String[ adminsEmails.size() ];
            int i = 0;
            for( Iterator iterator = adminsEmails.iterator(); iterator.hasNext(); ) {
                String email = (String) iterator.next();
                recipients[ i++ ] = email;
            }

            // always send mail to admin, even if no errors
            mailer.postMail( recipients,
                             "SANITY CHECK (ADMIN) - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
                             fullReport.toString(),
                             "skerrien@ebi.ac.uk" );
        }
    }

    private String getFullReportOutput() {

        StringBuffer fullReport = new StringBuffer( 256 );

        for( Iterator iterator = adminReport.keySet().iterator(); iterator.hasNext(); ) {
            ReportTopic topic = (ReportTopic) iterator.next();

            Collection messages = (Collection) adminReport.get( topic );
            fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
            for( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
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

            //get the Experiment and Interaction info from the DB for later use.
            Collection bioSources = helper.search( BioSource.class.getName(), "ac", "*" );
            System.out.println( bioSources.size() + " biosources loaded." );
            checker.checkBioSource( bioSources );
            bioSources = null;
            Runtime.getRuntime().gc(); // free memory before to carry on.

            //get the Experiment and Interaction info from the DB for later use.
            Collection experiments = helper.search( Experiment.class.getName(), "ac", "*" );
            System.out.println( experiments.size() + " experiments loaded." );
            checker.checkExperiments( experiments );
            checker.checkExperimentsPubmedIds( experiments );
            experiments = null;
            Runtime.getRuntime().gc(); // free memory before to carry on.

            Collection interactions = helper.search( Interaction.class.getName(), "ac", "*" );
            System.out.println( interactions.size() + " interactions loaded." );
            checker.checkInteractions( interactions );
            checker.checkInteractionsBaitAndPrey( interactions );
            checker.checkComponentOfInteractions( interactions );
            interactions = null;
            Runtime.getRuntime().gc(); // free memory before to carry on.

            Collection proteins = helper.search( Protein.class.getName(), "ac", "*" );
            System.out.println( proteins.size() + " proteins loaded." );
            checker.checkProteins( proteins );

            long end = System.currentTimeMillis();
            long total = end - start;
            System.out.println( "....Done. " );
            System.out.println();
            System.out.println( "Total time to perform checks: " + total / 1000 + "s" );

            // try to send emails
            try {

                checker.postEmails();

            } catch( MessagingException e ) {
                // scould not send emails, then how error ...
                e.printStackTrace();

                // ... and save the full report on hard disk instead.
                String filename = null;
                if( args.length != 1 ) {

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

        } catch( IntactException e ) {

            e.printStackTrace();
            if( e.getRootCause() != null ) {
                e.getRootCause().printStackTrace( );
            }
            System.exit( 1 );
        } catch( SQLException sqe ) {

            System.out.println( "DB error!" );
            sqe.printStackTrace();
            System.exit( 1 );
        } catch( OutOfMemoryError aome ) {

            aome.printStackTrace();
            System.err.println( "" );
            System.err.println( "SanityChecker ran out of memory." );
            System.err.println( "Please run it again and change the JVM configuration." );
            System.err.println( "Here are some the options: http://java.sun.com/docs/hotspot/VMOptions.html" );
            System.err.println( "Hint: You can use -Xms -Xmx to specify respectively the minimum and maximum" );
            System.err.println( "      amount of memory (heap size) that the JVM is allowed to allocate." );
            System.err.println( "      eg. java -Xms128m -Xmx640m <className>" );

            System.exit( 1 );
        } catch( Exception e ) {

            e.printStackTrace();
            System.exit( 1 );
        } finally {

            if( checker != null ) {
                checker.cleanUp();
            }
        }

        System.exit( 0 );
    }
}