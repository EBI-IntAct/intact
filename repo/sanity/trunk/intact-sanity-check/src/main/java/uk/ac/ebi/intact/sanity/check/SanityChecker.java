/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.commons.rules.RuleRunReport;
import uk.ac.ebi.intact.sanity.commons.rules.RuleRunner;

import java.util.Collection;

/**
 * Checks potential annotation error on IntAct objects.
 * <p/>
 * Reports are send to the curators.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: SanityChecker.java,v 1.33 2006/06/01 08:16:45 catherineleroy Exp $
 */
public class SanityChecker {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(SanityChecker.class);

   public static RuleRunReport executeSanityCheck(SanityCheckConfig sanityConfig) {

       if (log.isInfoEnabled()) log.info("Check on CvObjects");
       beginTransaction();
       checkAllCvObjects();
       commitTransaction();

       return RuleRunReport.getInstance();
   }

    protected static void checkAllCvObjects() {
        CvObjectDao cvObjectDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao();
        Collection<CvObject> allCvObjects = cvObjectDao.getAll();

        if (log.isInfoEnabled()) log.info("\tProcessing "+allCvObjects.size()+" CvObjects");

        checkCvObjects(cvObjectDao.getAll());
    }

    public static RuleRunReport checkCvObjects(Collection<? extends CvObject> cvObjectsToCheck) {
        RuleRunner.runAvailableRules(cvObjectsToCheck);

        return RuleRunReport.getInstance();
    }

    private static void beginTransaction() {
        if (!IntactContext.getCurrentInstance().getDataContext().isTransactionActive()) {
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        }
    }

    private static void commitTransaction() {
        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch (IntactTransactionException e) {
            throw new RuntimeException(e);
        }
    }
      /*
    private static void commitAndBeginTransaction() {
        commitTransaction();
        beginTransaction();
    }
    */
}
