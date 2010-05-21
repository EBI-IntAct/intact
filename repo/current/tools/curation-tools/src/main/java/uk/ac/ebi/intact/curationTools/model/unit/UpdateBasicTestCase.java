package uk.ac.ebi.intact.curationTools.model.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.persistence.dao.CurationToolsDaoFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-curationTools.spring.xml",
        "classpath*:/META-INF/standalone/*-standalone.spring.xml"})
@TransactionConfiguration( transactionManager = "curationToolsTransactionManager" )
@Transactional
public class UpdateBasicTestCase {

    @Autowired
    private CurationToolsDaoFactory daoFactory;

    @PersistenceContext(unitName = "intact-curationTools-default")    
    private EntityManager entityManager;

    private CurationMockBuilder mockBuilder;

    @Before
    public void prepareBasicTest() throws Exception {
        mockBuilder = new CurationMockBuilder();
    }

    @After
    public void afterBasicTest() throws Exception {
        mockBuilder = null;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public CurationToolsDaoFactory getDaoFactory() {
        return daoFactory;
    }

    public CurationMockBuilder getMockBuilder() {
        return mockBuilder;
    }
}
