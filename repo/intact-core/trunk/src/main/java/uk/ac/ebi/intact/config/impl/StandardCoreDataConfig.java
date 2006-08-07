/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import uk.ac.ebi.intact.model.Interactor;

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

    public String getName()
    {
        return NAME;
    }

    protected List<String> getPackagesWithEntities()
    {
        List<String> packages = new ArrayList<String>(1);

        // /uk/ac/ebi/intact/model
        packages.add(Interactor.class.getPackage().getName());

        return packages;
    }

    protected File getConfigFile()
    {
        // uses the default file in the classpath (/hibernate.cfg.xml)
        return null;
    }
}
