/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.uniprotExport;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Aug-2006</pre>
 */
//public class DrLineExportLiveTest extends TestCase
//{
//
//    private static final Log log = LogFactory.getLog(DrLineExportLiveTest.class);
//
//
//    @Override
//    protected void tearDown() throws Exception
//    {
//        super.tearDown();
//        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
//    }
//
//    @Override
//    protected void setUp() throws Exception
//    {
//        super.setUp();
//    }
//
//    public void testIsProteinEligible() throws Exception
//    {
//        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
//                .getProteinDao().getByUniprotId("Q9Z2Q6").iterator().next();
//
//        DRLineExport export = new DRLineExport();
//        boolean protEligible = export.isProteinEligible(prot);
//        assertTrue(protEligible);
//    }
//
//    public void testIsProteinEligibleOnlyIsoformWithInteractions() throws Exception
//    {
//        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
//                .getProteinDao().getByUniprotId("Q8R332-1").iterator().next();
//
//        DRLineExport export = new DRLineExport();
//        boolean protEligible = export.isProteinEligible(prot);
//        assertTrue("A protein with isoforms, and one of the isoforms have interactions but not the" +
//                " master protein, should be exported", protEligible);
//    }
//
//    public void testIsProteinEligible_OneWithSecondaryId() throws Exception
//    {
//        ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
//                .getProteinDao().getByUniprotId("P03020").iterator().next();
//
//        DRLineExport export = new DRLineExport();
//        boolean protEligible = export.isProteinEligible(prot);
//        assertTrue(protEligible);
//    }
//
//    public void testIsProteinEligible_() throws Exception
//        {
//            ProteinImpl prot = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
//                    .getProteinDao().getByUniprotId("P0ACJ8").iterator().next();
//
//            DRLineExport export = new DRLineExport();
//            boolean protEligible = export.isProteinEligible(prot);
//            assertTrue(protEligible);
//        }
//
//
//
//}
