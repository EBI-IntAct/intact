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

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SolrJettyRunner {

    private int port = 18080;

    private Server server;

    private boolean deleteIfExists;
    private File workingDir;
    private File solrHome;

    public SolrJettyRunner() {
        workingDir = new File(System.getProperty("java.io.tmpdir"), "solr-home-"+System.currentTimeMillis());

        try {
            FileUtils.forceDeleteOnExit(workingDir);
        } catch (IOException e) {
            throw new IllegalStateException("Problem forcing delete on exit for: "+workingDir);
        }
    }

    public SolrJettyRunner(File workingDir) {
        this(workingDir, false);
    }

    public SolrJettyRunner(File workingDir, boolean deleteIfExists) {
        this.workingDir = workingDir;
        this.deleteIfExists = deleteIfExists;

    }

    public void start() throws Exception {
        if (workingDir.exists() && deleteIfExists) {
            FileUtils.deleteDirectory(workingDir);
        }

        File solrWar;

        if (workingDir.exists()) {
            solrHome = new File(workingDir, "home");
            solrWar = new File(workingDir, "solr.war");

            if (!solrHome.exists()) {
                throw new IllegalStateException("Working dir "+workingDir+" exists, but no home/ directory could be found");
            }

            if (!solrWar.exists()) {
                throw new IllegalStateException("Working dir "+workingDir+" exists, but no solr.war folder could be found");
            }
        } else {
            SolrHomeBuilder solrHomeBuilder = new SolrHomeBuilder();
            solrHomeBuilder.install(workingDir);

            solrHome = solrHomeBuilder.getSolrHomeDir();
            solrWar = solrHomeBuilder.getSolrWar();
        }

        System.setProperty("solr.solr.home", solrHome.getAbsolutePath());

        server = new Server();

        Connector connector=new SelectChannelConnector();
        connector.setPort(Integer.getInteger("jetty.port",port).intValue());
        server.setConnectors(new Connector[]{connector});

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/solr");
        webapp.setWar(solrWar.getAbsolutePath());

        server.setHandler(webapp);

        server.start();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public File getSolrHome() {
       return solrHome; 
    }

    public String getSolrUrl() {
        return "http://localhost:"+port+"/solr";
    }

    public SolrServer getSolrServer() {
        try {
            return new CommonsHttpSolrServer(getSolrUrl());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL should be well formed", e);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
