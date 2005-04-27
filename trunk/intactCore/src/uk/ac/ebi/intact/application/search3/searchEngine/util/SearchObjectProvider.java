package uk.ac.ebi.intact.application.search3.searchEngine.util;

import uk.ac.ebi.intact.application.search3.searchEngine.lucene.model.SearchObject;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;

/**
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public interface SearchObjectProvider {

    public SearchObject getSearchObject(String ac, String objClass) throws IntactException;

    public Collection getAllExperiments(String sqlQuery) throws IntactException;

    public Collection getAllInteractions(String sqlQuery) throws IntactException;

    public Collection getAllProteins(String sqlQuery) throws IntactException;

    public Collection getAllCvObjects(String sqlQuery) throws IntactException;

    public Collection getAllBioSources(String sqlQuery) throws IntactException;

}
