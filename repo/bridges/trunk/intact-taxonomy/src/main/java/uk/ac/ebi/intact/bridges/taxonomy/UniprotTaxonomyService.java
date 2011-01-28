/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

import com.hp.hpl.jena.rdf.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
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

    private static final String UNIPROT_NS = "http://purl.uniprot.org/core/";
    private static final String UNIPROT_TAXONOMY_NS = "http://purl.uniprot.org/taxonomy/";

    public UniprotTaxonomyService() {
    }

    private InputStream getInputStream( int taxid ) throws IOException {
        String urlStr = UNIPROT_TAXONOMY_NS + taxid + ".rdf";
        URL url = new URL( urlStr );
        return url.openStream();
    }

    private TaxonomyTerm buildTerm( final int taxid ) throws IOException {

        final InputStream is = getInputStream( taxid );

        Model model = ModelFactory.createDefaultModel();
        model.read(is, null);

//        model.write(System.out);

        TaxonomyTerm term;

        // check first if it has been replaced by another record (would contain the replacedBy property)
        Resource taxonomyResource = model.getResource(UNIPROT_TAXONOMY_NS + taxid);
        Property replacedByProperty = model.getProperty(UNIPROT_NS, "replacedBy");

        boolean isReplaced = model.contains(taxonomyResource, replacedByProperty);

        if (isReplaced) {
            Statement replacedStatement = model.getProperty(taxonomyResource, replacedByProperty);
            String replacedUri = replacedStatement.getObject().asResource().getURI();

            String replacedByTaxidStr = replacedUri.replaceAll(UNIPROT_TAXONOMY_NS, "");
            int replacledByTaxid = Integer.parseInt(replacedByTaxidStr);

            if( log.isInfoEnabled() )
                        log.info( "WARNING - the taxid replacement for " + taxid + " is " + replacledByTaxid );

            term = buildTerm(replacledByTaxid);
            term.setObsoleteTaxid(taxid);

        } else {
           term = new TaxonomyTerm( taxid );
        }

        // standard properties
        String mnemonic = getLiteral(model, taxonomyResource, "mnemonic");
        if (mnemonic != null) term.setMnemonic(mnemonic);

        String commonName = getLiteral(model, taxonomyResource, "commonName");
        if (commonName != null) term.setCommonName(commonName);

        String scientificName = getLiteral(model, taxonomyResource, "scientificName");
        if (scientificName != null) term.setScientificName(scientificName);

        String synonym = getLiteral(model, taxonomyResource, "synonym");
        if (synonym != null) term.getSynonyms().add(synonym);


        return term;
    }

    private String getLiteral(Model model, Resource taxonomyResource, String propertyName) {
        Property property = model.getProperty(UNIPROT_NS, propertyName);

        if (model.contains(taxonomyResource, property)) {
            return model.getProperty(taxonomyResource, property).getLiteral().getString();
        }

        return null;
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