/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rigits reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.commons.search;

import java.io.Serializable;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * This class wraps a search result and provides extra information.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */
public class ResultWrapper implements Serializable {

    /**
     * The result.
     */
    private List myResults;

    private Map myInfo;

    /**
     * The actual number of records that could be retrieved by the search.
     */
    private int myPossibleSize;

    /**
     * Maximum search records allowed.
     */
    private int myMaxAllowedSize;

    /**
     * Use this constructor only when there are no matching search results.
     */
    public ResultWrapper(int maxSize) {
        this(Collections.EMPTY_LIST, maxSize);
    }

    /**
     * Creates an instance of this class with given records. Use this constructor only when the
     * result set is of acceptable size.
     *
     * @param results the list of search results
     * @param maxSize the maximum size for the search result.
     */
    public ResultWrapper(Collection results, int maxSize) {
        this(results, results.size(), maxSize);
    }

    public ResultWrapper(Collection results, int maxSize, Map info) {
            this(results, results.size(), maxSize);
            this.myInfo = info; 
        }


    /**
     * Creates an instance of this class with given maximum number of records allowed. Use this
     * constructor when the result set is too big.
     *
     * @param possibleSize the possible size of the returned set.
     * @param maxSize      maximum number of records allowed.
     */
    public ResultWrapper(int possibleSize, int maxSize) {
        this(null, possibleSize, maxSize);
    }

     /**
     * Creates an instance of this class with given maximum number of records allowed. Use this
     * constructor when the result set is too big.
     *
     * @param possibleSize the possible size of the returned set.
     * @param maxSize      maximum number of records allowed.
     */
    public ResultWrapper(int possibleSize, int maxSize, Map info) {
        this(null, possibleSize, maxSize);
        this.myInfo = info;
    }

    /**
     * Returns the result set.
     *
     * @return can be empty if the result set is too large or no matches.
     */
    public List getResult() {
        if (myResults == null) {
            return Collections.EMPTY_LIST;
        }
        return myResults;
    }

    public boolean isEmpty() {
        return this.getResult().isEmpty(); 
    }

    // Read only methods.

    public boolean isTooLarge() {
        return myPossibleSize > myMaxAllowedSize;
    }

    public int getPossibleResultSize() {
        return myPossibleSize;
    }

    public Map getInfo() {
            return myInfo;
        }


    // Helper methods.

    private ResultWrapper(Collection results, int potentialSize, int maxSize) {
        myResults = (List) results;
        myPossibleSize = potentialSize;
        myMaxAllowedSize = maxSize;
    }
}
