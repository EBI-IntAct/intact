package uk.ac.ebi.intact.plugins.targetspecies;



import org.junit.Test;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.persistence.dao.XrefDao;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.CvContext;
import uk.ac.ebi.intact.model.*;

import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;

import junitx.framework.Assert;

  @IntactUnitDataset(  dataset = PsiTestDatasetProvider.ALL_CVS,
            provider = PsiTestDatasetProvider.class )

public class UpdateTargetSpeciesTest extends IntactAbstractTestCase {

    
    @Test
    public void updateTest() throws IntactTransactionException, FileNotFoundException {
//        CvObjectDao cvObjectDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao();
//        Institution institution = IntactContext.getCurrentInstance().getInstitution();
//        CvTopic noUniprotUpdate = new CvTopic(institution, "no-uniprot-update");
//        cvObjectDao.saveOrUpdate(noUniprotUpdate);
        
//        int cvObjectCount = cvObjectDao.countAll();
//        Assert.assertTrue("The database should be filled with cv",cvObjectCount > 0);
//        System.out.println("cvObjectCount = " + cvObjectCount);
        super.beginTransaction();
        // MAKE SURE WE DELETE ALL THE XREF TARGET SPECIES IF ANY
        ExperimentDao expDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
        Collection<Experiment> experiments = expDao.getAll();
        Assert.assertTrue("The database should contains experiments", experiments.size() > 0);
        CvContext cvContext = IntactContext.getCurrentInstance().getCvContext();
        CvXrefQualifier targetSpecies = cvContext.getByLabel(CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES);
        CvDatabase newt = cvContext.getByMiRef(CvDatabase.class, CvDatabase.NEWT_MI_REF);
        XrefDao<ExperimentXref> xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao(ExperimentXref.class);
        for(Experiment exp : experiments){
            Collection<ExperimentXref> experimentXrefs = exp.getXrefs();
            for (Iterator<ExperimentXref> iterator = experimentXrefs.iterator(); iterator.hasNext();) {
                ExperimentXref experimentXref =  iterator.next();
                if(newt.equals(experimentXref.getCvDatabase()) && targetSpecies.equals(experimentXref.getCvXrefQualifier())){

                    experimentXref.setParent(null);
                    xrefDao.delete(experimentXref);
                    iterator.remove();
                }
            }
            ExperimentDao experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
            experimentDao.saveOrUpdate(exp);
        }
        super.commitTransaction();


        File statsFile = new File("statTarget.txt");
         PrintStream ps = new PrintStream( statsFile );
        super.beginTransaction();
        UpdateTargetSpeciesReport report = UpdateTargetSpecies.update( ps, false, "*" );
        super.commitTransaction();

        //CHECK THAT THE XREF TARGET SPECIES ARE BACK
        super.beginTransaction();
        expDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
        experiments = expDao.getAll();
        cvContext = IntactContext.getCurrentInstance().getCvContext();
        targetSpecies = cvContext.getByLabel(CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES);
        newt = cvContext.getByMiRef(CvDatabase.class, CvDatabase.NEWT_MI_REF);
        xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao(ExperimentXref.class);
        int xrefCount = 0;

        Assert.assertTrue(experiments.size()>0);
        for(Experiment exp : experiments){
            Collection<ExperimentXref> experimentXrefs = exp.getXrefs();
            for (Iterator<ExperimentXref> iterator = experimentXrefs.iterator(); iterator.hasNext();) {
                ExperimentXref experimentXref =  iterator.next();
                if(newt.equals(experimentXref.getCvDatabase()) && targetSpecies.equals(experimentXref.getCvXrefQualifier())){
                    xrefCount++;
                }
            }
            ExperimentDao experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
            experimentDao.saveOrUpdate(exp);
        }
        System.out.println("XREF COUNT IS " + xrefCount);
        Assert.assertTrue(xrefCount > 0);
        super.commitTransaction();



    }

}

