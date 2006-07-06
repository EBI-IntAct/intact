/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.business;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * This class inherits the <code>ManagerFiles</code> class.
 * The Wgetz process manages an URLConnection object. The method reads the output
 * line by line and put the line in a file already created.
 * The <code>PutInFile</code> method is overrided.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class ManagerFilesSrs extends ManagerFiles {

    //---------- INSTANCE VARIABLES -------------//

     //---------- CONSTRUCTOR -------------------//

    /**
      * constructor by default
      */
    public ManagerFilesSrs () {
    }

    /**
     * constructor which allows us to create a random file name,
     * this application is multi-user. So, it allows to reduce possible conflicts.
     *
     * @param pathFile String to know where to put this file
     * @param extension Object representing the logic extension behind the file name
     *
     */
    public ManagerFilesSrs (String pathFile, Object extension) {
        this.fileName = GetRandFileName((String)extension);
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
    public ManagerFilesSrs (String pathFile, String fileName) {
        this.fileName = fileName;
        this.inputFile = new File (pathFile.concat(fileName));
        this.absolutePath = inputFile.getAbsolutePath();
    }


    //---------- METHODS -------------------//

    /**
     * This method overrides the <code>PutInFile</code> method.
     * The Wgetz output is put in a file line by line. It means
     * that this method is processed in a loop. So, the
     * <code>CreateWriter</code> method must be called only once,
     * before the loop, and deleted from this method.
     *
     * @param theLine the string put in the file, with a <code>getLineSeparator</code>
     *          method to separate each line.
     *
     */
    public void PutInFile (String theLine) {

             try {
                 String oneLine = this.GetLineSeparator();
                    // retrieves the input file where the result is recorded.
                 if (inputFile.canWrite() == true) {
                     fileWriter.write(theLine + oneLine);
                        // to make sure that all the buffer is written in the file.
                     fileWriter.flush();
                 }
                 else {
                     inputFile.deleteOnExit();
                 }
             }
             catch (FileNotFoundException e){
                System.out.print(e);
             }
             catch (IOException io) {
                System.out.print(io);
             }
      }
}
