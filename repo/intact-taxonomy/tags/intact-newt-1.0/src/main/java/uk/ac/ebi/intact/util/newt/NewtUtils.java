/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.newt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection of utilities for handling NewtTerm.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jan-2007</pre>
 */
public class NewtUtils {

    public static NewtTerm getRootParent( NewtTerm term ) {

        NewtTerm root = null;

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

    public static void printNewtHierarchy( NewtTerm term ) {
        printNewtHierarchy( term, System.out );
    }

    public static void printNewtHierarchy( NewtTerm term, PrintStream ps ) {
        printNewtHierarchy( term, ps, "" );
    }

    private static void printNewtHierarchy( NewtTerm term, PrintStream ps, String indent ) {
        ps.println( indent + term );
        for ( NewtTerm child : term.getChildren() ) {
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
    public static Collection<NewtTerm> collectAllChildren( NewtTerm term ) {
        Collection<NewtTerm> terms = new ArrayList<NewtTerm>();

        collectAllChildren( term, terms );

        return terms;
    }

    private static void collectAllChildren( NewtTerm term, Collection<NewtTerm> terms ) {
        for ( NewtTerm child : term.getChildren() ) {
            terms.add( child );
            collectAllChildren( child, terms );
        }
    }
}