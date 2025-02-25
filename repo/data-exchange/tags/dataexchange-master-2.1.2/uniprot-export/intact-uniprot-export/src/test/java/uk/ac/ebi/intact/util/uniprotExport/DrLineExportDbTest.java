/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.uniprotExport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.ProteinImpl;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Aug-2006</pre>
 */
@Ignore
public class DrLineExportDbTest extends IntactBasicTestCase {

    private static final Log log = LogFactory.getLog(DrLineExportDbTest.class);

    @Test
    public void testIsProteinEligible() throws Exception {
        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getProteinDao().getByUniprotId("P14712").iterator().next();

        LineExportConfig config = new LineExportConfig();
        config.setIgnoreUniprotDrExportAnnotation(true);

        DRLineExport export = new DRLineExport(config);
        boolean protEligible = export.isProteinEligible(prot);
        Assert.assertTrue(protEligible);
    }

    /*
    public void testIsProteinEligibleOnlyIsoformWithInteractions() throws Exception
    {
        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getProteinDao().getByUniprotId("Q8R332-1").iterator().next();

        DRLineExport export = new DRLineExport();
        boolean protEligible = export.isProteinEligible(prot);
        Assert.assertTrue("A protein with isoforms, and one of the isoforms have interactions but not the" +
                " master protein, should be exported", protEligible);
    }

    public void testIsProteinEligible_OneWithSecondaryId() throws Exception
    {
        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getProteinDao().getByUniprotId("P03020").iterator().next();

        DRLineExport export = new DRLineExport();
        boolean protEligible = export.isProteinEligible(prot);
        Assert.assertTrue(protEligible);
    }
    */

    @Test
    @Ignore
    public void testIsProteinEligible_() throws Exception {
        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getProteinDao().getByUniprotId("P14713").iterator().next();

        LineExportConfig config = new LineExportConfig();
        config.setIgnoreUniprotDrExportAnnotation(true);

        DRLineExport export = new DRLineExport(config);
        boolean protEligible = export.isProteinEligible(prot);
        Assert.assertTrue(protEligible);
    }


}
