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
        private GoRecord() {
        }

        /**
         * Constructs a GO record from go id, term and short label.
         *
         * @param goId         the GO id.
         * @param goTerm       the GO term
         * @param goShortLabel GO shortlabel.
         */
        private GoRecord( String goId, String goTerm, String goShortLabel ) {
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
         *
         * @return annotation texts as an interator for given topic or an empty iterator is returned if there aren't any
         *         texts found found for <code>topic</code>.
         */
        private Iterator getAnnotationTexts( String topic ) {
            if ( myComments.containsKey( topic ) ) {
                return ( (List) myComments.get( topic ) ).iterator();
            }
            // No topics found for given key.
            return Collections.EMPTY_LIST.iterator();
        }

        /**
         * @return definitions as an interator or an empty iterator is returned if there aren't any definitions.
         */
        private Iterator getDefinitionReferences() {
            if ( myComments.containsKey( "definition_reference" ) ) {
                return ( (List) myComments.get( "definition_reference" ) ).iterator();
            }
            // No definitions.
            return Collections.EMPTY_LIST.iterator();
        }

        // Write methods

        private void setGoShortLabel( String shortlabel ) {
            myGoShortLabel = shortlabel;
        }

        private void setGoId( String goid ) {
            myGoId = goid;
        }

        private void setGoTerm( String term ) {
            myGoTerm = term;
            int index2 = term.indexOf( ':' );
            if ( index2 != -1 ) {
                setGoShortLabel( term.substring( 0, index2 ) );
                // The rest as the go term
                myGoTerm = term.substring( index2 + 1 ).trim();
            } else {
                // No short label present.
                myGoTerm = term;
            }
        }

        /**
         * Inserts given value under a key
         *
         * @param key   the key to store <code>value</code>
         * @param value is stored under <code>key</code>.
         */
        private void put( String key, String value ) {
            if ( !myComments.containsKey( key ) ) {
                myComments.put( key, new LinkedList() );
            }
            ( (List) myComments.get( key ) ).add( value );
        }


        public String toString() {
            StringBuffer sb = new StringBuffer( 256 );

            sb.append( "GoRecord{" ).append( "\n" );
            sb.append( "myGoShortLabel='" ).append( myGoShortLabel ).append( "'" ).append( "\n" );
            sb.append( "myGoTerm='" ).append( myGoTerm ).append( "'" ).append( "\n" );
            sb.append( "myGoId='" ).append( myGoId ).append( "'" ).append( "\n" );
            sb.append( "myComments={'" ).append( "\n" );
            for ( Iterator iterator = myComments.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                Collection values = (Collection) myComments.get( key );

                for ( Iterator iterator1 = values.iterator(); iterator1.hasNext(); ) {
                    String value = (String) iterator1.next();

                    sb.append( '\t' ).append( "'" + key + "'" ).append( " -> " ).append( "'" + value + "'" ).append( "\n" );
                }
            }
            sb.append( "}" );

            return sb.toString();
        }
    }
    // ------------------------------------------------------------------------

    // Global Data

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
    private static final Pattern ourPubmedRegex = Pattern.compile( "PMID:(\\d+)" );

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
     * Constructs an instance of this clas with given helper, database name and target class.
     *
     * @param helper       the IntAct helper
     * @param goIdDatabase the name of the Go database
     * @param targetClass  the target class.
     */
    public GoUtils( IntactHelper helper, String goIdDatabase, Class targetClass ) {
        myHelper = helper;
        myGoIdDatabase = goIdDatabase;
        myTargetClass = targetClass;

    }


    /**
     * The owner of the created object
     */
    protected static Institution myInstitution;

    /**
     * Xref databases
     */
    protected static CvDatabase pubmed;
    protected static CvDatabase resid;

    /**
     * Describe wether an Xref is related the primary SPTR AC (identityCrefQualifier) or not (secondaryXrefQualifier)
     */
    protected static CvXrefQualifier goDefinition;
    protected static CvXrefQualifier identity;

    public CvXrefQualifier getGoDefinitionQualifier() throws IntactException {

        if ( goDefinition == null ) {
            goDefinition = (CvXrefQualifier) getCvObjectViaMI( CvXrefQualifier.class, CvXrefQualifier.GO_DEFINITION_REF_MI_REF ); // go-definition-ref

            if ( goDefinition == null ) {
                throw new IntactException( "Could not find CvXrefQualifier(go-definition-ref) using " + CvXrefQualifier.GO_DEFINITION_REF_MI_REF );
            }
        }

        return goDefinition;
    }

    public CvXrefQualifier getIdentityQualifier() throws IntactException {

        if ( identity == null ) {
            identity = (CvXrefQualifier) getCvObjectViaMI( CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF ); // go-definition-ref

            if ( identity == null ) {
                throw new IntactException( "Could not find CvXrefQualifier(identity) using " + CvXrefQualifier.IDENTITY_MI_REF );
            }
        }

        return identity;
    }

    public CvDatabase getPubmedDatabase() throws IntactException {

        if ( pubmed == null ) {
            pubmed = (CvDatabase) getCvObjectViaMI( CvDatabase.class, CvDatabase.PUBMED_MI_REF ); // go-definition-ref

            if ( pubmed == null ) {
                throw new IntactException( "Could not find CvDatabase using " + CvDatabase.PUBMED_MI_REF );
            }
        }

        return pubmed;
    }

    public CvDatabase getResidDatabase() throws IntactException {

        if ( resid == null ) {
            resid = (CvDatabase) getCvObjectViaMI( CvDatabase.class, CvDatabase.RESID_MI_REF ); // go-definition-ref

            if ( resid == null ) {
                throw new IntactException( "Could not find CvDatabase using " + CvDatabase.RESID_MI_REF );
            }
        }

        return resid;
    }

    /**
     * Get a CvObject based on its class name and its shortlabel.
     *
     * @param clazz      the Class we are looking for
     * @param shortlabel the shortlabel of the object we are looking for
     *
     * @return the CvObject of type <code>clazz</code> and having the shortlabel <code>shorltabel<code>.
     *
     * @throws IntactException if the search failed or the object is not found.
     */
    private CvObject getCvObject( Class clazz, String shortlabel ) throws IntactException {

        CvObject cv = (CvObject) myHelper.getObjectByLabel( clazz, shortlabel );
        if ( cv == null ) {
            StringBuffer sb = new StringBuffer( 128 );
            sb.append( "Could not find " );
            sb.append( shortlabel );
            sb.append( ' ' );
            sb.append( clazz.getName() );
            sb.append( " in your IntAct node" );

            throw new IntactException( sb.toString() );
        }

        return cv;
    }

    /**
     * Get a CvObject based on its class name and its shortlabel.
     *
     * @param clazz the Class we are looking for
     * @param miRef the PSI-MI reference of the object we are looking for
     *
     * @return the CvObject of type <code>clazz</code> and having the PSI-MI reference.
     *
     * @throws IntactException if the search failed or the object is not found.
     */
    private CvObject getCvObjectViaMI( Class clazz, String miRef ) throws IntactException {

        CvObject cv = (CvObject) myHelper.getObjectByXref( clazz, miRef );

        if ( cv == null ) {
            StringBuffer sb = new StringBuffer( 128 );
            sb.append( "Could not find " );
            sb.append( miRef );
            sb.append( ' ' );
            sb.append( clazz.getName() );
            sb.append( " in your IntAct node" );

            throw new IntactException( sb.toString() );
        }

        return cv;
    }

    // Class methods.

    /**
     * Returns a string that has maximum of {@link AnnotatedObject.MAX_SHORT_LABEL_LEN} and all characters are in
     * lowercase. Givenb string is only truncated if it exceeds max characters.
     *
     * @param label given string
     *
     * @return the string after normalizing <code>label</code>.
     */
    public static String normalizeShortLabel( String label ) {

        return label.toLowerCase();
    }

    /**
     * Return an identifier to be used in the go flat file format for the goid: element.
     *
     * @param current      the current CvObjct to get xrefs.
     * @param goidDatabase the GO id database to match to get get the GO id.
     *
     * @return the GO id or null if <code>goidDatabase</code> is empty (i.e, '-') or no matching database found for
     *         given <code>goidDatabase</code>.
     */
    public static String getGoid( CvObject current, String goidDatabase ) {
        if ( goidDatabase.equals( "-" ) ) {
            return null;
        }
        Collection xref = current.getXrefs();
        for ( Iterator iterator = xref.iterator(); iterator.hasNext(); ) {
            Xref x = (Xref) iterator.next();
            if ( x.getCvDatabase().getShortLabel().equals( goidDatabase ) ) {
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
     * Select an appropriate CvObject for update if it exists. Criterion of object identity: if goidDatabase is '-' or
     * godid is null , try to match by shortlabel otherwise try to match by goid and goidDatabase
     *
     * @param goid       the GO id
     * @param shortLabel the short label to match
     */
    public CvObject selectCvObject( String goid, String shortLabel ) throws IntactException {
        // If the GO database '-' or no GO id
        if ( myGoIdDatabase.equals( "-" ) || ( goid == null ) ) {
            return (CvObject) myHelper.getObjectByLabel( myTargetClass, shortLabel );
        }
        // Try with Go id
        CvObject current = (CvObject) myHelper.getObjectByXref( myTargetClass, goid );
        if ( null != current ) {
            for ( Iterator iterator = current.getXrefs().iterator(); iterator.hasNext(); ) {
                Xref x = (Xref) iterator.next();
                if ( x.getCvDatabase().getShortLabel().equals( myGoIdDatabase )
                     && x.getPrimaryId().equals( goid ) ) {
                    return current;
                }
            }
        }
        // We have not found any match by goid. Try shortlabel.
        return (CvObject) myHelper.getObjectByLabel( myTargetClass, shortLabel );
    }

    /**
     * Inserts go definition entry.
     *
     * @param goId         the GO id.
     * @param goTerm       the GO term
     * @param goShortLabel GO shortlabel.
     * @param deleteold    true if to delete previous records.
     *
     * @return the CVObject inserted
     *
     * @throws IntactException for errors in accessing persistent system.
     */
//    public CvObject insertDefinition( String goId, String goTerm,
//                                      String goShortLabel, boolean deleteold )
//            throws IntactException {
//        return insertDefinition( new GoRecord( goId, goTerm, goShortLabel ), deleteold );
//    }


    /**
     * Read a GO definition flat file from the given URL, insert or update all terms into aTargetClass. <br> the
     * insertion of CV terms is done in the following manner:<br> (1) create all the CV term without Annotation and
     * Xref,<br> (2) create Xrefs for each term,<br> (3) create Annotation for each term.
     *
     * @param sourceFile the file that contains the def file.
     *
     * @throws IOException for I/O errors.
     */
    public void insertGoDefinitions( File sourceFile ) throws IOException {

        // Counter for progress report.
        int count = 0;

        System.out.print( "Reading GO records ... " );
        System.out.flush();

        // The reader to read GO defs file.
        BufferedReader in = null;

        Collection goTerms = new ArrayList( 32 );

        // 1. read the entire file into object model
        try {
            GoRecord goRec;
            in = new BufferedReader( new FileReader( sourceFile ) );
            while ( null != ( goRec = readRecord( in ) ) ) {
                // Progress report
                count++;

                goTerms.add( goRec );
            }
        } finally {
            if ( in != null ) {
                in.close();
            }
        }

        System.out.println( count + " term" + ( count > 1 ? "s" : "" ) );


        // 2. Insert all the CVs first without Xrefs nor Annotations
        // to keep track of what Intact Object has been created from what GO record.
        Map terms = new HashMap( goTerms.size() );

        count = 0;
        System.out.print( "Creating the terms ... " );
        System.out.flush();

        for ( Iterator iterator = goTerms.iterator(); iterator.hasNext(); ) {
            GoRecord goRecord = (GoRecord) iterator.next();

            try {
                CvObject cv = insertDefinition( goRecord, false );
                terms.put( goRecord, cv );

                if ( ( ++count % 10 ) == 0 ) {
                    System.out.print( count + " " );
                    System.out.flush();
                }

            } catch ( IntactException e ) {
                System.err.println( "Error storing GO record " + ( count - 1 ) );
                System.err.println( e );
            }
        }

        System.out.println( ( ( count % 10 ) != 0 ? count + "" : "" ) );


        // 3. Insert the Annotations now
        count = 0;
        System.out.print( "Adding/Updating terms' annotations ... " );
        System.out.flush();
        Collection messages = null;
        for ( Iterator iterator = goTerms.iterator(); iterator.hasNext(); ) {
            GoRecord goRecord = (GoRecord) iterator.next();

            try {

                CvObject cv = (CvObject) terms.get( goRecord );

                if ( cv != null ) {

                    updateAnnotations( cv, goRecord );

                    if ( ( ++count % 10 ) == 0 ) {
                        System.out.print( count + " " );
                        System.out.flush();
                    }

                } else {
                    if ( messages == null ) {
                        messages = new ArrayList( 2 );
                    }

                    messages.add( goRecord.getGoShortLabel() + "'s annotations could not be updated." );
                }

            } catch ( IntactException e ) {
                System.err.println( "Error storing GO record " + ( count - 1 ) );
                System.err.println( e );
            }
        }
        System.out.println( ( ( count % 10 ) != 0 ? count + "" : "" ) );

        if ( messages != null ) {
            for ( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
                String s = (String) iterator1.next();
                System.err.println( s );
            }
        }
    }


    /**
     * Read a GO DAG file from the given URL, insert or update DAG into aTargetClass.
     *
     * @param sourceFile the input file to read GO Dag info.
     *
     * @throws IOException     for I/O errors
     * @throws IntactException for errors in accesing IntAct database.
     */
    public void insertGoDag( String sourceFile ) throws IOException, IntactException {
        System.out.println( "Reading GO DAG lines: " );
        // initialisation
        BufferedReader in = null;
        try {
            in = new BufferedReader( new FileReader( sourceFile ) );
            new DagNodeUtils( this ).addNodes( in, null, 0 );
        } finally {
            if ( in != null ) {
                in.close();
            }
        }
        System.out.println( "\nGO DAG read." );
    }

    /**
     * Writes a Controlled vocabulary in GO definition format flat file.
     *
     * @param targetFile the name of the file to write to.
     *
     * @throws IntactException for errors in accessing the persisten system.
     * @throws IOException     for I/O errors.
     */
    public void writeGoDefinitions( String targetFile )
            throws IntactException, IOException {
        PrintWriter out = null;

        try {
            out = new PrintWriter( new BufferedWriter( new FileWriter( targetFile ) ) );

            // Get all members of the class
            Collection result = myHelper.search( myTargetClass, "ac", "*" );

            for ( Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                printGoDef( (CvObject) iterator.next(), out );
            }
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    /**
     * Print the GO format DAG to a file.
     *
     * @param targetFile the name of the file to write to.
     *
     * @throws IntactException for errors in accessing the persisten system.
     * @throws IOException     for I/O errors.
     */
    public void writeGoDag( String targetFile ) throws IntactException, IOException {
        // The writer to write the output.
        PrintWriter out = null;

        try {
            out = new PrintWriter( new BufferedWriter( new FileWriter( targetFile ) ) );

            // Get a random members of the class
            Collection result = myHelper.search( myTargetClass, "ac", "*" );

            if ( result.size() > 0 ) {
                Iterator iterator = result.iterator();
                CvDagObject o = (CvDagObject) iterator.next();
                DagGenerator dagGenerator = new DagGenerator( out, myGoIdDatabase );
                dagGenerator.toGoDag( o.getRoot() );
            }
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    // Helper methods (static)

    /**
     * Read a single GO term definition flat file record.
     *
     * @param in
     *
     * @return Hashtable containing the parsing results.
     */
    private static GoRecord readRecord( BufferedReader in ) throws IOException {
        GoRecord goEntry = null;

        // Empty line matching pattern.
        Pattern emptyLineRegex = Pattern.compile( "^\\s*$" );

        // A line to read from the input file.
        String line;

        while ( null != ( line = in.readLine() ) ) {
            // The empty line indicates the end of the record. Return parsed record.
            if ( emptyLineRegex.matcher( line ).matches() ) {
                break;
            }
            // Ignore comment lines.
            if ( line.startsWith( "!" ) ) {
                continue;
            }

            int index = line.indexOf( ':' );
            // Ignore all other lines by doing nothing.
            if ( index == -1 ) {
                continue;
            }

            String tag = line.substring( 0, index );
            String value = line.substring( index + 1 ).trim();

            // The term for new go format contains short label.
            if ( tag.equals( "term" ) ) {
                if ( goEntry == null ) {
                    goEntry = new GoRecord();
                }
                goEntry.setGoTerm( value );
            }

            // Old DAG format.
            else if ( tag.equals( "shortlabel" ) ) {
                if ( goEntry == null ) {
                    goEntry = new GoRecord();
                }
                goEntry.setGoShortLabel( value );
            }
            // Check for goid or id.
            else if ( tag.equals( "id" ) || tag.equals( "goid" ) ) {
                goEntry.setGoId( value );
            } else {
                goEntry.put( tag, value );
            }
        }
        return goEntry;
    }

    // Helper methods

    /**
     * Insert a GO term into IntAct.
     *
     * @param goRec     contains GO data
     * @param deleteold true if to delete previous records.
     *
     * @return the CVObject inserted
     *
     * @throws IntactException for errors in accessing persistent system.
     */
    private CvObject insertDefinition( GoRecord goRec, boolean deleteold ) throws IntactException {
        // Cache the institution.
        Institution inst = myHelper.getInstitution();


        // Update shortLabel. Label has to be unique!
        String goTerm = goRec.getGoTerm();

        // The short label for the current node.
        String label;
        if ( goRec.hasGoShortLabel() ) {
            label = goRec.getGoShortLabel();
        } else {
            // Use the GO term to form a short label.
            label = goTerm;
        }
        // Normalize the short label to IntAct format.
        label = normalizeShortLabel( label );

        // Get or create CvObject
        CvObject current = selectCvObject( goRec.getGoId(), label );

        if ( null == current ) {
            //This would be better done using the (owner, shortLabel) constructor
            //since - at least so far - all CvObject subclasses have the same form
            //of constructor. Thus do it further down after getting a shortLabel...
            current = createCvObject( myTargetClass );
            if ( current == null ) {
                throw new IntactException( "failed to create new CvObject of type " + myTargetClass.getName());
            }
            current.setOwner( inst );
            myHelper.create( current );
        } else {
            if ( deleteold ) {
                // Delete all old data
                myHelper.deleteAllElements( current.getXrefs() );
                current.getXrefs().clear();
                myHelper.deleteAllElements( current.getAnnotations() );
                current.getAnnotations().clear();
                myHelper.deleteAllElements( current.getAliases() );
                current.getAliases().clear();
            }
        }
        if ( shortLabelExist( label, current.getAc() ) ) {
            throw new IntactException( label + " already exists!" );
        }
        current.setShortLabel( label );

        // Update fullName
        current.setFullName( goTerm.substring( 0, Math.min( goTerm.length(), ourMaxNameLen ) ) );

        myHelper.update( current );


        // Update main object
        if ( myHelper.isPersistent( current ) ) {
            myHelper.update( current );
        }
        return current;
    }


    private void updateAnnotations( CvObject current, GoRecord goRec ) throws IntactException {

        // Cache the institution.
        Institution inst = myHelper.getInstitution();

        // Update all comments
        for ( Iterator comments = goRec.getKeys(); comments.hasNext(); ) {
            String topic = (String) comments.next();
            CvTopic cvtopic = (CvTopic) myHelper.getObjectByLabel( CvTopic.class, topic );
            if ( cvtopic == null ) {
                // Topic is not found, continue with the next.
                if ( ! "definition_reference".equals( topic ) ) {
                    System.err.println( "Warning! An annotation could not be added to the term " +
                                        current.getShortLabel() + " as the CvTopic( " + topic + " ) could not be found." );
                }
                continue;
            } else {
                CvTopic definition = (CvTopic) myHelper.getObjectByLabel( CvTopic.class, CvTopic.DEFINITION );
                // There should be only one definition
                if ( cvtopic.equals( definition ) ) {
                    handleDefinition( goRec, current );
                    continue;
                }
            }

            // Loop through each annotation stored under a single topic.
            for ( Iterator texts = goRec.getAnnotationTexts( topic ); texts.hasNext(); ) {
                // Create a proper annotation - needed it for equals method.
                Annotation annotation = new Annotation( inst, cvtopic );
                annotation.setAnnotationText( (String) texts.next() );

                // Avoid duplicate annotations.
                if ( current.getAnnotations().contains( annotation ) ) {
                    continue;
                }
                // Unique annotation, create it on the persistent system.
                current.addAnnotation( annotation );
                myHelper.create( annotation );
            }
        }

        // add xref to goidDatabase if it does not exist yet.

        // TODO check that upfront in GoTools.
        CvDatabase goidDB = null;
        if ( ! myGoIdDatabase.equals( "-" ) ) {
            goidDB = (CvDatabase) myHelper.getObjectByLabel( CvDatabase.class, myGoIdDatabase );

            if ( goidDB == null ) {
                System.err.println( "The requested CvDatabase: " + myGoIdDatabase + " could not be found." );
            }
        }


        if ( goRec.hasGoId() && goidDB != null ) {

            CvXrefQualifier identity = getIdentityQualifier();
            Xref xref = new Xref( inst, goidDB, goRec.getGoId(), null, null, identity );
            if ( ! current.getXrefs().contains( xref ) ) {
                current.addXref( xref );
                myHelper.create( xref );
            }
        }

        // add definition references
        for ( Iterator defs = goRec.getDefinitionReferences(); defs.hasNext(); ) {
            String defRef = (String) defs.next();
            Matcher m = ourPubmedRegex.matcher( defRef );

            if ( m.matches() ) {
                // add Pubmed xref
                CvXrefQualifier goDefRef = getGoDefinitionQualifier();
                CvDatabase pubmedDB = getPubmedDatabase();
                Xref xref = new Xref( inst, pubmedDB, m.group( 1 ), null, null, goDefRef );
                if ( ! current.getXrefs().contains( xref ) ) {
                    current.addXref( xref );
                    myHelper.create( xref );
                }
                continue;
            }

            if ( defRef.startsWith( ourResId ) ) {
                String residStr = defRef.substring( ourResId.length() );
                StringTokenizer stk = new StringTokenizer( residStr, "," );
                while ( stk.hasMoreTokens() ) {
                    String token = stk.nextToken();
                    // add Resid xref
                    CvXrefQualifier goDefRef = getGoDefinitionQualifier();
                    CvDatabase residDB = getResidDatabase();
                    Xref xref = new Xref( inst, residDB, token.trim(), null, null, goDefRef );
                    if ( ! current.getXrefs().contains( xref ) ) {
                        current.addXref( xref );
                        myHelper.create( xref );
                    }
                }
            }
        }
        
        // Update main object
        if ( myHelper.isPersistent( current ) ) {
            myHelper.update( current );
        }
    }


    /**
     * True if given label exists in the persistent system.
     *
     * @param label the label to search
     * @param ac    the AC to check the AC of the retrieved object (if it is same then this method assumes that label is
     *              unique).
     *
     * @return true if <code>label</code> exists in the persistent system. False is returned if a record exists but its
     *         AC is as same as <code>ac</code>.
     */
    private boolean shortLabelExist( String label, String ac ) {
        // get all objects by label. If more than one, need to modify label.
        try {
            AnnotatedObject result = (AnnotatedObject) myHelper.getObjectByLabel( myTargetClass, label );
            if ( result == null ) {
                return false;
            }
            if ( result.getAc().equals( ac ) ) {
                // No object has label, or only the current object.
                // Therefore, if label is assigned to current, it will still be unique.
                return false;
            }
        } catch ( DuplicateLabelException d ) {
        } catch ( IntactException e ) {
        }
        // There is another record exists with the same short label.
        return true;
    }

    /**
     * Used to run a no-arg constructor as we don't know what type of CvObject we have. This is OK since we are only in
     * this application when loading a DB and if the user does not have access rights to it then the load will fail
     * anyway.
     *
     * @param clazz The class to create an instance of
     *
     * @return CvObject A concrete subclass of CvObject, or null if the creation failed.
     */
    private static CvObject createCvObject( Class clazz ) {

        // TODO could be written giving as params institution + shortlabel
        Constructor[] constructors = clazz.getDeclaredConstructors();
        Constructor noArgs = null;
        for ( int i = 0; i < constructors.length; i++ ) {
            if ( constructors[ i ].getParameterTypes().length == 0 ) {
                //got the no-arg one - done
                noArgs = constructors[ i ];
                break;
            }
        }
        if ( ( noArgs != null ) & CvObject.class.isAssignableFrom( clazz ) ) {
            noArgs.setAccessible( true );
            try {
                return (CvObject) noArgs.newInstance( null );
            } catch ( InstantiationException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Prints given CvObject as a GO flatfile formatted string.
     *
     * @param cvobj the CVObject to print.
     * @param out   the write to write to
     */
    private void printGoDef( CvObject cvobj, PrintWriter out )
            throws IntactException {
        // Write GO term
        out.print( "term: " );

        // The new format combines short label and full name under term
        if ( cvobj.getShortLabel().equals( cvobj.getFullName() ) ) {
            out.println( cvobj.getShortLabel() );
        } else {
            out.println( cvobj.getShortLabel() + ": " + cvobj.getFullName() );
        }

        // write goid
        String goid = getGoid( cvobj, myGoIdDatabase );
        if ( goid != null ) {
            out.print( "id: " );
            out.println( goid );
        }

        // Write all comments in GO format
        Collection annotation = cvobj.getAnnotations();
        for ( Iterator iterator = annotation.iterator(); iterator.hasNext(); ) {
            Annotation a = (Annotation) iterator.next();
            String topic = a.getCvTopic().getShortLabel();
            // do not print internal remark.
            if ( false == CvTopic.INTERNAL_REMARK.equals( topic ) &&
                 false == CvTopic.UNIPROT_DR_EXPORT.equals( topic ) &&
                 false == CvTopic.HIDDEN.equals( topic )   ) {    
                out.print( topic + ": " );
                out.println( a.getAnnotationText() );
            }
        }

        // Print pubmed db info.
        String pubmedLine = getPubmedString( cvobj );
        if ( pubmedLine != null ) {
            out.print( pubmedLine );
        }

        // Print resid db info.
        String residLine = getResIdString( cvobj );
        if ( residLine != null ) {
            out.println( residLine );
        }

        // Need to print a dummy definition reference if none found.
        if ( ( pubmedLine == null ) && ( residLine == null ) ) {
            out.println( "definition_reference: PMID:INTACT" );
        }

        // Blank line to separate an entry.
        out.println();
    }

    private String getPubmedString( CvObject cvobj ) throws IntactException {
        // The line separator.
        String nl = System.getProperty( "line.separator" );

        // Construct the result.
        StringBuffer sb = new StringBuffer();

        CvDatabase pubmedDB = (CvDatabase) myHelper.getObjectByLabel( CvDatabase.class, ourPubMedDB );
        Collection xref = cvobj.getXrefs();
        for ( Iterator iterator = xref.iterator(); iterator.hasNext(); ) {
            Xref x = (Xref) iterator.next();
            if ( !x.getCvDatabase().equals( pubmedDB ) ) {
                continue;
            }
            if ( x.getCvDatabase().equals( pubmedDB ) ) {
                sb.append( "definition_reference: PMID:" );
                sb.append( x.getPrimaryId() );
                sb.append( nl );
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String getResIdString( CvObject cvobj ) throws IntactException {
        // Construct the result.
        StringBuffer sb = new StringBuffer();

        CvDatabase residDB = (CvDatabase) myHelper.getObjectByLabel( CvDatabase.class, ourResIdDB );

        // A flag to print to print first resid entry.
        boolean first = true;

        Collection xref = cvobj.getXrefs();
        for ( Iterator iterator = xref.iterator(); iterator.hasNext(); ) {
            Xref x = (Xref) iterator.next();
            if ( !x.getCvDatabase().equals( residDB ) ) {
                continue;
            }
            if ( first ) {
                sb.append( "definition_reference: " );
                sb.append( ourResId );
                sb.append( x.getPrimaryId() );
                first = false;
            } else {
                sb.append( " , " );
                sb.append( x.getPrimaryId() );
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private void handleDefinition( GoRecord goRec, CvObject current ) throws IntactException {
        // Cache the institution and definition topic
        Institution inst = myHelper.getInstitution();
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel( CvTopic.class, "definition" );

        // Collect any existing definition definition annotations (can be
        // more than one if we are updating an existing CVs
        List exdefs = new ArrayList();
        for ( Iterator annots = current.getAnnotations().iterator(); annots.hasNext(); ) {
            Annotation annotation = (Annotation) annots.next();
            if ( annotation.getCvTopic().equals( definition ) ) {
                exdefs.add( annotation );
            }
        }
        // Create an annotation to compare.

        // There should be only one definition stored under the topic 'definition'
        String text = (String) goRec.getAnnotationTexts( "definition" ).next();
        Annotation newdef = new Annotation( inst, definition, text );

        // If we don't have any existing defs, then create a new def and return.
        if ( exdefs.isEmpty() ) {
            current.addAnnotation( newdef );
            myHelper.create( newdef );
            return;
        }

        // Compare this new annotation with the existing annotations
        if ( exdefs.contains( newdef ) ) {
            // This new definition exists, we need to delete all others apart for
            // the existing one.
            for ( Iterator iter = exdefs.iterator(); iter.hasNext(); ) {
                Annotation annotation = (Annotation) iter.next();
                if ( !newdef.equals( annotation ) ) {
                    current.removeAnnotation( annotation );
                    myHelper.delete( annotation );
                }
            }
        } else {
            // Boolean to indicate whether we have updated the existing annotation or not.
            // Reuse an existing annotation instead of creating a new one.
            boolean updatedExisting = false;

            // A new definition. Delete all the existing ones apart for one.
            for ( Iterator iter = exdefs.iterator(); iter.hasNext(); ) {
                Annotation annotation = (Annotation) iter.next();
                if ( updatedExisting ) {
                    current.removeAnnotation( annotation );
                    myHelper.delete( annotation );
                } else {
                    annotation.setAnnotationText( text );
                    updatedExisting = true;
                }
            }
        }
    }
}
