package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

/**
 * Utility class to perform some sanity checks on the DB. Mainly for use by curators.
 * A report of anomolies detected (as per the list of checks) is sent via email to
 * the appropriate people.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SanityChecker {

    /**
     * TODO additional checks
     * 	    a) an annotation should only have 1 annotation concerning uniprot-dr-export
     *      b) an experiment could have 1..n annotation concerning uniprot-dr-export
     *      c) look for biosource with a taxid NULL
     *      d) is there annotations with same text and topic ? Could cause bug with OJB data loading.
     */


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
                } catch ( IntactException e ) {
                    System.err.println( "Could not close the connexion to the database." );
                    e.printStackTrace();
                }
            }
        }
    }


    private static final String NEW_LINE = System.getProperty( "line.separator" );

    public static final String SEPARATOR = NEW_LINE +
                                           "------------------------------------------------------------------------" +
                                           NEW_LINE;

    //Keep the helper at object level - may need it for other tests later
    private PrintWriter writer;

    //Holds the statements for finding userstamps in various tables
    private PreparedStatement experimentStatement;
    private PreparedStatement interationStatement;
    private PreparedStatement proteinStatement;
    private PreparedStatement bioSourceStatement;

    //holds the accumulated results of the test executions
    private StringBuffer experimentWithoutInteractions;
    private StringBuffer expCheckNoPubmed;
    private StringBuffer expCheckNoPubmedWithPrimaryReference;

    private StringBuffer experimentWithoutOrganism;
    private StringBuffer experimentWithoutCvIdentification;
    private StringBuffer experimentWithoutCvInteraction;


    private StringBuffer interactionWithNoExperimentCheck;
    private StringBuffer interactionWithNoOrganismCheck;
    private StringBuffer interactionWithNoCvInteractionTypeCheck;

    private StringBuffer interactionWithMixedComponentCategoriesCheck;
    private StringBuffer interactionWithNoCategoriesCheck;

    private StringBuffer interactionWithNoBaitCheck;
    private StringBuffer interactionWithNoPreyCheck;

    private StringBuffer interactionWithNoTargetCheck;
    private StringBuffer interactionWithNoAgentCheck;
    private StringBuffer interactionWithOnlyOneNeutralCheck;

    private StringBuffer interactionWithProteinCountLowerThan2;

    private StringBuffer interactionWithSelfProteinAndStoichiometryLowerThan2;
    private StringBuffer interactionWithMoreThan2SelfProtein;

    private StringBuffer bioSourceWithNoTaxId;

    private StringBuffer singleProteinCheck;
    private StringBuffer noProteinCheck;
    private StringBuffer proteinWithNoUniprotIdentity;
    private StringBuffer proteinWithMoreThanOneUniprotIdentity;

    //The Experiments and Interactions - may be used in more than one test
    private CvDatabase uniprot;
    private CvXrefQualifier identity;

    // for annotations at the experiment level.
    private CvTopic onHoldCvTopic;


    public SanityChecker( IntactHelper helper, PrintWriter writer ) throws IntactException, SQLException {
        this.writer = writer;

        //set up statements to get user info...
        //NB remember the Connection belongs to the helper - don't close it anywhere but
        //let the helper do it at the end!!
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

        String msg = null;

        //initialize buffers that will accumulate the test results..

        //
        // B I O S O U R C E
        //
        bioSourceWithNoTaxId = new StringBuffer( "BioSource having no taxId set" + SEPARATOR );


        //
        // E X P E R I M E N T S
        //
        msg = "Experiments with no Interactions" + SEPARATOR;
        experimentWithoutInteractions = new StringBuffer( msg );

        msg = "Experiments with no pubmed id" + SEPARATOR;
        expCheckNoPubmed = new StringBuffer( msg );

        msg = "Experiments with no pubmed id (with 'primary-reference' as qualifier)" + SEPARATOR;
        expCheckNoPubmedWithPrimaryReference = new StringBuffer( msg );

        msg = "Experiments with no Organism)" + SEPARATOR;
        experimentWithoutOrganism = new StringBuffer( msg );

        msg = "Experiments with no CvIdentification)" + SEPARATOR;
        experimentWithoutCvIdentification = new StringBuffer( msg );

        msg = "Experiments with no CvInteraction)" + SEPARATOR;
        experimentWithoutCvInteraction = new StringBuffer( msg );


        //
        // I N T E R A C T I O  N S
        //
        msg = "Interactions with no Experiment" + SEPARATOR;
        interactionWithNoExperimentCheck = new StringBuffer( msg );

        msg = "Interactions with no CvInteractionType" + SEPARATOR;
        interactionWithNoCvInteractionTypeCheck = new StringBuffer( msg );

        msg = "Interactions with no Organism" + SEPARATOR;
        interactionWithNoOrganismCheck = new StringBuffer( msg );

        msg = "Interactions with no categories (bait-prey, target-agent, neutral, complex, self, unspecified)" + SEPARATOR;
        interactionWithNoCategoriesCheck = new StringBuffer( msg );

        msg = "Interactions with mixed categories (bait-prey, target-agent, neutral, complex, self, unspecified)" + SEPARATOR;
        interactionWithMixedComponentCategoriesCheck = new StringBuffer( msg );

        msg = "Interactions with no bait" + SEPARATOR;
        interactionWithNoBaitCheck = new StringBuffer( msg );

        msg = "Interactions with no prey" + SEPARATOR;
        interactionWithNoPreyCheck = new StringBuffer( msg );

        msg = "Interactions with no target" + SEPARATOR;
        interactionWithNoTargetCheck = new StringBuffer( msg );

        msg = "Interactions with no agent" + SEPARATOR;
        interactionWithNoAgentCheck = new StringBuffer( msg );

        msg = "Interactions with only one neutral component" + SEPARATOR;
        interactionWithOnlyOneNeutralCheck = new StringBuffer( msg );

        msg = "Interactions with less than 2 proteins (Role = complex)" + SEPARATOR;
        interactionWithProteinCountLowerThan2 = new StringBuffer();

        msg = "Interactions with protein having their role set to self and its stoichiometry lower than 2.0" + SEPARATOR;
        interactionWithSelfProteinAndStoichiometryLowerThan2 = new StringBuffer( msg );

        msg = "Interactions with more than one protein having their role set to self" + SEPARATOR;
        interactionWithMoreThan2SelfProtein = new StringBuffer( msg );

        msg = "Interactions with only One Protein" + SEPARATOR;
        singleProteinCheck = new StringBuffer( msg );

        msg = "Interactions with No Components" + SEPARATOR;
        noProteinCheck = new StringBuffer( msg );


        //
        // P R O T E I N S
        //
        msg = "proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot)" + SEPARATOR;
        proteinWithNoUniprotIdentity = new StringBuffer( msg );

        msg = "proteins with more than one Xref with XrefQualifier(identity) and CvDatabase(uniprot)" + SEPARATOR;
        proteinWithMoreThanOneUniprotIdentity = new StringBuffer( msg );
    }

    /*--------------------- Work Methods ------------------------------------------------
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
    * TODO: We could check if some Xrefs are orphan (check is the parent_ac is found in IA_INTERACTOR, IA_EXPERIMENT, IA_CONTROLLEDVOCAB)
    *
    * To perform these checks we need to enhance the Helper/persistence code to
    * handle more complex queries, ie to be able to build Criteria and Query objects
    * probably used in OJB (easiest to do). This is going to be needed anyway so
    * that we can handle more complex search queries later....
    *
    */

    public void checkBioSource( Collection bioSources ) throws IntactException, SQLException {

        int bioSourceWithNoTaxIdCount = 0;

        System.out.println( "Checking on BioSource (rule 15) ..." );

        for ( Iterator it = bioSources.iterator(); it.hasNext(); ) {
            BioSource bioSource = (BioSource) it.next();

            //check 15
            if( bioSource.getTaxId() == null || "".equals( bioSource.getTaxId() ) ) {
                getUserInfo( bioSourceWithNoTaxId, bioSource );
                bioSourceWithNoTaxIdCount++;
            }
        }

        if( bioSourceWithNoTaxIdCount > 0 ) {
            writeResults( bioSourceWithNoTaxId );
            writer.println();
        }
    }

    private boolean isExperimentOnHold( Experiment experiment ) {

        boolean onHold = false;

        for ( Iterator iterator = experiment.getAnnotations().iterator(); iterator.hasNext() && !onHold; ) {
            Annotation annotation = (Annotation) iterator.next();

            if( onHoldCvTopic.equals( annotation.getCvTopic() ) ) {
                onHold = true;
            }
        }

        return onHold;
    }

    /**
     * Performs checks on Experiments.
     *
     * @throws IntactException Thrown if there was a Helper problem
     * @throws SQLException    Thrown if there was a DB access problem
     */
    public void checkExperiments( Collection experiments ) throws IntactException, SQLException {

        int experimentWithoutInteractionsCount = 0,
                experimentWithoutOrganismCount = 0,
                experimentWithoutCvInteractionCount = 0,
                experimentWithoutCvIdentificationCount = 0;

        System.out.println( "Checking on Experiment (rules 8, 11, 12, 13) ..." );

        for ( Iterator it = experiments.iterator(); it.hasNext(); ) {
            Experiment exp = (Experiment) it.next();

            if( !isExperimentOnHold( exp ) ) {

                //check 8
                if( exp.getInteractions().size() < 1 ) {
                    //record it.....
                    getUserInfo( experimentWithoutInteractions, exp );
                    experimentWithoutInteractionsCount++;
                }

                //check 11
                if( exp.getBioSource() == null ) {
                    getUserInfo( experimentWithoutOrganism, exp );
                    experimentWithoutOrganismCount++;
                }

                //check 12
                if( exp.getCvInteraction() == null ) {
                    getUserInfo( experimentWithoutCvInteraction, exp );
                    experimentWithoutCvInteractionCount++;
                }

                //check 13
                if( exp.getCvIdentification() == null ) {
                    getUserInfo( experimentWithoutCvIdentification, exp );
                    experimentWithoutCvIdentificationCount++;
                }
            } // if
        } // for

        // Write report
        if( experimentWithoutInteractionsCount > 0 ) {
            writeResults( experimentWithoutInteractions );
            writer.println();
        }

        if( experimentWithoutOrganismCount > 0 ) {
            writeResults( experimentWithoutOrganism );
            writer.println();
        }

        if( experimentWithoutCvInteractionCount > 0 ) {
            writeResults( experimentWithoutCvInteraction );
            writer.println();
        }

        if( experimentWithoutCvIdentificationCount > 0 ) {
            writeResults( experimentWithoutCvIdentification );
            writer.println();
        }
    }

    /**
     * Performs checks on Experiments.
     *
     * @throws IntactException Thrown if there was a Helper problem
     * @throws SQLException    Thrown if there was a DB access problem
     */
    public void checkExperimentsPubmedIds( Collection experiments ) throws IntactException, SQLException {

        int expCheckNoPubmedCount = 0,
                expCheckNoPubmedWithPrimaryReferenceCount = 0;

        System.out.println( "Checking on Experiment and their pubmed IDs (rules 1 and 2) ..." );

        //check 1 and 2
        for ( Iterator it = experiments.iterator(); it.hasNext(); ) {
            Experiment exp = (Experiment) it.next();

            if( !isExperimentOnHold( exp ) ) {
                int pubmedCount = 0;
                int pubmedPrimaryCount = 0;
                Collection Xrefs = exp.getXrefs();
                for ( Iterator iterator = Xrefs.iterator(); iterator.hasNext(); ) {
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
                    getUserInfo( expCheckNoPubmed, exp );
                    expCheckNoPubmedCount++;
                }

                if( pubmedPrimaryCount != 1 ) {
                    //record it.....
                    getUserInfo( expCheckNoPubmedWithPrimaryReference, exp );
                    expCheckNoPubmedWithPrimaryReferenceCount++;
                }
            } // if not on hold
        } // for

        if( expCheckNoPubmedCount > 0 ) {
            writeResults( expCheckNoPubmed );
            writer.println();
        }

        if( expCheckNoPubmedWithPrimaryReferenceCount > 0 ) {
            writeResults( expCheckNoPubmedWithPrimaryReference );
            writer.println();
        }
    }

    /**
     * Performs Interaction checks.
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there was a search problem
     */
    public void checkInteractions( Collection interactions ) throws IntactException, SQLException {

        int interactionWithNoExperimentCheckCount = 0,
                interactionWithNoCvInteractionTypeCheckCount = 0,
                interactionWithNoOrganismCheckCount = 0;

        System.out.println( "Checking on Interactions (rule 7) ..." );

        for ( Iterator it = interactions.iterator(); it.hasNext(); ) {
            Interaction interaction = (Interaction) it.next();

            //check 7
            if( interaction.getExperiments().size() < 1 ) {
                //record it.....
                getUserInfo( interactionWithNoExperimentCheck, interaction );
            }

            //check 9
            if( interaction.getCvInteractionType() == null ) {
                getUserInfo( interactionWithNoCvInteractionTypeCheck, interaction );
            }

            //check 10
            if( interaction.getBioSource() == null ) {
                getUserInfo( interactionWithNoOrganismCheck, interaction );
            }
        }
        //now dump the results...
        if( interactionWithNoExperimentCheckCount > 0 ) {
            writeResults( interactionWithNoExperimentCheck );
            writer.println();
        }

        if( interactionWithNoCvInteractionTypeCheckCount > 0 ) {
            writeResults( interactionWithNoCvInteractionTypeCheck );
            writer.println();
        }

        if( interactionWithNoOrganismCheckCount > 0 ) {
            writeResults( interactionWithNoOrganismCheck );
            writer.println();
        }
    }

    /**
     * Performs Interaction checks.
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *          thrown if there was a search problem
     */
    public void checkInteractionsBaitAndPrey( Collection interactions ) throws IntactException, SQLException {

        int interactionWithNoCategoriesCheckCount = 0,
                interactionWithMixedComponentCategoriesCheckCount = 0,
                interactionWithNoAgentCheckCount = 0,
                interactionWithNoTargetCheckCount = 0,
                interactionWithNoBaitCheckCount = 0,
                interactionWithNoPreyCheckCount = 0,
                interactionWithOnlyOneNeutralCheckCount = 0,
                interactionWithMoreThan2SelfProteinCount = 0,
                interactionWithSelfProteinAndStoichiometryLowerThan2Count = 0,
                interactionWithProteinCountLowerThan2Count = 0;

        System.out.println( "Checking on Interactions (rule 6) ..." );

        //check 7
        for ( Iterator it = interactions.iterator(); it.hasNext(); ) {
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

            for ( Iterator iterator = components.iterator(); iterator.hasNext(); ) {
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

            switch ( categoryCount ) {
                case 0:
                    // none of those categories
                    getUserInfo( interactionWithNoCategoriesCheck, interaction );
                    interactionWithNoCategoriesCheckCount++;
                    break;

                case 1:
                    // exactly 1 category
                    if( baitPrey == 1 ) {
                        // bait-prey
                        if( baitCount == 0 ) {
                            getUserInfo( interactionWithNoBaitCheck, interaction );
                            interactionWithNoBaitCheckCount++;
                        } else if( preyCount == 0 ) {
                            getUserInfo( interactionWithNoPreyCheck, interaction );
                            interactionWithNoPreyCheckCount++;
                        }

                    } else if( targetAgent == 1 ) {
                        // target-agent
                        if( targetCount == 0 ) {
                            getUserInfo( interactionWithNoTargetCheck, interaction );
                            interactionWithNoTargetCheckCount++;
                        } else if( agentCount == 0 ) {
                            getUserInfo( interactionWithNoAgentCheck, interaction );
                            interactionWithNoAgentCheckCount++;
                        }

                    } else if( self == 1 ) {
                        // it has to be > 1
                        if( selfCount > 1 ) {
                            getUserInfo( interactionWithMoreThan2SelfProtein, interaction );
                            interactionWithMoreThan2SelfProteinCount++;
                        } else { // = 1
                            if( selfStoichiometry < 2F ) {
                                getUserInfo( interactionWithSelfProteinAndStoichiometryLowerThan2, interaction );
                                interactionWithSelfProteinAndStoichiometryLowerThan2Count++;
                            }
                        }

                    } else if( complex == 1 ) {
                        // it has to be > 1
                        if( complexCount < 2 ) {
                            getUserInfo( interactionWithProteinCountLowerThan2, interaction );
                            interactionWithProteinCountLowerThan2Count++;
                        }

                    } else {
                        // neutral
                        if( neutralCount == 1 ) {
                            if( neutralStoichiometry < 2 ) {
                                getUserInfo( interactionWithOnlyOneNeutralCheck, interaction );
                                interactionWithOnlyOneNeutralCheckCount++;
                            }
                        }
                    }
                    break;

                default:
                    // > 1 : mixed up categories !
                    getUserInfo( interactionWithMixedComponentCategoriesCheck, interaction );
                    interactionWithMixedComponentCategoriesCheckCount++;

            } // switch

            // What about self or the unknown category ?
        }

        //now dump the results...
        if( interactionWithNoCategoriesCheckCount > 0 ) {
            writeResults( interactionWithNoCategoriesCheck );
            writer.println();
        }

        if( interactionWithMixedComponentCategoriesCheckCount > 0 ) {
            writeResults( interactionWithMixedComponentCategoriesCheck );
            writer.println();
        }

        if( interactionWithNoAgentCheckCount > 0 ) {
            writeResults( interactionWithNoAgentCheck );
            writer.println();
        }

        if( interactionWithNoTargetCheckCount > 0 ) {
            writeResults( interactionWithNoTargetCheck );
            writer.println();
        }

        if( interactionWithNoBaitCheckCount > 0 ) {
            writeResults( interactionWithNoBaitCheck );
            writer.println();
        }

        if( interactionWithNoPreyCheckCount > 0 ) {
            writeResults( interactionWithNoPreyCheck );
            writer.println();
        }

        if( interactionWithOnlyOneNeutralCheckCount > 0 ) {
            writeResults( interactionWithOnlyOneNeutralCheck );
            writer.println();
        }

        if( interactionWithMoreThan2SelfProteinCount > 0 ) {
            writeResults( interactionWithMoreThan2SelfProtein );
            writer.println();
        }

        if( interactionWithSelfProteinAndStoichiometryLowerThan2Count > 0 ) {
            writeResults( interactionWithSelfProteinAndStoichiometryLowerThan2 );
            writer.println();
        }

        if( interactionWithProteinCountLowerThan2Count > 0 ) {
            writeResults( interactionWithProteinCountLowerThan2 );
            writer.println();
        }
    }

    /**
     * Performs checks against Proteins.
     *
     * @throws IntactException Thrown if there were Helper problems
     * @throws SQLException    thrown if there were DB access problems
     */
    public void checkComponentOfInteractions( Collection interactions ) throws IntactException, SQLException {

        int singleProteinCheckCount = 0,
                noProteinCheckCount = 0;

        System.out.println( "Checking on Components (rules 5 and 6) ..." );

        //checks 5 and 6 (easier if done together)
        for ( Iterator it = interactions.iterator(); it.hasNext(); ) {

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

                for ( Iterator iter = components.iterator(); iter.hasNext(); ) {
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
                    getUserInfo( singleProteinCheck, interaction );
                    singleProteinCheckCount++;
                }

            } else {
                //Interaction has no Components!! This is in fact test 5...
                getUserInfo( noProteinCheck, interaction );
                noProteinCheckCount++;
            }
        }

        if( singleProteinCheckCount > 0 ) {
            writeResults( singleProteinCheck );
            writer.println();
        }

        if( noProteinCheckCount > 0 ) {
            writeResults( noProteinCheck );
            writer.println();
        }
    }

    public void checkProteins( Collection proteins ) throws SQLException {

        int proteinWithNoUniprotIdentityCount = 0,
                proteinWithMoreThanOneUniprotIdentityCount = 0;

        System.out.println( "Checking on Proteins (rules 14 and 16) ..." );

        //checks 14
        for ( Iterator it = proteins.iterator(); it.hasNext(); ) {
            Protein protein = (Protein) it.next();

            Collection xrefs = protein.getXrefs();
            int count = 0;
            for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
                Xref xref = (Xref) iterator.next();

                if( uniprot.equals( xref.getCvDatabase() ) && identity.equals( xref.getCvXrefQualifier() ) ) {
                    count++;
                }
            } // xrefs

            if( count == 0 ) {
                getUserInfo( proteinWithNoUniprotIdentity, protein );
                proteinWithNoUniprotIdentityCount++;
            } else if( count > 1 ) {
                getUserInfo( proteinWithMoreThanOneUniprotIdentity, protein );
                proteinWithMoreThanOneUniprotIdentityCount++;
            }
        } // proteins

        if( proteinWithNoUniprotIdentityCount > 0 ) {
            writeResults( proteinWithNoUniprotIdentity );
            writer.println();
        }

        if( proteinWithMoreThanOneUniprotIdentityCount > 0 ) {
            writeResults( proteinWithMoreThanOneUniprotIdentity );
            writer.println();
        }
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
        } catch ( SQLException se ) {
            System.out.println( "failed to close statement!!" );
            se.printStackTrace();
        }
    }

    //--------------------------- private methods ------------------------------------------

    /**
     * Helper method to obtain userstamp info from a given record, and
     * then if it has any to append the details to a result buffer.
     *
     * @param buf The result buffer we want the info put into (may want more than one
     *            result displayed for a single Intact type)
     * @param obj The Intact object that user info is required for.
     * @throws SQLException thrown if there were DB problems
     */
    private void getUserInfo( StringBuffer buf, IntactObject obj ) throws SQLException {

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

        //Connection conn = null;
        //stmt = conn.prepareStatement(sql);
        if( results.next() ) {
            user = results.getString( "userstamp" );
            date = results.getTimestamp( "timestamp" );
        }

        buf.append( "AC: " + obj.getAc() + "\t" + " User: " + user + "\t" + "When: " + date + NEW_LINE );
    }

    /**
     * Handles dumping results to a file. If no results were found
     * then an apporpiate message is printed instead.
     *
     * @param buf The data to be dumped to the file
     */
    private void writeResults( StringBuffer buf ) {

        writer.println( buf );
        if( buf.indexOf( "User" ) == -1 ) {
            //none found - write useful message
            writer.println( "No matches for this test." );
            writer.println();
        }

    }

    public static void main( String[] args ) throws Exception {

        IntactHelper helper = null;
        PrintWriter out = null;
        SanityChecker checker = null;

        try {

            String filename = null;
            if( args.length != 1 ) {

                java.util.Date date = new java.util.Date();
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd@HH.mm" );
                String time = formatter.format( date );
                filename = "sanityCheck-" + time + ".txt";

                System.out.println( "Usage: javaRun.sh SanityChecker <filename>" );
                System.out.println( "<filename> automatically set to: " + filename );
            } else {
                filename = args[ 0 ];
            }

            File file = new File( filename );
            System.out.println( "Output will be written in: " + file.getAbsolutePath() );
            out = new PrintWriter( new BufferedWriter( new FileWriter( file ) ) );

            helper = new IntactHelper();

            // Install termination hook, that allows to close cleanly the db connexion if the user hits CTRL+C.
            Runtime.getRuntime().addShutdownHook( new DatabaseConnexionShutdownHook( helper ) );

            System.out.println( "Helper created (User: " + helper.getDbUserName() + " " +
                                "Database: " + helper.getDbName() + ")" );
            System.out.println( "results filename: " + filename );
            out.println( "Checks against Database " + helper.getDbName() );
            out.println( "----------------------------------" );
            out.println();
            System.out.print( "checking data integrity..." );
            checker = new SanityChecker( helper, out );

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

        } catch ( IntactException e ) {

            System.out.println( "Root cause: " + e.getRootCause() );
            e.printStackTrace();
            System.exit( 1 );
        } catch ( EOFException fe ) {

            System.err.println( "End of stream" );
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
            System.err.println( "      eg. java -Xms128m -Xmx576m <className>" );

            System.exit( 1 );
        } catch ( Exception e ) {

            e.printStackTrace();
            System.exit( 1 );
        } finally {

            if( checker != null ) {
                checker.cleanUp();
            }
            if( out != null ) {
                out.close();
            }
        }

        System.exit( 0 );
    }
}