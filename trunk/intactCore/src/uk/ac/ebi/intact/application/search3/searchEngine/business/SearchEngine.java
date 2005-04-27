package uk.ac.ebi.intact.application.search3.searchEngine.business;

import uk.ac.ebi.intact.business.IntactException;

import java.util.Map;

/**
 * @author Anja Friedrichsen
 * @version $id$
 */
public interface SearchEngine {

//    public Map findObjectByQuery(final String query, final int numberOfResults);

    public Map findObjectByIQL(final String iqlQuery, final int numberOfResults) throws IntactException;

    public Map findObjectByIQL(final String iqlQuery) throws IntactException;

    public Map getResult(Map searchKeys) throws IntactException;

}
