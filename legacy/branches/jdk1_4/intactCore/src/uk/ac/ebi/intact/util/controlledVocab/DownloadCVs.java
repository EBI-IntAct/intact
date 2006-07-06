/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.controlledVocab.model.IntactOntology;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Oct-2005</pre>
 */
public class DownloadCVs {

    public static final String VERSION = "0.1";

    public static final String ONTOLOGY_NAME = "intact";

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    private static final String TIME;

    public CvDatabase psi = null;

    private IntactHelper helper;

    public DownloadCVs( IntactHelper helper ) {
        psi = (CvDatabase) helper.getObjectByPrimaryId( CvDatabase.class, CvDatabase.PSI_MI_MI_REF );
        if ( psi == null ) {
            throw new IllegalArgumentException( "Could not find PSI via MI reference " + CvDatabase.PSI_MI_MI_REF );
        }

        this.helper = helper;
    }

    static {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd@HH_mm" );
        TIME = formatter.format( new Date() );
        formatter = null;
    }

    /**
     * Implementation of a basic search replace.
     *
     * @param buffer the buffer containing the text on which we will apply the search-replace.
     * @param from   text to replace.
     * @param to     replacement text.
     *
     * @return the updated StringBuffer.
     */
    private static StringBuffer replace( StringBuffer buffer, String from, String to ) {

        int index = buffer.indexOf( from );

        while ( index != -1 ) {
            int start = index;
            int stop = start + from.length();
            buffer.replace( start, stop, to );

            // we don't search what before where we just inserted. That also prevent infinite loops
            int fromIndex = start + to.length();
            index = buffer.indexOf( from, fromIndex );
        }

        return buffer;
    }

    /**
     * Escape special characters according to: http://www.geneontology.org/GO.format.shtml
     *
     * @param text the text to escape
     *
     * @return the excaped text
     */
    public static String escapeCharacter( String text ) {
        /*
        \n  	 newline      -
        \W 	whitespace
        \t 	tab               -
        \: 	colon             -
        \, 	comma             -
        \" 	double quote      -
        \\ 	backslash         -
        \(  \) 	parentheses    -
        \[  \] 	brackets       -
        \{  \} 	curly brackets -
        \<newline> 	<no value> -
        */

        // we would insert 1 new character per replacement, 15 if not likely to be ever reached.
        StringBuffer sb = new StringBuffer( text.length() + 15 );
        sb.append( text );

        replace( sb, "\\", "\\\\" );

        // http://en.wikipedia.org/wiki/Newline
        replace( sb, "\r\n", "\\n" ); // Windows  style ... has to be done before Unix Style
        replace( sb, "\n", "\\n" );   // Unix/Mac style

        replace( sb, "\t", "\\t" );
        replace( sb, ":", "\\:" );
        replace( sb, ",", "\\," );
        replace( sb, "\"", "\\\"" );

        // This is adviced on the OBO web site but not respected by oboedit.
//        replace( sb, "(", "\\(" );
//        replace( sb, ")", "\\)" );

        replace( sb, "[", "\\[" );
        replace( sb, "]", "\\]" );
        replace( sb, "{", "\\{" );
        replace( sb, "}", "\\}" );

        return sb.toString();
    }

    /**
     * Format a CvObject into an OBO record.
     *
     * @param cvObject
     *
     * @return
     */
    private String formatToObo( CvObject cvObject, CvObject root ) throws IntactException {
        StringBuffer sb = new StringBuffer( 1024 );

        //[Term]
        //id: MI:0001
        //name: interaction detectio: interaction detection method
        //def: "Method to determine the interaction." [PMID:14755292]
        //is_a: MI:0000 ! ppi: protein-protein interactions

        //[Term]
        //id: MI:0097
        //name: reverse rrs: reverse ras recruitment system
        //def: "In this complementation approach the bait can be any membrane protein (for example a receptor or a channel protein)\, the prey is cloned as a fusion protein of any cDNA from a library and the coding sequence of cytoplasmic RAS (cdc25 in yeast). If the bait and the prey interact\, RAS is recruited close to the membrane and can activate cell growth. This procedure must take place in cells having a mutated RAS (Cdc25-2 yeast strain having a temperature sensitive mutation of RAS) to avoid constitutive growth activation." [PMID:11160938]
        //synonym: "reverse RRS" []
        //is_a: MI:0228 ! cytoplasmic compl: cytoplasmic complementation assay

        //[Term]
        //id: MI:0557
        //name: adp ribosylation: adp ribosylation reaction
        //def: "Reaction that can affect Arg\, Cys\, Glu\, Arg and Asn residues." [RESID:AA0168, RESID:AA0169, RESID:AA0295, RESID:AA0237, RESID:AA0231]
        //xref_analog: GO:0006471 "Gene Ontology"
        //is_a: MI:0414 ! enzymatic reaction
        //xref_unknown: GO:0006471 "Gene Ontology"

        // TODO how to export a CvObject that doesn't have an MI ref ? well don't !!

        String id = getMiReference( cvObject );
        System.out.println( "Processing " + cvObject.getShortLabel() + " (" + id + ")" );
        if( id == null ) {
            System.out.println( "\tWARNING: That term doesn't have an MI reference, skip it." );
            return null;
        }

        sb.append( "[Term]" ).append( NEW_LINE );

        // 1. ID handling
        // TODO is no mi ref, do we put something else ? AC ? IA:$counter ? then we have to flag different ontologies!
        sb.append( "id: " ).append( id ).append( NEW_LINE );

        sb.append( "name: " ).append( escapeCharacter( cvObject.getShortLabel() ) );
        if ( cvObject.getFullName() != null
             &&
             ! cvObject.getShortLabel().equals( cvObject.getFullName() ) ) {
            sb.append( ' ' ).append( ':' ).append( ' ' ).append( escapeCharacter( cvObject.getFullName() ) );
        }
        sb.append( NEW_LINE );

        // 2. Definition handling
        // TODO how do we do if a CV has no pubmed ? how do we import it in IntAct ?
        StringBuffer xrefs = new StringBuffer( 64 );
        List pubmeds = new ArrayList( getXrefIds( cvObject, CvDatabase.PUBMED_MI_REF ) );
        Collections.sort( pubmeds );
        if ( !pubmeds.isEmpty() ) {
            for ( Iterator iterator = pubmeds.iterator(); iterator.hasNext(); ) {
                String pmid = (String) iterator.next();
                xrefs.append( "PMID:" ).append( pmid );
                if ( iterator.hasNext() ) {
                    xrefs.append( ", " );
                }
            }
        }

        // TODO check for RESID ?
        // TODO MI:0122, MI:0126, MI:0127 have both RESID and PUBMED
        List resids = new ArrayList( getXrefIds( cvObject, CvDatabase.RESID_MI_REF ) );
        Collections.sort( pubmeds );
        if ( !resids.isEmpty() ) {
            if( xrefs.length() > 0 ) {
                xrefs.append( ", " );
            }
            for ( Iterator iterator = resids.iterator(); iterator.hasNext(); ) {
                String resid = (String) iterator.next();
                xrefs.append( "RESID:" ).append( resid );
                if ( iterator.hasNext() ) {
                    xrefs.append( ", " );
                }
            }
        }

        if ( xrefs.length() == 0 ) {
            // TODO what do we do here ?!?!
            System.err.println( "No PUBMED or RESID Xref." );
        }

        // 3. Definition
        String definition = getDefinition( cvObject );
        Annotation obsolete = getObsolete( cvObject );
        if ( obsolete != null && obsolete.getAnnotationText() != null ) {
            // append OBSOLETE reason
            definition = definition + "\nOBSOLETE " + obsolete.getAnnotationText();
        }
        if ( definition != null ) {
            sb.append( "def: " ).append( '"' ).append( escapeCharacter( definition ) ).append( '"' );
            if ( xrefs != null ) {
                sb.append( ' ' ).append( '[' ).append( xrefs.toString() ).append( ']' );
            }
            sb.append( NEW_LINE );
        } else {
            System.err.println( "'" + cvObject.getShortLabel() + "' doesn't have a definition !!" );
        }

        // 4. Aliases handling
        List synonyms = new ArrayList( getSynonyms( cvObject ) );
        Collections.sort( synonyms );
        for ( Iterator iterator = synonyms.iterator(); iterator.hasNext(); ) {
            String syn = (String) iterator.next();
            sb.append( "synonym: \"" ).append( escapeCharacter( syn ) ).append( "\" []" );
            sb.append( NEW_LINE );
        }

        // 5. xref_analog handling
        Map db2name = new HashMap();
        db2name.put( CvDatabase.GO, "Gene Ontology" );
        db2name.put( CvDatabase.SO, "Sequence Ontology" );
        // export any Xref that is not pubmed or resid
        for ( Iterator iterator = cvObject.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();
            String midb = getMiReference( xref.getCvDatabase() );
            if( CvDatabase.PUBMED_MI_REF.equals( midb )
                ||
                CvDatabase.PSI_MI_MI_REF.equals( midb )
                ||
                CvDatabase.RESID_MI_REF.equals( midb ) ) {
                // we do nothing
            } else {
                // write a xref_analog
                String name = (String) db2name.get( xref.getCvDatabase().getShortLabel() );
                if( name == null) {
                    name = xref.getCvDatabase().getShortLabel();
                }
                sb.append( "xref_analog: " ).append( xref.getPrimaryId() );
                sb.append( " \"" ).append( name ).append( "\"" );
                sb.append( NEW_LINE );
            }
        }

        // 6. DAG handling
        // Note: one OBO term can be actually mapped to multiple IntAct CV, in which case these terms would have the
        //       same MI ref but different concrete classes.
        if ( CvDagObject.class.isAssignableFrom( cvObject.getClass() ) ) {
            // this is a DAG object, may have parents to mention here
            Collection otherTypes = helper.getObjectsByXref( CvDagObject.class, id );
            Set alreadyExported = new HashSet( 4 );

            // keeps a mapping MI -> CvObject to allow later ordering my MI reference.
            Map mi2parents = new HashMap();

            // add the remaining parents to the current definition
            for ( Iterator iterator = otherTypes.iterator(); iterator.hasNext(); ) {
                CvDagObject dag = (CvDagObject) iterator.next();

                if ( ! dag.getParents().isEmpty() ) {

                    for ( Iterator iterator2 = dag.getParents().iterator(); iterator2.hasNext(); ) {
                        CvDagObject parent = (CvDagObject) iterator2.next();
                        String mi = getMiReference( parent );

                        if ( alreadyExported.contains( mi ) ) {
                            // avoid the same term from appearing multiple times
                            continue;
                        }

                        alreadyExported.add( mi );

                        mi2parents.put( mi, parent );
                    }
                }

            } // loop otherType

            List parents = new ArrayList( mi2parents.keySet() );
            Collections.sort( parents ); // sort MI references by alphabetical order

            // print out all sorted parents
            for ( Iterator iterator = parents.iterator(); iterator.hasNext(); ) {
                String mi = (String) iterator.next();
                CvDagObject parent = (CvDagObject) mi2parents.get( mi );

                sb.append( "is_a: " ).append( mi ).append( ' ' ).append( '!' ).append( ' ' );
                sb.append( escapeCharacter( parent.getShortLabel() ) );
                if ( parent.getFullName() != null
                     &&
                     ! parent.getShortLabel().equals( parent.getFullName() ) ) {
                    sb.append( ':' ).append( ' ' ).append( escapeCharacter( parent.getFullName() ) );
                }

                sb.append( NEW_LINE );
            }

        } else {
            // this is not a DAG, generate a root if there is one given as param
            if ( root != null ) {
                String mi = getMiReference( root );
                String childMi = getMiReference( cvObject );

                if( !mi.equals( childMi ) ) {
                    sb.append( "is_a: " ).append( mi ).append( ' ' ).append( '!' ).append( ' ' );
                    sb.append( escapeCharacter( root.getShortLabel() ) );
                    sb.append( NEW_LINE );
                }
            }
        }

        // 7. Obsolete terms
        if ( obsolete != null ) {
            sb.append( "is_obsolete: true" ).append( NEW_LINE );
        }

        return sb.toString();
    }

    /**
     * Collect all GO Synonyms.
     *
     * @param cvObject the object form which we get the synonyms.
     *
     * @return a COllection of String. Not null.
     */
    private Collection getSynonyms( CvObject cvObject ) {
        Collection synonyms = new ArrayList( cvObject.getAliases().size() );
        for ( Iterator iterator = cvObject.getAliases().iterator(); iterator.hasNext(); ) {
            Alias alias = (Alias) iterator.next();
            if ( CvAliasType.GO_SYNONYM.equals( alias.getCvAliasType().getShortLabel() ) ) {
                synonyms.add( alias.getName() );
            }
        }
        return synonyms;
    }

    /**
     * Return a definition for the given CvObject.
     *
     * @param cvObject the CvObject that we will introspect.
     *
     * @return a String that can be null.
     */
    private String getDefinition( CvObject cvObject ) {
        for ( Iterator iterator = cvObject.getAnnotations().iterator(); iterator.hasNext(); ) {
            Annotation annotation = (Annotation) iterator.next();
            if ( CvTopic.DEFINITION.equals( annotation.getCvTopic().getShortLabel() ) ) {
                // TODO enhance by retreiving the CvTopic
                return annotation.getAnnotationText();
            }
        }
        return null;
    }

    /**
     * If any, get the unique Obsolete Annotation of the given object.
     *
     * @param cvObject the CvObject that we will introspect.
     *
     * @return an Annotation object if it can be found, otherwise null.
     */
    private Annotation getObsolete( CvObject cvObject ) {
        for ( Iterator iterator = cvObject.getAnnotations().iterator(); iterator.hasNext(); ) {
            Annotation annotation = (Annotation) iterator.next();
            if ( CvTopic.OBSOLETE.equals( annotation.getCvTopic().getShortLabel() ) ) {
                // TODO enhance by retreiving the CvTopic
                return annotation;
            }
        }
        return null;
    }

    private boolean isObsolete( CvObject cvObject ) {
        Annotation obsolete = getObsolete( cvObject );
        if ( obsolete != null ) {
            return true;
        }
        return false;
    }

    private Collection getXrefIds( CvObject cvObject, String databaseMI ) {

        if ( databaseMI == null ) {
            throw new IllegalArgumentException( "Database MI cannot be null" );
        }

        Collection xrefs = new ArrayList( cvObject.getXrefs().size() );

        for ( Iterator iterator = cvObject.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();
            if ( hasMiReference( xref.getCvDatabase(), databaseMI ) ) {
                xrefs.add( xref.getPrimaryId() );
            }
        }

        return xrefs;
    }

    private String getMiReference( CvObject cvObject ) {
        String mi = null;
        for ( Iterator iterator = cvObject.getXrefs().iterator(); iterator.hasNext() && mi == null; ) {
            Xref xref = (Xref) iterator.next();
            if ( psi.equals( xref.getCvDatabase() ) ) {
                mi = xref.getPrimaryId();
            }
        }
        return mi;
    }

    private boolean hasMiReference( CvObject cvObject, String mi ) {

        if ( mi == null ) {
            throw new IllegalArgumentException( "The MI given must be not null." );
        }

        String _mi = getMiReference( cvObject );
        if ( mi.equals( _mi ) ) {
            return true;
        }
        return false;
    }

    /**
     * Generate the Header of the OBO file.
     *
     * @return the content of the header.
     */
    public String generateHeader() {
        //format-version: 1.0
        //date: 25:10:2005 15:55
        //saved-by: luisa
        //auto-generated-by: DAG-Edit 1.419 rev 3
        //default-namespace: psi-mi25.dag

        StringBuffer sb = new StringBuffer( 128 );

        sb.append( "format-version: 1.0" ).append( NEW_LINE );

        SimpleDateFormat formatter = new SimpleDateFormat( "dd:MM:yyyy HH:mm" );
        String date = formatter.format( new Date() );
        sb.append( "date: " ).append( date ).append( NEW_LINE );

        sb.append( "saved-by: samuel" ).append( NEW_LINE );

        sb.append( "auto-generated-by: IntAct - " ).append( getClass().getName() );
        sb.append( " - v" ).append( VERSION ).append( NEW_LINE );

//        out.write( "default-namespace: psi-mi25.dag" );

        return sb.toString();
    }

    private class VirtualCvRoot extends CvObject {
        public VirtualCvRoot( Institution owner, String shortLabel ) {
            super( owner, shortLabel );
        }

        public VirtualCvRoot( Institution owner, String shortLabel, String mi ) {
            super( owner, shortLabel );

            if ( mi == null ) {
                throw new IllegalArgumentException( "mi must not be null" );
            }

            // Create Psi CvDatabase / identity
            CvDatabase psi = new CvDatabase( owner, CvDatabase.PSI_MI );
            CvXrefQualifier identity = new CvXrefQualifier( owner, CvXrefQualifier.IDENTITY );
            identity.addXref( new Xref( owner, psi, CvXrefQualifier.IDENTITY_MI_REF, null, null, identity ) );
            psi.addXref( new Xref( owner, psi, CvDatabase.PSI_MI_MI_REF, null, null, identity ) );

            // add PSI Xref
            addXref( new Xref( owner, psi, mi, null, null, identity ) );
        }
    }

    private void download( BufferedWriter out ) throws IOException, IntactException {

        // Get all CvObject
        Collection cvObjects = helper.search( CvObject.class, "", null );
        System.out.println( cvObjects.size() + " CvObject(s) found." );

        // add potentially missing non Dag root
        Map typeMapping = IntactOntology.getTypeMapping( false );
        Map mi2name = IntactOntology.getNameMapping();
        Map cv2root = new HashMap();
        for ( Iterator iterator = typeMapping.keySet().iterator(); iterator.hasNext(); ) {
            Class classKey = (Class) iterator.next();
            String[] mis = (String[]) typeMapping.get( classKey );

            String mi = null;
            // now try to get that Cv from IntAct via its MI ref, if we can't find it, then create a virtual Cv
            for ( int i = 0; i < mis.length; i++ ) {
                mi = mis[ i ];

                CvObject myRoot = (CvObject) helper.getObjectByPrimaryId( classKey, mi );
                if ( myRoot != null ) {
                    // found it

                } else {
                    // if there is no more mi to try, then create a virtual CV
                    if ( i < mis.length ) {
                        System.out.println( "Adding virtual root: " + mi );
                        String shortlabel = (String) mi2name.get( mi );
                        VirtualCvRoot root = new VirtualCvRoot( new Institution( "not defined" ), mi, shortlabel );
                        cvObjects.add( root );
                    }
                }

                if ( myRoot != null ) {
                    // keep the root to use here
                    cv2root.put( classKey, myRoot );
                }
            }

            if ( mis.length > 0 ) {

            }
        }

        // sort terms by MI reference
        Map mi2cvObject = new HashMap( cvObjects.size() );
        Collection noMiTerms = new ArrayList();
        for ( Iterator iterator = cvObjects.iterator(); iterator.hasNext(); ) {
            CvObject cvObject = (CvObject) iterator.next();

            String mi = getMiReference( cvObject );
            if ( mi != null ) {
                mi2cvObject.put( mi, cvObject );
            } else {
                // store it under a special flag
                noMiTerms.add( cvObject );
            }
        }

        List keys = new ArrayList( mi2cvObject.keySet() );
        Collections.sort( keys );

        // include header
        out.write( generateHeader() );
        out.write( NEW_LINE );

        // process term that have an MI reference in order
        for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            String ref = (String) iterator.next();
            CvObject cvObject = (CvObject) mi2cvObject.get( ref );

            // if we are handling here CvObject that is not a DAG, then give it a root
            CvObject root = null;
            if ( ! CvDagObject.class.isAssignableFrom( cvObject.getClass() ) ) {
                root = (CvObject) cv2root.get( cvObject.getClass() );
            }

            String term = formatToObo( cvObject, root );
            if( term != null ) {
                out.write( term );
                out.write( NEW_LINE );
                out.flush();
            }
        }

        // process terms that don't have a MI reference
        for ( Iterator iterator = noMiTerms.iterator(); iterator.hasNext(); ) {
            CvObject cvObject = (CvObject) iterator.next();

            CvObject root = null;
            if ( ! CvDagObject.class.isAssignableFrom( cvObject.getClass() ) ) {
                root = (CvObject) cv2root.get( cvObject.getClass() );
            }

            String term = formatToObo( cvObject, root );
            if( term != null ) {
                out.write( term );
                out.write( NEW_LINE );
                out.flush();
            }
        }
    }

    // TODO term MI:0000 is missing - do we insert it into IntAct ? (yes, but hidden) --> Cannot do that, what type ??

    /**
     * @param args
     *
     * @throws IntactException
     */
    public static void main( String[] args ) throws IntactException {

        String outputFilename = null;
        if ( args.length > 0 ) {
            outputFilename = args[ 0 ];
        } else {
            // assign default filename
            outputFilename = "CvDownload-" + TIME + ".obo";
        }

        try {
            BufferedWriter out = new BufferedWriter( new FileWriter( outputFilename ) );

            IntactHelper helper = null;
            try {
                helper = new IntactHelper();

                DownloadCVs downloadCVs = new DownloadCVs( helper );
                downloadCVs.download( out );

            } finally {
                if ( helper == null ) {
                    helper.closeStore();
                }
            }

            out.flush();
            out.close();
            System.out.println( "Closing " + outputFilename );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}