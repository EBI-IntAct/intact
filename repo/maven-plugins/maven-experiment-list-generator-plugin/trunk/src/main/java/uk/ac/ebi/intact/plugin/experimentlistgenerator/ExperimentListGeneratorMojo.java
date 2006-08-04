/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.experimentlistgenerator;

import org.apache.maven.plugin.MojoExecutionException;
import uk.ac.ebi.intact.application.dataConversion.FileHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.util.HibernateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Generates list of experiments
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 *
 * @goal generate-list
 * @phase process-resources
 */
public class ExperimentListGeneratorMojo extends ExperimentListGeneratorAbstractMojo
{

    /**
    * File containing the publications
    * @parameter default-value="%"
    */
    protected String searchPattern;

    /**
     * If true, all experiment without a PubMed ID (primary-reference) will be filtered out.
     *
     * @parameter default-value="true"
     */
    protected boolean onlyWithPmid;

    /**
     * Whether to update the existing project files or overwrite them.
     *
     * @parameter expression="${overwrite}" default-value="false"
     */
    protected boolean overwrite;

    /**
     * @parameter default-value="${project.build.outputDirectory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;
    

    public void execute() throws MojoExecutionException
    {
        getLog().info("ExperimentListGeneratorMojo in action");

        getLog().debug("Using hibernate cfg file: "+hibernateConfig);

        if (!hibernateConfig.exists())
        {
            throw new MojoExecutionException("No hibernate config file found: "+hibernateConfig);
        }

        HibernateUtil.setHibernateConfig(hibernateConfig);

        if (!targetPath.exists())
        {
            targetPath.mkdirs();
        }

        File speciesFile = getSpeciesFile();
        File publicationsFile = getPublicationsFile();

        if (speciesFile.exists() && !overwrite)
        {
            throw new MojoExecutionException("Target species file already exist and overwrite is set to false: "+speciesFile);
        }

        if (publicationsFile.exists() && !overwrite)
        {
            throw new MojoExecutionException("Target publications file already exist and overwrite is set to false: "+speciesFile);
        }

        getLog().debug("Species filename: "+speciesFile);
        getLog().debug("Publications filename: "+publicationsFile);

        IntactTransaction tx = DaoFactory.beginTransaction();

        try
        {
            Writer writerSpecies = new FileWriter( speciesFile );
            Writer writerPublication = new FileWriter( publicationsFile );

            getLog().info( "Using search pattern: " + searchPattern );

            ExperimentClassification classification = classifyExperiments( searchPattern, onlyWithPmid );

            writeExperimentsClassificationBySpecies( classification.getSpecie2experimentSet(),
                                                     classification.getNegativeExperiments(),
                                                     writerSpecies );

            tx.commit();

            writerSpecies.flush();
            writerSpecies.close();

            tx = DaoFactory.beginTransaction();

            writeExperimentsClassificationByPubmed( classification.getPubmed2experimentSet(),
                                                    classification.getNegativeExperiments(),
                                                    writerPublication );
            writerPublication.flush();
            writerPublication.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new MojoExecutionException("Exception when producing the output files", e);
        }
        finally
        {
            if (!tx.wasCommitted())
            {
                tx.commit();
            }
        }
    }

    /**
     * File separator, will be converted to a plateform specific separator later.
     */
    public static final String SLASH = "/";

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
     * @return the collection of experiments
     *
     * @throws uk.ac.ebi.intact.business.IntactException thrown if there was a search problem
     */
    public Collection<Experiment> getExperiments( String searchPattern ) throws IntactException
    {

        //try this for now, but it may be better to use SQL and get the ACs,
        //then cycle through them and generate PSI one by one.....
        ArrayList<Experiment> searchResults = new ArrayList<Experiment>();
        System.out.print( "Retrieving data from DB store..." );
        System.out.flush();

        StringTokenizer patterns = new StringTokenizer( searchPattern, "," );

        while ( patterns.hasMoreTokens() ) {
            String shortlabel = patterns.nextToken().trim();

            searchResults.addAll( DaoFactory.getExperimentDao().getByShortLabelLike(shortlabel));

        }

        int resultSize = searchResults.size();
        logOut( "done (found " + resultSize + " experiment" + ( resultSize > 1 ? "s" : "" ) + ")" );

        return searchResults;
    }

    /**
     * Classify experiments matching searchPattern into a data structure according to species and experiment size.
     *
     * @param searchPattern the pattern to use in search
     *
     * @return HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     * @param forcePubmed only use those experiments with pubmed id
     */
    public ExperimentClassification classifyExperiments( String searchPattern, boolean forcePubmed ) throws IntactException {

        ExperimentClassification classification = new ExperimentClassification();

            if (getLog().isDebugEnabled())
            {
                try
                {
                    getLog().debug( "Database: " + DaoFactory.getBaseDao().getDbName() );
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

            // Obtain data, probably experiment by experiment, build
            // PSI data for it then write it to a file....
            Collection<Experiment> searchResults = getExperiments( searchPattern );

            Set<String> experimentFilter = getExperimentWithoutPubmedId( forcePubmed );

            // Split the list of experiments into species- and size-specific files
        for (Experiment experiment : searchResults)
        {

            if (experimentFilter.contains(experiment.getAc()))
            {
                logOut("Skipping " + experiment.getShortLabel());
                continue;
            }

            // Skip empty experiments and give a warning about'em
            if (experiment.getInteractions().isEmpty())
            {
                logOut("ERROR: experiment " + experiment.getShortLabel() + " (" + experiment.getAc() + ") has no interaction.");
                continue;
            }

            // 1. Get the species of one of the interactors of the experiment.
            //    The bioSource of the Experiment is irrelevant, as it may be an auxiliary experimental system.
            Collection<BioSource> sources = getTargetSpecies(experiment);
            int size = experiment.getInteractions().size();
            logOut("Classifying " + experiment.getShortLabel() + " (" + size + " interaction" + (size > 1 ? "s" : "") + ")");

            // 2. get the pubmedId (primary-ref)
            String pubmedId = getPrimaryId(experiment);

            // 3. create the classification by publication
            if (pubmedId != null)
            {

                Collection<Experiment> experimentSet = null;
                Map<String,Collection<Experiment>> pubmed2experimentSet = classification.getPubmed2experimentSet();

                if (!pubmed2experimentSet.containsKey(pubmedId))
                {
                    // create an empty set
                    experimentSet = new HashSet<Experiment>();
                    pubmed2experimentSet.put(pubmedId, experimentSet);
                }
                else
                {
                    // retreive the existing set
                    experimentSet = pubmed2experimentSet.get(pubmedId);
                }

                // add the experiment to the set of experiments.
                experimentSet.add(experiment);
            }
            else
            {
                logOut("ERROR: Could not find a pubmed ID for experiment: " + experiment.getShortLabel() + "(" + experiment.getAc() + ")");
            }

            // 4. create the classification by species
            Map<BioSource,Collection<Experiment>> specie2experimentSet = classification.getSpecie2experimentSet();

            // if multiple target-species have been found, that experiment will be associated redundantly
            // to each BioSource. only the publication classification is non redundant.
            for (Iterator iterator1 = sources.iterator(); iterator1.hasNext();)
            {
                BioSource source = (BioSource) iterator1.next();

                if (!specie2experimentSet.containsKey(source))
                {
                    // not yet in the structure, create an entry
                    Collection<Experiment> experiments = new HashSet<Experiment>();
                    specie2experimentSet.put(source, experiments);
                }

                // associate experiment to the source
                Collection<Experiment> experiments = specie2experimentSet.get(source);
                experiments.add(experiment);
            }
        }

        // 5. Now all experiments have been sorted, check for those containing 'negative' results...
            classifyNegatives( classification );

        return classification;
    }

    private Set<String> getExperimentWithoutPubmedId( boolean forcePubmed ) throws IntactException {

        Set<String> filter = null;

        if (forcePubmed == false)
        {
            filter = Collections.EMPTY_SET;
        }
        else
        {

            filter = new HashSet<String>();

            List<String[]> expAcAndLabels = ExperimentListGeneratorDao.getExperimentAcAndLabelWithoutPubmedId();

            for (String[] acAndLabel : expAcAndLabels)
            {
                String ac = acAndLabel[0];
                String shortlabel = acAndLabel[1];

                logOut("Filter out: " + shortlabel + " (" + ac + ")");
                filter.add(ac);
            }

            logOut(filter.size() + " experiment filtered out.");

        }

        return filter;
    }

    /**
     * Output the experiment classification, suitable for scripting
     *
     * @param allExp HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     */
    public void writeExperimentsClassificationBySpecies( Map<BioSource,Collection<Experiment>> allExp,
                                                                Collection negExpLabels,
                                                                Writer writer ) throws IOException {

        for (BioSource bioSource : allExp.keySet())
        {

            Collection<Experiment> smallScaleExp = allExp.get(bioSource);

            // split the set into subset of size under SMALL_SCALE_LIMIT
            String filePrefixGlobal = bioSource.getShortLabel().replace(' ', '-');
            Map filename2experimentList = splitExperiment(smallScaleExp,
                    filePrefixGlobal + "_" + SMALL, // small scale
                    filePrefixGlobal);             // large scale

            writeLines(filename2experimentList, negExpLabels, writer);
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
    private String getPrimaryId( Experiment experiment ) {
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
                        logOut( experiment.getShortLabel() + " has pubmedId(" + xref.getPrimaryId() + ") which  is not an integer value, skip it." );
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
     *
     * @return A collection of BioSource, or empty if non is found.
     *
     * @throws IntactException if an error occurs.
     */
    private static Collection<BioSource> getTargetSpecies( Experiment experiment ) throws IntactException {
        Collection<BioSource> species = new ArrayList<BioSource>( 4 );

        for (ExperimentXref xref : experiment.getXrefs())
        {
            if (CvXrefQualifier.TARGET_SPECIES.equals(xref.getCvXrefQualifier().getShortLabel()))
            {
                String taxid = xref.getPrimaryId();
                Collection<BioSource> bioSources = DaoFactory.getBioSourceDao().getByTaxonId(taxid);

                if (bioSources.isEmpty())
                {
                    throw new IntactException("Experiment(" + experiment.getAc() + ", " + experiment.getShortLabel() +
                            ") has a target-species:" + taxid +
                            " but we cannot find the corresponding BioSource.");
                }

                // if choice given, get the less specific one (without tissue, cell type...)
                BioSource selectedBioSource = null;
                for (Iterator iterator1 = bioSources.iterator(); iterator1.hasNext() && selectedBioSource == null;)
                {
                    BioSource bioSource = (BioSource) iterator1.next();
                    if (bioSource.getCvCellType() == null && bioSource.getCvTissue() == null
                            &&
                            bioSource.getCvCellCycle() == null && bioSource.getCvCompartment() == null)
                    {
                        selectedBioSource = bioSource;
                    }
                }

                if (selectedBioSource != null)
                {
                    species.add(selectedBioSource);
                }
                else
                {
                    // add the first one we find
                    species.add(bioSources.iterator().next());
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
    private void classifyNegatives( ExperimentClassification classification ) throws IntactException {

        Collection<Experiment> negExps = classification.getNegativeExperiments();

        negExps.addAll(ExperimentListGeneratorDao.getExpWithInteractionsContainingAnnotation(CvTopic.NEGATIVE));
        negExps.addAll(ExperimentListGeneratorDao.getContainingAnnotation(Experiment.class, CvTopic.NEGATIVE));
        
        logOut( negExps.size() + " negative experiment found." );
    }

    /**
     * Sort a collection of String (shorltabel). The given collection is not modified, a new one is returned.
     *
     * @param experiments collection to sort.
     *
     * @return the sorted collection.
     */
    private static List<String> getSortedShortlabel( Collection<Experiment> experiments ) {

        List<String> sorted = new ArrayList<String>( experiments.size() );

        for (Experiment experiment : experiments)
        {
            sorted.add(experiment.getShortLabel());
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
    private static Map<String,Collection<Experiment>> splitExperiment( Collection<Experiment> experiments, String smallScalePrefix, String largeScalePrefix ) {

        final Collection<Collection<Experiment>> smallScaleChunks = new ArrayList<Collection<Experiment>>();

        final Map<String,Collection<Experiment>> name2smallScale = new HashMap<String,Collection<Experiment>>();
        final Map<String,Collection<Experiment>> name2largeScale = new HashMap<String,Collection<Experiment>>();

        Collection<Experiment> subset = null;

        int sum = 0;

        // 1. Go through the list of experiments and separate the small scale from the large scale.
        //    The filename prefix of the large scale get generated here, though the small scales' get
        //    generated later.
        for (Experiment experiment : experiments)
        {

            final int size = experiment.getInteractions().size();

            if (size >= LARGE_SCALE_CHUNK_SIZE)
            {
                // Process large scale dataset appart from the small ones.

                // generate the large scale format: filePrefix[chunkSize]
                Collection<Experiment> largeScale = new ArrayList<Experiment>(1);
                largeScale.add(experiment);
                // [LARGE_SCALE_CHUNK_SIZE] should be interpreted when producing XML as split that experiment into
                // chunks of size LARGE_SCALE_CHUNK_SIZE.
                String prefix = largeScalePrefix + "_" + experiment.getShortLabel() + "[" + LARGE_SCALE_CHUNK_SIZE + "]";

                // put it in the map
                name2largeScale.put(prefix, largeScale);

            }
            else
            {
                // that experiment is not large scale.

                if (size > SMALL_SCALE_LIMIT)
                {

                    // that experiment by itself is a chunk.
                    // we do not alter the current subset being processed, whether there is one or not.
                    Collection<Experiment> subset2 = new ArrayList<Experiment>(1);
                    subset2.add(experiment);

                    smallScaleChunks.add(subset2);


                }
                else
                    if ((sum + size) >= SMALL_SCALE_LIMIT)
                    {

                        // that experiment would overload that chunk ... then store the subset.

                        if (subset == null)
                        {

                            // that experiment will be a small chunk by itself
                            subset = new ArrayList<Experiment>();
                        }

                        // add the current experiment
                        subset.add(experiment);

                        // put it in the list
                        smallScaleChunks.add(subset);

                        // re-init
                        subset = null;
                        sum = 0;

                    }
                    else
                    {

                        // ( sum + size ) < SMALL_SCALE_LIMIT
                        sum += size;

                        if (subset == null)
                        {
                            subset = new ArrayList<Experiment>();
                        }

                        subset.add(experiment);
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
        for (Collection<Experiment> chunk : smallScaleChunks)
        {
            // generate a prefix
            if (hasMoreThanOneChunk)
            {
                // other prefix in use, use the next chunk id

                // prefix index with a zero if lower than 10, so we get 01, 02, ..., 10, 11 ...
                String indexPrefix = "-";
                if (index < 10)
                {
                    indexPrefix = "-0";
                }

                prefix = smallScalePrefix + indexPrefix + index;
                index++;
            }
            else
            {
                // if no other subset have been stored, we don't bother with chunk id.
                prefix = smallScalePrefix;
            }

            // add to the map
            name2smallScale.put(prefix, chunk);
        }

        // 3. merge both maps
        name2smallScale.putAll( name2largeScale );

        // return merged result
        return name2smallScale;
    }

    /**
     * Given a set of Experiments, it returns the year of the date of creation of the oldest experiment.
      * @param experiments experiments
     * @return an int corresponding to the year.
     */

    private static String getCreatedYear( Set<Experiment> experiments ) {

        if (experiments.isEmpty()){
            throw new IllegalArgumentException("The given Set of Experiments is empty");
        }

        int year = Integer.MAX_VALUE;

        for (Experiment exp : experiments)
        {
            Date created = exp.getCreated();

            java.sql.Date d = new java.sql.Date(created.getTime());
            Calendar c = new GregorianCalendar();
            c.setTime(d);

            if (year > c.get(Calendar.YEAR))
            {
                year = c.get(Calendar.YEAR);
            }
        }

        return String.valueOf( year );
    }


    /**
     * Build the classification by pubmed id.<br/> we keep the negative experiment separated from the non negative.
     *
     * @param pubmed2experimentSet pubmed2experimentSet
     * @param negExps  negExps
     * @param writer writer
     *
     * @throws IOException
     */
    private void writeExperimentsClassificationByPubmed( Map<String,Collection<Experiment>> pubmed2experimentSet,
                                                                Collection<Experiment> negExps,
                                                                Writer writer ) throws IOException {

        List<String> pubmedOrderedList = new ArrayList<String>( pubmed2experimentSet.keySet() );
        Collections.sort( pubmedOrderedList );

        // Go through all clusters and split if needs be.
        for (String pubmedid : pubmedOrderedList)
        {
            // get experiments associated to that pubmed ID.
            Set<Experiment> experiments = (Set<Experiment>) pubmed2experimentSet.get(pubmedid);

            // all experiment under that pubmed if should have the same year
            Experiment exp = experiments.iterator().next();

            String year = getCreatedYear(experiments);
            String prefix = year + SLASH;

            // split the set into subset of size under SMALL_SCALE_LIMIT
            Map<String,Collection<Experiment>> file2experimentSet = splitExperiment(experiments,
                    prefix + pubmedid,   // small scale
                    prefix + pubmedid); // large scale

            // write the line in the pubmed classification file
            writeLines(file2experimentSet, negExps, writer);
        } // pubmeds
    }

    /**
     * Answers the following question: "Is the given shortlabel refering to a negative experiment ?".
     *
     * @param negativeExperiments a list of negative experiment
     * @param experimentLabel     the experiment shortlabel.
     *
     * @return true if the label refers to a negative experiment, false otherwise.
     */
    private static boolean isNegative( Collection<Experiment> negativeExperiments, String experimentLabel ) {

        for (Experiment experiment : negativeExperiments)
        {
            if (experiment.getShortLabel().equals(experimentLabel))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a Map containing the following associations: filename -> List of Experiment, generate a flat file
     * representing these associations for later processing.
     *
     * @param file2experimentSet  the map upon which we generate the file.
     * @param negativeExperiments a collection of known negative experiment.
     * @param writer              the writer to the output file.
     *
     * @throws IOException
     */
    private  void writeLines( Map<String,Collection<Experiment>> file2experimentSet,
            Collection<Experiment> negativeExperiments, Writer writer ) throws IOException {

        // write each subset into the classification file
        List<String> orderedFilenames = new ArrayList<String>( file2experimentSet.keySet() );
        Collections.sort( orderedFilenames );

        for (String filePrefix : orderedFilenames)
        {
            Collection<Experiment> chunk = file2experimentSet.get(filePrefix);

            //buffers to hold the labels for small and negative small exps
            StringBuffer negPattern = new StringBuffer(20);  // AVG 1 experiment
            StringBuffer pattern = new StringBuffer(5 * 20); // AVG 5 experiments

            // sort the collection by alphabetical order
            List shortlabels = getSortedShortlabel(chunk);
            for (Iterator iterator2 = shortlabels.iterator(); iterator2.hasNext();)
            {
                String shortlabel = (String) iterator2.next();

                //put the Experiment label in the correct place, depending upon
                //its sub-classification (ie negative or not)
                // TODO bug here ... the collection contains ACs
                boolean negative = isNegative(negativeExperiments, shortlabel);
                if (negative)
                {
                    negPattern.append(shortlabel);
                }
                else
                {
                    pattern.append(shortlabel);
                }

                if (iterator2.hasNext())
                {
                    if (negative)
                    {
                        negPattern.append(',');
                    }
                    else
                    {
                        pattern.append(',');
                    }
                }
            }

            // classification for this BioSource is output as:
            // '<filename> <comma-seperated shortLabel list>'
            // only print patterns if they are non-empty
            if (pattern.length() != 0)
            {
                String smallFilename = filePrefix + FileHelper.XML_FILE_EXTENSION;
                String line = smallFilename + " " + pattern.toString();
                logOut(line);
                writer.write(line);
                writer.write(NEW_LINE);
            }

            if (negPattern.length() != 0)
            {
                String negativeFilename = filePrefix + "_negative" + FileHelper.XML_FILE_EXTENSION;
                String line = negativeFilename + " " + negPattern.toString();
                logOut(line);
                writer.write(line);
                writer.write(NEW_LINE);
            }
        } // chunk of experiments
    }

    /**
     * Holder for the experiment classification by species and by publication as well as the negative experiments.
     */
    private static class ExperimentClassification {

        /**
         * Classification of experiments by pubmedId
         */
        private Map<String,Collection<Experiment>> pubmed2experimentSet = new HashMap();

        /**
         * Classification of experiments by species
         */
        private Map<BioSource,Collection<Experiment>> specie2experimentSet = new HashMap();

        /**
         * Holds the shortLabels of any Experiments found to contain Interactions with 'negative' information. It has to
         * be a static because the method used for writing the classifications is a static...
         */
        private Set<Experiment> negativeExperiments = new HashSet<Experiment>();

        ////////////////////////////////
        // Constructors

        public ExperimentClassification() {
        }

        /////////////////////////////////
        // Getters and Setters

        public Map<String,Collection<Experiment>> getPubmed2experimentSet() {
            return pubmed2experimentSet;
        }

        public Map<BioSource,Collection<Experiment>> getSpecie2experimentSet() {
            return specie2experimentSet;
        }

        public Set<Experiment> getNegativeExperiments() {
            return negativeExperiments;
        }
    }
}