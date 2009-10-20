/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection of utilities for handling TaxonomyTerms.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class TaxonomyTermUtils {

    /**
     * Retrieve the highest parent of the given term. this method doesn't alter the structure of the given hierarchy.
     * That is, it doesn't load additional parent using a Taxonomy service.
     *
     * @param term a non null term.
     *
     * @return the highest parent or the term itself if it doesn't have any parent.
     */
    public static TaxonomyTerm getRootParent( TaxonomyTerm term ) {

        if ( term == null ) {
            throw new NullPointerException( "term must not be null." );
        }

        TaxonomyTerm root = null;

        if ( term.getParents().size() == 0 ) {
            root = term;
        } else if ( term.getParents().size() > 1 ) {
            throw new IllegalStateException( term + " has more than one parent. Abort." );
        } else {
            // exactly 1 parent, do recursion.
            root = getRootParent( term.getParents().iterator().next() );
        }

        return root;
    }

    /////////////////////
    // Display

    public static void printNewtHierarchy( TaxonomyTerm term ) {
        printNewtHierarchy( term, System.out );
    }

    public static void printNewtHierarchy( TaxonomyTerm term, PrintStream ps ) {
        printNewtHierarchy( term, ps, "" );
    }

    private static void printNewtHierarchy( TaxonomyTerm term, PrintStream ps, String indent ) {
        ps.println( indent + term );
        for ( TaxonomyTerm child : term.getChildren() ) {
            printNewtHierarchy( child, ps, indent + "  " );
        }
    }

    /////////////////////
    // Collect children

    /**
     * Traverse the children of the given term recursively and add them all into a Collection.
     *
     * @param term the term of which we want all children.
     *
     * @return a non null collection.
     */
    public static Collection<TaxonomyTerm> collectAllChildren( TaxonomyTerm term ) {
        Collection<TaxonomyTerm> terms = new ArrayList<TaxonomyTerm>();

        collectAllChildren( term, terms );

        return terms;
    }

    private static void collectAllChildren( TaxonomyTerm term, Collection<TaxonomyTerm> terms ) {
        for ( TaxonomyTerm child : term.getChildren() ) {
            terms.add( child );
            collectAllChildren( child, terms );
        }
    }
}