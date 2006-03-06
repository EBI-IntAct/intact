/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.util.controlledVocab.model.CvTerm;
import uk.ac.ebi.intact.util.controlledVocab.model.CvTermXref;
import uk.ac.ebi.intact.util.controlledVocab.model.IntactOntology;
import uk.ac.ebi.ook.loader.impl.AbstractLoader;
import uk.ac.ebi.ook.loader.parser.OBOFormatParser;
import uk.ac.ebi.ook.model.implementation.TermBean;
import uk.ac.ebi.ook.model.implementation.TermDbXrefBean;
import uk.ac.ebi.ook.model.implementation.TermSynonymBean;
import uk.ac.ebi.ook.model.implementation.AnnotationBean;
import uk.ac.ebi.ook.model.interfaces.TermRelationship;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

/**
 * Wrapper class that hides the way OLS handles OBO files.
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since <pre>30-Sep-2005</pre>
 */
public class PSILoader extends AbstractLoader {

    /////////////////////////////
    // AbstractLoader's methods

    protected void configure() {
        /**
         * ensure we get the right logger
         */
//        logger = Logger.getLogger( PSILoader.class );

//        logger = LoggerFactory.getLogger( PSILoader.class,
//                                          CommonUtilities.getResource( Constants.LOG4J_CONFIG_FILE, Constants.OLS_JAR ) );
        logger = Logger.getLogger( PSILoader.class );

        parser = new OBOFormatParser();
        ONTOLOGY_DEFINITION = "PSI MI";
        FULL_NAME = "PSI Molecular Interactions";
        SHORT_NAME = "PSI-MI";
        FQCN = PSILoader.class.getName();
    }

    protected void parse( Object params ) {
        try {
            Vector v = new Vector();
            v.add( (String) params );
            ( (OBOFormatParser) parser ).configure( v );
            parser.parseFile();

        } catch ( Exception e ) {
            logger.fatal( "Parse failed: " + e.getMessage(), e );
        }
    }

    protected void printUsage() {
        // done to comply to AbstractLoader requirements
    }

    //////////////////////////////
    // User's methods

    private IntactOntology buildIntactOntology() {

        IntactOntology ontology = new IntactOntology();

        // 1. convert and index all terms (note: at this stage we don't handle the hierarchy)
        for ( Iterator iterator = ontBean.getTerms().iterator(); iterator.hasNext(); ) {
            TermBean term = (TermBean) iterator.next();

            // convert term into a CvTerm
            CvTerm cvTerm = new CvTerm( term.getIdentifier() );

            // try to split the name into short and long name
            int index = term.getName().indexOf( ':' );
            if ( index != -1 ) {
                // found it !
                String name = term.getName();
                String shortName = name.substring( 0, index ).trim();
                String longName = name.substring( index + 1, name.length() ).trim();

                cvTerm.setShortName( shortName );
                cvTerm.setFullName( longName );

            } else {
                // not found
                cvTerm.setShortName( term.getName() );
                cvTerm.setFullName( term.getName() );
            }

            cvTerm.setDefinition( term.getDefinition() );

            // check for Xrefs: pubmed, resid...
            if ( term.getTermDbXrefs() != null ) {
                for ( Iterator iterator1 = term.getTermDbXrefs().iterator(); iterator1.hasNext(); ) {
                    TermDbXrefBean dbXref = (TermDbXrefBean) iterator1.next();

                    String db = dbXref.getDbXref().getDbName();

                    CvTermXref xref = new CvTermXref( dbXref.getDbXref().getAccession(), db );
                    cvTerm.addXref( xref );
                }
            }

            // Check for synonyms
            if ( term.getSynonyms() != null ) {
                for ( Iterator iterator1 = term.getSynonyms().iterator(); iterator1.hasNext(); ) {
                    TermSynonymBean syn = (TermSynonymBean) iterator1.next();

                    cvTerm.addSynonym( syn.getSynonym() );
                }
            }

            // Check for annotation
            if ( term.getAnnotations() != null ) {
                for ( Iterator iterator1 = term.getAnnotations().iterator(); iterator1.hasNext(); ) {
                    AnnotationBean annot = (AnnotationBean) iterator1.next();
                    cvTerm.addAnnotation( annot.getAnnotationName(), annot.getAnnotationValue() );
                }
            }

            // TODO how do I get URL, search-url, search-url-ascii, example, id-validation-regexp, comment ?
            // TODO how can I re-export those values in OBO later on ?

            cvTerm.setObsolete( term.isObsolete() );

            // TODO cvTerm.setObsoleteMessage( );

            ontology.addTerm( cvTerm );
        }

        // 2. build hierarchy based on the relations of the Terms
        for ( Iterator iterator = ontBean.getTerms().iterator(); iterator.hasNext(); ) {
            TermBean term = (TermBean) iterator.next();

            if ( term.getRelationships() != null ) {
                for ( Iterator iterator1 = term.getRelationships().iterator(); iterator1.hasNext(); ) {
                    TermRelationship relation = (TermRelationship) iterator1.next();

                    ontology.addLink( relation.getObjectTerm().getIdentifier(),
                                      relation.getSubjectTerm().getIdentifier() );
                }
            }
        }

        // 3. Organise a mapping between IntAct CV classes and PSI-MI Terms.
        ontology.updateMapping();

        return ontology;
    }


    /**
     * Parse the given OBO file and build a representation of the DAG into an IntactOntology.
     *
     * @param file the input file. It has to exist and to be readable, otherwise it will break.
     *
     * @return a non null IntactOntology.
     */
    public IntactOntology parseOboFile( File file ) {

        if ( !file.exists() ) {
            throw new IllegalArgumentException( file.getAbsolutePath() + " doesn't exist." );
        }

        if ( !file.canRead() ) {
            throw new IllegalArgumentException( file.getAbsolutePath() + " could not be read." );
        }

        //setup vars
        configure();

        //parse obo file
        System.out.println( "Reading " + file.getAbsolutePath() );
        parse( file.getAbsolutePath() );

        //process into relations
        process();

        return buildIntactOntology();
    }
}