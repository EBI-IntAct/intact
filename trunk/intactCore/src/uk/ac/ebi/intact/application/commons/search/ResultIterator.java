/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rigits reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.commons.search;

import java.io.Serializable;
import java.util.*;

/**
 * This class wraps a search result in an iterator and provides extra information.
 * Similar to ResultWrapper but the access to result is provided via an Interator.
 *
 * @author Sugath Mudali
 * @version $Id$
 */
public class ResultIterator implements Serializable {

    /**
     * The result.
     */
    private Iterator myIter;

    /**
     * Maximum search records allowed.
     */
    private int myMaxAllowedSize;

    /**
     * The actual number of records that could be retrieved by the search.
     */
    private int myPossibleSize;

    /**
     * Use this constructor only when there are no matching search results.
     */
    public ResultIterator(int maxSize) {
        this(null, maxSize, 0);
    }

    /**
     * Creates an instance of this class with given maximum number of records allowed. Use this
     * constructor when the result set is too big.
     *
     * @param possibleSize the possible size of the returned set.
     * @param maxSize      maximum number of records allowed.
     */
    public ResultIterator(int possibleSize, int maxSize) {
        this(null, possibleSize, maxSize);
    }

    /**
     * Creates an instance of this class with an iterator. Use this constructor only when the
     * result set is of acceptable size.
     *
     * @param iter the iterator access the search results
     * @param maxSize the maximum size for the search result.
     * @param potentialSize the size that could be retrieved by the search.
     */
    public ResultIterator(Iterator iter, int maxSize, int potentialSize) {
        myIter = iter;
        myPossibleSize = potentialSize;
        myMaxAllowedSize = maxSize;
    }

    /**
     * Returns the iterator to access ther search result.
     * @return the iterator to access ther search result. This could be empty when
     * the result is too big or empty. Each item is an Object[].
     *
     * <pre>
     * pre:  forall(obj : Object | obj.oclIsTypeOf(Object[]))
     * </pre>
     */
    public Iterator getIterator() {
        return myIter;
    }

    // Read only methods.

    public boolean isEmpty() {
        return myPossibleSize == 0;
    }

    public boolean isTooLarge() {
        return myPossibleSize > myMaxAllowedSize;
    }

    /**
     * @return true only if the search has produced non empty result and its
     * size is within the maximum bounds.
     */
    public boolean isNonEmptyAndWithinBounds() {
        return !(isEmpty() || isTooLarge());
    }

    public int getPossibleResultSize() {
        return myPossibleSize;
    }

    public boolean hasOneItem() {
        return myPossibleSize == 1;
    }
}
