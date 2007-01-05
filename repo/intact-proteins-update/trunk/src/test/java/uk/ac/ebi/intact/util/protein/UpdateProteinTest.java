package uk.ac.ebi.intact.util.protein;

import junit.framework.TestCase;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;

import java.util.Collection;
import java.util.List;
import java.net.URL;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: CatherineLeroy
 * Date: 15-Dec-2006
 * Time: 14:02:38
 * To change this template use File | Settings | File Templates.
 */
public class UpdateProteinTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInsertSPTrProteins() throws Exception{
        // Open the Hibernate session
        IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        UpdateProteins updateProteins = new UpdateProteins();
        String sourceUrl = "http://www.ebi.uniprot.org/entry/P12345?format=text&ascii";
        //Update the sp ac P12345, parsing the sourceUrl
        int number = updateProteins.insertSPTrProteinsFromURL(sourceUrl, null , true );
        // The number of protein updated should at least be equal to 1. 
        if(number < 1){
            fail("No protein updated");
        }
    }
}
