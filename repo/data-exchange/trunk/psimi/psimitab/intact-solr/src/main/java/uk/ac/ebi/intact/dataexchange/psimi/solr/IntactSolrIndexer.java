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
package uk.ac.ebi.intact.dataexchange.psimi.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;

import java.io.*;

/**
 * Indexes information into a SOLR server
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSolrIndexer {

    private SolrServer solrServer;
    private SolrDocumentConverter converter;

    public IntactSolrIndexer(SolrServer solrServer) {
        this.solrServer = solrServer;
        this.converter = new SolrDocumentConverter(new IntactDocumentDefinition());
    }

    public int indexMitab(File mitabFile, boolean hasHeader) throws IOException, IntactSolrException {
        return indexMitab(new FileInputStream(mitabFile), hasHeader);
    }

    public int indexMitabFromClasspath(String resourceUrl, boolean hasHeader) throws IOException, IntactSolrException {
        InputStream resourceStream = IntactSolrIndexer.class.getResourceAsStream(resourceUrl);

        if (resourceStream == null) throw new IntactSolrException("Resource not found in the classpath: "+resourceUrl);

        return indexMitab(resourceStream, hasHeader);
    }

    /**
     * Indexes a MITAB formatted input stream into the database.
     * @param mitabStream The stream to index
     * @param hasHeader Whether the data has header or not
     * @return Count of indexed lines
     * @throws IOException Thrown if there is a problem reading the stream
     * @throws IntactSolrException Thrown if there is a problem indexing the data
     */
    public int indexMitab(InputStream mitabStream, boolean hasHeader) throws IOException, IntactSolrException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(mitabStream));

        return indexMitab(reader, hasHeader);
    }

    /**
     * Indexes MITAB data using a Reader into the database.
     * @param reader The reader to use
     * @param hasHeader Whether the data has header or not
     * @return Count of indexed lines
     * @throws IOException Thrown if there is a problem reading
     * @throws IntactSolrException Thrown if there is a problem indexing the data
     */
    private int indexMitab(BufferedReader reader, boolean hasHeader) throws IOException, IntactSolrException {
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (hasHeader && lineNumber == 1) continue;
            
            SolrInputDocument inputDocument = converter.toSolrDocument(line);

            try {
                solrServer.add(inputDocument);
            } catch (SolrServerException e) {
                throw new IntactSolrException("Problem processing line "+lineNumber+": "+line, e);
            }

            if (lineNumber > 0 && lineNumber % 100 == 0) {
                commitSolr(false);
            }
        }

        commitSolr(true);

        return lineNumber-1;
    }

    private void commitSolr(boolean optimize) throws IOException, IntactSolrException {
        try {
            solrServer.commit();
            if (optimize) solrServer.optimize();
        } catch (SolrServerException e) {
            throw new IntactSolrException("Problem during commit", e);
        }
    }

}
