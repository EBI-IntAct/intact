/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uniprot Taxonomy access via their RDF download.
 * <p>Example: http://www.uniprot.org/taxonomy/9615.rdf</p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public class UniprotTaxonomyService implements TaxonomyService {

    private static final Log log = LogFactory.getLog( UniprotTaxonomyService.class );

    private static final String TAXID_PLACEHOLDER = "_TAXID_";
    private static final String UNIPROT_TAXONOMY_URL = "http://www.uniprot.org/taxonomy/" + TAXID_PLACEHOLDER + ".rdf";

    public static final Pattern MNENOMIC_PATTERN        = Pattern.compile( "^<mnemonic>(.*)</mnemonic>$" );
    public static final Pattern SCIENTIFIC_NAME_PATTERN = Pattern.compile( "^<scientificName>(.*)</scientificName>$" );
    public static final Pattern COMMON_NAME_PATTERN     = Pattern.compile( "^<commonName>(.*)</commonName>$" );
    public static final Pattern SYNONYM_PATTERN         = Pattern.compile( "^<synonym>(.*)</synonym>$" );

    public static final String OBSOLETE_FLAG = "<obsolete rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</obsolete>";
    public static final Pattern REPLACED_BY_PATTERN = Pattern.compile( "^<replacedBy rdf:resource=\"http:\\/\\/purl.uniprot.org\\/taxonomy\\/(\\d+)\" \\/>$" );

    // public static final Pattern OTHER_NAME_PATTERN      = Pattern.compile( "^<otherName>(.*)</otherName>$" );

    public UniprotTaxonomyService() {
    }

    private InputStream getInputStream( int taxid ) throws IOException {
        String urlStr = UNIPROT_TAXONOMY_URL.replaceAll( TAXID_PLACEHOLDER, String.valueOf( taxid ) );
        URL url = new URL( urlStr );
        return url.openStream();
    }

    private TaxonomyTerm buildTerm( final int taxid ) throws IOException {

        final InputStream is = getInputStream( taxid );

        TaxonomyTerm term = new TaxonomyTerm( taxid );

        String replacedByTaxid = null;

        try {
            BufferedReader in = new BufferedReader( new InputStreamReader( is ) );
            String line;
            boolean stop = false;

            while ( ( line = in.readLine() ) != null && !stop ) {

                log.trace(line);

                String value = null;
                boolean otherLine = false;

                if ( !term.hasMnemonic() && ( value = getValue( MNENOMIC_PATTERN, line ) ) != null ) {
                    term.setMnemonic( value );
                } else if ( !term.hasCommonName() && ( value = getValue( COMMON_NAME_PATTERN, line ) ) != null ) {
                    term.setCommonName( value );
                } else if ( !term.hasScientificName() && ( value = getValue( SCIENTIFIC_NAME_PATTERN, line ) ) != null ) {
                    term.setScientificName( value );
                } else if ( ( value = getValue( SYNONYM_PATTERN, line ) ) != null ) {
                    term.getSynonyms().add( value );
//                } else if( line.equals( OBSOLETE_FLAG ) ) {
//                    System.err.println( "WARNING - taxid " + taxid + " was made obsolete" );
                } else if ( ( value = getValue( REPLACED_BY_PATTERN, line ) ) != null ) {
                    replacedByTaxid = value;
                    if( log.isInfoEnabled() )
                        log.info( "WARNING - the taxid replacement for " + taxid + " is " + replacedByTaxid );
                } else {
                    // ignore these lines
                    otherLine = true;
                }

                stop = !term.getSynonyms().isEmpty() && otherLine || replacedByTaxid != null;
            }
        } finally {
            is.close();
        }

        if( replacedByTaxid != null ) {
            // abandon whatever has been built and fetch the new taxid instead.
            if( log.isInfoEnabled() )
                log.info("Attempting to fetch taxon "+ replacedByTaxid +" as "+ taxid +" is obsolete...");
            term = buildTerm( Integer.parseInt( replacedByTaxid ) );
            term.setObsoleteTaxid( taxid );
        }

        return term;
    }

    private String getValue( final Pattern pattern, final String line ) {
        Matcher matcher = pattern.matcher( line );
        String value = null;
        if ( matcher.matches() ) {
            value = matcher.group( 1 );
        }
        return value;
    }

    public TaxonomyTerm getTaxonomyTerm( int taxid ) throws TaxonomyServiceException {

        if ( !TaxonomyUtils.isSupportedTaxid( taxid ) ) {
            throw new TaxonomyServiceException( "You must give a positive taxid or one of the exception supported " +
                                                "by PSI-MI (-1, -2, -3, -4, -5): " + taxid );
        }

        TaxonomyTerm term = null;

        if ( taxid == -1 ) {
            term = new TaxonomyTerm( -1 );
            term.setScientificName( "In vitro" );
            term.setCommonName( "In vitro" );
        } else if ( taxid == -2 ) {
            term = new TaxonomyTerm( -2 );
            term.setScientificName( "Chemical synthesis" );
            term.setCommonName( "Chemical synthesis" );
        } else if ( taxid == -3 ) {
            term = new TaxonomyTerm( -3 );
            term.setScientificName( "Unknown" );
            term.setCommonName( "Unknown" );
        } else if ( taxid == -4 ) {
            term = new TaxonomyTerm( -4 );
            term.setScientificName( "In vivo" );
            term.setCommonName( "In vivo" );
        } else if ( taxid == -5 ) {
            term = new TaxonomyTerm( -5 );
            term.setScientificName( "In Silico" );
            term.setCommonName( "In Silico" );
        }

        if ( term == null ) {
            try {
                term = buildTerm( taxid );
            } catch ( IOException e ) {
                throw new TaxonomyServiceException( "Could not build the taxonomy term by taxid: " + taxid, e );
            }
        }

        return term;
    }

    public void retrieveChildren( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
        // This could be achieved by using getting the relationship of a term and iteratively getting the terms details...
        throw new UnsupportedOperationException();
    }

    public void retrieveParents( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException();
    }

    public List<TaxonomyTerm> getTermChildren( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException();
    }

    public List<TaxonomyTerm> getTermParent( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException();
    }

    public String getSourceDatabaseMiRef() {
        return "MI:0942"; // uniprot taxonomy
    }
}