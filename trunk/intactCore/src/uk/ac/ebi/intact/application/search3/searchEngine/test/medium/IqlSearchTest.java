package uk.ac.ebi.intact.application.search3.searchEngine.test.medium;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.parser.IQLParserImpl;
import uk.ac.ebi.intact.application.search3.searchEngine.business.SearchEngineImpl;
import uk.ac.ebi.intact.application.search3.searchEngine.business.dao.SearchDAO;
import uk.ac.ebi.intact.application.search3.searchEngine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.application.search3.searchEngine.lucene.IntactAnalyzer;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Anja
 * Date: 01.03.2005
 * Time: 22:07:02
 * To change this template use File | Settings | File Templates.
 */
public class IqlSearchTest extends TestCase {
    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public IqlSearchTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(IqlSearchTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        // create the index
        IntactHelper helper = new IntactHelper();
        SearchDAO dao = new SearchDAOImpl(helper);
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexMedium"), dao, new IQLParserImpl());
    }

    public void testIqlQuery() {
        String iql = "SELECT experiment FROM intact WHERE ac='EBI-12'";
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByIQL(iql));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
        assertNotNull(result);
        MapIterator it = result.mapIterator();
        assertTrue(it.hasNext());
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("experiment", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-12", anObject.getAc());
            }
        }
    }
}
