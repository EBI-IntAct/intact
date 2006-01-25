/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.cli.*;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * <pre>
 * Generates a classified list of experiments based on :
 *  - their count of interaction,
 *  - the fact that they contain negative interaction.
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Jul-2005</pre>
 */
public class ExperimentListGenerator {

    /**
     * Holder for the experiment classification by species and by publication as well as the negative experiments.
     */
    private static class ExperimentClassification {

        /**
         * Classification of experiments by pubmedId
         */
        private Map pubmed2experimentSet = new HashMap();

        /**
         * Classification of experiments by species
         */
        private Map specie2experimentSet = new HashMap();

        /**
         * Holds the shortLabels of any Experiments found to contain Interactions with 'negative' information. It has to
         * be a static because the method used for writing the classifications is a static...
         */
        private Set negativeExperiments = new HashSet();

        ////////////////////////////////
        // Constructors

        public ExperimentClassification() {
        }

        /////////////////////////////////
        // Getters and Setters

        public Map getPubmed2experimentSet() {
            return pubmed2experimentSet;
        }

        public void setPubmed2experimentSet( Map pubmed2experimentSet ) {
            this.pubmed2experimentSet = pubmed2experimentSet;
        }

        public Map getSpecie2experimentSet() {
            return specie2experimentSet;
        }

        public void setSpecie2experimentSet( Map specie2experimentSet ) {
            this.specie2experimentSet = specie2experimentSet;
        }

        public Set getNegativeExperiments() {
            return negativeExperiments;
        }

        public void setNegativeExperiments( Set negativeExperiments ) {
            this.negativeExperiments = negativeExperiments;
        }
    }

    /**
     * File separator, will be converted to a plateform specific separator later.
     */
    public static final String SLASH = "/";

    /**
     * Current time.
     */
    private static String TIME;

    static {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd@HH.mm" );
        TIME = formatter.format( new Date() );
        formatter = null;
    }

    /**
     * Maximum count of interaction for a small scale experiment.
     */
    public static final int SMALL_SCALE_LIMIT = 500;

    /**
     * if an experiment has more than this many interactions it is considered to be large scale.
     */
    public static final int LARGE_SCALE_CHUNK_SIZE = 2000;

    public static final String SMALL = "small";
    public static final String LARGE = "large";

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    //////////////////////////////////////
    // Public methods

    /**
     * Obtains the data from the dataSource, in preparation for the flat file generation.
     *
     * @param searchPattern for search by shortLabel. May be a comma-separated list.
     *
     * @throws IntactException thrown if there was a search problem
     */
    public static Collection getExperiments( IntactHelper helper, String searchPattern ) throws IntactException {

        //try this for now, but it may be better to use SQL and get the ACs,
        //then cycle through them and generate PSI one by one.....
        ArrayList searchResults = new ArrayList();
        System.out.print( "Retrieving data from DB store..." );
        System.out.flush();

        StringTokenizer patterns = new StringTokenizer( searchPattern, "," );

        while ( patterns.hasMoreTokens() ) {
            String experimentShortlabel = patterns.nextToken().trim();
            searchResults.addAll( helper.search( Experiment.class, "shortLabel", experimentShortlabel ) );
        }

        int resultSize = searchResults.size();
        System.out.println( "done (found " + resultSize + " experiment" + ( resultSize > 1 ? "s" : "" ) + ")" );

        return searchResults;
    }

    /**
     * Classify experiments matching searchPattern into a data structure according to species and experiment size.
     *
     * @param searchPattern
     *
     * @return HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     */
    public static ExperimentClassification classifyExperiments( String searchPattern ) throws IntactException {

        ExperimentClassification classification = new ExperimentClassification();
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();

            // Obtain data, probably experiment by experiment, build
            // PSI data for it then write it to a file....
            Collection searchResults = getExperiments( helper, searchPattern );

            // Split the list of experiments into species- and size-specific files
            for ( Iterator iterator = searchResults.iterator(); iterator.hasNext(); ) {
                Experiment experiment = (Experiment) iterator.next();

                // Skip empty experiments and give a warning about'em
                if ( experiment.getInteractions().isEmpty() ) {
                    System.out.println( "ERROR: experiment " + experiment.getShortLabel() + " (" + experiment.getAc() + ") has no interaction." );
                    continue;
                }

                // 1. Get the species of one of the interactors of the experiment.
                //    The bioSource of the Experiment is irrelevant, as it may be an auxiliary experimental system.
                Collection sources = getTargetSpecies( experiment, helper );
                int size = experiment.getInteractions().size();
                System.out.println( "Classifying " + experiment.getShortLabel() + " (" + size + " interaction" + ( size > 1 ? "s" : "" ) + ")" );

                // 2. get the pubmedId (primary-ref)
                String pubmedId = getPrimaryId( experiment );

                // 3. create the classification by publication
                if ( pubmedId != null ) {

                    Set experimentSet = null;
                    Map pubmed2experimentSet = classification.getPubmed2experimentSet();

                    if ( ! pubmed2experimentSet.containsKey( pubmedId ) ) {
                        // create an empty set
                        experimentSet = new HashSet();
                        pubmed2experimentSet.put( pubmedId, experimentSet );
                    } else {
                        // retreive the existing set
                        experimentSet = (Set) pubmed2experimentSet.get( pubmedId );
                    }

                    // add the experiment to the set of experiments.
                    experimentSet.add( experiment );
                } else {
                    System.out.println( "ERROR: Could not find a pubmed ID for experiment: " + experiment.getShortLabel() + "(" + experiment.getAc() + ")" );
                }

                // 4. create the classification by species
                Map specie2experimentSet = classification.getSpecie2experimentSet();

                // if multiple target-species have been found, that experiment will be associated redundantly
                // to each BioSource. only the publication classification is non redundant.
                for ( Iterator iterator1 = sources.iterator(); iterator1.hasNext(); ) {
                    BioSource source = (BioSource) iterator1.next();

                    if ( ! specie2experimentSet.containsKey( source ) ) {
                        // not yet in the structure, create an entry
                        Collection experiments = new HashSet();
                        specie2experimentSet.put( source, experiments );
                    }

                    // associate experiment to the source
                    Collection experiments = (Collection) specie2experimentSet.get( source );
                    experiments.add( experiment );
                }
            }

            // 5. Now all experiments have been sorted, check for those containing 'negative' results...
            classifyNegatives( classification );

        } finally {
            if ( helper != null ) {
                helper.closeStore();
            }
        }

        return classification;
    }

    /**
     * Output the experiment classification, suitable for scripting
     *
     * @param allExp HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     */
    public static void writeExperimentsClassificationBySpecies( Map allExp,
                                                                Collection negExpLabels,
                                                                Writer writer ) throws IOException {

        for ( Iterator iterator = allExp.keySet().iterator(); iterator.hasNext(); ) {

            BioSource bioSource = (BioSource) iterator.next();
            Collection smallScaleExp = (Collection) allExp.get( bioSource );

            // split the set into subset of size under SMALL_SCALE_LIMIT
            String filePrefixGlobal = bioSource.getShortLabel().replace( ' ', '-' );
            Map filename2experimentList = splitExperiment( smallScaleExp,
                                                           filePrefixGlobal + "_" + SMALL, // small scale
                                                           filePrefixGlobal );             // large scale

            writeLines( filename2experimentList, negExpLabels, writer );
        }
    }

    ///////////////////////////////////
    // private helper methods

    /**
     * Fetch publication primaryId from experiment.
     *
     * @param experiment the experiment for which we want the primary pubmed ID.
     *
     * @return a pubmed Id or null if none found.
     */
    private static String getPrimaryId( Experiment experiment ) {
        String pubmedId = null;

        for ( Iterator iterator1 = experiment.getXrefs().iterator(); iterator1.hasNext() && null == pubmedId; ) {
            Xref xref = (Xref) iterator1.next();

            if ( CvDatabase.PUBMED.equals( xref.getCvDatabase().getShortLabel() ) ) {

                if ( xref.getCvXrefQualifier() != null
                     &&
                     CvXrefQualifier.PRIMARY_REFERENCE.equals( xref.getCvXrefQualifier().getShortLabel() ) ) {

                    try {

                        Integer.parseInt( xref.getPrimaryId() );
                        pubmedId = xref.getPrimaryId();

                    } catch ( NumberFormatException e ) {
                        System.out.println( experiment.getShortLabel() + " has pubmedId(" + xref.getPrimaryId() + ") which  is not an integer value, skip it." );
                    }
                }
            }
        }

        return pubmedId;
    }

    /**
     * Retreive BioSources corresponding ot the target-species assigned to the given experiment.
     *
     * @param experiment The experiment for which we want to get all target-species.
     * @param helper     Data source.
     *
     * @return A collection of BioSource, or empty if non is found.
     *
     * @throws IntactException if an error occurs.
     */
    private static Collection getTargetSpecies( Experiment experiment, IntactHelper helper ) throws IntactException {
        Collection species = new ArrayList( 4 );

        for ( Iterator iterator = experiment.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();
            if ( CvXrefQualifier.TARGET_SPECIES.equals( xref.getCvXrefQualifier().getShortLabel() ) ) {
                String taxid = xref.getPrimaryId();
                Collection bioSources = helper.search( BioSource.class, "taxid", taxid );

                if ( bioSources.isEmpty() ) {
                    throw new IntactException( "Experiment(" + experiment.getAc() + ", " + experiment.getShortLabel() +
                                               ") has a target-species:" + taxid +
                                               " but we cannot find the corresponding BioSource." );
                }

                // if choice given, get the less specific one (without tissue, cell type...)
                BioSource selectedBioSource = null;
                for ( Iterator iterator1 = bioSources.iterator(); iterator1.hasNext() && selectedBioSource == null; ) {
                    BioSource bioSource = (BioSource) iterator1.next();
                    if ( bioSource.getCvCellType() == null && bioSource.getCvTissue() == null
                         &&
                         bioSource.getCvCellCycle() == null && bioSource.getCvCompartment() == null ) {
                        selectedBioSource = bioSource;
                    }
                }

                if ( selectedBioSource != null ) {
                    species.add( selectedBioSource );
                } else {
                    // add the first one we find
                    species.add( bioSources.iterator().next() );
                }
            }
        }

        return species;
    }

    /**
     * Checks for a negative interaction. NB This will have to be done using SQL otherwise we end up materializing all
     * interactions just to do the check.
     * <p/>
     * Also the new intact curation rules specify that an Experiment should ONLY contain negative Interactions if it is
     * annotated as 'negative'. Thus to decide if an Experiment is classified as 'negative', the Annotations of that
     * Experiment need to be checked for one with a 'negative' Controlled Vocab attached to it as a topic. </p>
     * <p/>
     * However at some point in the future there may be a possibility that only the Interactions will be marked as
     * 'negative' (not the Experiment), and so these should be checked also, with duplicate matches being ignored. </p>
     * This method has to be static because it is called by the static 'classifyExperiments'.
     */
    private static void classifyNegatives( ExperimentClassification classification ) throws IntactException {

        Collection negExpLabels = classification.getNegativeExperiments();

        // Query to get at the Experiment ACs containing negative interaction annotations
        String sql = "SELECT experiment_ac " +
                     "FROM ia_int2exp " +
                     "WHERE interaction_ac in " +
                     "   (SELECT interactor_ac " +
                     "    FROM ia_int2annot " +
                     "    WHERE annotation_ac in " +
                     "       (SELECT ac " +
                     "        FROM ia_annotation " +
                     "        WHERE topic_ac in " +
                     "            (SELECT ac " +
                     "             FROM ia_controlledvocab " +
                     "             WHERE shortlabel='" + CvTopic.NEGATIVE + "'" +
                     "            )" +
                     "        )" +
                     "    )";

        // Query to obtain Experiment ACs by searching for an Annotation for the
        // Experiment classified as 'negative' itself
        String expSql = "SELECT experiment_ac " +
                        "FROM ia_exp2annot " +
                        "WHERE annotation_ac in " +
                        "     (SELECT ac " +
                        "      FROM ia_annotation " +
                        "      WHERE topic_ac in " +
                        "            (SELECT ac " +
                        "             FROM ia_controlledvocab " +
                        "             WHERE shortlabel = '" + CvTopic.NEGATIVE + "'" +
                        "            )" +
                        "     )";

        Set expAcs = new HashSet( 1024 ); //used to collect ACs from a query - Set avoids duplicates

        Connection conn = null;
        Statement stmt = null;  //ordinary Statement will do - won't be reused
        PreparedStatement labelStmt = null; //needs a parameter
        ResultSet rs = null;
        IntactHelper helper = null;

        try {
            helper = new IntactHelper();

            // Safest way to do this is directly through the Connection.....
            conn = helper.getJDBCConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery( expSql );
            while ( rs.next() ) {
                //stick them into the Set of ACs
                expAcs.add( rs.getString( "experiment_ac" ) );
            }
            rs.close();
            stmt.close();

            // Now query via the Interactions...
            stmt = conn.createStatement();
            rs = stmt.executeQuery( sql );
            while ( rs.next() ) {
                //stick them into the Set of ACs
                expAcs.add( rs.getString( "experiment_ac" ) );
            }
            rs.close();
            stmt.close();
            // do not close the connexion ... helper.closeStore() takes care of giving it back to the connexion pool.

            // Now get the Experiments by AC as these are what we need...
            for ( Iterator it = expAcs.iterator(); it.hasNext(); ) {

                String ac = (String) it.next();
                Experiment experiment = (Experiment) helper.getObjectByAc( Experiment.class, ac );
                negExpLabels.add( experiment );
            }

        } catch ( SQLException se ) {

            System.out.println( se.getSQLState() );
            System.out.println( se.getErrorCode() );
            se.printStackTrace();

            while ( ( se.getNextException() ) != null ) {
                System.out.println( se.getSQLState() );
                System.out.println( se.getErrorCode() );
                se.printStackTrace();
            }

        } finally {
            try {
                if ( stmt != null ) {
                    stmt.close();
                }
                if ( labelStmt != null ) {
                    labelStmt.close();
                }

                if ( helper != null ) {
                    helper.closeStore();
                }
            } catch ( SQLException se ) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Sort a collection of String (shorltabel). The given collection is not modified, a new one is returned.
     *
     * @param l collection to sort.
     *
     * @return the sorted collection.
     */
    private static List getSortedShortlabel( Collection l ) {

        List sorted = new ArrayList( l.size() );

        for ( Iterator iterator = l.iterator(); iterator.hasNext(); ) {
            Experiment experiment = (Experiment) iterator.next();
            sorted.add( experiment.getShortLabel() );
        }

        Collections.sort( sorted );
        return sorted;
    }

    /**
     * Split a set of experiment into (if necessary) subsets so that each subset has not more interaction than
     * LARGE_SCALE_CHUNK_SIZE.
     *
     * @param experiments      the set of experiments.
     * @param smallScalePrefix the prefix for small scale files.
     * @param largeScalePrefix the prefix for large scale files.
     *
     * @return a map (filename_prefix -> subset)
     */
    private static Map splitExperiment( Collection experiments, String smallScalePrefix, String largeScalePrefix ) {

        final Collection smallScaleChunks = new ArrayList();

        final Map name2smallScale = new HashMap();
        final Map name2largeScale = new HashMap();

        Collection subset = null;

        int sum = 0;

        // 1. Go through the list of experiments and separate the small scale from the large scale.
        //    The filename prefix of the large scale get generated here, though the small scales' get
        //    generated later.
        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {

            Experiment experiment = (Experiment) iterator.next();
            final int size = experiment.getInteractions().size();

            if ( size >= LARGE_SCALE_CHUNK_SIZE ) {
                // Process large scale dataset appart from the small ones.

                // generate the large scale format: filePrefix[chunkSize]
                Collection largeScale = new ArrayList( 1 );
                largeScale.add( experiment );
                // [LARGE_SCALE_CHUNK_SIZE] should be interpreted when producing XML as split that experiment into
                // chunks of size LARGE_SCALE_CHUNK_SIZE.
                String prefix = largeScalePrefix + "_" + experiment.getShortLabel() + "[" + LARGE_SCALE_CHUNK_SIZE + "]";

                // put it in the map
                name2largeScale.put( prefix, largeScale );

            } else {
                // that experiment is not large scale.

                if ( size > SMALL_SCALE_LIMIT ) {

                    // that experiment by itself is a chunk.
                    // we do not alter the current subset being processed, whether there is one or not.
                    Collection subset2 = new ArrayList( 1 );
                    subset2.add( experiment );

                    smallScaleChunks.add( subset2 );


                } else if ( ( sum + size ) >= SMALL_SCALE_LIMIT ) {

                    // that experiment would overload that chunk ... then store the subset.

                    if ( subset == null ) {

                        // that experiment will be a small chunk by itself
                        subset = new ArrayList();
                    }

                    // add the current experiment
                    subset.add( experiment );

                    // put it in the list
                    smallScaleChunks.add( subset );

                    // re-init
                    subset = null;
                    sum = 0;

                } else {

                    // ( sum + size ) < SMALL_SCALE_LIMIT
                    sum += size;

                    if ( subset == null ) {
                        subset = new ArrayList();
                    }

                    subset.add( experiment );
                }

            } // else
        } // experiments

        if ( subset != null && ( ! subset.isEmpty() ) ) {

            // put it in the list
            smallScaleChunks.add( subset );
        }

        // 2. Look at the list of small scale chunks and generate their filename prefixes
        //    Note: no index if only one chunk
        boolean hasMoreThanOneChunk = ( smallScaleChunks.size() > 1 );
        int index = 1;
        String prefix = null;
        for ( Iterator iterator = smallScaleChunks.iterator(); iterator.hasNext(); ) {
            Collection chunk = (Collection) iterator.next();

            // generate a prefix
            if ( hasMoreThanOneChunk ) {
                // other prefix in use, use the next chunk id
                prefix = smallScalePrefix + "-" + index;
                index++;
            } else {
                // if no other subset have been stored, we don't bother with chunk id.
                prefix = smallScalePrefix;
            }

            // add to the map
            name2smallScale.put( prefix, chunk );
        }

        // 3. merge both maps
        name2smallScale.putAll( name2largeScale );

        // return merged result
        return name2smallScale;
    }


    private static String getCreatedYear( Experiment exp ) {

        Timestamp created = exp.getCreated();
        java.sql.Date d = new java.sql.Date( created.getTime() );
        Calendar c = new GregorianCalendar();
        c.setTime( d );

        int year = c.get( Calendar.YEAR );

        return String.valueOf( year );
    }

    /**
     * Build the classification by pubmed id.<br> we keep the negative experiment separated from the non negative.
     *
     * @param pubmed2experimentSet
     * @param negExpLabels
     * @param writer
     *
     * @throws IOException
     */
    private static void writeExperimentsClassificationByPubmed( Map pubmed2experimentSet,
                                                                Collection negExpLabels,
                                                                Writer writer ) throws IOException {

        List pubmedOrderedList = new ArrayList( pubmed2experimentSet.keySet() );
        Collections.sort( pubmedOrderedList );

        // Go through all clusters and split if needs be.
        for ( Iterator iterator = pubmedOrderedList.iterator(); iterator.hasNext(); ) {
            String pubmedid = (String) iterator.next();

            // get experiments associated to that pubmed ID.
            Collection experiments = (Set) pubmed2experimentSet.get( pubmedid );

            // all experiment under that pubmed if should have the same year
            Experiment exp = (Experiment) experiments.iterator().next();

            String year = getCreatedYear( exp );
            String prefix = year + SLASH;


            // split the set into subset of size under SMALL_SCALE_LIMIT
            Map file2experimentSet = splitExperiment( experiments,
                                                      prefix + pubmedid,   // small scale
                                                      prefix + pubmedid ); // large scale

            writeLines( file2experimentSet, negExpLabels, writer );

        } // pubmeds
    }

    /**
     * Given a Map containing the following associations: filename -> List of Experiment, generate a flat file
     * representing these associations for later processing.
     *
     * @param file2experimentSet the map upon which we generate the file.
     * @param negExpLabels       a collection of known negative experiment.
     * @param writer             the writer to the output file.
     *
     * @throws IOException
     */
    private static void writeLines( Map file2experimentSet, Collection negExpLabels, Writer writer ) throws IOException {

        // write each subset into the classification file
        List orderedFilenames = new ArrayList( file2experimentSet.keySet() );
        Collections.sort( orderedFilenames );

        for ( Iterator iterator1 = orderedFilenames.iterator(); iterator1.hasNext(); ) {
            String filePrefix = (String) iterator1.next();
            Collection chunk = (Collection) file2experimentSet.get( filePrefix );

            //buffers to hold the labels for small and negative small exps
            StringBuffer negPattern = new StringBuffer( 20 ); // AVG 1 experiment
            StringBuffer pattern = new StringBuffer( 100 );   // AVG 5 experiments

            // sort the collection by alphabetical order
            List shortlabels = getSortedShortlabel( chunk );
            for ( Iterator iterator2 = shortlabels.iterator(); iterator2.hasNext(); ) {
                String shortlabel = (String) iterator2.next();

                //put the Experiment label in the correct place, depending upon
                //its sub-classification (ie negative or not)
                if ( negExpLabels.contains( shortlabel ) ) {
                    negPattern.append( shortlabel );
                } else {
                    pattern.append( shortlabel );
                }

                if ( iterator2.hasNext() ) {
                    pattern.append( "," );
                }
            }

            // classification for this BioSource is output as:
            // '<filename> <comma-seperated shortLabel list>'
            // only print patterns if they are non-empty
            if ( pattern.length() != 0 ) {
                String smallFilename = filePrefix + ".xml";
                String line = smallFilename + " " + pattern.toString();
                System.out.println( line );
                writer.write( line );
                writer.write( NEW_LINE );
            }

            if ( negPattern.length() != 0 ) {
                String negativeFilename = filePrefix + "_negative.xml";
                String line = negativeFilename + " " + negPattern.toString();
                System.out.println( line );
                writer.write( line );
                writer.write( NEW_LINE );
            }
        } // chunk of experiments
    }

    private static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ExperimentListGenerator " +
                             "-outputFilePrefix <filename> " +
                             "-pattern <shortlabel pattern> ",
                             options );
    }

    /**
     * Run the program that create a flat file containing the classification of IntAct experiment for PSI download.
     *
     * @param args -output <filename> -pattern <shortlabel pattern>
     *
     * @throws IntactException
     * @throws IOException
     */
    public static void main( String[] args ) throws IntactException, IOException {

        // if only one argument, then dump the matching experiment classified by specied into a file

        // create Option objects
        Option helpOpt = new Option( "help", "print this message." );

        Option outputSpeciesOpt = OptionBuilder.withArgName( "outputSpeciesFilenamePrefix" )
                .hasArg()
                .withDescription( "output filename prefix" )
                .create( "speciesFile" );
        outputSpeciesOpt.setRequired( false );

        Option outputPublicationOpt = OptionBuilder.withArgName( "outputPublicationFilenamePrefix" )
                .hasArg()
                .withDescription( "output filename prefix" )
                .create( "publicationsFile" );
        outputPublicationOpt.setRequired( false );

        Option patternOpt = OptionBuilder.withArgName( "experimentPattern" )
                .hasArg()
                .withDescription( "experiment shortlabel pattern" )
                .create( "pattern" );
        patternOpt.setRequired( false );

        Options options = new Options();
        options.addOption( helpOpt );
        options.addOption( outputSpeciesOpt );
        options.addOption( outputPublicationOpt );
        options.addOption( patternOpt );

        // create the parser
        CommandLineParser parser = new BasicParser();
        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse( options, args, true );
        } catch ( ParseException exp ) {
            // Oops, something went wrong

            displayUsage( options );

            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit( 1 );
        }

        if ( line.hasOption( "help" ) ) {
            displayUsage( options );
            System.exit( 0 );
        }

        // Process arguments
        String speciesFilename = line.getOptionValue( "speciesFile" );
        String publicationsFilename = line.getOptionValue( "publicationsFile" );
        File fileSpecies = null;
        File filePublication = null;

        if ( speciesFilename != null ) {
            // handle species file name
            try {
                fileSpecies = new File( speciesFilename );
                if ( fileSpecies.exists() ) {
                    System.err.println( "Please give a new file name for the output file: " + fileSpecies.getAbsoluteFile() );
                    System.err.println( "We will use the default filename instead (instead of overwritting the existing file)." );
                    speciesFilename = null;
                    fileSpecies = null;
                }
            } catch ( Exception e ) {
                // nothing, the default filename will be given
            }
        }

        if ( publicationsFilename != null ) {
            // handle publication file name
            try {
                filePublication = new File( publicationsFilename );
                if ( filePublication.exists() ) {
                    System.err.println( "Please give a new file name for the output file: " + filePublication.getAbsoluteFile() );
                    System.err.println( "We will use the default filename instead (instead of overwritting the existing file)." );
                    publicationsFilename = null;
                    filePublication = null;
                }
            } catch ( Exception e ) {
                // nothing, the default filename will be given
            }
        }

        if ( fileSpecies == null | filePublication == null ) {
            String detaultPrefix = "classification_" + TIME;

            if ( fileSpecies == null ) {
                String filename = detaultPrefix + "_by_species.txt";
                System.out.println( "Using default filename for the export by species: " + filename );
                fileSpecies = new File( filename );
            }

            if ( filePublication == null ) {
                String filename = detaultPrefix + "_by_publication.txt";
                System.out.println( "Using default filename for the export by publications: " + filename );
                filePublication = new File( filename );
            }
        }

        Writer writerSpecies = new FileWriter( fileSpecies );
        Writer writerPublication = new FileWriter( filePublication );

        System.out.println( "Species fileName:     " + fileSpecies.getAbsolutePath() );
        System.out.println( "Publication fileName: " + filePublication.getAbsolutePath() );

        String pattern = line.getOptionValue( "pattern" );
        if ( pattern == null || pattern.trim().equals( "" ) ) {
            pattern = "%";
        }

        System.err.println( "Pattern: " + pattern );

        ExperimentClassification classification = classifyExperiments( pattern );

        writeExperimentsClassificationBySpecies( classification.getSpecie2experimentSet(),
                                                 classification.getNegativeExperiments(),
                                                 writerSpecies );
        writerSpecies.flush();
        writerSpecies.close();

        writeExperimentsClassificationByPubmed( classification.getPubmed2experimentSet(),
                                                classification.getNegativeExperiments(),
                                                writerPublication );
        writerPublication.flush();
        writerPublication.close();
    }
}