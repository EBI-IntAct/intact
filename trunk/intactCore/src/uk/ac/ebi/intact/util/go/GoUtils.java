/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.go;

import uk.ac.ebi.intact.business.DuplicateLabelException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities to read and write files in GO format
 */
public class GoUtils {

    // ------------------------------------------------------------------------

    /**
     * Collects GO data.
     */
    private static class GoRecord {

        private String myGoTerm;
        private String myGoId;
        private String myGoShortLabel;

        /**
         * Topic:->List of of comments
         */
        private Map myComments = new HashMap();

        /**
         * Default constructor
         */
        private GoRecord() {}

        /**
         * Constructs a GO record from go id, term and short label.
         *
         * @param goId the GO id.
         * @param goTerm the GO term
         * @param goShortLabel GO shortlabel.
         */
        private GoRecord(String goId, String goTerm, String goShortLabel) {
            myGoId = goId;
            myGoTerm = goTerm;
            myGoShortLabel = goShortLabel;
        }

        // Read methods.

        private String getGoTerm() {
            return myGoTerm;
        }

        private String getGoId() {
            return myGoId;
        }

        private String getGoShortLabel() {
            return myGoShortLabel;
        }

        /**
         * @return true if a GO id exists for this record.
         */
        private boolean hasGoId() {
            return myGoId != null;
        }

        /**
         * @return true if a short label exists for this record.
         */
        private boolean hasGoShortLabel() {
            return myGoShortLabel != null;
        }

        private Iterator getKeys() {
            return myComments.keySet().iterator();
        }

        /**
         * @param topic the topic to get the annotation texts
         * @return annotation texts as an interator for given topic or
         *         an empty iterator is returned if there aren't any texts found
         *         found for <code>topic</code>.
         */
        private Iterator getAnnotationTexts(String topic) {
            if (myComments.containsKey(topic)) {
                return ((List) myComments.get(topic)).iterator();
            }
            // No topics found for given key.
            return Collections.EMPTY_LIST.iterator();
        }

        /**
         * @return definitions as an interator or an empty iterator is returned
         *         if there aren't any definitions.
         */
        private Iterator getDefinitionReferences() {
            if (myComments.containsKey("definition_reference")) {
                return ((List) myComments.get("definition_reference")).iterator();
            }
            // No definitions.
            return Collections.EMPTY_LIST.iterator();
        }

        // Write methods

        private void setGoShortLabel(String shortlabel) {
            myGoShortLabel = shortlabel;
        }

        private void setGoId(String goid) {
            myGoId = goid;
        }

        private void setGoTerm(String term) {
            myGoTerm = term;
            int index2 = term.indexOf(':');
            if (index2 != -1) {
                setGoShortLabel(term.substring(0, index2));
                // The rest as the go term
                myGoTerm = term.substring(index2 + 1).trim();
            }
            else {
                // No short label present.
                myGoTerm = term;
            }
        }

        /**
         * Inserts given value under a key
         *
         * @param key the key to store <code>value</code>
         * @param value is stored under <code>key</code>.
         */
        private void put(String key, String value) {
            if (!myComments.containsKey(key)) {
                myComments.put(key, new LinkedList());
            }
            ((List) myComments.get(key)).add(value);
        }
    }
    // ------------------------------------------------------------------------

    // Global Data

    /**
     * Maximum length of a shortLabel
     */
    public static final int ourMaxLabelLen = 20;

    // Class Data

    /**
     * Maximum length of a fullName
     */
    private static final int ourMaxNameLen = 250;

    /**
     * The short label of the pubmed database.
     */
    private static final String ourPubMedDB = "pubmed";

    /**
     * The short label of the resid database.
     */
    private static final String ourResIdDB = "resid";

    /**
     * The pattern to match the PubMed id.
     */
    private static final Pattern ourPubmedRegex = Pattern.compile("PMID:(\\d+)");

    /**
     * The line identifier for a RES.
     */
    private static final String ourResId = "RESID:";

    // Private attributes

    /**
     * Reference to the Intact helper.
     */
    private IntactHelper myHelper;

    /**
     * The name of the GO database.
     */
    private String myGoIdDatabase;

    /**
     * The target class.
     */
    private Class myTargetClass;

    /**
     * Constructs an instance of this clas with given helper, database name and
     * target class.
     *
     * @param helper the IntAct helper
     * @param goIdDatabase the name of the Go database
     * @param targetClass the target class.
     */
    public GoUtils(IntactHelper helper, String goIdDatabase, Class targetClass) {
        myHelper = helper;
        myGoIdDatabase = goIdDatabase;
        myTargetClass = targetClass;
    }

    // Class methods.

    /**
     * Returns a string that has maximum of {@link #ourMaxLabelLen} and all characters
     * are in lowercase. Givenb string is only truncated if it erxceeds max characters.
     *
     * @param label given string
     * @return the string after normalizing <code>label</code>.
     */
    public static String normalizeShortLabel(String label) {
        if (label.length() > ourMaxLabelLen) {
            return label.substring(0, Math.min(label.length(),
                    ourMaxLabelLen)).toLowerCase();
        }
        return label.toLowerCase();
    }

    /**
     * Return an identifier to be used in the go flat file format
     * for the goid: element.
     * @param current the current CvObjct to get xrefs.
     * @param goidDatabase the GO id database to match to get get the GO id.
     * @return the GO id or null if <code>goidDatabase</code> is empty (i.e, '-')
     * or no matching database found for given <code>goidDatabase</code>.
     */
    public static String getGoid(CvObject current, String goidDatabase) {
        if (goidDatabase.equals("-")) {
            return null;
        }
        Collection xref = current.getXrefs();
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref x = (Xref) iterator.next();
            if (x.getCvDatabase().getShortLabel().equals(goidDatabase)) {
                // There should be only one GO id
                return x.getPrimaryId();
            }
        }
        return null;
    }

    // Read methods

    public IntactHelper getHelper() {
        return myHelper;
    }

    public String getGoIdDatabase() {
        return myGoIdDatabase;
    }

    public Class getTargetClass() {
        return myTargetClass;
    }

    /**
     * Select an appropriate CvObject for update if it exists.
     * Criterion of object identity:
     * if goidDatabase is '-', try to match by shortlabel
     * otherwise try to match by goid and goidDatabase
     * @param goid the GO id
     * @param shortLabel the short label to match
     */
    public CvObject selectCvObject(String goid, String shortLabel)
            throws IntactException {

        if (myGoIdDatabase.equals("-")) {
            if (null != shortLabel) {
                return (CvObject) myHelper.getObjectByLabel(myTargetClass, shortLabel);
            }
        }
        else {
            if ((null != myGoIdDatabase) && (null != goid)) {
                CvObject current = (CvObject) myHelper.getObjectByXref(myTargetClass, goid);
                if (null != current) {
                    Collection xref = current.getXrefs();
                    for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
                        Xref x = (Xref) iterator.next();
                        if (x.getCvDatabase().getShortLabel().equals(myGoIdDatabase)
                                && x.getPrimaryId().equals(goid)) {
                            return current;
                        }
                    }
                }
            }
            // We have not found any match by goid. Try shortlabel.
            return (CvObject) myHelper.getObjectByLabel(myTargetClass, shortLabel);
        }
        return null;
    }

    /**
     * Inserts go definition entry.
     *
     * @param goId the GO id.
     * @param goTerm the GO term
     * @param goShortLabel GO shortlabel.
     * @param deleteold true if to delete previous records.
     * @return the CVObject inserted
     * @throws IntactException for errors in accessing persistent system.
     */
    public CvObject insertDefinition(String goId, String goTerm,
                                     String goShortLabel, boolean deleteold)
            throws IntactException {
        return insertDefinition(new GoRecord(goId, goTerm, goShortLabel), deleteold);
    }

    /**
     * Read a GO definition flat file from the given URL,
     * insert or update all terms into aTargetClass.
     * @param sourceFile the name of the source file.
     * @throws IOException for I/O errors.
     */
    public void insertGoDefinitions(String sourceFile) throws IOException {
        // The go record to return.
        GoRecord goRec;

        // Counter for progress report.
        int count = 0;

        System.err.print("GO records read: ");

        // The reader to read GO defs file.
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(sourceFile));
            while (null != (goRec = readRecord(in))) {
                // Progress report
                if ((++count % 10) == 0) {
                    System.out.print(count + " ");
                }

                // Insert the definition
                try {
                    insertDefinition(goRec, false);
                }
                catch (IntactException e) {
                    System.err.println("Error storing GO record " + (count - 1));
                    System.err.println(e);
                }
            }
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
        System.out.println(count + " ");
    }

    /**
     * Read a GO DAG file from the given URL,
     * insert or update DAG into aTargetClass.
     * @param sourceFile the input file to read GO Dag info.
     * @throws IOException for I/O errors
     * @throws IntactException for errors in accesing IntAct database.
     */
    public void insertGoDag(String sourceFile) throws IOException, IntactException {
        System.out.println("Reading GO DAG lines: ");
        // initialisation
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(sourceFile));
            new DagNodeUtils(this).addNodes(in, null, 0);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
        System.out.println("\nGO DAG read.");
    }

    /**
     * Writes a Controlled vocabulary in GO definition format flat file.
     *
     * @param targetFile the name of the file to write to.
     * @param v14 true to write the file in new DAG V14 format.
     * @throws IntactException for errors in accessing the persisten system.
     * @throws IOException for I/O errors.
     */
    public void writeGoDefinitions(String targetFile, boolean v14)
            throws IntactException, IOException {
        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)));

            // Get all members of the class
            Collection result = myHelper.search(myTargetClass, "ac", "*");

            for (Iterator iterator = result.iterator(); iterator.hasNext();) {
                printGoDef((CvObject) iterator.next(), out, v14);
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Print the GO format DAG to a file.
     *
     * @param targetFile the name of the file to write to.
     * @param v14 true to write the file in new DAG V14 format.
     * @throws IntactException for errors in accessing the persisten system.
     * @throws IOException for I/O errors.
     */
    public void writeGoDag(String targetFile, boolean v14) throws IntactException, IOException {
        // The writer to write the output.
        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)));

            // Get a random members of the class
            Collection result = myHelper.search(myTargetClass, "ac", "*");

            if (result.size() > 0) {
                Iterator iterator = result.iterator();
                CvDagObject o = (CvDagObject) iterator.next();
                DagGenerator dagGenerator = new DagGenerator(out, myGoIdDatabase, v14);
                dagGenerator.toGoDag(o.getRoot());
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // Helper methods (static)

    /**
     * Read a single GO term definition flat file record.
     *
     * @param in
     * @return Hashtable containing the parsing results.
     */
    private static GoRecord readRecord(BufferedReader in) throws IOException {
        GoRecord goEntry = null;

        // Empty line matching pattern.
        Pattern emptyLineRegex = Pattern.compile("^\\s*$");

        // A line to read from the input file.
        String line;

        while (null != (line = in.readLine())) {
            // The empty line indicates the end of the record. Return parsed record.
            if (emptyLineRegex.matcher(line).matches()) {
                break;
            }
            // Ignore comment lines.
            if (line.startsWith("!")) {
                continue;
            }

            int index = line.indexOf(':');
            // Ignore all other lines by doing nothing.
            if (index == -1) {
                continue;
            }

            String tag = line.substring(0, index);
            String value = line.substring(index + 1).trim();

            // The term for new go format contains short label.
            if (tag.equals("term")) {
                if (goEntry == null) {
                    goEntry = new GoRecord();
                }
                goEntry.setGoTerm(value);
            }

            // Old DAG format.
            else if (tag.equals("shortlabel")) {
                if (goEntry == null) {
                    goEntry = new GoRecord();
                }
                goEntry.setGoShortLabel(value);
            }
            // Check for goid or id.
            else if (tag.equals("id") || tag.equals("goid")) {
                goEntry.setGoId(value);
            }
            else {
                goEntry.put(tag, value);
            }
        }
        return goEntry;
    }

    // Helper methods

    /**
     * Insert a GO term into IntAct.
     *
     * @param goRec contains GO data
     * @param deleteold true if to delete previous records.
     * @return the CVObject inserted
     * @throws IntactException for errors in accessing persistent system.
     */
    private CvObject insertDefinition(GoRecord goRec, boolean deleteold)
            throws IntactException {
        // Cache the institution.
        Institution inst = myHelper.getInstitution();

        // Cache the CV objects.
        CvDatabase goidDB = null;
        if (!myGoIdDatabase.equals("-")) {
            goidDB = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                    myGoIdDatabase);
        }
        CvDatabase pubmedDB = (CvDatabase) myHelper.getObjectByLabel(
                CvDatabase.class, ourPubMedDB);
        CvDatabase residDB = (CvDatabase) myHelper.getObjectByLabel(
                CvDatabase.class, ourResIdDB);
        CvXrefQualifier identity = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "identity");
        CvXrefQualifier goDefRef = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "go-definition-ref");

        // Update shortLabel. Label has to be unique!
        String goTerm = goRec.getGoTerm();

        // The short label for the current node.
        String label;
        if (goRec.hasGoShortLabel()) {
            label = goRec.getGoShortLabel();
        }
        else {
            // Use the GO term to form a short label.
            label = goTerm;
        }
        // Normalize the short label to IntAct format.
        label = normalizeShortLabel(label);

        // Get or create CvObject
        CvObject current = selectCvObject(goRec.getGoId(), label);

        if (null == current) {
            //This would be better done using the (owner, shortLabel) constructor
            //since - at least so far - all CvObject subclasses have the same form
            //of constructor. Thus do it further down after getting a shortLabel...
            current = getCvObject(myTargetClass);
            if (current == null)
                throw new IntactException("failed to create new CvObject of type "
                        + myTargetClass.getName());
            current.setOwner(inst);
            myHelper.create(current);
        }
        else {
            if (deleteold) {
                // Delete all old data
                myHelper.deleteAllElements(current.getXrefs());
                current.getXrefs().clear();
                myHelper.deleteAllElements(current.getAnnotations());
                current.getAnnotations().clear();
                myHelper.deleteAllElements(current.getAliases());
                current.getAliases().clear();
            }
        }
        if (shortLabelExist(label, current.getAc())) {
            throw new IntactException(label + " already exists!");
        }
        current.setShortLabel(label);

        // Update fullName
        current.setFullName(goTerm.substring(0, Math.min(goTerm.length(), ourMaxNameLen)));

        // Update all comments
        for (Iterator comments = goRec.getKeys(); comments.hasNext();) {
            String topic = (String) comments.next();
            CvTopic cvtopic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, topic);
            if (cvtopic == null) {
                // Topic is not found, continue with the next.
                continue;
            }
            // Loop through each annotation stored under a single topic.
            for (Iterator texts = goRec.getAnnotationTexts(topic); texts.hasNext();) {
                // Create a proper annotation - needed it for equals method.
                Annotation annotation = new Annotation(inst, cvtopic);
                annotation.setAnnotationText((String) texts.next());

                // Avoid duplicate annotations.
                if (current.getAnnotations().contains(annotation)) {
                    continue;
                }
                // Unique annotation, create it on the persistent system.
                current.addAnnotation(annotation);
                myHelper.create(annotation);
            }
        }

        // add xref to goidDatabase if it does not yet exist.
        if (goRec.hasGoId() && goidDB != null) {
            Xref xref = new Xref(inst, goidDB, goRec.getGoId(), null, null, identity);
            if (!current.getXrefs().contains(xref)) {
                current.addXref(xref);
                myHelper.create(xref);
            }
        }

        // add definition references
        for (Iterator defs = goRec.getDefinitionReferences(); defs.hasNext();) {
            String defRef = (String) defs.next();
            Matcher m = ourPubmedRegex.matcher(defRef);
            if (m.matches()) {
                // add Pubmed xref
                Xref xref = new Xref(inst, pubmedDB, m.group(1), null, null, goDefRef);
                if (!current.getXrefs().contains(xref)) {
                    current.addXref(xref);
                    myHelper.create(xref);
                }
                continue;
            }
            if (defRef.startsWith(ourResId)) {
                String residStr = defRef.substring(ourResId.length());
                StringTokenizer stk = new StringTokenizer(residStr, ",");
                while (stk.hasMoreTokens()) {
                    String token = stk.nextToken();
                    // add Resid xref
                    Xref xref = new Xref(inst, residDB, token.trim(), null, null, goDefRef);
                    if (!current.getXrefs().contains(xref)) {
                        current.addXref(xref);
                        myHelper.create(xref);
                    }
                }
            }
        }
        // Update main object
        if (myHelper.isPersistent(current)) {
            myHelper.update(current);
        }
        return current;
    }

    /**
     * True if given label exists in the persistent system.
     *
     * @param label the label to search
     * @param ac the AC to check the AC of the retrieved object (if it is same
     * then this method assumes that label is unique).
     * @return true if <code>label</code> exists in the persistent system. False
     *         is returned if a record exists but its AC is as same as <code>ac</code>.
     */
    private boolean shortLabelExist(String label, String ac) {
        // get all objects by label. If more than one, need to modify label.
        try {
            AnnotatedObject result = (AnnotatedObject) myHelper.getObjectByLabel(
                    myTargetClass, label);
            if (result == null) {
                return false;
            }
            if (result.getAc().equals(ac)) {
                // No object has label, or only the current object.
                // Therefore, if label is assigned to current, it will still be unique.
                return false;
            }
        }
        catch (DuplicateLabelException d) {
        }
        catch (IntactException e) {
        }
        // There is another record exists with the same short label.
        return true;
    }

    /**
     * Used to run a no-arg constructor as we don't know what
     * type of CvObject we have. This is OK since we are only in this
     * application when loading a DB and if the user does not have
     * access rights to it then the load will fail anyway.
     *
     * @param clazz The class to create an instance of
     * @return CvObject A concrete subclass of CvObject, or null
     *         if the creation failed.
     */
    private static CvObject getCvObject(Class clazz) {

        Constructor[] constructors = clazz.getDeclaredConstructors();
        Constructor noArgs = null;
        for (int i = 0; i < constructors.length; i++) {
            if (constructors[i].getParameterTypes().length == 0) {
                //got the no-arg one - done
                noArgs = constructors[i];
                break;
            }
        }
        if ((noArgs != null) & CvObject.class.isAssignableFrom(clazz)) {
            noArgs.setAccessible(true);
            try {
                return (CvObject) noArgs.newInstance(null);
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Prints given CvObject as a GO flatfile formatted string.
     *
     * @param cvobj the CVObject to print.
     * @param out the write to write to
     * @param v14 true for DAG 14 format (the new format).
     */
    private void printGoDef(CvObject cvobj, PrintWriter out, boolean v14)
            throws IntactException {
        // Write shortlabel (for the old format)
        if (!v14) {
            out.print("shortlabel: ");
            out.println(cvobj.getShortLabel());
        }
        // Write GO term
        out.print("term: ");

        if (v14) {
            // The new format combines short label and full name under term
            if (cvobj.getShortLabel().equals(cvobj.getFullName())) {
                out.println(cvobj.getShortLabel());
            }
            else {
                out.println(cvobj.getShortLabel() + ": " + cvobj.getFullName());
            }
        }
        else {
            out.println(cvobj.getFullName());
        }

        // write goid
        String goid = getGoid(cvobj, myGoIdDatabase);
        if (goid != null) {
            out.print(v14 ? "id: " : "goid: ");
            out.println(goid);
        }
	
        // Write all comments in GO format
        Collection annotation = cvobj.getAnnotations();
        for (Iterator iterator = annotation.iterator(); iterator.hasNext();) {
            Annotation a = (Annotation) iterator.next();
            out.print(a.getCvTopic().getShortLabel() + ": ");
            out.println(a.getAnnotationText());
        }
        // Print pubmed db info.
        String pubmedLine = getPubmedString(cvobj);
        if (pubmedLine != null) {
            out.print(pubmedLine);
        }

        // Print resid db info.
        String residLine = getResIdString(cvobj);
        if (residLine != null) {
            out.println(residLine);
        }
        // Need to print a dummy definition reference if none found.
        if ((pubmedLine == null) && (residLine == null)) {
            out.println("definition_reference: PMID:INTACT");
        }
        // Blank line to separate an entry.
        out.println();
    }

    private String getPubmedString(CvObject cvobj) throws IntactException {
        // The line separator.
        String nl = System.getProperty("line.separator");

        // Construct the result.
        StringBuffer sb = new StringBuffer();

        CvDatabase pubmedDB = (CvDatabase) myHelper.getObjectByLabel(
                CvDatabase.class, ourPubMedDB);

        Collection xref = cvobj.getXrefs();
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref x = (Xref) iterator.next();
            if (!x.getCvDatabase().equals(pubmedDB)) {
                 continue;
            }
            if (x.getCvDatabase().equals(pubmedDB)) {
                sb.append("definition_reference: PMID:");
                sb.append(x.getPrimaryId());
                sb.append(nl);
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String getResIdString(CvObject cvobj) throws IntactException {
        // Construct the result.
        StringBuffer sb = new StringBuffer();

        CvDatabase residDB = (CvDatabase) myHelper.getObjectByLabel(
                CvDatabase.class, ourResIdDB);

        // A flag to print to print first resid entry.
        boolean first = true;

        Collection xref = cvobj.getXrefs();
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref x = (Xref) iterator.next();
            if (!x.getCvDatabase().equals(residDB)) {
                 continue;
            }
            if (first) {
                sb.append("definition_reference: ");
                sb.append(ourResId);
                sb.append(x.getPrimaryId());
                first = false;
            }
            else {
                sb.append(" , ");
                sb.append(x.getPrimaryId());
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }
}
