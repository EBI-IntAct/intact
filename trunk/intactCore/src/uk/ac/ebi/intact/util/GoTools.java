/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.DuplicateLabelException;
import uk.ac.ebi.intact.model.*;


import java.net.URL;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Utilities to read and write files in GO format
 */
public class GoTools {

    /**
     * Maximum length of a shortLabel
     */
    private static final int MAX_LABEL_LEN = 20;

    /**
     *  Maximum length of a fullName
     */
    private static final int MAX_NAME_LEN = 50;

    /**
     * Generate a label which is unique in targetClass and resembles label.
     * Label as a label of "current" is considered unique.
     * @param helper
     * @param targetClass
     * @param current may be null
     * @param label
     * @param externalAc
     * @return a label unique in targetClass
     */
    public static String getUniqueShortLabel(IntactHelper helper,
                                             Class targetClass,
                                             AnnotatedObject current,
                                             String label,
                                             String externalAc)
            throws IntactException, DuplicateLabelException {

        AnnotatedObject searchResult;

        // trim label
        label = label.substring(0, Math.min(label.length(), MAX_LABEL_LEN));

        // get all objects by label. If more than one, need to modify label.
        try {
            searchResult = (AnnotatedObject) helper.getObjectByLabel(targetClass, label);
            if ((null == searchResult)
                    ||
                    (current == searchResult)) {
                // No object has label, or only the current object.
                // Therefore, if label is assigned to current, it will still be unique.
                return label;
            }
        } catch (DuplicateLabelException d) {
        } catch (IntactException e) {
        }

        // Modify label to get it unique
        label = externalAc;

        // Check again if label is unique. If not, throw an exception.
        searchResult = (AnnotatedObject) helper.getObjectByLabel(targetClass, label);
        if ((null == searchResult)
                ||
                (current == searchResult)) {
            // No object has label, or only the current object.
            // Therefore, if label is assigned to current, it will still be unique.
            return label;
        }

        return null;
    }

    /**
     * Insert a GO term into IntAct.
     */
    public static CvObject insertDefinition (Hashtable definition,
                                             IntactHelper helper,
                                             Class targetClass,
                                             boolean deleteold)
        throws IntactException, Exception {

        /* Update strategy:
        Criterion of object identity: GO id == IntAct GO xref.
        Update shortlabel, name, description, GO comment.
        Subobjects are only removed if deleteOld is true.
        */

        RE pubmedRefPat =  new RE("PMID\\:(\\d+)");

        // Get or create CvObject
        CvObject current;
        current = (CvObject)helper.getObjectByXref(targetClass,
                                       ((Vector)definition.get("goid")).elementAt(0).toString());

        if (null == current){
            current = (CvObject) targetClass.newInstance();
            current.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
        } else {
            if (deleteold){
                // Delete all old data
                helper.deleteAllElements(current.getXref());
                current.getXref().removeAll(current.getXref());
                helper.deleteAllElements(current.getAnnotation());
                current.getAnnotation().removeAll(current.getAnnotation());
            }
        }

        // Update shortLabel. Label has to be unique!
        String goTerm = ((Vector)definition.get("term")).elementAt(0).toString();
        String label  = goTerm.substring(0,Math.min(goTerm.length(), MAX_LABEL_LEN));

        label = getUniqueShortLabel(helper, targetClass, current, label,
                ((Vector) definition.get("goid")).elementAt(0).toString());
        current.setShortLabel(label);

        // Update fullName
        current.setFullName(goTerm.substring(0,Math.min(goTerm.length(), MAX_NAME_LEN)));

        helper.update(current);

        // Update GO term
        // The term needs to be stored separately, as the length may exceed the maximum fullName length.
        current.updateUniqueAnnotation((CvTopic) helper.getObjectByLabel(CvTopic.class, "GO term"),
                                       goTerm,
                                       (Institution) helper.getObjectByLabel(Institution.class, "EBI"));

        // Update GO description
        if (null != definition.get("definition")) {
            current.updateUniqueAnnotation((CvTopic) helper.getObjectByLabel(CvTopic.class, "GO description"),
                                           ((Vector) definition.get("definition")).elementAt(0).toString(),
                                           (Institution) helper.getObjectByLabel(Institution.class, "EBI"));
        }

        // Update GO comment
        if (null != definition.get("comment")) {

            current.updateUniqueAnnotation((CvTopic) helper.getObjectByLabel(CvTopic.class, "GO comment"),
                                           ((Vector) definition.get("comment")).elementAt(0).toString(),
                                           (Institution) helper.getObjectByLabel(Institution.class, "EBI"));
        }

        // add xref
        current.addXref(new Xref ((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                  (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "GO"),
                                  ((Vector) definition.get("goid")).elementAt(0).toString(),
                                  null, null));

        // add definition references
        Vector v = (Vector) definition.get("definition_reference");
        if (null != v) {
            for (int i = 0; i < v.size(); i++) {
                String s = (String) v.elementAt(i);
                if (pubmedRefPat.match(s)){
                    // add Pubmed xref
                    current.addXref(new Xref ((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                              (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "PubMed"),
                                              pubmedRefPat.getParen(1),
                                              null, null));
                }
            }
        }

        // Update main object
        current.update(helper);
        return current;
    }

    /**
     * Read a GO definition flat file from the given URL,
     * insert or update all terms into aTargetClass.
     */
    public static void insertGoDefinitions (Class aTargetClass,
                                            IntactHelper helper,
                                            String aSourceUrl)
                                             throws Exception {

        Hashtable goRecord;

            // Parse input line by line
            URL goServer = new URL(aSourceUrl);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            goServer.openStream()));

            int count = 0;

            System.err.print("GO records read: ");

            while (null != ( goRecord = readRecord(in))) {

                // Progress report
                if((++count % 10 ) == 0){
                   System.err.print(count + " ");
                }

                // Insert the definition
                try {
                    insertDefinition(goRecord, helper, aTargetClass, false);
                }
                catch (IntactException e){
                    System.err.println("Error storing GO record " + (count - 1) + "\n" + e);
                }
            }

        System.err.println();

        return;
    }

    /**
     * Read a GO DAG file from the given URL,
     * insert or update DAG into aTargetClass.
     */
    public static void insertGoDag(Class aTargetClass,
                                   IntactHelper helper,
                                   String aSourceUrl)
            throws Exception {

        // initialisation
        Vector goRecord;
        URL goServer = new URL(aSourceUrl);
        BufferedReader in = new BufferedReader(new InputStreamReader(goServer.openStream()));

        System.err.println("Reading GO DAG lines: ");
        DagNode.addNodes(in, null, aTargetClass, helper, 0);
        System.err.println("\nGO DAG read.");

        return;
    }

    /**
     * Write a Controlled vocabulary in GO definition format flat file.
     */
    public static void writeGoDefinitions(Class aTargetClass,
                                          IntactHelper helper,
                                          String targetFile)
        throws IntactException, IOException {

        PrintWriter out = new PrintWriter( new BufferedWriter(new FileWriter(targetFile)));
        // Get all members of the class
        Collection result = helper.search(aTargetClass.getName(), "ac", "*");

        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            CvObject o = (CvObject) iterator.next();

            out.print(toGoString(o));
        }

        out.close();
    }

    /**
     * Read a single GO term definition flat file record.
     * @param in
     * @return Hashtable containing the parsing results.
     */
    private static Hashtable readRecord (BufferedReader in) throws IOException, RESyntaxException {
        Hashtable parsed = new Hashtable();
        String line;
        RE emptyLinePat = new RE("^[:blank:]*$");
        RE tagValuePat = new RE("(\\S*): (.*)");

        while (null != (line = in.readLine())) {

            // The empty line indicates the end of the record. Return parsed record.
            if(emptyLinePat.match(line)){
                break;
            };

            // Parse a tag-value line
            if (tagValuePat.match(line)){
                String tag = tagValuePat.getParen(1);
                String value = tagValuePat.getParen(2);

                if (null == parsed.get(tag)){
                    parsed.put(tag, new Vector());
                };
                ((Vector) parsed.get(tag)).add(value);
            }

            // Ignore all other lines by doing nothing.
        }

        if (parsed.isEmpty()){
            return null;
        } else {
            return parsed;
        }
    };

    /**
     * Return a single CvObject as a GO flatfile formatted string
     */
    public static String toGoString(CvObject current){
        StringBuffer buf = new StringBuffer();

        Collection annotation = current.getAnnotation();
        for (Iterator iterator = annotation.iterator(); iterator.hasNext();) {
            Annotation a = (Annotation) iterator.next();
            if (a.getCvTopic().getShortLabel().equals("GO term")){
                buf.append("term: ");
                buf.append(a.getAnnotationText());
                buf.append("\n");
            }
        }

        Collection xref = current.getXref();
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref x = (Xref) iterator.next();
            if (x.getCvDatabase().getShortLabel().equals("GO")){
                buf.append("goid: ");
                buf.append(x.getPrimaryId());
                buf.append("\n");
            }
        }

        annotation = current.getAnnotation();
        for (Iterator iterator = annotation.iterator(); iterator.hasNext();) {
            Annotation a = (Annotation) iterator.next();

            if (a.getCvTopic().getShortLabel().equals("GO description")){
                buf.append("definition: ");
                buf.append(a.getAnnotationText());
                buf.append("\n");
            }
        }

        xref = current.getXref();
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref x = (Xref) iterator.next();
            if (x.getCvDatabase().getShortLabel().equals("PubMed")){
                buf.append("definition_reference: PMID:");
                buf.append(x.getPrimaryId());
                buf.append("\n");
            }
        }

        annotation = current.getAnnotation();
        for (Iterator iterator = annotation.iterator(); iterator.hasNext();) {
            Annotation a = (Annotation) iterator.next();

            if (a.getCvTopic().getShortLabel().equals("GO comment")){
                buf.append("comment: ");
                buf.append(a.getAnnotationText());
                buf.append("\n");
            }
        }
        buf.append("\n");

        return buf.toString();
    }

    /** Print the GO format DAG to a file.
     *
     * @param aTargetClass The class for which to print the DAG
     * @param helper       IntactHelper object for database access
     * @param aTargetFile  The file to print to
     * @throws IntactException
     * @throws IOException
     */
    private static void writeGoDag(Class aTargetClass, IntactHelper helper, String aTargetFile)
        throws IntactException, IOException {

        PrintWriter out = new PrintWriter( new BufferedWriter(new FileWriter(aTargetFile)));

        // Get a random members of the class
        Collection result = helper.search(aTargetClass.getName(), "ac", "*");

        if (result.size()>0){
            Iterator iterator = result.iterator();
            CvDagObject o = (CvDagObject) iterator.next();
            o = o.getRoot();
            out.print(o.toGoDag());
        }

        out.close();
    }


    /** Load or unload Controlled Vocabularies in GO format.
     *  Usage:
     *  GoTools upload IntAct_classname Go_DefinitionFile_Url Go_DagFile_Url |
     *  GoTools download IntAct_classname
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        final String usage = "Usage:\n" +
                             "GoTools upload IntAct_classname Go_DefinitionFile_Url Go_DagFile_Url    OR\n" +
                             "GoTools download IntAct_classname Go_DefinitionsFile Go_DagFile";
        Class targetClass;

        try {
            // Check parameters
            if (args.length != 4) {
                throw new IntactException("Invalid number of arguments.\n" + usage);
            }

            try {
                targetClass = Class.forName(args[1]);
            } catch (ClassNotFoundException e) {
                throw new IntactException("Class " + args[1] + " not found.\n" + usage);
            }


            // Create database access object
            IntactHelper helper = new IntactHelper();

            if (args[0].equals("upload")) {

                // Insert definitions
                insertGoDefinitions(targetClass, helper, args[2]);
                // Insert DAG
                insertGoDag(targetClass, helper, args[3]);

            } else if (args[0].equals("download")) {

                // Write definitions
                System.err.println("Writing GO definitons to " + args[2] + " ...");
                writeGoDefinitions(targetClass, helper, args[2]);

                // Write go dag format
                System.err.println("Writing GO DAG to " + args[3] + " ...");
                writeGoDag(targetClass, helper, args[3]);
                System.err.println("Done.");

            } else
                throw new IntactException("Invalid argument " + args[0] + "\n" + usage);

        } catch (IntactException e) {
            System.err.println(e.getMessage());
        }
    }
}

