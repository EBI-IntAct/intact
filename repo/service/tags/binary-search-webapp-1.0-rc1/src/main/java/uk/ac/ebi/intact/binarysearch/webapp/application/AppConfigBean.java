/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.binarysearch.webapp.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.binarysearch.webapp.SearchWebappException;
import uk.ac.ebi.intact.binarysearch.webapp.generated.SearchConfig;
import uk.ac.ebi.intact.binarysearch.webapp.util.WebappUtils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Application scope bean, with configuration stuff
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AppConfigBean implements Serializable {

    private Log log = LogFactory.getLog(AppConfigBean.class);

    public static final String DEFAULT_CONFIG_FILE_INIT_PARAM = "psidev.DEFAULT_CONFIG_FILE";
    public static final String DEFAULT_INDEX_LOCATION_INIT_PARAM = "psidev.DEFAULT_INDEX";

    private String absoluteContextPath;
    private HttpServletRequest request;
    private SearchConfig config;
    private String configFileLocation;

    private XrefLinkContext linkContext;

    public AppConfigBean() {
        FacesContext context = FacesContext.getCurrentInstance();
        this.request = (HttpServletRequest) context.getExternalContext().getRequest();

        this.absoluteContextPath = request.getScheme() + "://" +
                                   request.getServerName() + ":" +
                                   request.getServerPort() +
                                   request.getContextPath();

        configFileLocation = context.getExternalContext().getInitParameter(DEFAULT_CONFIG_FILE_INIT_PARAM);

        try {
            if (log.isInfoEnabled()) log.info("Trying to read configuration from: " + configFileLocation);
            config = WebappUtils.readConfiguration(configFileLocation);
        }
        catch (SearchWebappException e) {
            e.printStackTrace();
        }

        if (config == null) {
            if (log.isInfoEnabled()) log.info("No configuration File found. First time setup");
        }

        if (log.isInfoEnabled()) log.info("Initializing xref link context...");
        this.linkContext = XrefLinkContextFactory.createDefaultXrefLinkContext();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getAbsoluteContextPath() {
        return absoluteContextPath;
    }

    public SearchConfig getConfig() {
        return config;
    }

    public void setConfig(SearchConfig config) {
        this.config = config;
    }

    public XrefLinkContext getLinkContext() {
        return linkContext;
    }

    public void setLinkContext(XrefLinkContext linkContext) {
        this.linkContext = linkContext;
    }

    public String getConfigFileLocation() {
        return configFileLocation;
    }

    public void setConfigFileLocation(String configFileLocation) {
        this.configFileLocation = configFileLocation;
    }
}