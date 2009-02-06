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

import org.mortbay.jetty.Server;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import java.io.File;
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

    private File solrTemplate;
    private File solrWar;
    private File workingDir;

    public SolrJettyRunner(File solrTemplate, File solrWar, File workingDir) {
        this.solrTemplate = solrTemplate;
        this.solrWar = solrWar;
        this.workingDir = workingDir;
    }

    public void start() throws Exception {
        File solrParentFolder = new File(workingDir, "solr-home"+System.currentTimeMillis());
        solrParentFolder.mkdirs();

        File solrHome = new File(solrParentFolder, "home");

        FileUtils.copyDirectoryToDirectory(solrTemplate, solrParentFolder);

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
