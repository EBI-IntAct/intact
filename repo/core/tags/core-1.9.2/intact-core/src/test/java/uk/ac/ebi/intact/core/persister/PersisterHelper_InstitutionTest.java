package uk.ac.ebi.intact.core.persister;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Institution;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_InstitutionTest extends IntactBasicTestCase {

    @Test
    public void persistInstitution_default_withHelper() throws Exception {
        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        PersisterHelper.saveOrUpdate(institution);

        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);

        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());
    }

    @Test
    public void persistInstitution_default() throws Exception {

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        PersisterHelper.saveOrUpdate(institution);

        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);

        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());
    }

    @Test
    public void persistInstitution_annotations() throws Exception {

        Assert.assertNull( getDaoFactory().getInstitutionDao().getByXref( Institution.MINT_REF ) );

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        institution.getAnnotations().add(getMockBuilder().createAnnotation("nowhere", CvTopic.CONTACT_EMAIL_MI_REF, CvTopic.CONTACT_EMAIL));

        PersisterHelper.saveOrUpdate(institution);

        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);

        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());
        Assert.assertEquals(1, reloadedInstitution.getAnnotations().size());
    }

    @Test
    public void persistInstitution_syncWithDatabase() throws Exception {

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        PersisterHelper.saveOrUpdate(institution);

        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);
        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());

        institution = getMockBuilder().createInstitution(Institution.MINT_REF, "mint2");
        PersisterHelper.saveOrUpdate(institution);

        reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);
        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());
    }

}