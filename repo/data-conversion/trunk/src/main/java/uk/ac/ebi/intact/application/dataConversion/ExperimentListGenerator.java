/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.dataConversion.dao.ExperimentListGeneratorDao;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static final Log log = LogFactory.getLog(ExperimentListGenerator.class);

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

    public static final String NEW_LINE = System.getProperty("line.separator");

    private static final int MAX_EXPERIMENTS_PER_CHUNK_DEFAULT = 100;

    private enum Classification
    { SPECIES, PUBLICATIONS };

    /**
     * Pattern used to select experiment by its label
     */
    protected String searchPattern;

    /**
     * If true, all experiment without a PubMed ID (primary-reference) will be filtered out.
     */
    protected boolean onlyWithPmid = true;

    private List<ExperimentListItem> speciesListItems = new ArrayList<ExperimentListItem>();
    private List<ExperimentListItem> publicationsListItems = new ArrayList<ExperimentListItem>();

    private boolean experimentsClassified;
    private Set<String> filteredExperimentAcs;

    private int experimentsPerChunk = MAX_EXPERIMENTS_PER_CHUNK_DEFAULT;

    /**
     * The key being the experiment AC and the value the number of interactions for that experiment
     */
    private Map<String,Integer> interactionCount;

    private Map<String,String> expAcToPmid;

    private Map<String,List<String>> expAcToTaxid;

    private Map<String,BioSource> targetSpeciesCache = new HashMap<String,BioSource>();

    /**
     * Classification of experiments by pubmedId
     */
    private Map<String, Collection<SimplifiedAnnotatedObject<Experiment>>> pubmed2experimentSet = new HashMap<String, Collection<SimplifiedAnnotatedObject<Experiment>>>();

    /**
     * Classification of experiments by species
     */
    private Map<SimplifiedAnnotatedObject<BioSource>, Collection<SimplifiedAnnotatedObject<Experiment>>> species2experimentSet = new HashMap<SimplifiedAnnotatedObject<BioSource>, Collection<SimplifiedAnnotatedObject<Experiment>>>();

    /**
     * Holds the shortLabels of any Experiments found to contain Interactions with 'negative' information. It has to
     * be a static because the method used for writing the classifications is a static...
     */
    private Set<Experiment> negativeExperiments = new HashSet<Experiment>();

    private Map<String,String> experimentsWithErrors = new HashMap<String,String>();

    public ExperimentListGenerator()
    {
        this("%");
    }

    public ExperimentListGenerator(String searchPattern)
    {
        this.searchPattern = searchPattern;
    }

    public List<ExperimentListItem> generateClassificationBySpecies()
    {
        if (!experimentsClassified)
        {
            classifyExperiments();
        }

        createItemClassificationBySpecies();

        return speciesListItems;
    }

    public List<ExperimentListItem> generateClassificationByPublications()
    {
        if (!experimentsClassified)
        {
            classifyExperiments();
        }

        createItemClassificationByPubmed();

        return publicationsListItems;
    }

    public Set<Experiment> getNegativeExperiments()
    {
        if (!experimentsClassified)
        {
            classifyExperiments();
        }

        return negativeExperiments;
    }

    public Map<String,String> getExperimentWithErrors()
    {
        if (!experimentsClassified)
        {
            classifyExperiments();
        }

        return experimentsWithErrors;
    }

    /**
     * Classify experiments matching searchPattern into a data structure according to species and experiment size.
     *
     * @return HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     */
    private void classifyExperiments()
    {

        if (log.isDebugEnabled())
        {
            try
            {
                log.debug("Database: " + getDaoFactory().getBaseDao().getDbName());
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        // Obtain data, probably experiment by experiment, build
        // PSI data for it then write it to a file....
        Collection<Experiment> searchResults;
        int firstResult = 0;

        Set<String> experimentFilter = getFilteredExperimentAcs();

        do
        {
            searchResults = getExperiments(firstResult, experimentsPerChunk);

            // Split the list of experiments into species- and size-specific files
            for (Experiment experiment : searchResults)
            {
                if (experimentFilter.contains(experiment.getAc()))
                {
                    log.debug("Skipping " + experiment.getShortLabel());
                    continue;
                }

                int interactionCount = interactionsForExperiment(experiment.getAc());
                // Skip empty experiments and give a warning about'em
                if (interactionCount == 0)
                {
                    log.debug("ERROR: experiment " + experiment.getShortLabel() + " (" + experiment.getAc() + ") has no interaction.");
                    experimentsWithErrors.put(experiment.getShortLabel(), "Experiment without interactions");
                    continue;
                }

                // 1. Get the species of one of the interactors of the experiment.
                //    The bioSource of the Experiment is irrelevant, as it may be an auxiliary experimental system.
                Collection<BioSource> sources = getTargetSpecies(experiment.getAc());

                log.debug("Classifying " + experiment.getShortLabel() + " (" + interactionCount + " interaction" + (interactionCount > 1 ? "s" : "") + ")");

                // 2. get the pubmedId (primary-ref)
                String pubmedId = String.valueOf(getPubmedId(experiment.getAc()));

                // 3. create the classification by publication
                if (pubmedId != null)
                {

                    Collection<SimplifiedAnnotatedObject<Experiment>> experimentSet = null;

                    if (!pubmed2experimentSet.containsKey(pubmedId))
                    {
                        // create an empty set
                        experimentSet = new HashSet<SimplifiedAnnotatedObject<Experiment>>();
                        pubmed2experimentSet.put(pubmedId, experimentSet);
                    }
                    else
                    {
                        // retreive the existing set
                        experimentSet = pubmed2experimentSet.get(pubmedId);
                    }

                    // add the experiment to the set of experiments.
                    experimentSet.add(new SimplifiedAnnotatedObject<Experiment>(experiment));
                }
                else
                {
                    log.debug("ERROR: Could not find a pubmed ID for experiment: " + experiment.getShortLabel() + "(" + experiment.getAc() + ")");
                }

                // if multiple target-species have been found, that experiment will be associated redundantly
                // to each BioSource. only the publication classification is non redundant.
                for (BioSource bioSource : sources)
                {
                    SimplifiedAnnotatedObject<BioSource> source = new SimplifiedAnnotatedObject<BioSource>(bioSource);

                    if (!species2experimentSet.containsKey(source))
                    {
                        // not yet in the structure, create an entry
                        Collection<SimplifiedAnnotatedObject<Experiment>> experiments = new HashSet<SimplifiedAnnotatedObject<Experiment>>();
                        species2experimentSet.put(source, experiments);
                    }

                    // associate experiment to the source
                    Collection<SimplifiedAnnotatedObject<Experiment>> experiments = species2experimentSet.get(source);
                    experiments.add(new SimplifiedAnnotatedObject<Experiment>(experiment));
                }
            }

            firstResult = firstResult + experimentsPerChunk;

        } while (!searchResults.isEmpty());

        //Now all experiments have been sorted, check for those containing 'negative' results...
        classifyNegatives();

        experimentsClassified = true;

    }

    private List<Experiment> getExperiments(int firstResult, int maxResults)
    {
        ArrayList<Experiment> searchResults = new ArrayList<Experiment>();
        log.debug("Retrieving data from DB store, from "+firstResult);

        StringTokenizer patterns = new StringTokenizer(searchPattern, ",");

        while (patterns.hasMoreTokens())
        {
            String shortlabel = patterns.nextToken().trim();

            searchResults.addAll(getDaoFactory().getExperimentDao().getByShortLabelLike(shortlabel, true, firstResult, maxResults, true));

        }

        int resultSize = searchResults.size();
        log.debug("done (retrieved " + resultSize + " experiment" + (resultSize > 1 ? "s" : "") + ")");

        return searchResults;
    }

    /**
     * Retreive BioSources corresponding ot the target-species assigned to the given experiment.
     *
     * @param experimentAc  The experiment AC for which we want to get all target-species.
     * @return A collection of BioSource, or empty if non is found.
     * @throws IntactException if an error occurs.
     */
    private Collection<BioSource> getTargetSpecies(String experimentAc) throws IntactException
    {
        List<String> taxIds = taxIdsForExperiment(experimentAc);

        List<BioSource> targetSpeciesList = new ArrayList<BioSource>();

        for (String taxId : taxIds)
        {
            if (targetSpeciesCache.containsKey(taxId))
            {
                targetSpeciesList.add(targetSpeciesCache.get(taxId));
            }

            Collection<BioSource> bioSources = getDaoFactory().getBioSourceDao().getByTaxonId(taxId);

            if (bioSources.isEmpty())
            {
                throw new IntactException("Experiment(" + experimentAc + ") has a target-species:" + taxId +
                        " but we cannot find the corresponding BioSource.");
            }

            BioSource targetSpecies;

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
                targetSpecies = selectedBioSource;
            }
            else
            {
                // add the first one we find
                targetSpecies = bioSources.iterator().next();
            }

            targetSpeciesCache.put(experimentAc, targetSpecies);
            targetSpeciesList.add(targetSpecies);
        }

        return targetSpeciesList;
    }

    /**
     * Fetch publication primaryId from experiment.
     *
     * @param experimentAc the experiment AC for which we want the primary pubmed ID.
     * @return a pubmed Id or null if none found.
     */
    private String getPubmedId(String experimentAc)
    {
        if (expAcToPmid == null)
        {
            // map all exps to pmid
            expAcToPmid = ExperimentListGeneratorDao.getExperimentAcAndPmid(searchPattern);
        }

        String pubmedId = expAcToPmid.get(experimentAc);

        if (pubmedId == null)
        {
            experimentsWithErrors.put(experimentAc, "Null pubmed Id");
        }

        try
        {
            Integer.parseInt(pubmedId);
        }
        catch (NumberFormatException e)
        {
            experimentsWithErrors.put(experimentAc, "Not a number pubmedId");
        }

        return pubmedId;
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
    private void classifyNegatives()
    {
        negativeExperiments.addAll(ExperimentListGeneratorDao.getExpWithInteractionsContainingAnnotation(CvTopic.NEGATIVE, searchPattern));
        negativeExperiments.addAll(ExperimentListGeneratorDao.getContainingAnnotation(Experiment.class, CvTopic.NEGATIVE, searchPattern));

        log.debug(negativeExperiments.size() + " negative experiment found.");
    }

    public Set<String> getFilteredExperimentAcs()
    {
        if (filteredExperimentAcs != null)
        {
            return filteredExperimentAcs;
        }

        if (!onlyWithPmid)
        {
            filteredExperimentAcs = Collections.EMPTY_SET;
        }
        else
        {

            filteredExperimentAcs = new HashSet<String>();

            Map<String, String> expAcAndLabels = ExperimentListGeneratorDao.getExperimentAcAndLabelWithoutPubmedId(searchPattern);

            for (Map.Entry<String, String> expAcAndLabel : expAcAndLabels.entrySet())
            {
                String ac = expAcAndLabel.getKey();
                String shortlabel = expAcAndLabel.getValue();

                log.debug("Filter out: " + shortlabel + " (" + ac + ")");
                filteredExperimentAcs.add(ac);
            }

            log.debug(filteredExperimentAcs.size() + " experiment filtered out.");

        }

        return filteredExperimentAcs;
    }

    public void createItemClassificationBySpecies()
    {

        for (SimplifiedAnnotatedObject<BioSource> bioSource : species2experimentSet.keySet())
        {

            Collection<SimplifiedAnnotatedObject<Experiment>> smallScaleExp = species2experimentSet.get(bioSource);

            // split the set into subset of size under SMALL_SCALE_LIMIT
            String filePrefixGlobal = bioSource.getShortLabel().replace(' ', '-');
            Map<String, Collection<SimplifiedAnnotatedObject<Experiment>>> filename2experimentList =
                    splitExperiment(smallScaleExp,
                            filePrefixGlobal + "_" + SMALL, // small scale
                            filePrefixGlobal);             // large scale

            createExperimentListItems(filename2experimentList, Classification.SPECIES);
        }
    }

    /**
     * Build the classification by pubmed id.<br/> we keep the negative experiment separated from the non negative.
     *
     * @throws IOException
     */
    private void createItemClassificationByPubmed()
    {

        List<String> pubmedOrderedList = new ArrayList<String>(pubmed2experimentSet.keySet());
        Collections.sort(pubmedOrderedList);

        // Go through all clusters and split if needs be.
        for (String pubmedid : pubmedOrderedList)
        {
            // get experiments associated to that pubmed ID.
            Set<SimplifiedAnnotatedObject<Experiment>> experiments = (Set<SimplifiedAnnotatedObject<Experiment>>) pubmed2experimentSet.get(pubmedid);

            // all experiment under that pubmed if should have the same year
            SimplifiedAnnotatedObject<Experiment> exp = experiments.iterator().next();

            String year = getCreatedYear(experiments);
            String prefix = year + FileGenerator.SLASH;

            // split the set into subset of size under SMALL_SCALE_LIMIT
            Map<String, Collection<SimplifiedAnnotatedObject<Experiment>>> file2experimentSet = splitExperiment(experiments,
                    prefix + pubmedid,   // small scale
                    prefix + pubmedid); // large scale

            // write the line in the pubmed classification file
            createExperimentListItems(file2experimentSet, Classification.PUBLICATIONS);
        } // pubmeds
    }

    /**
     * Given a set of Experiments, it returns the year of the date of creation of the oldest experiment.
     *
     * @param experiments experiments
     * @return an int corresponding to the year.
     */

    private static String getCreatedYear(Set<SimplifiedAnnotatedObject<Experiment>> experiments)
    {

        if (experiments.isEmpty())
        {
            throw new IllegalArgumentException("The given Set of Experiments is empty");
        }

        int year = Integer.MAX_VALUE;

        for (SimplifiedAnnotatedObject<Experiment> exp : experiments)
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

        return String.valueOf(year);
    }

    /**
     * Split a set of experiment into (if necessary) subsets so that each subset has not more interaction than
     * LARGE_SCALE_CHUNK_SIZE.
     *
     * @param experiments      the set of experiments.
     * @param smallScalePrefix the prefix for small scale files.
     * @param largeScalePrefix the prefix for large scale files.
     * @return a map (filename_prefix -> subset)
     */
    private Map<String, Collection<SimplifiedAnnotatedObject<Experiment>>> splitExperiment(Collection<SimplifiedAnnotatedObject<Experiment>> experiments, String smallScalePrefix, String largeScalePrefix)
    {

        final Collection<Collection<SimplifiedAnnotatedObject<Experiment>>> smallScaleChunks = new ArrayList<Collection<SimplifiedAnnotatedObject<Experiment>>>();

        final Map<String, Collection<SimplifiedAnnotatedObject<Experiment>>> name2smallScale = new HashMap<String, Collection<SimplifiedAnnotatedObject<Experiment>>>();
        final Map<String, Collection<SimplifiedAnnotatedObject<Experiment>>> name2largeScale = new HashMap<String, Collection<SimplifiedAnnotatedObject<Experiment>>>();

        Collection<SimplifiedAnnotatedObject<Experiment>> subset = null;

        int sum = 0;

        // 1. Go through the list of experiments and separate the small scale from the large scale.
        //    The filename prefix of the large scale get generated here, though the small scales' get
        //    generated later.
        for (SimplifiedAnnotatedObject<Experiment> experiment : experiments)
        {

            final int size = interactionsForExperiment(experiment.getAc());

            if (size >= LARGE_SCALE_CHUNK_SIZE)
            {
                // Process large scale dataset appart from the small ones.

                // generate the large scale format: filePrefix[chunkSize]
                Collection<SimplifiedAnnotatedObject<Experiment>> largeScale = new ArrayList<SimplifiedAnnotatedObject<Experiment>>(1);
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
                    Collection<SimplifiedAnnotatedObject<Experiment>> subset2 = new ArrayList<SimplifiedAnnotatedObject<Experiment>>(1);
                    subset2.add(experiment);

                    smallScaleChunks.add(subset2);


                }
                else if ((sum + size) >= SMALL_SCALE_LIMIT)
                {

                    // that experiment would overload that chunk ... then store the subset.

                    if (subset == null)
                    {

                        // that experiment will be a small chunk by itself
                        subset = new ArrayList<SimplifiedAnnotatedObject<Experiment>>();
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
                        subset = new ArrayList<SimplifiedAnnotatedObject<Experiment>>();
                    }

                    subset.add(experiment);
                }

            } // else
        } // experiments

        if (subset != null && (!subset.isEmpty()))
        {

            // put it in the list
            smallScaleChunks.add(subset);
        }

        // 2. Look at the list of small scale chunks and generate their filename prefixes
        //    Note: no index if only one chunk
        boolean hasMoreThanOneChunk = (smallScaleChunks.size() > 1);
        int index = 1;
        String prefix = null;
        for (Collection<SimplifiedAnnotatedObject<Experiment>> chunk : smallScaleChunks)
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
        name2smallScale.putAll(name2largeScale);

        // return merged result
        return name2smallScale;
    }

    /**
     * Given a Map containing the following associations: filename -> List of Experiment, generate a flat file
     * representing these associations for later processing.
     *
     * @param file2experimentSet the map upon which we generate the file.
     * @throws java.io.IOException
     */
    private void createExperimentListItems(Map<String, Collection<SimplifiedAnnotatedObject<Experiment>>> file2experimentSet, Classification classification)
    {

        // write each subset into the classification file
        List<String> orderedFilenames = new ArrayList<String>(file2experimentSet.keySet());
        Collections.sort(orderedFilenames);

        for (String filePrefix : orderedFilenames)
        {
            Collection<SimplifiedAnnotatedObject<Experiment>> chunk = file2experimentSet.get(filePrefix);

            //buffers to hold the labels for small and negative small exps
            StringBuffer negPattern = new StringBuffer(20);  // AVG 1 experiment
            StringBuffer pattern = new StringBuffer(5 * 20); // AVG 5 experiments

            // sort the collection by alphabetical order
            List<String> shortlabels = getSortedShortlabel(chunk);
            for (Iterator<String> iterator2 = shortlabels.iterator(); iterator2.hasNext();)
            {
                String shortlabel = iterator2.next();

                //put the Experiment label in the correct place, depending upon
                //its sub-classification (ie negative or not)
                boolean negative = isNegative(shortlabel);
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

                ExperimentListItem eli = new ExperimentListItem(smallFilename, pattern.toString());
                addToList(eli, classification);
                log.debug(eli);
            }

            if (negPattern.length() != 0)
            {
                String negativeFilename = filePrefix + "_negative" + FileHelper.XML_FILE_EXTENSION;

                ExperimentListItem eli = new ExperimentListItem(negativeFilename, negPattern.toString());
                addToList(eli, classification);
                log.debug(eli);
            }
        } // chunk of experiments
    }

    private void addToList(ExperimentListItem eli, Classification classification)
    {
        switch(classification) {
            case SPECIES:
                speciesListItems.add(eli);
                break;
            case PUBLICATIONS:
                publicationsListItems.add(eli);
                break;
        }
    }

    /**
     * Sort a collection of String (shorltabel). The given collection is not modified, a new one is returned.
     *
     * @param experiments collection to sort.
     * @return the sorted collection.
     */
    private static List<String> getSortedShortlabel(Collection<SimplifiedAnnotatedObject<Experiment>> experiments)
    {

        List<String> sorted = new ArrayList<String>(experiments.size());

        for (SimplifiedAnnotatedObject<Experiment> experiment : experiments)
        {
            sorted.add(experiment.getShortLabel());
        }

        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Answers the following question: "Is the given shortlabel refering to a negative experiment ?".
     *
     * @param experimentLabel the experiment shortlabel.
     * @return true if the label refers to a negative experiment, false otherwise.
     */
    private boolean isNegative(String experimentLabel)
    {

        for (Experiment experiment : negativeExperiments)
        {
            if (experiment.getShortLabel().equals(experimentLabel))
            {
                return true;
            }
        }
        return false;
    }

    private int interactionsForExperiment(String experimentAc)
    {
        if (interactionCount == null)
        {
            interactionCount = ExperimentListGeneratorDao.countInteractionCountsForExperiments(searchPattern);
        }

        return interactionCount.get(experimentAc);
    }

    private List<String> taxIdsForExperiment(String experimentAc)
    {
        if (expAcToTaxid == null)
        {
            expAcToTaxid = ExperimentListGeneratorDao.getExperimentAcAndTaxids(searchPattern);
        }

        return expAcToTaxid.get(experimentAc);
    }

    public String getSearchPattern()
    {
        return searchPattern;
    }

    public boolean isOnlyWithPmid()
    {
        return onlyWithPmid;
    }

    public void setOnlyWithPmid(boolean onlyWithPmid)
    {
        this.onlyWithPmid = onlyWithPmid;
    }

    public int getExperimentsPerChunk()
    {
        return experimentsPerChunk;
    }

    public void setExperimentsPerChunk(int experimentsPerChunk)
    {
        this.experimentsPerChunk = experimentsPerChunk;
    }

    private static DaoFactory getDaoFactory()
    {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    private class SimplifiedAnnotatedObject<T extends AnnotatedObject>
    {

        private String ac;
        private String shortLabel;
        private Date created;

        public SimplifiedAnnotatedObject(AnnotatedObject annotatedObject)
        {
            this.ac = annotatedObject.getAc();
            this.shortLabel = annotatedObject.getShortLabel();
            this.created = annotatedObject.getCreated();
        }

        public String getAc()
        {
            return ac;
        }

        public String getShortLabel()
        {
            return shortLabel;
        }


        public Date getCreated()
        {
            return created;
        }



        @Override
        public boolean equals(Object obj)
        {
            SimplifiedAnnotatedObject o = (SimplifiedAnnotatedObject) obj;
            return ac.equals(o.getAc()) && shortLabel.equals(o.getShortLabel());
        }

        @Override
        public String toString()
        {
            return getAc() + " " + getShortLabel();
        }

        @Override
        public int hashCode()
        {
            return 37 * ac.hashCode() * shortLabel.hashCode();
        }
    }

    }
