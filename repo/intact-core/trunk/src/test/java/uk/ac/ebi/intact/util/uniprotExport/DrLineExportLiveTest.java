/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.uniprotExport;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.ProteinImpl;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Aug-2006</pre>
 */
public class DrLineExportLiveTest extends TestCase
{

    private static final Log log = LogFactory.getLog(DrLineExportLiveTest.class);


    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void testIsProteinEligible() throws Exception
    {
        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getProteinDao().getByUniprotId("Q9Z2Q6").iterator().next();

        DRLineExport export = new DRLineExport();
        boolean protEligible = export.isProteinEligible(prot);
        assertTrue(protEligible);
    }

    public void testIsProteinEligibleOnlyIsoformWithInteractions() throws Exception
    {
        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getProteinDao().getByUniprotId("Q8R332-1").iterator().next();

        DRLineExport export = new DRLineExport();
        boolean protEligible = export.isProteinEligible(prot);
        assertTrue("A protein with isoforms, and one of the isoforms have interactions but not the" +
                " master protein, should be exported", protEligible);
    }
         /*

         Q8IN81 NOT eligible !!!!
Q8INK9 NOT eligible !!!!
Q8R332 NOT eligible !!!!

    public void testRemovedFile() throws Exception
    {
        File idsToCheckFile = new File("/scratch/projects/intact-current/plugins/maven-uniprotexport-plugin/temp/target/uniprotexport/drFileComparator_removed.log");

        BufferedReader reader = new BufferedReader(new FileReader(idsToCheckFile));
        String line;

        int i=0;

        while ((line = reader.readLine()) != null)
        {
            String id = line.trim();

            ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getProteinDao().getByUniprotId(id).iterator().next();

            DRLineExport export = new DRLineExport();
            export.init();
            boolean protEligible = export.isProteinEligible(prot);
            if (protEligible)
            {
                //System.out.println(id + " eligible");
            }
            else
            {
                System.out.println(id + " NOT eligible !!!!");
            }

            i++;

            if (i%50 == 0)
            {
               IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
            }
        }

    }  */

}
