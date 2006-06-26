package uk.ac.ebi.intact.application.search3.advancedSearch.parser;

import uk.ac.ebi.intact.business.IntactException;

/**
 * This is an interface that provides a method to get an Lucene query string out of an IQL query string.
 *
 * @author Anja Friedrichsen
 * @version IQLParserI.java Date: Feb 10, 2005 Time: 12:55:32 PM
 */
public interface IQLParser {

    /**
     * Returns the lucene query value.
     *
     * @param IQLStatement a IQL statement.
     *
     * @return a Lucene query.
     *
     * @throws IntactException 
     */
    public String getLuceneQuery( String IQLStatement ) throws IntactException;
}
