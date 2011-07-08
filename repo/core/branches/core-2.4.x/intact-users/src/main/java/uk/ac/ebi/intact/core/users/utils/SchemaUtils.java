package uk.ac.ebi.intact.core.users.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.*;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.AbstractAuxiliaryDatabaseObject;
import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;
import java.util.Properties;

/**
 * Utlity to generate the DDL statement for a variety of RDMBS.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class SchemaUtils {

    private static final Log log = LogFactory.getLog(SchemaUtils.class);

    private SchemaUtils(){}

    /**
     * Generates the DDL schema
     * @param dialect the dialect to use (complete class name for the hibernate dialect object)
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDL(String dialect) {
        Properties props = new Properties();
        props.put( Environment.DIALECT, dialect);

        Configuration cfg = getBasicConfiguration(props);

        String[] sqls = cfg.generateSchemaCreationScript( Dialect.getDialect(props));
        addDelimiters(sqls);

        return sqls;
    }

    private static void addDelimiters(String[] sqls) {
        for (int i=0; i<sqls.length; i++) {
            sqls[i] = sqls[i]+";";
        }
    }

    private static Configuration getBasicConfiguration(Properties props) {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitName("intact-users-default");

        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(Dialect.getDialect(props).getClass().getName());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.afterPropertiesSet();

        Ejb3Configuration cfg = new Ejb3Configuration();
        Ejb3Configuration configured = cfg.configure(factoryBean.getPersistenceUnitInfo(), new HashMap());

        factoryBean.getNativeEntityManagerFactory().close();

        return configured.getHibernateConfiguration();
    }

    /**
     * Generates the DDL schema for Oracle 9i.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForOracle() {
        return generateCreateSchemaDDL( Oracle9iDialect.class.getName());
    }

    /**
     * Generates the DDL schema for PostgreSQL.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForPostgreSQL() {
        return generateCreateSchemaDDL( PostgreSQLDialect.class.getName());
    }

    /**
     * Generates the DDL schema for HSQL DB.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForHSQL() {
        return generateCreateSchemaDDL( HSQLDialect.class.getName());
    }

    /**
     * Generates the DDL schema for HSQL DB.
     * @return an array containing the SQL statements
     */
    public static String[] generateCreateSchemaDDLForH2() {
        return generateCreateSchemaDDL( H2Dialect.class.getName());
    }
}
