package uk.ac.ebi.intact.application.dataConversion;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException;


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
    public static final int SMALLSCALELIMIT = 100;

    private IntactHelper helper;

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
        builder.writeData(fileName);
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
                // Avoid outOfMemory from huge datasets (Giot)
                if (size>1000){
                    continue;
                }
                Interaction interaction = (Interaction) interactions.iterator().next();
                Collection components = interaction.getComponents();
                Interactor protein = (Interactor) ((Component)components.iterator().next()).getInteractor();
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

    public static void main(String[] args) throws Exception {

        try {
            String fileName = null;
            String searchPattern = null;

            long start = System.currentTimeMillis();
            if (args.length >= 1) {
                searchPattern = args[0];
                System.err.println("SearchPattern: " + searchPattern);

                if (args.length == 1) {
                    writeExperimentsClassification(classifyExperiments(searchPattern));
                } else {

                    fileName = args[1];
                    System.err.println("FileName: " + fileName);
                    generatePsiData(searchPattern, fileName);
                }

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
