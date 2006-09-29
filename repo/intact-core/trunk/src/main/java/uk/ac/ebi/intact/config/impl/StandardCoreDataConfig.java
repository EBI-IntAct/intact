/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.event.IntactObjectEventListener;
import uk.ac.ebi.intact.model.meta.DbInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public class StandardCoreDataConfig extends AbstractHibernateDataConfig
{

    private static final Log log = LogFactory.getLog(StandardCoreDataConfig.class);

    public static final String NAME = "uk.ac.ebi.intact.config.STANDARD_CORE";

    public StandardCoreDataConfig(IntactSession session)
    {
        super(session);
    }

    public String getName()
    {
        return NAME;
    }

    protected List<String> getPackagesWithEntities()
    {
        List<String> packages = new ArrayList<String>(1);

        // /uk/ac/ebi/intact/model
        packages.add(Interactor.class.getPackage().getName());
        packages.add(DbInfo.class.getPackage().getName());

        return packages;
    }


    @Override
    public Configuration getConfiguration()
    {
        Configuration configuration = super.getConfiguration();
        configuration.setListener("pre-insert", new IntactObjectEventListener());
        configuration.setListener("pre-update", new IntactObjectEventListener());
        return configuration;
    }

    protected File getConfigFile()
    {
        // uses the default file in the classpath (/hibernate.cfg.xml)
        return null;
    }
}
