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
package uk.ac.ebi.intact.plugins.ontology.index;

import uk.ac.ebi.intact.plugin.IntactAbstractMojo;
import uk.ac.ebi.intact.bridges.ontologies.util.OntologyUtils;
import uk.ac.ebi.intact.bridges.ontologies.OntologyMapping;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;


/**
 * Goal which creates a Lucene Index for a given set of ontologies
 *
 * @goal ontologyindex
 * @phase process-sources
 */
public class OntologyIndexerMojo extends IntactAbstractMojo {

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;


    /**
     * Location of the output ontologyindex file.
     *
     * @parameter expression="${intact.ontology.index.directory}"
     * @required
     */
    private File indexDirectory;



    /**
     * If true, create a new index. If false, update the existing index. Default: true
     *
     * @parameter expression="${intact.ontology.index.create}" default-value="true"
     */
    private boolean createIndex = true;


    /**
     * OntologyMap, the key should be the ontology name and value the url,
     * as given in ontologyindex-config
     *
     * @parameter
     * @required
     */
    private Map ontologyMap;

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        enableLogging();

        try {
            OntologyMapping[] mappings = createOntologyMap();
            
            Directory ontologiesIndex = FSDirectory.getDirectory( indexDirectory );
            OntologyUtils.buildIndexFromObo( ontologiesIndex, mappings, createIndex );

        } catch ( Exception e ) {
            throw new MojoExecutionException( "Failed ontology-index creation", e );
        }

    }

    /**
     * Creates an Array of OntologyMapping objects from the ontologyindex-config
     *
     * @return OntologyMapping[]
     * @throws MojoExecutionException handled by MojoExecutionException
     * @throws MojoFailureException   handled by   MojoFailureException
     */
    private OntologyMapping[] createOntologyMap() throws MojoExecutionException, MojoFailureException {

        try {
            List<OntologyMapping> mappingsList = new ArrayList<OntologyMapping>();
            for ( Object ontology : ontologyMap.keySet() ) {
                String ontologyName = ontology.toString();
                String oboUrl = ( String ) ontologyMap.get( ontologyName );
                mappingsList.add( new OntologyMapping( ontologyName, new URL( oboUrl ) ) );
            }

            OntologyMapping[] mappingArray = new OntologyMapping[ontologyMap.size()];
            mappingsList.toArray( mappingArray );
            return mappingArray;

        } catch ( MalformedURLException e ) {
            throw new MojoExecutionException( "Failed to create ontology map", e );
        }

    }


    public boolean isCreateIndex() {
        return createIndex;
    }

    public Map getOntologyMap() {
        return ontologyMap;
    }

    public File getIndexDirectory() {
        return indexDirectory;
    }

    public MavenProject getProject() {
        return project;
    }
}
