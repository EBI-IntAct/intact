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
package uk.ac.ebi.intact.task.mitab.index;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import uk.ac.ebi.intact.bridges.ontologies.OntologyMapping;
import uk.ac.ebi.intact.dataexchange.psimi.solr.ontology.OntologyIndexer;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OntologyPopulatorTasklet implements Tasklet{

    private Resource ontologiesSolrUrl;
    private List<OntologyMapping> oboOntologyMappings;
    private boolean indexUniprotTaxonomy;

    private boolean clearIndex = true;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (ontologiesSolrUrl == null) {
            throw new NullPointerException("ontologiesSolrUrl is null");
        }
        SolrServer ontologiesSolrServer = new CommonsHttpSolrServer(ontologiesSolrUrl.getURL());

        if (clearIndex) {
            ontologiesSolrServer.deleteByQuery("*:*");
            ontologiesSolrServer.commit();
            ontologiesSolrServer.optimize();
        }

        OntologyIndexer ontologyIndexer = new OntologyIndexer(ontologiesSolrServer);

        if (indexUniprotTaxonomy) {
            ontologyIndexer.indexUniprotTaxonomy();
        }

        ontologyIndexer.indexObo(oboOntologyMappings.toArray(new OntologyMapping[oboOntologyMappings.size()]));

        return RepeatStatus.FINISHED;
    }

    public void setOntologiesSolrUrl(Resource ontologiesSolrUrl) {
        this.ontologiesSolrUrl = ontologiesSolrUrl;
    }

    public void setOboOntologyMappings(List<OntologyMapping> oboOntologyMappings) {
        this.oboOntologyMappings = oboOntologyMappings;
    }

    public void setIndexUniprotTaxonomy(boolean indexUniprotTaxonomy) {
        this.indexUniprotTaxonomy = indexUniprotTaxonomy;
    }

    public void setClearIndex(boolean clearIndex) {
        this.clearIndex = clearIndex;
    }
}
