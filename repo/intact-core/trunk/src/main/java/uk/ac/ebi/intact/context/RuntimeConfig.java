/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.PropertyLoader;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.config.DataConfig;

import java.util.*;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class RuntimeConfig implements Serializable
{
    private static final Log log = LogFactory.getLog(RuntimeConfig.class);

    private static final String APPLICATION_PARAM_NAME = RuntimeConfig.class.getName()+".CONFIG";

    private Institution institution;
    private String acPrefix;
    private final Map<String, DataConfig> dataConfigs;

    private RuntimeConfig()
    {
        this.dataConfigs = new HashMap<String,DataConfig>();
    }

    public static RuntimeConfig getCurrentInstance(IntactSession session)
    {
        RuntimeConfig runtimeConfig
                = (RuntimeConfig)session.getApplicationAttribute(APPLICATION_PARAM_NAME);
        if (runtimeConfig == null)
        {
            log.debug("Creating new RuntimeConfig");
            runtimeConfig = new RuntimeConfig();
            session.setApplicationAttribute(APPLICATION_PARAM_NAME, runtimeConfig);
        }
        return runtimeConfig;
    }

     public Institution getInstitution() throws IntactException
     {
        return institution;
     }

    public void setInstitution(Institution institution)
    {
        this.institution = institution;
    }

    public String getAcPrefix()
    {
        return acPrefix;
    }

    public void setAcPrefix(String acPrefix)
    {
        this.acPrefix = acPrefix;
    }


    public Collection<DataConfig> getDataConfigs()
    {
        return dataConfigs.values();
    }

    public DataConfig getDataConfig(String name)
    {
        return dataConfigs.get(name);
    }

    public void addDataConfig(DataConfig dataConfig)
    {
        if (!dataConfig.isInitialized())
        {
            throw new IllegalArgumentException("DataConfig added to RuntimeConfig must be already initialized: "+dataConfig.getName());
        }

        this.dataConfigs.put(dataConfig.getName(), dataConfig);
    }

}
