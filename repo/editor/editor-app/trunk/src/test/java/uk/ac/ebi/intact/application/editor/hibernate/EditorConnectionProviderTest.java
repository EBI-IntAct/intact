/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.editor.hibernate;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import uk.ac.ebi.intact.application.commons.context.IntactContext;
import uk.ac.ebi.intact.application.editor.LoginPropertiesGetter;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.util.HibernateUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Jul-2006</pre>
 */
public class EditorConnectionProviderTest extends TestCase
{

    private static final Log log = LogFactory.getLog(EditorConnectionProviderTest.class);

    public void testDaoAccess()
    {
        String url = HibernateUtil.getConfiguration().getProperty(Environment.URL);

        LoginPropertiesGetter props = new LoginPropertiesGetter();
        String name = props.getName();
        String password = props.getPassword();


        log.info("Testing data access for USER_1");

        IntactContext.getCurrentInstance().getUserContext().setUserId("USER_1");

        try
        {
            Connection c = DriverManager.getConnection(url, name, password);
            IntactContext.getCurrentInstance().getUserContext().setConnection(c);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        IntactTransaction tx = DaoFactory.beginTransaction();

        Protein p = DaoFactory.getProteinDao().getByAc("EBI-493");
        assertNotNull(p);

        tx.commit();

        log.info("Testing data access for USER_2");

        IntactContext.getCurrentInstance().getUserContext().setUserId("USER_2");

        try
        {
            Connection c = DriverManager.getConnection(url, name, password);
            IntactContext.getCurrentInstance().getUserContext().setConnection(c);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        tx = DaoFactory.beginTransaction();

        p = DaoFactory.getProteinDao().getByAc("EBI-493");
        assertNotNull(p);

        tx.commit();
    }

}
