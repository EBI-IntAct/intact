/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dataexchange.psimi.solr.server;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.hupo.psi.mi.psicquic.model.server.SolrJettyRunner;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSolrJettyRunner extends SolrJettyRunner {

    private static Logger log = LoggerFactory.getLogger(IntactSolrHomeBuilder.class);


    public IntactSolrJettyRunner() {
        this(new File(System.getProperty("java.io.tmpdir"), "solr-home-"+System.currentTimeMillis()));
    }

    public IntactSolrJettyRunner(File workingDir) {
        super(workingDir);
    }

    @Override
    public void start() throws Exception {
        File solrWar;

        if (workingDir.exists()) {
            solrHome = new File(workingDir, "home");
            solrWar = new File(workingDir, "solr.war");

            if (!solrHome.exists()) {
                throw new IllegalStateException("Working dir "+workingDir+" exists, but no solr-home/ directory could be found");
            }

            if (!solrWar.exists()) {
                throw new IllegalStateException("Working dir "+workingDir+" exists, but no solr.war folder could be found");
            }

            if (log.isDebugEnabled()) log.debug("Using existing directory");

        } else {
            IntactSolrHomeBuilder solrHomeBuilder = new IntactSolrHomeBuilder();

            solrHomeBuilder.install(workingDir);

            solrHome = solrHomeBuilder.getSolrHomeDir();
            solrWar = solrHomeBuilder.getSolrWar();
        }

        System.setProperty("solr.solr.home", solrHome.getAbsolutePath());

        server = new Server();

        Connector connector=new SelectChannelConnector();
        connector.setPort(Integer.getInteger("jetty.port",getPort()).intValue());
        server.setConnectors(new Connector[]{connector});

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/solr");
        webapp.setWar(solrWar.getAbsolutePath());
        webapp.setTempDirectory(workingDir);

        server.setHandler(webapp);

        server.start();
    }

    public String getSolrUrl(String coreName) {
        return "http://localhost:"+getPort()+"/solr/"+coreName;
    }

    public HttpSolrServer getSolrServer(String coreName) {
        solrServer = new HttpSolrServer(getSolrUrl(coreName), createHttpClient());

        solrServer.setConnectionTimeout(5000);
        solrServer.setSoTimeout(5000);
        solrServer.setAllowCompression(true);

        return solrServer;
    }

    public HttpSolrServer getSolrServerNoTimeOut(String coreName) {
        solrServer = new HttpSolrServer(getSolrUrl(coreName), createHttpClient());
        solrServer.setAllowCompression(true);

        return solrServer;
    }

    public ConcurrentUpdateSolrServer getStreamingSolrServer(String coreName) {
        try {
            return new ConcurrentUpdateSolrServer(getSolrUrl(coreName).toString(), 4, 4);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL should be well formed", e);
        }
    }
}
