/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.business;



import uk.ac.ebi.intact.business.IntactException;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;



/**
 * This class is a base class which allows to manage the file operations like opening,
 * reading, writing, parsing, deleting.
 * Previous command lines or URL in this project need the file creation and management.
 * It allows to abstract other classes from the specific objects needed like File, FileWriter...
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */

public class ManagerFiles {

    static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /**
     * Inner class to generate unique ids to use primary keys for CommentBean
     * class.
     */
    private static class UniqueID {

        /**
         * The initial value. All the unique ids are based on this value for any
         * (all) user(s).
         */
        private static long theirCurrentTime = System.currentTimeMillis();

        /**
         * Returns a unique id using the initial seed value.
         */
        private static synchronized long get() {
            return theirCurrentTime++;
        }
    }


    //---------- INSTANCE VARIABLES -------------//

    /**
     * the whole path file with the file name
     */
    File inputFile = null;
    FileWriter fileWriter = null;


    /**
     * absolute path file
     */
    protected String absolutePath = "";
    protected String fileName = "";

    FileReader fr = null;
    BufferedReader bufReader;



    //---------- CONSTRUCTORS -------------------//


    /**
     * constructor by default
     */
    public ManagerFiles () {
    }

    /**
     * constructor which allows us to create a random file name,
     * this application is multi-user. So, it allows to reduce possible conflicts.
     *
     * @param pathFile String to know where to put this file
     * @param extension Object representing the logic extension behind the file name
     *
     */
    public ManagerFiles (String pathFile, Object extension) {
        this.fileName = GetRandFileName(extension);
        this.inputFile = new File (pathFile.concat(fileName));
        this.absolutePath = inputFile.getAbsolutePath();
    }

    /**
     * constructor which passes a hard file name in parameter
     *
     * @param pathFile String to know where to put this file
     * @param fileName String to represent the hard name file
     *
     */
    public ManagerFiles (String pathFile, String fileName) {
        this.fileName = fileName;
        this.inputFile = new File (pathFile.concat(fileName));
        this.absolutePath = inputFile.getAbsolutePath();
    }

    //--------- PROPERTIES ---------------------//


    /**
     * to get the absolute path of a file
     *
     * @return string.
     */
    public String GetPathFile () {
        return (this.absolutePath);
    }

    public void SetPathFile (String path) {
        this.absolutePath = path;
    }

    /**
     * to get the file name only
     *
     * @return string.
     */
    public String GetFileName () {
        return (this.fileName);
    }

    public void SetFileName (String name) {
        this.fileName = name;
    }


    //---------- PUBLIC METHODS -----------------//

    /**
     * Generate a file name randomly, with a long added at the end of the name.
     * The corresponding extension is added afterwards.
     * the application is multi-user: each user have to get his own file name !
     *
     * @return string which represents the generated file name.
     */
    public String GetRandFileName (Object extensionFile) {

        long rand = UniqueID.get();
        //to concat the value with the file pathname string,
        // cast the random long into a string.
        String s = String.valueOf(rand);
        fileName = "file".concat(s).concat((String)extensionFile);

        return fileName;
    }


    /**
     * Create a file, checking some important points before:
     * if it already exists, and if it is readable.
     *
     * @return boolean which answers if the file has been well created.
     */
    public boolean CreateFile () {

        boolean create = false;

        try {
//            inputFile.
            inputFile.createNewFile();
            if (inputFile.exists() == true && inputFile.canRead() == true) {
                create = true;
                return create;
            } else if (inputFile.isDirectory() == true) {
                logger.error ("The given path is a directory, can't create a file");
                return create;
            } else {
                logger.error ("Could not create a file:" + inputFile.getName());
                return create;
            }
        }
        catch (IOException ioe) {
            logger.error ("Could not create a file, cause: " + ioe.getMessage(), ioe);
            return create;
        }
    }


    /**
     * This method allows to write a string, more or less long, in a file
     * which has been created before.
     * Write the user query in a file which already exists.
     *
     * @param sequence the string which is going to full the file. It is used, for instance,
     *      in the RunSimilaritySearch method, where the blast command need to put the query protein sequence
     *      in a file.
     *
     */
    public void PutInFile (String sequence) throws IntactException {
        try {
            //retrieve the input file name where the user query will be recorded.
            if (inputFile.canWrite() == true && inputFile.length() == 0) {

                this.CreateWriter();
                fileWriter.write(sequence);
                //to make sure that all the buffer is write in the file.
                fileWriter.flush();
                //protects the file in reading.
                inputFile.setReadOnly();
            }
            else {
                inputFile.deleteOnExit();
            }
        }
        catch (FileNotFoundException e){
            logger.error ("Error while trying to look up for a file: ", e);
            throw new IntactException ("Error while trying to look up for a file: ", e);
        }
        catch (IOException io) {
            logger.error ("Error while creating a file: " + inputFile, io);
            throw new IntactException ("Error while creating a file: " + inputFile, io);
        }
    }

    /**
     * if the PutInFile method needs to be managed in a loop,
     * the FileWriter has to be created only once. Create a File
     * Writer separately.
     *
     */
    public void CreateWriter () throws IntactException {
        try {
            fileWriter = new FileWriter(inputFile);
        }
        catch (IOException io) {
            logger.error ("Error while creating a file ("+ inputFile +"), cause: " + io.getCause(), io);
            throw new IntactException ("Error while creating a file: " + inputFile, io);
        }
    }

    /**
     * This method allows to recover the file content in a String.
     *
     * @return string which is in the file.
     */
    public String ReadingFile () throws IntactException {
        try {
            this.PrepareBufferedReader();

            if (bufReader.ready()) {

                String currentLine = null;
                String oneLine = GetLineSeparator();
                String result = "";

                while ((currentLine = bufReader.readLine()) != null) {

                    result = result.concat(currentLine + oneLine);
                }
                bufReader.close();
                fr.close();

                return result;
            }
            else
                return null;
        }
        catch (IOException io) {
            logger.error ("Error while reading file, cause: " + io.getCause(), io);
            throw new IntactException ("Error while reading file", io);
        }
    }



    /**
     * Delete a file which isn't used anymore.
     *
     */
    public void DeleteFile () {
        //if (inputFile.createNewFile() == false) { //the file name already exists...
        //String name = inputFile.getName();
        if (inputFile.isFile() == true) {
            inputFile.delete();
        }
    }


    /**
     * This method parses a file with only one item requiered and returns the list of this
     * item possibly retrieved in several lines.
     *
     * @param patternList while there are pattern strings in this list (corresponds to Regular
     *       Expression, we have to continue the parsing with this string.
     * @param jWhichGroup decides which group of the regular expression is retrieved.
     *
     * @return list made of another list with the results from one line of the file.
     *
     */
    public ArrayList ResultParsing (ArrayList patternList, int jWhichGroup) throws IntactException {
        try {
            this.PrepareBufferedReader();
            if (bufReader.ready()) {

                String currentLine = null;
                String oneLine = GetLineSeparator();
                StringBuffer stringBuf = new StringBuffer ();
                ArrayList result = new ArrayList();

                while ((currentLine = bufReader.readLine()) != null) {
                    int i = 0;
                    while (i < patternList.size()) {
                        String theDataRetrieved = "";
                        theDataRetrieved = ParseWithReOneGroup(currentLine, (String)patternList.get(i), jWhichGroup);

                        result.add(theDataRetrieved);
                        stringBuf.append(currentLine + oneLine);
                        i++;
                    }
                }

                bufReader.close();
                fr.close();

                return result;
            }
            else {
                logger.error ("Buffer is not ready, something goes wrong when preparing it.");
                return null;
            }
        }
        catch (IOException io) {
            logger.error ("Error while parsing a file, cause: " + io.getCause(), io);
            throw new IntactException ("Error while parsing a file: ", io);
        }

    }

    /**
     * This method parses a file from one Regular Expression given.
     *  When the file to parse is in a table format, this method is required instead
     * of the <code>ResultParsing</code> method.
     *
     * @param pattern string which corresponds to a regular expression which will allow to
     *        parse the file.
     *
     * @return list made of another list with the results from one line of the file.
     *
     */
    public ArrayList ResultParsingTableFile (String pattern) throws IntactException {

        try {
            this.PrepareBufferedReader();

            // TODO: has to be tested to avoid NullPointerException !!!

            if (bufReader.ready()) {

                String currentLine = null;
                String oldLine = null;

                ArrayList items = new ArrayList();

                while ((currentLine = bufReader.readLine()) != null) {

                    // avoid retrieving two identical lines which follow themselves.
                    if (currentLine.equals(oldLine) == false) {

                        logger.info("Line to parse: " + currentLine);

                        ArrayList result = ParseWithReAllFollow (currentLine, pattern);

                        if (result.size() > 0) {
                            items.add(result);
                        }
                    }
                    else {
                        continue;
                    }
                    oldLine = currentLine;
                }

                bufReader.close();
                fr.close();

                return items;
            }
            else {
                logger.error ("Buffer is not ready, something goes wrong when preparing it.");
                return null;
            }
        }
        catch (IOException io) {
            logger.error ("Error while processing a file, cause: " + io.getCause(), io);
            throw new IntactException ("Error while processing a file: ", io);
        }
    }

    //---------- PROTECTED METHODS -----------------//


    /**
     * This method prepares the buffered reader, to put the file managed
     * in a buffer, which allow to work on its content and parse it.
     * This method is also separated from the parsing methods because
     * it must be created only once.
     *
     */
    protected void PrepareBufferedReader () throws IntactException {

        if (CreateFile() == true) {
            try {
                fr = new FileReader(inputFile);
                bufReader = new BufferedReader(fr);
            } catch (FileNotFoundException fnfe) {
                logger.error ("");
                throw new IntactException ("Unable to create the buffer reader, file not found");
            }
        } else {
            throw new IntactException ("Could not create a new file");
        }
    }



    /**
     * This method allows to parse one line of a file with a Regular Expression in JAVA
     * and to retrieve only one item of this line. This method is useful when we want to
     * retrieve data in one simple list. It is included in a loop, and the String is added
     * in a list.
     *
     * @param inputStr the line reading in the file.
     * @param patternStr the Regular Expression which describes the file line to retrieve.
     * @param iWhichGroup to choose which group will be retrieved from the RE (if it is equal
     *        to 0, the whole line is retrieved).
     *
     * @return string which contains one item retrieved in the line
     */
    protected String ParseWithReOneGroup (String inputStr, String patternStr, int iWhichGroup) {

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        boolean matchFound = matcher.find();

        String groupStr = "";
        if (matchFound == true) {
            groupStr = matcher.group(iWhichGroup);
        }

        return groupStr;
    }

    /**
     * This method allows to parse one line with one Regular Expression in JAVA and to
     * retrieve all items described in this RE, except the whole line.
     * The String <code>groupStr</code> retrieves items requiered by the regular expression.
     * So, each group is added in the <code>result</code> ArrayList, like a String.
     *
     * @param inputStr the current line reading in the file.
     * @param patternStr the Regular Expression which describes the file line to retrieve.
     *
     * @return ArrayList which contains the items list retrieved in the line.
     */
    protected ArrayList ParseWithReAllFollow (String inputStr, String patternStr) {

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        boolean matchFound = matcher.find();

        ArrayList result = new ArrayList();

        if (matchFound == true) {
            // the "0" one is the whole line
            //begins with 1 because we don't want to retrieve the whole line
            for (int i = 1; i <= matcher.groupCount(); i++) {

                String groupStr = "";
                groupStr = matcher.group(i);

                result.add(groupStr);
            }
        }
        return result;
    }

    /**
     * get the line separator string.
     * It allows to use the same separator int the service and int the client
     * to keep the multiplateform aspect.
     *
     * @return the line separator
     */
    protected String GetLineSeparator () {
        return System.getProperty ("line.separator");
    } // getLineSeparator


}
