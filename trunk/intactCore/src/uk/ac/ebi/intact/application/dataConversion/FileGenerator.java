package uk.ac.ebi.intact.application.dataConversion;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.IntactObject;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class is the main application class for generating a flat file format
 * from the contents of a dattabase. Currently the file format is PSI, and the DBs are
 * postgres or oracle, though the DB details are hidden behind the IntactHelper/persistence
 * layer as usual.
 *
 * @author Chris Lewington
 * @version $id$
 */
public class FileGenerator {

    private IntactHelper helper;

    /**
     * The class to build the file data in the required format
     */
    private DataBuilder builder;

    /**
     * NB this Collections is the real 'wrkhorse' as it is
     * needed to hold all the DB data. Maybe best to generate Experiment by
     * Experiment rather than get all data in one go, as the ArrayList may not be able
     * to handle that much data in one hit.
     */
    private Collection searchResults = new ArrayList();

    public FileGenerator(IntactHelper helper, DataBuilder builder) {
        this.helper = helper;
        this.builder = builder;

    }

    /**
     * Obtains the data from the dataSource, in preparation
     * for the flat file generation.
     * @exception IntactException thrown if there was a search problem
     */
    public void getDbData() throws IntactException {

        //try this for now, but it may be better to use SQL and get the ACs,
        //then cycle through them and generate PSI one by one.....
        System.out.print("retrieving data from DB store...");
        searchResults = helper.search(Experiment.class.getName(), "ac", "*");
        System.out.println("....Done. ");
        System.out.println("Found " + searchResults.size() + " Experiments.");

    }

    /**
     * Creates the necessary file data from the DB information, using the
     * specified <code>DataBuilder</code>.
     */
    public void buildFileData() {

        int i=1;
        System.out.println("Building flat file data......");
        System.out.println("Experiments processed: ");
        for(Iterator it = searchResults.iterator(); it.hasNext();) {
            IntactObject obj = (IntactObject)it.next();
            builder.createData(obj);
            System.out.print(i + " ");
            i++;
        }
        System.out.println();

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
        System.out.println("writing data to file " + fileName + "....");
        builder.writeData(fileName);
        System.out.println("Done.");
    }


    public static void main(String[] args) throws Exception {

            IntactHelper helper = null;

            try {
                String fileName = null;

                if (args.length >= 1) {
                    fileName = args[0];
                    System.out.println("FileName: " + fileName);
                } else {
                    System.out.println("Usage: psiRun.sh FileGenerator <filename>");
                    System.exit(1);
                }
                helper = new IntactHelper();
                System.out.println("Helper created (User: "+helper.getDbUserName()+ " " +
                                   "Database: "+helper.getDbName()+")");

                //obtain data, probably experiment by experiment, build
                //PSI data for it then write it to a file....
                DataBuilder builder = new PsiDataBuilder();
                FileGenerator generator = new FileGenerator(helper, builder);
                long start = System.currentTimeMillis();
                generator.getDbData();
                generator.buildFileData();
                generator.generateFile(fileName);
                long end = System.currentTimeMillis();
                long total = end - start;
                System.out.println("Total time to build and generate file: " + total/1000 + "s");

            }
            catch (IntactException e) {
                System.out.println("Root cause: " + e.getRootCause());
                e.printStackTrace();
                System.exit (1);
            }
            catch (Exception e ) {
                e.printStackTrace();
            }
            finally {
                if (helper != null) helper.closeStore();
            }
        }

}
