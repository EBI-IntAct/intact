/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import uk.ac.ebi.intact.business.DuplicateLabelException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

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
     * @param ac the ac of the current object.
     * @param label
     * @param externalAc
     * @return a label unique in targetClass
     */
    public static String getUniqueShortLabel(IntactHelper helper,
                                             Class targetClass,
                                             String ac,
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
             if (ac != null) {
                 if (ac.equals(searchResult.getAc())) {
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
         if (ac != null) {
             if (ac.equals(searchResult.getAc())) {
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
					    String goidDatabase, 
                                            Class targetClass,
                                            boolean deleteold)
            throws IntactException, Exception {

        /* Update strategy:
	   Select correct term to update.
	   Update shortlabel, name, description, GO comment.
	   Subobjects are only removed if deleteOld is true.
        */

        RE pubmedRefPat = new RE("PMID\\:(\\d+)");

        // Get or create CvObject
        CvObject current = selectCvObject(helper,
					  goidDatabase,
					  definition, 
					  targetClass);

        if (null == current) {
            //This would be better done using the (owner, shortLabel) constructor
            //since - at least so far - all CvObject subclasses have the same form
            //of constructor. Thus do it further down after getting a shortLabel...
            current = getCvObject(targetClass);
            if(current == null) throw new IntactException("failed to create new CvObject of type "
             + targetClass.getName());
            current.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            helper.create(current);
        }
        else {
            if (deleteold) {
                // Delete all old data
                helper.deleteAllElements(current.getXrefs());
                current.getXrefs().removeAll(current.getXrefs());
                helper.deleteAllElements(current.getAnnotations());
                current.getAnnotations().removeAll(current.getAnnotations());
            }
        }

        // Update shortLabel. Label has to be unique!
        String label;
        String goTerm = ((Vector) definition.get("term")).elementAt(0).toString();
	String goid = "";
	try {
	    goid =  ((Vector) definition.get("goid")).elementAt(0).toString();
	} 
	catch (Exception e) {
	};
	
        // The short label might be properly defined in the GO flat file.
        if ((Vector) definition.get("shortlabel") != null) {
            label = (((Vector) definition.get("shortlabel")).elementAt(0)).toString();
        }
        else {
            label = goTerm.substring(0, Math.min(goTerm.length(), MAX_LABEL_LEN));
            label = getUniqueShortLabel(helper, targetClass, current.getAc(), label,
					goid);
        }

        current.setShortLabel(label);

        // Update fullName
        current.setFullName(goTerm.substring(0, Math.min(goTerm.length(), MAX_NAME_LEN)));

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

        // add xref to goidDatabase if it does not yet exist.
        if (((Vector) definition.get("goid") != null) &&
	    (! goidDatabase.equals("-"))) {
            Xref xref = new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                    (CvDatabase) helper.getObjectByLabel(CvDatabase.class, goidDatabase),
                    ((Vector) definition.get("goid")).elementAt(0).toString(),
                    null, null, null);
            if (! current.getXrefs().contains(xref)){
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
                    if (! current.getXrefs().contains(xref)){
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
					   String goidDatabase,
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
                insertDefinition(goRecord, helper, goidDatabase, aTargetClass, false);
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
				   String goidDatabase,
                                   String sourceFile)
            throws Exception {

        // initialisation
        Vector goRecord;
        BufferedReader in = new BufferedReader(new FileReader(sourceFile));

        System.err.println("Reading GO DAG lines: ");
        DagNode.addNodes(in, null, aTargetClass, helper, goidDatabase, 0);
        System.err.println("\nGO DAG read.");

        return;
    }

    /**
     * Write a Controlled vocabulary in GO definition format flat file.
     */
    public static void writeGoDefinitions(Class aTargetClass,
                                          IntactHelper helper,
					  String goidDatabase,
                                          String targetFile)
            throws IntactException, IOException {

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)));
        // Get all members of the class
        Collection result = helper.search(aTargetClass.getName(), "ac", "*");

        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            CvObject o = (CvObject) iterator.next();

            out.print(toGoString(o,goidDatabase));
        }

        out.close();
    }

    /**
     * Read a single GO term definition flat file record.
     *
     * @param in
     * @return Hashtable containing the parsing results.
     */
    private static Hashtable readRecord( BufferedReader in ) throws IOException, RESyntaxException {
        Hashtable parsed = new Hashtable();
        String line;

        while ( null != ( line = in.readLine() ) ) {

            // The empty line indicates the end of the record. Return parsed record.
            if( "".equals( line.trim() ) ) { // emptyLinePat.match( line ) )
                break;
            }

            int index = line.indexOf( ':' );
            if( index != -1 ) {

                String tag = line.substring( 0, index);
                String value = line.substring( index+1 ).trim();

                if( null == parsed.get( tag ) ) {
                    parsed.put( tag, new Vector() );
                }

                ( (Vector) parsed.get( tag ) ).add( value );
            }

            // Ignore all other lines by doing nothing.
        }

        if( parsed.isEmpty() ) {
            return null;
        } else {
            return parsed;
        }
    }

    /**
     * Return a single CvObject as a GO flatfile formatted string
     */
    public static String toGoString(CvObject current, String goidDatabase) {
        StringBuffer buf = new StringBuffer();

	// Write shortlabel  
	buf.append("shortlabel: ");
	buf.append(current.getShortLabel());
	buf.append("\n");
	
        // Write GO term
        buf.append("term: ");
        buf.append(current.getFullName());
        buf.append("\n");

	// write goid
	String goid = getGoid(current, goidDatabase);

	if (null != goid) {
	    buf.append("goid: ");
	    buf.append(goid);
	    buf.append("\n");
        }
	
        // Write all comments in GO format
        Collection annotation = current.getAnnotations();
        for (Iterator iterator = annotation.iterator(); iterator.hasNext();) {
            Annotation a = (Annotation) iterator.next();

            buf.append((a.getCvTopic().getShortLabel()).toLowerCase() + ": ");
            buf.append(a.getAnnotationText());
            buf.append("\n");
        }

        // Write definition references
        Collection xref = current.getXrefs();
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
     * @param goidDatabase The database xref to use for the goid.
     * @param aTargetFile  The file to print to
     * @throws IntactException
     * @throws IOException
     */
    private static void writeGoDag(Class aTargetClass, IntactHelper helper, String goidDatabase, String aTargetFile)
            throws IntactException, IOException {

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(aTargetFile)));

        // Get a random members of the class
        Collection result = helper.search(aTargetClass.getName(), "ac", "*");

        if (result.size() > 0) {
            Iterator iterator = result.iterator();
            CvDagObject o = (CvDagObject) iterator.next();
            o = o.getRoot();
            out.print(o.toGoDag(goidDatabase));
        }

        out.close();
    }


    /** Load or unload Controlled Vocabularies in GO format.
     *  Usage:
     *  GoTools upload   IntAct_classname goid_db Go_DefinitionFile [Go_DagFile] |
     *  GoTools download IntAct_classname goid_db Go_DefinitionFile [Go_DagFile]
     *  
     *  goid_db is the shortLabel of the database which is to be used to establish 
     *  object identity by mapping it to goid: in the GO flat file.
     *  Example: If goid_db is psi-mi, an CvObject with an xref "psi-mi; MI:123" is
     *  considered to be the same object as an object from the flat file with goid MI:123.
     *  If goid_db is '-', the shortLabel will be used if present.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        final String usage = "Usage:\n" +
	    "GoTools upload   IntAct_classname goid_db Go_DefinitionFile [Go_DagFile]    OR\n" +
	    "GoTools download IntAct_classname goid_db Go_DefinitionFile [Go_DagFile]\n"  +
	    "\n"  +
	    "goid_db is the shortLabel of the database which is to be used to establish \n"  +
	    "object identity by mapping it to goid: in the GO flat file.\n" +
	    "Example: If goid_db is psi-mi, an CvObject with an xref psi-mi; MI:123 is\n" +
	    "considered to be the same object as an object from the flat file with goid MI:123.\n" +
	    "If goid_db is '-', the short label will be used if present.";

        Class targetClass = null;

        try {
            // Check parameters
            if ((args.length < 4) || (args.length > 5)) {
                System.out.println ( "Invalid number of arguments.\n" + usage );
                System.exit( 1 );
            }

            try {
                targetClass = Class.forName(args[1]);
            }
            catch (ClassNotFoundException e) {
                System.out.println ( "Class " + args[1] + " not found.\n" + usage );
                System.exit( 1 );
            }


            // Create database access object
            IntactHelper helper = new IntactHelper();

            if (args[0].equals("upload")) {

                // Insert definitions
                insertGoDefinitions(targetClass, helper, args[2], args[3]);

                // Insert DAG
                if (args.length == 5) {
                    insertGoDag(targetClass, helper, args[2], args[4]);
                }

            }
            else if (args[0].equals("download")) {

                // Write definitions
                System.err.println("Writing GO definitons to " + args[3] + " ...");
                writeGoDefinitions(targetClass, helper, args[2], args[3]);

                // Write go dag format
                if (args.length == 5) {
                    System.err.println("Writing GO DAG to " + args[4] + " ...");
                    writeGoDag(targetClass, helper, args[2], args[4]);
                    System.err.println("Done.");
                }

            }
            else {
                System.out.println ( "Invalid argument " + args[0] + "\n" + usage );
                System.exit( 1 );
            }
        }
        catch (IntactException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Used to run a no-arg constructor as we don't know what
     * type of CvObject we have. This is OK since we are only in this
     * application when loading a DB and if the user does not have
     * access rights to it then the load will fail anyway.
     * @param clazz The class to create an instance of
     * @return CvObject A concrete subclass of CvObject, or null
     * if the creation failed.
     * @throws Exception thrown if there was a reflection problem
     */
    private static CvObject getCvObject(Class clazz) throws Exception {

        Constructor[] constructors = clazz.getDeclaredConstructors();
        Constructor noArgs = null;
        for(int i=0; i < constructors.length; i++) {
            if(constructors[i].getParameterTypes().length == 0) {
                //got the no-arg one - done
                noArgs = constructors[i];
                break;
            }
        }
        if((noArgs != null) & CvObject.class.isAssignableFrom(clazz)) {
            noArgs.setAccessible(true);
            return (CvObject)noArgs.newInstance(null);
        }
        return null;

    }

    /** 
	Select an appropriate CvObject for update if it exists.
	Criterion of object identity:
	if goidDatabase is '-', try to match by shortlabel
	otherwise try to match by goid and goidDatabase	
    */
    public static CvObject selectCvObject(IntactHelper helper, 
					  String goidDatabase,
					  String goid,
					  String shortLabel,
					  Class targetClass) 
	throws IntactException {
	
	if (goidDatabase.equals("-")){
	    if(null != shortLabel){
		return (CvObject) helper.getObjectByLabel(targetClass, shortLabel);
	    }
	} else {
	    if ((null != goidDatabase) 
		&&
		(null != goid)){
		CvObject current = (CvObject) helper.getObjectByXref(targetClass, goid);
		if (null != current){
		    Collection xref = current.getXrefs();
		    for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
			Xref x = (Xref) iterator.next();
			if (x.getCvDatabase().getShortLabel().equals(goidDatabase)
			    &&
			    x.getPrimaryId().equals(goid)) {
			    return current;
			}			    
		    }
		}
	    }
	    // We have not found any match by goid. Try shortlabel.
	    return (CvObject) helper.getObjectByLabel(targetClass, shortLabel); 
	}
	
	return null;
    }

    /** 
	Select an appropriate CvObject for update if it exists.
	Criterion of object identity:
	if goidDatabase is '-', try to match by shortlabel
	otherwise try to match by goid and goidDatabase	
    */
    public static CvObject selectCvObject(IntactHelper helper, 
					  String goidDatabase,
					  Hashtable definition,
					  Class targetClass)  
	throws IntactException {
	
        // Get goid
	String goid = null;
	try {
	    goid =  ((Vector) definition.get("goid")).elementAt(0).toString();
	} 
	catch (Exception e) {
	};

        // Get shortLabel
	String shortLabel = null;
	try {
	    shortLabel =  ((Vector) definition.get("shortlabel")).elementAt(0).toString();
	} 
	catch (Exception e) {
	};

	return selectCvObject(helper, goidDatabase, goid, shortLabel, targetClass);
    }


    /** Return an identifier to be used in the go flat file format
	for the goid: element.
    */
    public static String getGoid(CvObject current, 
				 String goidDatabase){

	if (! goidDatabase.equals("-")){
	    Collection xref = current.getXrefs();
	    for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
		Xref x = (Xref) iterator.next();
		if (x.getCvDatabase().getShortLabel().equals(goidDatabase)) {
		    // There should be only one GO id
		    return x.getPrimaryId();
		}
	    };
	}	
	return null;
    }
}

