package uk.ac.ebi.intact.protein.mapping.update;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

import javax.xml.ws.soap.SOAPFaultException;

/**
 * Unit test for proteinUpdateManager
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>06-May-2010</pre>
 */
public class proteinUpdateManagerTest  extends IntactBasicTestCase {

    @Test
    @Ignore
    public void test_Protein_Update_Process(){

        ProteinUpdateManager updateManager = new ProteinUpdateManager();
        try {
            updateManager.writeResultsOfProteinUpdate();
            updateManager.writeUpdateReportForProteinsWithUniprotCrossReferences();
        } catch (ProteinUpdateException e) {
            System.err.println(e.getMessage());
            Assert.assertFalse(true);
        } catch (StrategyException e) {
            System.err.println(e.getMessage());
            Assert.assertFalse(true);
        } catch(SOAPFaultException e){
            System.err.println(e.getMessage());
            Assert.assertFalse(true);
        }
    }
}