/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.config.IntactConfiguration;
import uk.ac.ebi.intact.core.config.SchemaVersion;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.context.IntactInitializationError;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.meta.DbInfo;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.core.persistence.dao.DbInfoDao;
import uk.ac.ebi.intact.core.persistence.dao.InstitutionDao;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;

/**
 * TODO write description of the class.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Component
public class IntactInitializer implements InitializingBean {

    @Autowired
    private IntactContext intactContext;

    @Autowired
    private IntactConfiguration configuration;

    @Autowired
    private SchemaVersion requiredSchemaVersion;

    @Autowired
    private PersisterHelper persisterHelper;
    
    @Autowired
    private DbInfoDao dbInfoDao;

    @Autowired
    private CvObjectDao cvObjectDao;

    @Autowired
    private InstitutionDao institutionDao;

    private static final Log log = LogFactory.getLog(IntactInitializer.class);

    public IntactInitializer() {
        
    }

    public void afterPropertiesSet() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Starting IntAct Core module");
            log.info("\tDefault institution: " + configuration.getDefaultInstitution());
            log.info("\tSchema version: " + requiredSchemaVersion);
        }

        checkSchemaCompatibility();
        persistInstitution();
        persistBasicCvObjects();
    }

    @Transactional
    public void persistInstitution() {
        Institution institution = institutionDao.getByShortLabel(configuration.getDefaultInstitution().getShortLabel());

        if (institution == null) {
            persisterHelper.save(configuration.getDefaultInstitution());
        } else {
            configuration.setDefaultInstitution(institution);
        }
    }

    @Transactional
    public void checkSchemaCompatibility() {

        DbInfo dbInfoSchemaVersion = dbInfoDao.get(DbInfo.SCHEMA_VERSION);

        SchemaVersion schemaVersion;

        if (dbInfoSchemaVersion == null) {
            log.info("Schema version does not exist. Will be created: " + requiredSchemaVersion);
            DbInfo dbInfo = new DbInfo(DbInfo.SCHEMA_VERSION, requiredSchemaVersion.toString());
            dbInfoDao.persist(dbInfo);
            return;
        }

        try {
            schemaVersion = SchemaVersion.parse(dbInfoSchemaVersion.getValue());
        }
        catch (Exception e) {
            throw new IntactInitializationError("Error parsing schema version", e);
        }

        if (!schemaVersion.isCompatibleWith(requiredSchemaVersion)) {
            throw new IntactInitializationError("Database schema version " + requiredSchemaVersion + " is required" +
                    " to use this version of intact-core. Schema version found: " + schemaVersion);
        }
    }

    @Transactional
     public void persistBasicCvObjects() {

        if (cvObjectDao.getByPsiMiRef(CvDatabase.INTACT_MI_REF) == null) {
            log.info("Persisting necessary CvObjects");

            CvDatabase intact = CvObjectUtils.createCvObject(intactContext.getInstitution(), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
            persisterHelper.save(intact);
        }
    }

}
