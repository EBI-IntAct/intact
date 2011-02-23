/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.plugins.psimitab.index;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import uk.ac.ebi.intact.plugin.IntactAbstractMojo;
import uk.ac.ebi.intact.psimitab.PsimitabTools;
import uk.ac.ebi.intact.psimitab.search.IntactPsimiTabIndexWriter;
import psidev.psi.mi.search.index.PsimiIndexWriter;

/**
 * Goal which creates a Lucene index from a PSIMITAB file.
 *
 * @goal index
 * @phase process-sources
 */
public class IndexerMojo extends IntactAbstractMojo {

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * Location of the output PSIMITAB file.
     *
     * @parameter expression="${intact.psimitab.index.directory}"
     * @required
     */
    private File indexDirectory;

    /**
     * A list of file/directories to convert into a single PSIMITAB file.
     *
     * @parameter expression="${intact.psimitab.index.file}"
     * @required
     */
    private File psimitabFile;

    /**
     * If true, create a new index. If false, update the existing index. Default: true
     *
     * @parameter expression="${intact.psimitab.index.create}" default-value="true"
     */
    private boolean createIndex = true;

    /**
     * If true, the PSIMITAB file contains a header. Default: true
     *
     * @parameter expression="${intact.psimitab.index.containsHeader}" default-value="true"
     */
    private boolean containsHeader = true;
        
    /**
     * A class that extends psidev.psi.mi.search.index.AbstractIndexWriter and has an empty constructor
     *
     * @parameter expression="${intact.psimitab.index.builder}" default-value="uk.ac.ebi.intact.psimitab.search.IntActPsimiTabIndexWriter"
     */
    private String indexWriter = IntactPsimiTabIndexWriter.class.getName();
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        enableLogging();

        // instantiate the writer
        PsimiIndexWriter indexWriterInstance = null;
        try {
            Class<?> indexWriterClass = Class.forName(indexWriter);
            indexWriterInstance = (PsimiIndexWriter) indexWriterClass.newInstance();
        } catch (Throwable e) {
            throw new MojoExecutionException("Failed index creation", e);
        }

        try {
            PsimitabTools.buildIndex(indexDirectory, psimitabFile, createIndex, containsHeader, indexWriterInstance);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed index creation", e);
        }
    }

    /**
     * Getter for property 'containsHeader'.
     *
     * @return Value for property 'containsHeader'.
     */
    public boolean isContainsHeader() {
        return containsHeader;
    }

    /**
     * Getter for property 'createIndex'.
     *
     * @return Value for property 'createIndex'.
     */
    public boolean isCreateIndex() {
        return createIndex;
    }

    /**
     * Getter for property 'indexDirectory'.
     *
     * @return Value for property 'indexDirectory'.
     */
    public File getIndexDirectory() {
        return indexDirectory;
    }

    /**
     * Getter for property 'psimitabFile'.
     *
     * @return Value for property 'psimitabFile'.
     */
    public File getPsimitabFile() {
        return psimitabFile;
    }

    public MavenProject getProject() {
        return project;
    }

}