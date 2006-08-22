/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence;

import uk.ac.ebi.intact.AbstractIntactTest;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.BioSourceXref;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.InteractorXref;

import java.util.Collection;


/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>1-Aug-2006</pre>
 */
public class XrefTest extends AbstractIntactTest
{
    private static final String INTERACTOR_LABEL = "TestProt-Xref";

    private Institution institution;

    private static CvDatabase cvDatabase;
    private static CvXrefQualifier cvXrefQualifier;
    private static CvInteractorType cvInteractionType;

    private static String interactorAc;

    protected void setUp() throws Exception
    {
        super.setUp();
        institution = IntactContext.getCurrentInstance().getInstitution();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        institution = null;
    }

    public void testCreateCvDatabase()
    {
        cvDatabase = new CvDatabase(institution, "MyTestDatabase_XrefTest");
        cvDatabase.setFullName("Mock CvDatabase Object");

        getDaoFactory().getCvObjectDao(CvDatabase.class).persist(cvDatabase);

        assertNotNull(cvDatabase);
    }

    public void testCreateCvXrefQualifier()
    {
       cvXrefQualifier = new CvXrefQualifier(institution, CvXrefQualifier.IDENTITY);
       getDaoFactory().getCvObjectDao(CvXrefQualifier.class).saveOrUpdate(cvXrefQualifier);

       assertNotNull(cvXrefQualifier);
    }

    public void testCreateCvInteractionType()
    {
       cvInteractionType = new CvInteractorType(institution, "myCvInteractionType-Xref");
       getDaoFactory().getCvObjectDao(CvInteractorType.class).saveOrUpdate(cvInteractionType);

       assertNotNull(cvInteractionType);
    }

    public void testPersistBioSource()
    {
        BioSource organism = new BioSource(institution, "human", "9610");
        getDaoFactory().getBioSourceDao().persist(organism);

        assertNotNull(cvInteractionType);

        ProteinImpl prot = new ProteinImpl(institution, organism, INTERACTOR_LABEL, cvInteractionType);
        prot.setFullName("Test protein in XrefTest");
        prot.setCrc64("InvalidCRC_Test");

        assertNotNull(cvDatabase);
        assertNotNull(cvXrefQualifier);

        InteractorXref xref = new InteractorXref(institution, cvDatabase,  "testPrimaryId-Xref", cvXrefQualifier);
        prot.addXref(xref);

        getDaoFactory().getProteinDao().saveOrUpdate(prot);

        String protAc = prot.getAc();

        assertNotNull(protAc);

        ProteinImpl loadedProt = getDaoFactory().getProteinDao().getByAc(protAc);
        assertNotNull(organism);

        Collection<InteractorXref> xrefs = loadedProt.getXrefs();
        assertEquals(1, xrefs.size());

        Xref loadedXref = xrefs.iterator().next();
        assertEquals(xref, loadedXref);

        assertEquals(prot, loadedProt);

    }

}
