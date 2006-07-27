/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.tutorial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.AbstractIntactTest;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.Collection;

/**
 * Test the tutorial persisting examples
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-Jul-2006</pre>
 */
public class PersistTutorialTest extends AbstractIntactTest
{

    private static final Log log = LogFactory.getLog(PersistTutorialTest.class);

    private static final String ORGANISM_LABEL = "drosophila";
    private static final String ORGANISM_TAXID = "7215";

    private static String cvDatabaseAc;
    private static String cvXrefQualifierAc;
    private static String organismAc;

    public void testVerifyInstitution()
    {
        Institution institution = getInstitution();
        assertNotNull(institution);

        log.debug("Institution: " + institution.getFullName());
    }

    public void testCreateCvDatabase()
    {
        CvDatabase cvDatabase = new CvDatabase(getInstitution(), "MyTestDatabase");
        cvDatabase.setFullName("Mock CvDatabase Object");

        DaoFactory.getCvObjectDao(CvDatabase.class).persist(cvDatabase);

        assertNotNull(cvDatabase.getAc());
        cvDatabaseAc = cvDatabase.getAc();
    }

    public void testCreateCvXrefQualifier()
    {
       CvXrefQualifier cvXrefQualifier = new CvXrefQualifier(getInstitution(), CvXrefQualifier.IDENTITY);
       DaoFactory.getCvObjectDao(CvXrefQualifier.class).persist(cvXrefQualifier);

       assertNotNull(cvXrefQualifier.getAc());
       cvXrefQualifierAc = cvXrefQualifier.getAc();
    }

    public void testPersistBioSource()
    {
        BioSource organism = new BioSource(getInstitution(), ORGANISM_LABEL, ORGANISM_TAXID);

        BioSourceXref xref = new BioSourceXref(getInstitution(), getCvDatabase(),  "testPrimaryId", getCvXrefQualifier());
        organism.addXref(xref);

        DaoFactory.getBioSourceDao().persist(organism);

        organismAc = organism.getAc();
        assertNotNull(organismAc);

        BioSource loadedOrganism = DaoFactory.getBioSourceDao().getByAc(organismAc);
        assertNotNull(organism);

        Collection<BioSourceXref> xrefs = loadedOrganism.getXrefs();
        assertEquals(1, xrefs.size());

        Xref loadedXref = xrefs.iterator().next();
        assertEquals(xref, loadedXref);

        assertEquals(organism, loadedOrganism);

        assertEquals(1, DaoFactory.getBioSourceDao().getByTaxonId(ORGANISM_TAXID).size());
        assertNotNull(DaoFactory.getBioSourceDao().getByTaxonIdUnique(ORGANISM_TAXID));

    }

    private Institution getInstitution()
    {
        return IntactContext.getCurrentInstance().getInstitution();
    }

    private CvDatabase getCvDatabase()
    {
        assertNotNull(cvDatabaseAc);
        CvDatabase cvDatabase = DaoFactory.getCvObjectDao(CvDatabase.class).getByAc(cvDatabaseAc);
        assertNotNull(cvDatabase);
        return cvDatabase;
    }

    private CvXrefQualifier getCvXrefQualifier()
    {
        assertNotNull(cvXrefQualifierAc);
        CvXrefQualifier cvXrefQual = DaoFactory.getCvObjectDao(CvXrefQualifier.class).getByAc(cvXrefQualifierAc);
        assertNotNull(cvXrefQual);
        return cvXrefQual;
    }

}
