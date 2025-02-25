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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.intact.core.config.IntactConfiguration;
import uk.ac.ebi.intact.core.config.SchemaVersion;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.DbInfoDao;
import uk.ac.ebi.intact.core.persistence.dao.InstitutionDao;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.meta.DbInfo;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Intact database initializer.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactInitializer implements ApplicationContextAware{

    @Autowired
    private IntactContext intactContext;

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

    @Autowired
    private ApplicationContext applicationContext;

    private boolean autoPersist = true;

    private static final Log log = LogFactory.getLog(IntactInitializer.class);

    public IntactInitializer() {
    }

    public void init() {
        this.configuration = (IntactConfiguration) applicationContext.getBean("intactConfig");

        Assert.notNull(configuration, "An IntactConfiguration must exist in the context");


        Institution defaultInstitution = getDefaultInstitution(configuration);

        if (log.isInfoEnabled()) {
            log.info("Starting IntAct Core module");
            log.info("\tDefault institution: " + defaultInstitution);
            log.info("\tSchema version:      " + requiredSchemaVersion);
            log.info("\tAutopersist:         " + autoPersist);
            log.info("\tConfiguration:       " + configuration);

            Map<String, DataSource> datasourceMap = intactContext.getSpringContext().getBeansOfType(DataSource.class);
            log.info("\tDataSources("+ datasourceMap.size() +"):");
            for ( Map.Entry<String, DataSource> e : datasourceMap.entrySet() ) {
                log.info("\t\t" + e.getKey() + ": " + printDataSource( (DataSource) e.getValue() ));
            }
        }

        if (!configuration.isSkipSchemaCheck()) {
            checkSchemaCompatibility();
        }
        
        persistInstitution(defaultInstitution, true);

        // persist all institutions
        Map<String,Institution> institutionMap = applicationContext.getBeansOfType(Institution.class);

        if (isAutoPersist()) {
            for (Institution institution : institutionMap.values()) {
                persistInstitution(institution, false);
            }

            persistBasicCvObjects();
        }
    }

    private String printDataSource( DataSource ds ) {
        StringBuilder sb = new StringBuilder( 256 );

        try {
            if (PropertyUtils.isReadable(ds, "url")) {
                sb.append( "url: '" ).append( PropertyUtils.getSimpleProperty( ds, "url" ) ).append("', ");
                sb.append( "username: '" ).append( PropertyUtils.getSimpleProperty( ds, "username" ) ).append("'");
            } else if (PropertyUtils.isReadable( ds, "dataSource" )) {
                return printDataSource( (DataSource) PropertyUtils.getProperty(ds, "dataSource") );
            }

        } catch ( Exception e ) {
            log.warn( "Exception while trying to print " + ds.getClass().getSimpleName(), e );
        }
        return sb.toString();
    }

    private Institution getDefaultInstitution(IntactConfiguration configuration) {
        if (configuration.getDefaultInstitution() != null) {
            return configuration.getDefaultInstitution();
        }

        return (Institution) applicationContext.getBean("institutionUndefined");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistInstitution(Institution candidateInstitution, boolean isDefault) {
        Institution institution = institutionDao.getByShortLabel(candidateInstitution.getShortLabel());

        if (institution == null && isAutoPersist()) {
            if (log.isDebugEnabled()) log.debug("Persisting institution: "+candidateInstitution);
            persisterHelper.save(candidateInstitution);
        } else if (isDefault) {
            if (institution == null) {
                institution = candidateInstitution;
            }

            configuration.setDefaultInstitution(institution);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkSchemaCompatibility() {

        if( ! configuration.isSkipSchemaCheck() ) {

            DbInfo dbInfoSchemaVersion = dbInfoDao.get(DbInfo.SCHEMA_VERSION);

            SchemaVersion schemaVersion;

            if (dbInfoSchemaVersion == null ) {

                if( ! autoPersist  ) {
                    throw new IntactInitializationError( "Could not find key "+ DbInfo.SCHEMA_VERSION +" in table IA_DB_INFO, please set IntactInitializer.autoPersist to true so it can be initialized at startup." );
                }

                if ( log.isInfoEnabled() ) log.info( "Schema version does not exist. Will be created: " + requiredSchemaVersion);
                DbInfo dbInfo = new DbInfo(DbInfo.SCHEMA_VERSION, requiredSchemaVersion.toString());
                dbInfoDao.persist(dbInfo);

            } else {

                try {
                    schemaVersion = SchemaVersion.parse(dbInfoSchemaVersion.getValue());
                } catch ( Exception e) {
                    throw new IntactInitializationError("Error parsing schema version", e);
                }

                if (!schemaVersion.isCompatibleWith(requiredSchemaVersion)) {
                    throw new IntactInitializationError("Database schema version " + requiredSchemaVersion + " is required" +
                                                        " to use this version of intact-core. Schema version found: " + schemaVersion);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
     public void persistBasicCvObjects() {

        if (isAutoPersist() && cvObjectDao.getByPsiMiRef(CvDatabase.INTACT_MI_REF) == null) {
            log.info("Persisting necessary CvObjects");

            CvDatabase intact = CvObjectUtils.createCvObject(configuration.getDefaultInstitution(), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
            persisterHelper.save(intact);
        }
    }

    public boolean isAutoPersist() {
        return autoPersist;
    }

    public void setAutoPersist(boolean autoPersist) {
        this.autoPersist = autoPersist;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
