package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.parser;

import uk.ac.ebi.intact.business.IntactException;

/**
 * This is an interface that provides a method to get an Lucene query string out of an IQL query string
 *
 * @author Anja Friedrichsen
 * @version IQLParserI.java Date: Feb 10, 2005 Time: 12:55:32 PM
 */
public interface IQLParser {

    public String getLuceneQuery(String IQLStatement) throws IntactException;
}
