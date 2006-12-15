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
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        List<ProteinImpl> proteins = proteinDao.getByUniprotId("P60304");
        if(proteins.isEmpty()){
            UpdateProteins updateProteins = new UpdateProteins();
            String sourceUrl = "http://www.ebi.uniprot.org/entry/P60304?format=text&ascii";
            URL url = new URL( sourceUrl );
            InputStream is = url.openStream();

            int number = updateProteins.insertSPTrProteinsFromURL(sourceUrl, "8656" , true );

            assertEquals(1,number);
        }
    }
}
