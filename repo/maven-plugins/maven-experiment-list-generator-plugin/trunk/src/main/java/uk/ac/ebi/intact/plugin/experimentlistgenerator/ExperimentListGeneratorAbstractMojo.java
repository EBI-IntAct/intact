/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.experimentlistgenerator;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;

import java.io.File;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:$
 * @since <pre>04/08/2006</pre>
 */
public abstract class ExperimentListGeneratorAbstractMojo extends AbstractMojo
{

    /**
    * Project instance
    * @parameter default-value="${project}"
    * @readonly
    */
    protected MavenProject project;

    /**
    * File containing the species
    * @parameter default-value="target/experiment-list"
    * @required
    */
    protected File targetPath;

    /**
    * File containing the species
    * @parameter default-value="classification_by_species.txt"
    */
    protected String speciesFilename;

    /**
    * File containing the publications
    * @parameter default-value="classification_by_publications.txt"
    */
    protected String publicationsFilename;

    /**
    * File containing the publications
    * @parameter default-value="%"
    */
    protected String searchPattern;

    /**
     * If true, all experiment without a PubMed ID (primary-reference) will be filtered out.
     *
     * @parameter default-value="true"
     */
    protected boolean onlyWithPmid;

    /**
     * Whether to update the existing project files or overwrite them.
     *
     * @parameter expression="${overwrite}" default-value="false"
     */
    protected boolean overwrite;


    protected File getSpeciesFile()
    {
        return new File(targetPath, speciesFilename);
    }

    protected File getPublicationsFile()
    {
        return new File(targetPath, publicationsFilename);
    }

}
