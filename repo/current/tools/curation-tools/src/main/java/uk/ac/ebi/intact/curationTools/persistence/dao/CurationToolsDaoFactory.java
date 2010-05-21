package uk.ac.ebi.intact.curationTools.persistence.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.persistence.dao.impl.ActionReportDaoImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */
@Component
public class CurationToolsDaoFactory implements Serializable{
    @PersistenceContext( unitName = "intact-curationTools-default" )
    private EntityManager currentEntityManager;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BlastReportDao blastReportDao;
    @Autowired
    private BlastResultsDao blastResultsDao;
    @Autowired
    private PICRCrossReferencesDAO picrCrossReferencesDao;
    @Autowired
    private PICRReportDao picrReportDao;
    @Autowired
    private UpdateResultsDao updateResultsDao;

    public CurationToolsDaoFactory() {
    }

    public EntityManager getEntityManager() {
        return currentEntityManager;
    }

    public <T extends ActionReport> ActionReportDao<T> getActionReportDao( Class<T> entityType) {
        ActionReportDao actionReportDao = getBean(ActionReportDaoImpl.class);
        actionReportDao.setEntityClass(entityType);
        return actionReportDao;
    }

    public BlastResultsDao getBlastResultsDao() {
        return blastResultsDao;
    }

    public PICRCrossReferencesDAO getPicrCrossReferencesDao() {
        return picrCrossReferencesDao;
    }

    public UpdateResultsDao getUpdateResultsDao() {
        return updateResultsDao;
    }

    public BlastReportDao getBlastReportDao() {
        return blastReportDao;
    }

    public PICRReportDao getPICRReportDao() {
        return picrReportDao;
    }

    private <T> T getBean(Class<T> beanType) {
        return (T) applicationContext.getBean(StringUtils.uncapitalize(beanType.getSimpleName()));
    }

}
