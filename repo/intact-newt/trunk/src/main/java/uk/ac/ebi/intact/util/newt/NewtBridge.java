/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.newt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Access to the Newt data (that is, the NCBI taxonomy).
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jan-2007</pre>
 */
public class NewtBridge {

    /**
     * Parameter that will be replaced by a taxid in a generic URL.
     */
    public static final String TAXID_FLAG = "${taxid}";

    /**
     * Newt base URL
     */
    public static final String NEWT_URL = "http://www.ebi.ac.uk/newt/display";

    /**
     * URL allowing to retreive a single term.
     */
    public static final String NEWT_URL_SPECIFIC_TERM = NEWT_URL + "?mode=IntAct&search=" + TAXID_FLAG + "&scope=term";

    /**
     * URL allowing to retreive a term and its children.
     */
    public static final String NEWT_URL_TERMS_CHILDREN = NEWT_URL + "?mode=IntAct&search=" + TAXID_FLAG + "&scope=children";

    /**
     * URL allowing to retreive a term and its parents.
     */
    public static final String NEWT_URL_TERMS_PARENT = NEWT_URL + "?mode=IntAct&search=" + TAXID_FLAG + "&scope=parent";


    public NewtBridge() {
    }

    /**
     * Read a URL content and store each line in a List.
     *
     * @param url the URL to read.
     *
     * @return a non null Collection of String.
     *
     * @throws IOException
     */
    private List<String> readUrl( URL url ) throws IOException {

        List<String> lines = new ArrayList<String>();

        // Read all the text returned by the server
        BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
        String str;
        while ( ( str = in.readLine() ) != null ) {
            // str is one line of text; readLine() strips the newline character(s)
            lines.add( str );
        }
        in.close();

        return lines;
    }

    /**
     * Check the validity of a given taxid.
     *
     * @param taxid
     */
    private void checkTaxidValidity( int taxid ) {
        if ( taxid != -1 && taxid < 1 ) {
            throw new IllegalArgumentException( taxid + ": a taxid must be > 0 or be -1 (in vitro)." );
        }
    }

    /**
     * Retreives a term by taxid.
     *
     * @param taxid the taxid of the wanted term
     *
     * @return the term or null if not found.
     *
     * @throws IOException
     */
    public NewtTerm getNewtTerm( int taxid ) throws IOException {

        checkTaxidValidity( taxid );

        NewtTerm term = null;

        List<String> terms = readUrl( new URL( NEWT_URL_SPECIFIC_TERM.replace( TAXID_FLAG, String.valueOf( taxid ) ) ) );
        switch ( terms.size() ) {
            case 0:
                // null is returned.
                break;
            case 1:
                term = new NewtTerm( terms.iterator().next() );
                break;
            default:
                throw new IllegalArgumentException( "More than one line was returned by Newt" );
        }

        return term;
    }

    /**
     * Update a NewtTerm by adding its children. the process can be done recursively if the given flag it true.
     *
     * @param term        the term to update.
     * @param recursively if true, update recursively.
     *
     * @throws IOException
     */
    public void retreiveChildren( NewtTerm term, boolean recursively ) throws IOException {

        List<NewtTerm> children = getNewtTermChildren( term.getTaxid() );
        for ( NewtTerm child : children ) {
            term.addChild( child );
        }

        if ( recursively ) {
            for ( NewtTerm child : term.getChildren() ) {
                retreiveChildren( child, recursively );
            }
        }
    }

    /**
     * Update a NewtTerm by adding its parents. the process can be done recursively if the given flag it true.
     *
     * @param term        the term to update.
     * @param recursively if true, update recursively.
     *
     * @throws IOException
     */
    public void retreiveParents( NewtTerm term, boolean recursively ) throws IOException {

        // this is the root of the Newt taxonomy. stop here.
//            return;
//        }

        List<NewtTerm> parents = getNewtTermParent( term.getTaxid() );
        for ( NewtTerm parent : parents ) {
            if ( parent.getTaxid() != 1 ) {
                // taxid 1 is the root of the Newt taxonomy. skip this.
                term.addParent( parent );
            }
        }

        if ( recursively ) {
            for ( NewtTerm parent : term.getParents() ) {
                retreiveParents( parent, recursively );
            }
        }
    }

    /**
     * Get the list of children of a given term given its taxid.
     *
     * @param taxid the term's taxid.
     *
     * @return a non null list of NewtTerm.
     *
     * @throws IOException
     */
    public List<NewtTerm> getNewtTermChildren( int taxid ) throws IOException {

        checkTaxidValidity( taxid );

        List<NewtTerm> terms = new ArrayList<NewtTerm>();

        List<String> lines = readUrl( new URL( NEWT_URL_TERMS_CHILDREN.replace( TAXID_FLAG, String.valueOf( taxid ) ) ) );
        switch ( lines.size() ) {
            case 0:
                // null is returned.
                break;
            default:
                for ( String line : lines ) {
                    terms.add( new NewtTerm( line ) );
                }
        }

        // remove the first one
        terms.remove( terms.get( 0 ) );

        return terms;
    }

    /**
     * Get the list of parents of a given term given its taxid.
     *
     * @param taxid the term's taxid.
     *
     * @return a non null list of NewtTerm.
     *
     * @throws IOException
     */
    public List<NewtTerm> getNewtTermParent( int taxid ) throws IOException {

        checkTaxidValidity( taxid );

        List<NewtTerm> terms = new ArrayList<NewtTerm>();

        List<String> lines = readUrl( new URL( NEWT_URL_TERMS_PARENT.replace( TAXID_FLAG, String.valueOf( taxid ) ) ) );
        switch ( lines.size() ) {
            case 0:
                // null is returned.
                break;
            default:
                for ( String line : lines ) {
                    terms.add( new NewtTerm( line ) );
                }
        }

        // remove the first one
        terms.remove( terms.get( 0 ) );

        return terms;
    }
}