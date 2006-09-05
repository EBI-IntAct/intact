/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.tutorial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import uk.ac.ebi.intact.AbstractIntactTest;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.Collection;
import java.io.IOException;

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

    public void testVerifyInstitution() throws Exception
    {
        Institution institution = getInstitution();
        assertNotNull(institution);

        log.debug("Institution: " + institution.getFullName());
    }

    public void testCreateCvDatabase() throws Exception
    {
        CvDatabase cvDatabase = new CvDatabase(getInstitution(), "MyTestDatabase");
        cvDatabase.setFullName("Mock CvDatabase Object");

        getDaoFactory().getCvObjectDao(CvDatabase.class).persist(cvDatabase);

        assertNotNull(cvDatabase.getAc());
        cvDatabaseAc = cvDatabase.getAc();
    }

    public void testCreateCvXrefQualifier() throws Exception
    {
       CvXrefQualifier cvXrefQualifier = new CvXrefQualifier(getInstitution(), CvXrefQualifier.IDENTITY);
       getDaoFactory().getCvObjectDao(CvXrefQualifier.class).persist(cvXrefQualifier);

       assertNotNull(cvXrefQualifier.getAc());
       cvXrefQualifierAc = cvXrefQualifier.getAc();
    }

    public void testPersistBioSource() throws Exception
    {
        BioSource organism = new BioSource(getInstitution(), ORGANISM_LABEL, ORGANISM_TAXID);

        BioSourceXref xref = new BioSourceXref(getInstitution(), getCvDatabase(),  "testPrimaryId", getCvXrefQualifier());
        organism.addXref(xref);

        getDaoFactory().getBioSourceDao().persist(organism);

        organismAc = organism.getAc();
        assertNotNull(organismAc);

        BioSource loadedOrganism = getDaoFactory().getBioSourceDao().getByAc(organismAc);
        assertNotNull(organism);

        Collection<BioSourceXref> xrefs = loadedOrganism.getXrefs();
        assertEquals(1, xrefs.size());

        Xref loadedXref = xrefs.iterator().next();
        assertEquals(xref, loadedXref);

        assertEquals(organism, loadedOrganism);

        assertEquals(1, getDaoFactory().getBioSourceDao().getByTaxonId(ORGANISM_TAXID).size());
        assertNotNull(getDaoFactory().getBioSourceDao().getByTaxonIdUnique(ORGANISM_TAXID));

    }

    public void testSearchXref() throws Exception
    {
        Collection<Xref> xrefs = getDaoFactory().getXrefDao().getByPrimaryId("testPrimaryId");
        assertEquals(1, xrefs.size());

        Xref xref = xrefs.iterator().next();
        assertNotNull(xref);
        log.debug("Xref: "+xref.getAc()+" ParentAc: "+xref.getParent().getAc());
    }

    public void testSearchIndex() throws Exception
    {
        try
        {
            IndexReader reader = IndexReader.open("lucene-indexes/intact-objects");

            IndexSearcher is = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("shortLabel", analyzer);
            Query query = parser.parse("dros*");
            Hits hits = is.search(query);

            //assertEquals(1, hits.length());

            Document doc = hits.doc(0);
            assertEquals(organismAc, doc.get("ac"));
            log.debug("LUCENE AC: " + doc.get("ac"));

            is.close();
            reader.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error(e);
            fail(e.getMessage());
        }

    }

    private Institution getInstitution()
    {
        return IntactContext.getCurrentInstance().getInstitution();
    }

    private CvDatabase getCvDatabase()
    {
        assertNotNull(cvDatabaseAc);
        CvDatabase cvDatabase = getDaoFactory().getCvObjectDao(CvDatabase.class).getByAc(cvDatabaseAc);
        assertNotNull(cvDatabase);
        return cvDatabase;
    }

    private CvXrefQualifier getCvXrefQualifier()
    {
        assertNotNull(cvXrefQualifierAc);
        CvXrefQualifier cvXrefQual = getDaoFactory().getCvObjectDao(CvXrefQualifier.class).getByAc(cvXrefQualifierAc);
        assertNotNull(cvXrefQual);
        return cvXrefQual;
    }

}
