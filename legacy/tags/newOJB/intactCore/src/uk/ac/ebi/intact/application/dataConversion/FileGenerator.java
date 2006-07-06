package uk.ac.ebi.intact.application.dataConversion;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException;
import org.w3c.dom.*;


import java.util.*;
import java.sql.*;


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
    public static final int SMALLSCALELIMIT = 500;
    public static final int LARGESCALESIZE = 2500;

    /**
     * Holds the shortLabels of any Experiments found to contain
     * Interactions with 'negative' information. It has to be a static
     * because the method used for writing the classifications is a static...
     */
    private static List negExpLabels = new ArrayList();

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
        int resultSize = searchResults.size();
        if((resultSize > 1) || (resultSize == 0)) {
        System.err.println("Found " + resultSize + " Experiments.");
        }
        else {
           System.err.println("Found 1 Experiment.");
        }

        return searchResults;
    }

    /**
     * Creates the necessary file data from the DB information, using the
     * specified <code>DataBuilder</code>.
     */
    public void buildFileData(Collection searchResults) throws ElementNotParseableException {

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

            String fileNameRoot = bioSource.getShortLabel().replace(' ','-');
            //create two filenames, one for usual exps and one for any 'negatives'
            String smallFile = fileNameRoot + "_small.xml";
            String negFile = fileNameRoot + "_small_negative.xml";

            //buffers to hold the labels for small and negative small exps
            StringBuffer negPattern = new StringBuffer();
            StringBuffer pattern = new StringBuffer();

            String label = null;
            for (int i = 0; i < smallScaleExp.size(); i++) {
                Experiment experiment = (Experiment) smallScaleExp.get(i);
                label = experiment.getShortLabel();

                //put the Experiment label in the correct place, depending upon
                //its sub-classification (ie negative or not)
                if(negExpLabels.contains(label)) {
                    negPattern.append(label + ",");
                }
                else {
                pattern.append(label + ",");
                }
            }
            //strip off trailing comma - can't do this in advance as we don't
            //know how big the buffers can be now!
            pattern.deleteCharAt(pattern.length()-1);
            if(negPattern.length() != 0) negPattern.deleteCharAt(negPattern.length()-1);

            //classification for this BioSource is output as:
            //'<filename> <comma-seperated shortLabel list>'
            System.out.println(smallFile + " " + pattern.toString());

            //only need to print out the negatives if the negative StringBuffer
            //has anything in it....
            if(negPattern.length() != 0)
                System.out.println(negFile + " " + negPattern.toString());


            //Now do the large ones...
            //NB these are classified into single files, one for each shortLabel
            ArrayList largeScaleExp = (ArrayList) ((HashMap) allExp.get(bioSource)).get("large");
            String fileName = fileNameRoot;
            for (int i = 0; i < largeScaleExp.size(); i++) {
                Experiment experiment = (Experiment) largeScaleExp.get(i);
                label = experiment.getShortLabel();
                if(negExpLabels.contains(label)) {
                    fileName = fileName + "_" + label + "_negative";
                }
                else {
                    fileName = fileName + "_" + label;
                }

                //dump out the generated <filename> <label> pair
                System.out.println(fileName + ".xml" + " " + label);
                fileName = fileNameRoot;    //reset to Biosource for the next Experiment
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
                System.err.println("number of Interactions for " + exp.getShortLabel()
                + " = " + size);

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

        // Now all experiments have been sorted into allExp - check for those containing
        //'negative' results...
        classifyNegatives();
        return allExp;
    }

    /**
     * Generates a PSI MI formatted file for a searchPattern. Large scale
     * Experiments will typically be a searchpattern of a single shortlabel, and
     * these are processed as chunks. Small scale ones will generally have a searchpattern
     * consisting of multiple shortlabels, and these will be placed into a single file.
     * @param searchPattern
     * @param fileName
     * @throws Exception
     */
    public static void generatePsiData(String searchPattern, String fileName)
            throws Exception {

        IntactHelper helper = new IntactHelper();
        DataBuilder builder = new PsiDataBuilder();
        FileGenerator generator = new FileGenerator(helper, builder);

        //get all of the Experiment shortlabels and process them according
        //to the size of their interaction list..
        //NB this currently means that if the search result size
        //is one and the number of interactions is large then we process seperately.
        Collection searchResults = generator.getDbData(searchPattern);
        if(searchResults.size() == 1) {
            //may be a large experiment - check and process if necessary
            Experiment exp = (Experiment)searchResults.iterator().next();
            if(exp.getInteractions().size() > FileGenerator.LARGESCALESIZE) {
                System.err.println("processing large experiment "
                        + exp.getShortLabel() + " ....");
                FileGenerator.processLargeExperiment(exp, fileName);
                return;     //done
            }
        }
        //not a large experiment - may be a single small one or a set of small ones,
        //so process 'normally' into a single file...
        generator.buildFileData(searchResults);
        generator.generateFile(fileName);

        if (helper != null) helper.closeStore();

    }

    public static void processLargeExperiment(Experiment exp, String fileName) throws Exception {

        //Too many statics - now we have to create lots of unnecessary objects..
        PsiDataBuilder builder = new PsiDataBuilder(); //need a specific one here

        //need to process the big ones chunk by chunk -
        //do this by splitting the Interactions into manageable
        //pieces (LARGESCALESIZE each), then building and writing some XML to seperate
        //files....
        int startIndex = 0;
        int endIndex = LARGESCALESIZE; //NB END INDEX IS EXCLUSIVE!!
        //int endIndex = 2;
        int chunkCount = 1; //used to distinguish files
        String mainFileName = null;
        List itemsToProcess = null;

        //set up the basic PSI stuff..
        //Document singleFileDoc = builder.getCurrentDocument();
        Document singleFileDoc = builder.newPsiDoc(true);
        Node root = singleFileDoc.getDocumentElement();
        if (!root.hasChildNodes()) throw new Exception("Fatal Error initialising PSI document!");
        Node entry = root.getFirstChild(); //this is the entry Node

        //first need to build a file with just the initial PSI info..
        //String rootPsiDoc = exp.getShortLabel() + "_psi_entry.xml";
        //builder.writeData(rootPsiDoc, builder.getCurrentDocument());

        //build the interactionList files..
        System.out.println("generating Interaction files for experiment "
                + exp.getShortLabel() + ": Blocks completed: ");
        //mainFileName = exp.getShortLabel() + "_interactions_";  //chunk number added later
        Collection interactions = exp.getInteractions();
        //System.out.println("Number of interactions found: " + interactions.size());
        //System.out.println("Type of interaction Collection: " + interactions.getClass().getName());
        //Lists are easier to work with...
        if (List.class.isAssignableFrom(interactions.getClass())) {
            while (startIndex < interactions.size()) {
                if (endIndex > interactions.size()) endIndex = interactions.size(); //check for the end
                itemsToProcess = ((List) interactions).subList(startIndex, endIndex);
                //System.out.println("number of interactions per chunk: " + itemsToProcess.size());
                //now build the XML and generate a file for it...
                if (!itemsToProcess.isEmpty()) {

                    //build a Document containing the PSI info, the Experiment
                    //info, the interactionList and the interactorList for
                    //each chunk of interactions, then dump to a file
                    System.out.println("Generating InteractionList for chunk "
                            + chunkCount + "...");
                    System.out.println();
                    //the next line is too long really, but you have to pass the
                    //result Element in, not a reference, to importNode otherwise you get a
                    //'Wrong Document' error from the XML parser!
                    Node interactionRoot =
                            singleFileDoc.importNode(builder.buildInteractionsOnly(itemsToProcess, FileGenerator.LARGESCALESIZE), true);

                    //now the interactionList has been built we can get at the other info...
                    //NB need to rearrange the order of child appending, but AFTER
                    //generation of interactions...
                    System.out.println();
                    System.out.println("Generating ExperimentList for chunk "
                            + chunkCount + "...");
                    System.out.println();
                    Node expRoot = singleFileDoc.importNode(builder.getExperimentList(), true);

                    System.out.println("Generating InteractorList for chunk "
                            + chunkCount + "...");
                    System.out.println();
                    Node interactorRoot = singleFileDoc.importNode(builder.getInteractorList(), true);

                    //Now add the new elements into the root to be dumped...
                    //NB this is the current order of the PSI elements...
                    entry.appendChild(expRoot);
                    entry.appendChild(interactorRoot);
                    entry.appendChild(interactionRoot);

                    //interactionList = builder.buildInteractionsOnly(itemsToProcess);
                    //String fileName = mainFileName + chunkCount + ".xml";
                    //add a leading zero to the chunk count and use the fileName
                    //if it is given - otherwise use the shortlabel
                    if(fileName != null) {
                        mainFileName = fileName.substring(0, fileName.indexOf(".")) + "_0" + chunkCount + ".xml";
                    }
                    else {
                         mainFileName = mainFileName + "0" + chunkCount + ".xml";
                    }
                    //builder.writeData(fileName, interactionList);
                    System.out.println("Dumping chunk data...");
                    builder.writeData(mainFileName, singleFileDoc);
                    System.out.println("chunk " + chunkCount + " complete.");
                    System.out.println();

                    //now need to reset the singleFileDoc for the next chunk..
                    singleFileDoc = builder.newPsiDoc(true);
                    root = singleFileDoc.getDocumentElement();
                    entry = root.getFirstChild();

                    chunkCount++;
                    startIndex = endIndex;
                    endIndex = endIndex + LARGESCALESIZE;
                    //endIndex = endIndex + 2; //should normally be eg LARGESCALESIZE
                }

            }
        }
        else {
            //This will only NOT be a List if someone changes the model
            //data types!!
            throw new Exception("can't process large experiment - " +
                    "the Collection of Interactions must be a List but is instead " +
                    interactions.getClass().getName());
        }

    }

    /**
     * Main method for the PSI application. The application is typically run twice -
     * firstly with a wildcard ('%') argument to generate a file containing classifications
     * into species of experiment labels, then secondly to use that file to generate the
     * PSI XML data for each classification. This secodn step is handled via a perl script
     * which repeatedly calls this application to generate the files. Note that the
     * exceptions to the species classification are large-scale experiments (as defined
     * by the SMALLSCALELIMIIT constant) - these cannot be put into XMl files with other
     * experiments due to size and memory constraints, and so they are generated in 'chunks'
     * of data divided by 'chunks' of interactions.
     * @param args
     * @throws Exception
     */
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

    //----------------------- private helper methods -------------------------------------

    /**
     * Checks for a negative interaction.
     * NB This will have to be done using SQL otherwise we end up materializing
     * all interactions just to do the check.
     * This method has to be static because it is called by the static 'classifyExperiments'.
     *
     */
    private static void classifyNegatives() throws IntactException {

        //query to get at the Experiment ACs containing negative interaction annotations
        String sql = "select experiment_ac from ia_int2exp where interaction_ac in " +
                "(select interactor_ac from ia_int2annot where annotation_ac in " +
                "(select ac from ia_annotation where topic_ac in " +
                "(select ac from ia_controlledvocab where shortlabel='negative')))";

        IntactHelper helper = new IntactHelper();
        Set expAcs = new HashSet(); //used to collect ACs from a query

        Connection conn = null;
        Statement stmt = null;  //ordinary Statement will do - won't be reused
        PreparedStatement labelStmt = null; //needs a parameter
        ResultSet rs = null;
        try {
            //safest way to do this is directly through the Connection.....
            conn = helper.getJDBCConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                //stick them into the Set of ACs
                //NB check that these do not disappear when the ResultSet gets closed!!
                expAcs.add(rs.getString("experiment_ac"));
            }
            rs.close();
            //System.out.println("DEBUG: size of AC List: " + expAcs.size());

            //now get the shortlabels of the Experiments as these are what we need...
            for(Iterator it = expAcs.iterator(); it.hasNext();) {
                labelStmt = conn.prepareStatement("SELECT shortlabel FROM ia_experiment WHERE ac= ?");
                labelStmt.setString(1, (String)it.next());
                rs = labelStmt.executeQuery();
                while(rs.next()) negExpLabels.add(rs.getString("shortlabel"));
            }
            //System.out.println("DEBUG: size of shortlabel list: " + negExpLabels.size());
            //System.out.println("DEBUG: shortlabels in neg list:");
            //for(Iterator it = negExpLabels.iterator(); it.hasNext();) {
                  //System.out.println(it.next());
            //}

        }
        catch(SQLException se) {

            System.out.println(se.getSQLState());
            System.out.println(se.getErrorCode());
            se.printStackTrace();
            while((se.getNextException()) != null) {
                System.out.println(se.getSQLState());
                System.out.println(se.getErrorCode());
                se.printStackTrace();
            }
        }
        finally {
            try {
                if (stmt != null) stmt.close();
                if(labelStmt != null) labelStmt.close();
                if (conn != null) conn.close();
            }
            catch(SQLException se) {
                se.printStackTrace();
            }

        }



    }

}
