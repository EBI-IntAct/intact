package uk.ac.ebi.intact.editor.services.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.dao.DbInfoDao;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.model.meta.Application;
import uk.ac.ebi.intact.jami.model.meta.DbInfo;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
@Service
public class ApplicationInfoService extends AbstractEditorService {

    public ApplicationInfoService() {
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public ApplicationInfo getCurrentApplicationInfo() {
        String uniprotJapiVersion = UniProtJAPI.factory.getVersion();

        final DbInfoDao infoDao = getIntactDao().getDbInfoDao();

        String schemaVersion = getDbInfoValue(infoDao, DbInfo.SCHEMA_VERSION);
        String lastUniprotUpdate = getDbInfoValue(infoDao, DbInfo.LAST_PROTEIN_UPDATE);
        String lastCvUpdate = getDbInfoValue(infoDao, DbInfo.LAST_CV_UPDATE_PSIMI);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        PrintStream ps = new PrintStream(baos);
        String databaseCounts ="";
        try{
            printDatabaseCounts(ps);
            databaseCounts = baos.toString().replaceAll("\n","<br/>");
        }
        finally {
            ps.close();
        }

        return new ApplicationInfo(uniprotJapiVersion, schemaVersion, lastUniprotUpdate, lastCvUpdate, databaseCounts);
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public Application reloadApplication(String ac) {
        return getIntactDao().getApplicationDao().getByAc(ac);
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void saveApplicationProperties(Application application) throws SynchronizerException, FinderException, PersisterException {
        // attach dao to transaction manager to clear cache after commit
        attachDaoToTransactionManager();

        updateIntactObject(application, getIntactDao().getApplicationDao());
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void persistConfig(Application application) throws SynchronizerException, FinderException, PersisterException {
        // attach dao to transaction manager to clear cache after commit
        attachDaoToTransactionManager();

        persistIntactObject(application, getIntactDao().getApplicationDao());
    }

    private String getDbInfoValue(DbInfoDao infoDao, String key) {
        String value;
        uk.ac.ebi.intact.jami.model.meta.DbInfo dbInfo = infoDao.get(key);
        if (dbInfo != null) {
            value = dbInfo.getValue();
        } else {
            value = "<unknown>";
        }
        return value;
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public List<Source> getAvailableInstitutions() {
        return new ArrayList<Source>(getIntactDao().getSourceDao().getAll());
    }

    public List<Map.Entry<String,DataSource>> getDataSources() {
        return new ArrayList<Map.Entry<String,DataSource>>(
                ApplicationContextProvider.getApplicationContext().getBeansOfType(DataSource.class).entrySet());
    }

    public List<Map.Entry<String,PlatformTransactionManager>> getTransactionManagers() {
        return new ArrayList<Map.Entry<String,PlatformTransactionManager>>(
                ApplicationContextProvider.getApplicationContext().getBeansOfType(PlatformTransactionManager.class).entrySet());
    }

    public String[] getBeanNames() {
        return ApplicationContextProvider.getApplicationContext().getBeanDefinitionNames();
    }

    private void printDatabaseCounts(PrintStream ps) {

        ps.println("Publications: "+ getIntactDao().getPublicationDao().countAll());
        ps.println("Experiments: "+ getIntactDao().getExperimentDao().countAll());
        ps.println("Interactors: "+ getIntactDao().getInteractorDao(IntactInteractor.class).countAll());
        ps.println("\tInteractions: "+ getIntactDao().getInteractionDao().countAll());
        ps.println("\tComplexes: "+ getIntactDao().getComplexDao().countAll());
        ps.println("\tTotal Polymers: " + getIntactDao().getPolymerDao(IntactPolymer.class).countAll());
        ps.println("\t\tProteins: "+ getIntactDao().getProteinDao().countAll());
        ps.println("\t\tNucleic Acids: "+ getIntactDao().getNucleicAcidDao().countAll());
        ps.println("\tSmall molecules: " + getIntactDao().getBioactiveEntityDao().countAll());
        ps.println("\tTotal molecules: " + getIntactDao().getMoleculeDao().countAll());
        ps.println("\tInteractor pool: " + getIntactDao().getInteractorPoolDao().countAll());
        ps.println("Components: "+ getIntactDao().getParticipantEvidenceDao().countAll());
        ps.println("Features: "+ getIntactDao().getFeatureEvidenceDao().countAll());
        ps.println("Complex participants: "+ getIntactDao().getModelledParticipantDao().countAll());
        ps.println("Complex Features: "+ getIntactDao().getModelledFeatureDao().countAll());
        ps.println("CvObjects: "+ getIntactDao().getCvTermDao().countAll());
        ps.println("BioSources: "+ getIntactDao().getOrganismDao().countAll());
        ps.println("Institutions: "+ getIntactDao().getSourceDao().countAll());

    }

    public class ApplicationInfo implements Serializable {

        private String uniprotJapiVersion;
        private String schemaVersion;
        private String lastUniprotUpdate;
        private String lastCvUpdate;
        private String databaseCounts;

        private ApplicationInfo(String uniprotJapiVersion, String schemaVersion, String lastUniprotUpdate, String lastCvUpdate, String databaseCounts) {
            this.uniprotJapiVersion = uniprotJapiVersion;
            this.schemaVersion = schemaVersion;
            this.lastUniprotUpdate = lastUniprotUpdate;
            this.lastCvUpdate = lastCvUpdate;
            this.databaseCounts = databaseCounts;
        }

        public String getUniprotJapiVersion() {
            return uniprotJapiVersion;
        }

        public String getSchemaVersion() {
            return schemaVersion;
        }

        public String getLastUniprotUpdate() {
            return lastUniprotUpdate;
        }

        public String getLastCvUpdate() {
            return lastCvUpdate;
        }

        public String getDatabaseCounts() {
            return databaseCounts;
        }
    }
}
