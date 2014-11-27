package uk.ac.ebi.intact.editor.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.editor.controller.BaseController;
import uk.ac.ebi.intact.editor.services.admin.ApplicationInfoService;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.meta.Application;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.annotation.Resource;
import javax.faces.event.ActionEvent;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller("applicationInfo")
@Lazy
public class ApplicationInfoController extends BaseController {

    private String uniprotJapiVersion;
    private String schemaVersion;
    private String lastUniprotUpdate;
    private String lastCvUpdate;
    private String databaseCounts;

    @Resource(name = "defaultApp")
    private Application application;

    @Autowired
    @Qualifier("applicationInfoService")
    private transient ApplicationInfoService applicationInfoService;

    private boolean isInitialised = false;

    public ApplicationInfoController() {
    }

    public void init() {
        ApplicationInfoService.ApplicationInfo appInfo = getApplicationInfoService().getCurrentApplicationInfo();
        uniprotJapiVersion = appInfo.getUniprotJapiVersion();
        schemaVersion = appInfo.getSchemaVersion();
        lastUniprotUpdate = appInfo.getLastUniprotUpdate();
        lastCvUpdate = appInfo.getLastCvUpdate();
        databaseCounts = appInfo.getDatabaseCounts();
        isInitialised = true;
    }

    public void saveApplicationProperties(ActionEvent evt) {
        try {
            getApplicationInfoService().saveApplicationProperties(this.application);
        } catch (SynchronizerException e) {
            addErrorMessage("Cannot save application details ", e.getCause() + ": " + e.getMessage());
        } catch (FinderException e) {
            addErrorMessage("Cannot save application details ", e.getCause() + ": " + e.getMessage());
        } catch (PersisterException e) {
            addErrorMessage("Cannot save application details ", e.getCause() + ": " + e.getMessage());
        }
    }

    public void persistConfig(ActionEvent evt) {
        try {
            getApplicationInfoService().persistConfig(this.application);
        } catch (SynchronizerException e) {
            addErrorMessage("Cannot save application details ", e.getCause()+": "+e.getMessage());
        } catch (FinderException e) {
            addErrorMessage("Cannot save application details ", e.getCause() + ": " + e.getMessage());
        } catch (PersisterException e) {
            addErrorMessage("Cannot save application details ", e.getCause() + ": " + e.getMessage());
        }
    }

    public List<Source> getAvailableInstitutions() {
        return getApplicationInfoService().getAvailableInstitutions();
    }

    public List<Map.Entry<String,DataSource>> getDataSources() {
        return getApplicationInfoService().getDataSources();
    }

    public List<Map.Entry<String,PlatformTransactionManager>> getTransactionManagers() {
        return getApplicationInfoService().getTransactionManagers();
    }

    public String[] getBeanNames() {
        return getApplicationInfoService().getBeanNames();
    }

    public String getUniprotJapiVersion() {
        if (!isInitialised){
           init();
        }
        return uniprotJapiVersion;
    }

    public List<String> getSystemPropertyNames() {
        return new ArrayList<String>(System.getProperties().stringPropertyNames());
    }

    public String getSystemProperty(String propName) {
        return System.getProperty(propName);
    }

    public String getSchemaVersion() {
        if (!isInitialised){
            init();
        }
        return schemaVersion;
    }

    public String getLastUniprotUpdate() {
        if (!isInitialised){
            init();
        }
        return lastUniprotUpdate;
    }

    public String getLastCvUpdate() {
        if (!isInitialised){
            init();
        }
        return lastCvUpdate;
    }

    public String getDatabaseCounts() {
        if (!isInitialised){
            init();
        }
        return databaseCounts;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationInfoService getApplicationInfoService() {
        if (this.applicationInfoService == null){
            this.applicationInfoService = ApplicationContextProvider.getBean("applicationInfoService");
        }
        return applicationInfoService;
    }
}
