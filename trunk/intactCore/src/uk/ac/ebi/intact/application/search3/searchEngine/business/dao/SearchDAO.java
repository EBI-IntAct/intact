package uk.ac.ebi.intact.application.search3.searchEngine.business.dao;

import uk.ac.ebi.intact.application.search3.searchEngine.lucene.model.SearchObject;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;
import java.util.Map;

/**
 * @author Anja Friedrichsen
 * @version $id$
 */
public interface SearchDAO {

    public Object findObjectsbyAC(final String ac, final Class clazz) throws IntactException;

    public Map findObjectsbyACs(final Map someACs) throws IntactException;

    public Collection getAllSearchObjects() throws IntactException;

    public SearchObject getSearchObject(final String ac, String objClass) throws IntactException;

}
