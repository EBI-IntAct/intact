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


import java.io.*;
import java.util.*;

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
    private static final int MAX_NAME_LEN = 250;

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
            // if there is a backslash within a label, we must escape that backslash.
            // "\\\\" because one backslash is escaped by java AND one by sql - so in fact it is only one
             searchResult = (AnnotatedObject)helper.getObjectByLabel(targetClass, label.replaceAll("\\\\","\\\\\\\\"));
             if (null == searchResult)
                 return label;
             if (current.getAc() != null) {
                 if (current.getAc().equals(searchResult.getAc())) {
                     // No object has label, or only the current object.
                     // Therefore, if label is assigned to current, it will still be unique.
                     return label;
                 }
             }
         }
         catch (DuplicateLabelException d) {
         }
         catch (IntactException e) {
         }

         // Modify label to get it unique
         label = externalAc;

         // Check again if label is unique. If not, throw an exception.
         searchResult = (AnnotatedObject)helper.getObjectByLabel(targetClass, label.replaceAll("\\\\","\\\\\\\\"));
         if (null == searchResult)
             return label;
         if (current.getAc() != null) {
             if (current.getAc().equals(searchResult.getAc())) {
                 // No object has label, or only the current object.
                 // Therefore, if label is assigned to current, it will still be unique.
                 return label;
             }
         }
         return null;
     }

    /**
     * Insert a GO term into IntAct.
     */
    public static CvObject insertDefinition(Hashtable definition,
                                            IntactHelper helper,
                                            Class targetClass,
                                            boolean deleteold)
            throws IntactException, Exception {

        /* Update strategy:
        Criterion of object identity: GO id == IntAct GO xref.
        Update shortlabel, name, description, GO comment.
        Subobjects are only removed if deleteOld is true.
        */

        RE pubmedRefPat = new RE("PMID\\:(\\d+)");

        // Get or create CvObject
        CvObject current;

        // If the intact shortlabel is defined, use it, otherwise use the GO id to try to retrieve the
        // corresponding IntAct object.
        if (((Vector) definition.get("shortlabel")) != null) {
            String shortLabel = ((Vector) definition.get("shortlabel")).elementAt(0).toString();
            current = (CvObject) helper.getObjectByLabel(targetClass, shortLabel);
        }
        else {
            current = (CvObject) helper.getObjectByXref(targetClass,
                    ((Vector) definition.get("goid")).elementAt(0).toString());
        }

        if (null == current) {
            current = (CvObject) targetClass.newInstance();
            current.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            helper.create(current);
        }
        else {
            if (deleteold) {
                // Delete all old data
                helper.deleteAllElements(current.getXref());
                current.getXref().removeAll(current.getXref());
                helper.deleteAllElements(current.getAnnotation());
                current.getAnnotation().removeAll(current.getAnnotation());
            }
        }

        // Update shortLabel. Label has to be unique!
        String label;
        String goTerm = ((Vector) definition.get("term")).elementAt(0).toString();

        // The short label might be properly defined in the GO flat file.
        if ((Vector) definition.get("shortlabel") != null) {
            label = (((Vector) definition.get("shortlabel")).elementAt(0)).toString();
        }
        else {
            label = goTerm.substring(0, Math.min(goTerm.length(), MAX_LABEL_LEN));
            label = getUniqueShortLabel(helper, targetClass, current, label,
                    ((Vector) definition.get("goid")).elementAt(0).toString());
        }

        current.setShortLabel(label);

        // Update fullName
        current.setFullName(goTerm.substring(0, Math.min(goTerm.length(), MAX_NAME_LEN)));

//        if (helper.isPersistent(current)) {
//            helper.update(current);
//        }

        // Update all comments
        for (Enumeration comments = definition.keys(); comments.hasMoreElements();) {
            String annotationTopic = (String) comments.nextElement();
            CvTopic topic = null;
            topic = (CvTopic) helper.getObjectByLabel(CvTopic.class, annotationTopic);
            if (topic != null) {
                // The annotationTopic is a valid IntAct annotation topic
                Annotation annotation = current.updateUniqueAnnotation(topic,
                        ((Vector) definition.get(annotationTopic)).elementAt(0).toString(),
                        (Institution) helper.getObjectByLabel(Institution.class, "EBI"));
                // Only create a new annotation if it doesn't exist in the DB.
                if (helper.isPersistent(annotation)) {
                    helper.update(annotation);
                }
                else {
                    helper.create(annotation);
                }
            }
        }

        // add GO xref if it does not yet exist.
        if ((Vector) definition.get("goid") != null) {
            Xref xref = new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                    (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "go"),
                    ((Vector) definition.get("goid")).elementAt(0).toString(),
                    null, null, null);
            if (! current.getXref().contains(xref)){
		current.addXref(xref);
		helper.create(xref);
	    }
        }

        // add definition references
        Vector v = (Vector) definition.get("definition_reference");
        if (null != v) {
            for (int i = 0; i < v.size(); i++) {
                String s = (String) v.elementAt(i);
                if (pubmedRefPat.match(s)) {
                    // add Pubmed xref
                    Xref xref = new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                            (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "pubmed"),
                            pubmedRefPat.getParen(1),
                            null,
                            null,
                            (CvXrefQualifier) helper.getObjectByLabel(CvXrefQualifier.class,
                                    "go-definition-ref"));
                    if (! current.getXref().contains(xref)){
			current.addXref(xref);
			helper.create(xref);
		    }
                }
            }
        }

        // Update main object

        if (helper.isPersistent(current)) {
            helper.update(current);
        }
//        current.update(helper);
        return current;
    }

    /**
     * Read a GO definition flat file from the given URL,
     * insert or update all terms into aTargetClass.
     */
    public static void insertGoDefinitions(Class aTargetClass,
                                           IntactHelper helper,
                                           String sourceFile)
            throws Exception {

        Hashtable goRecord;

        // Parse input line by line
        BufferedReader in = new BufferedReader(new FileReader(sourceFile));

        int count = 0;

        System.err.print("GO records read: ");

        while (null != (goRecord = readRecord(in))) {

            // Progress report
            if ((++count % 10) == 0) {
                System.err.print(count + " ");
            }

            // Insert the definition
            try {
                // helper.startTransaction(BusinessConstants.JDBC_TX);
                insertDefinition(goRecord, helper, aTargetClass, false);
                // helper.finishTransaction();
            }
            catch (IntactException e){
                System.err.println("Error storing GO record " + (count - 1) + "\n" + e);
            }
        }

        System.err.println(count + " ");

        return;
    }

    /**
     * Read a GO DAG file from the given URL,
     * insert or update DAG into aTargetClass.
     */
    public static void insertGoDag(Class aTargetClass,
                                   IntactHelper helper,
                                   String sourceFile)
            throws Exception {

        // initialisation
//        Vector goRecord;
        BufferedReader in = new BufferedReader(new FileReader(sourceFile));

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

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)));
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
    private static Hashtable readRecord(BufferedReader in) throws IOException, RESyntaxException {
        Hashtable parsed = new Hashtable();
        String line;
        RE emptyLinePat = new RE("^[:blank:]*$");
        RE tagValuePat = new RE("(\\S*): (.*)");

        while (null != (line = in.readLine())) {

            // The empty line indicates the end of the record. Return parsed record.
            if (emptyLinePat.match(line)) {
                break;
            }
            ;

            // Parse a tag-value line
            if (tagValuePat.match(line)) {
                String tag = tagValuePat.getParen(1);
                String value = tagValuePat.getParen(2);

                if (null == parsed.get(tag)) {
                    parsed.put(tag, new Vector());
                }
                ;
                ((Vector) parsed.get(tag)).add(value);
            }

            // Ignore all other lines by doing nothing.
        }

        if (parsed.isEmpty()) {
            return null;
        }
        else {
            return parsed;
        }
    };

    /**
     * Return a single CvObject as a GO flatfile formatted string
     */
    public static String toGoString(CvObject current) {
        StringBuffer buf = new StringBuffer();

        // Write IntAct ac and shortlabel
        buf.append("intact_ac: ");
        buf.append(current.getAc());
        buf.append("\n");
        buf.append("shortlabel: ");
        buf.append(current.getShortLabel());
        buf.append("\n");

        // Write GO term
        buf.append("term: ");
        buf.append(current.getFullName());
        buf.append("\n");

        // Write GO id
        Collection xref = current.getXref();
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref x = (Xref) iterator.next();
            if (x.getCvDatabase().getShortLabel().equals("go")) {
                buf.append("goid: ");
                buf.append(x.getPrimaryId());
                buf.append("\n");
            }
        }

        // Write all comments in GO format
        Collection annotation = current.getAnnotation();
        for (Iterator iterator = annotation.iterator(); iterator.hasNext();) {
            Annotation a = (Annotation) iterator.next();

            buf.append((a.getCvTopic().getShortLabel()).toLowerCase() + ": ");
            buf.append(a.getAnnotationText());
            buf.append("\n");
        }

        // Write definition references
        xref = current.getXref();
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref x = (Xref) iterator.next();
            if (x.getCvDatabase().getShortLabel().equals("pubmed")) {
                buf.append("definition_reference: PMID:");
                buf.append(x.getPrimaryId());
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

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(aTargetFile)));

        // Get a random members of the class
        Collection result = helper.search(aTargetClass.getName(), "ac", "*");

        if (result.size() > 0) {
            Iterator iterator = result.iterator();
            CvDagObject o = (CvDagObject) iterator.next();
            o = o.getRoot();
            out.print(o.toGoDag());
        }

        out.close();
    }


    /** Load or unload Controlled Vocabularies in GO format.
     *  Usage:
     *  GoTools upload   IntAct_classname Go_DefinitionFile [Go_DagFile] |
     *  GoTools download IntAct_classname Go_DefinitionFile [Go_DagFile]
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        final String usage = "Usage:\n" +
                "GoTools upload   IntAct_classname Go_DefinitionFile [Go_DagFile]    OR\n" +
                "GoTools download IntAct_classname Go_DefinitionFile [Go_DagFile]";

        Class targetClass = null;

        try {
            // Check parameters
            if ((args.length < 3) || (args.length > 4)) {
                throw new IntactException("Invalid number of arguments.\n" + usage);
            }

            try {
                targetClass = Class.forName(args[1]);
            }
            catch (ClassNotFoundException e) {
                throw new IntactException("Class " + args[1] + " not found.\n" + usage);
            }


            // Create database access object
            IntactHelper helper = new IntactHelper();

            if (args[0].equals("upload")) {

                // Insert definitions
                insertGoDefinitions(targetClass, helper, args[2]);

                // Insert DAG
                if (args.length == 4) {
                    insertGoDag(targetClass, helper, args[3]);
                }
                ;

            }
            else if (args[0].equals("download")) {

                // Write definitions
                System.err.println("Writing GO definitons to " + args[2] + " ...");
                writeGoDefinitions(targetClass, helper, args[2]);

                // Write go dag format
                if (args.length == 4) {
                    System.err.println("Writing GO DAG to " + args[3] + " ...");
                    writeGoDag(targetClass, helper, args[3]);
                    System.err.println("Done.");
                }

            }
            else
                throw new IntactException("Invalid argument " + args[0] + "\n" + usage);

        }
        catch (IntactException e) {
            System.err.println(e.getMessage());
        }
    }
}

