package uk.ac.ebi.intact.application.dataConversion;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException;
import org.w3c.dom.Document;


import java.util.*;


/**
 * This class is the main application class for generating a flat file format
 * from the contents of a database. Currently the file format is PSI, and the DBs are
 * postgres or oracle, though the DB details are hidden behind the IntactHelper/persistence
 * layer as usual.
 *
 * @author Chris Lewington
 * @version $id$
 */
public class FileGenerator {

    // if an experiment has more than this many interactions it is considered to be large scale.
    //NB changed for testing - usually 100
    public static final int SMALLSCALELIMIT = 1;

    private IntactHelper helper;

    /**
     * Used to accumulate those Experiments which contain a large number of
     * Interactions (currently more than 1000)
     */
    private static Collection largeExperimentList = new ArrayList();

    /**
     * The class to build the file data in the required format
     */
    private DataBuilder builder;

    public FileGenerator(IntactHelper helper, DataBuilder builder) {
        this.helper = helper;
        this.builder = builder;

    }

    /**
     * Obtains the data from the dataSource, in preparation
     * for the flat file generation.
     * @param searchPattern for search by shortLabel. May be a comma-separated list.
     * @exception IntactException thrown if there was a search problem
     */
    public Collection getDbData(String searchPattern) throws IntactException {

        //try this for now, but it may be better to use SQL and get the ACs,
        //then cycle through them and generate PSI one by one.....
        ArrayList searchResults = new ArrayList();
        System.err.print("retrieving data from DB store...");
        StringTokenizer patterns = new StringTokenizer(searchPattern, ",");
        while (patterns.hasMoreTokens()) {
            searchResults.addAll(helper.search(Experiment.class.getName(),
                    "shortLabel", patterns.nextToken()));
        }
        System.err.println("....Done. ");
        System.err.println("Found " + searchResults.size() + " Experiments.");

        return searchResults;
    }

    /**
     * Creates the necessary file data from the DB information, using the
     * specified <code>DataBuilder</code>.
     */
    public void buildFileData(Collection searchResults) throws ElementNotParseableException {

        int i = 1;
        System.err.println("Building flat file data......");
        System.err.println("Experiments to process: " + searchResults.size());
        builder.processExperiments(searchResults);
        System.err.println();
    }

    /**
     * Generates the file from the previously created data.
     * @param fileName the name to use for the generated file.
     * @exception DataConversionException thrown if there were problems with writing
     * the data.
     */
    public void generateFile(String fileName) throws DataConversionException {

        //just delegate it to the data builder - it will use its
        //own method for writing to the file
        System.err.println("writing data to file " + fileName + "....");
        builder.writeData(fileName, null);
        System.err.println("Done.");
    }

    /**
     * Output the experiment classification, suitable for scripting
     * @param allExp HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     */
    public static void writeExperimentsClassification(HashMap allExp) {
        for (Iterator iterator = allExp.keySet().iterator(); iterator.hasNext();) {
            BioSource bioSource = (BioSource) iterator.next();
            ArrayList smallScaleExp = (ArrayList) ((HashMap) allExp.get(bioSource)).get("small");

            String fileName = bioSource.getShortLabel().replace(' ','-') + "_small.xml";
            StringBuffer pattern = new StringBuffer();
            for (int i = 0; i < smallScaleExp.size(); i++) {
                Experiment experiment = (Experiment) smallScaleExp.get(i);
                pattern.append(experiment.getShortLabel());
                // Add comma as separator, but not after last shortLabel
                if (i < smallScaleExp.size() - 1) {
                    pattern.append(",");
                }
            }

            System.out.println(fileName + " " + pattern.toString());

            ArrayList largeScaleExp = (ArrayList) ((HashMap) allExp.get(bioSource)).get("large");

            for (int i = 0; i < largeScaleExp.size(); i++) {
                Experiment experiment = (Experiment) largeScaleExp.get(i);

                fileName = bioSource.getShortLabel().replace(' ','-')
                        + "_" + experiment.getShortLabel()
                        + ".xml";
                System.out.println(fileName + " " + experiment.getShortLabel());
            }
        }
    }


    /**
     * Classify experiments matching searchPattern into a data structure according to
     * species and experiment size.
     * @param searchPattern
     * @return HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     * @throws IntactException
     */
    public static HashMap classifyExperiments(String searchPattern)
            throws IntactException {
        IntactHelper helper = new IntactHelper();

        //obtain data, probably experiment by experiment, build
        //PSI data for it then write it to a file....
        DataBuilder builder = new PsiDataBuilder();
        FileGenerator generator = new FileGenerator(helper, builder);
        Collection searchResults = generator.getDbData(searchPattern);

        HashMap allExp = new HashMap();

        // Split the list of experiments into species- and size-specific files
        for (Iterator iterator = searchResults.iterator(); iterator.hasNext();) {
            Experiment exp = (Experiment) iterator.next();

            System.err.println("Classifying " + exp.getShortLabel());

            // Get the species of one of the interactors of the experiment.
            // The bioSource of the Experiment is irrelevant, as it may be an
            // auxiliary experimental system.
            BioSource source = null;
            int size = 0;
            try {

                Collection interactions = exp.getInteractions();
                size = interactions.size();
                System.out.println("number of Interactions for " + exp.getShortLabel()
                + " = " + size);
                // Avoid outOfMemory from huge datasets (Giot)
                //NB changed to a small size for testing!!...
                if (size>1){
                    //add to the large experiments list for different processing
                    System.out.println("adding a large experiment to list...");
                    largeExperimentList.add(exp);
                    continue;
                }
                //THIS is where OJB loads ALL the Interactions - a call to Iterator..
                Interaction interaction = (Interaction) interactions.iterator().next();
                Collection components = interaction.getComponents();
                Interactor protein = ((Component)components.iterator().next()).getInteractor();
                source = protein.getBioSource();

                size = interactions.size();
            } catch (Exception e) {
                /* If anything goes wrong in determining the source,
                experiments without interactions etc are the most likely cause.
                Just ignore these in the classification and therefore in the output.
                */
                continue;
            }

            // fully initialize the data structure for a new BioSource
            if (null == allExp.get(source)) {
                HashMap speciesMap = new HashMap();
                speciesMap.put("small", new ArrayList());
                speciesMap.put("large", new ArrayList());
                allExp.put(source, speciesMap);
            }

            // Sort experiment into appropriate bin
            if (size > SMALLSCALELIMIT) {
                ((ArrayList) ((HashMap) allExp.get(source)).get("large")).add(exp);
            } else {
                ((ArrayList) ((HashMap) allExp.get(source)).get("small")).add(exp);
            }
        }

        // Now all experiments have been sorted into allExp
        return allExp;
    }

    /**
     * Generates a PSI MI formatted file for a searchPattern.
     * @param searchPattern
     * @param fileName
     * @throws Exception
     */
    public static void generatePsiData(String searchPattern, String fileName)
            throws Exception {
        IntactHelper helper = new IntactHelper();

        //obtain data, probably experiment by experiment, build
        //PSI data for it then write it to a file....
        DataBuilder builder = new PsiDataBuilder();
        FileGenerator generator = new FileGenerator(helper, builder);
        generator.buildFileData(generator.getDbData(searchPattern));
        generator.generateFile(fileName);

        if (helper != null) helper.closeStore();
    }

    public static void processLargeExperiments() throws Exception {

        //Too many statics - now we have to create lots of unnecessary objects..
        IntactHelper helper = new IntactHelper();
        PsiDataBuilder builder = new PsiDataBuilder(); //need a specific one here
        FileGenerator generator = new FileGenerator(helper, builder);

        if (!FileGenerator.largeExperimentList.isEmpty()) {
            //need to process the big ones chunk by chunk -
            //do this by splitting the Interactions into manageable
            //pieces (1000 each), building and writing some XML to seperate
            //files, and then merging them all.
            //NB this last step may have to ve done in a seperate process..
            int startIndex = 0;
            int endIndex = 1000;
            //int endIndex = 2;  //test size for eg small Ho NB END INDEX IS EXCLUSIVE!!
            int chunkCount = 1; //used to distinguish files
            String mainFileName = null;
            List itemsToProcess = null;
            Document interactionList = null;
            for (Iterator it = FileGenerator.largeExperimentList.iterator(); it.hasNext();) {
                Experiment exp = (Experiment)it.next();
                //first need to build a file with just the initial PSI info..
                String rootPsiDoc = exp.getShortLabel() + "_psi_entry.xml";
                builder.writeData(rootPsiDoc, builder.getCurrentDocument());

                //build the interactionList files..
                System.out.println("generating Interaction files for experiment "
                        + exp.getShortLabel() + ": Blocks completed: ");
                mainFileName = exp.getShortLabel() + "_interactions_";  //chunk number added later
                Collection interactions = exp.getInteractions();
                //System.out.println("Number of interactions found: " + interactions.size());

                //Lists are easier to work with...
                if (List.class.isAssignableFrom(interactions.getClass())) {
                    while(startIndex < interactions.size()) {
                        if(endIndex > interactions.size()) endIndex = interactions.size(); //check for the end
                        itemsToProcess = ((List) interactions).subList(startIndex, endIndex);
                        //System.out.println("number of interactions per chunk: " + itemsToProcess.size());
                        //now build the XML and generate a file for it...
                        if(!itemsToProcess.isEmpty()) {
                            interactionList = builder.buildInteractionsOnly(itemsToProcess);
                            String fileName = mainFileName + chunkCount + ".xml";
                            builder.writeData(fileName, interactionList);
                            System.out.println(chunkCount);
                            chunkCount++;
                            startIndex = endIndex;
                            endIndex = endIndex + 1000;
                            //endIndex = endIndex + 2; //should normally be eg 1000
                        }
                        //if it is empty we have done the last one already

                        //dump the interactorList for this chunk - NB
                        //this is not the best as it SHOULD have them all at th end but
                        //currently it does not. Don't have time to debug this right now!!
                        System.out.println("Dumping interactorList XML...");
                        String protListFile = exp.getShortLabel() + "_interactorList"
                                + chunkCount + ".xml";
                        builder.writeData(protListFile, builder.getInteractorList());
                    }
                }
                //This will only NOT be a List if someone changes the model
                //data types!!

                //now dump the ExperimentList
                System.out.println(("Dumping ExperimentList XML.."));
                String expListFile = exp.getShortLabel() + "_experimentList.xml";
                builder.writeData(expListFile, builder.getExperimentList());

                //dump the interactorList - NB can only do this at the end
                //System.out.println("Dumping interactorList XML...");
                //String protListFile = exp.getShortLabel() + "_interactorList.xml";
                //builder.writeData(protListFile, builder.getInteractorList());

            }
        }

    }

    public static void main(String[] args) throws Exception {

        try {
            String fileName = null;
            String searchPattern = null;

            long start = System.currentTimeMillis();
            if (args.length >= 1) {
                searchPattern = args[0];
                System.err.println("SearchPattern: " + searchPattern);

                if (args.length == 1) {
                    HashMap exps = classifyExperiments(searchPattern);
                    writeExperimentsClassification(exps);
                } else {

                    //need to check here also for large experiments...
                    fileName = args[1];
                    System.err.println("FileName: " + fileName);
                    generatePsiData(searchPattern, fileName);
                }
                processLargeExperiments();

            } else {
                System.err.println("Usage: psiRun.sh FileGenerator <searchPattern> [<filename>] ");
                System.err.println("<searchPattern> will be used to select experiment shortlabels. May be a comma-separated list");
                System.err.println("If <filename> is given, all matches will be written into it.");
                System.err.println("Otherwise a list of suggested filenames and experiment shortLabels will be written to standard out.");
                System.exit(1);
            }

            long end = System.currentTimeMillis();
            long total = end - start;
            System.err.println("Total time to build and generate file: " + total / 1000 + "s");
        } catch (IntactException e) {
            System.err.println("Root cause: " + e.getRootCause());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
