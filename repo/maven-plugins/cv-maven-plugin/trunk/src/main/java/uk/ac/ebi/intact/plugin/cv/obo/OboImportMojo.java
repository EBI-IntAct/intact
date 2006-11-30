/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.plugin.cv.obo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.*;

import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;
import uk.ac.ebi.intact.dbutil.cv.UpdateCVs;
import uk.ac.ebi.intact.dbutil.cv.PsiLoaderException;
import uk.ac.ebi.intact.dbutil.cv.UpdateCVsReport;
import uk.ac.ebi.intact.dbutil.cv.model.CvTerm;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * Export an OBO file from the provided database in the hibernateConfig file
 *
 * @goal obo-imp
 *
 * @phase process-resources
 */
public class OboImportMojo
        extends IntactHibernateMojo
{
    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

     /**
     * @parameter expression="${project.build.directory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;

    /**
     * @parameter expression="${intact.obo}"
     * @required
     */
    private File importedOboFile;

    /**
     * @parameter expression="${project.build.directory}/updatedTerms.txt"
     * @required
     */
    private File updatedTermsFile;

    /**
     * @parameter expression="${project.build.directory}/createdTerms.txt"
     * @required
     */
    private File createdTermsFile;

    /**
     * @parameter expression="${project.build.directory}/obsoleteTerms.txt"
     * @required
     */
    private File obsoleteTermsFile;

    /**
     * @parameter expression="${project.build.directory}/orphanTerms.txt"
     * @required
     */
    private File orphanTermsFile;

    /**
     * @parameter expression="${project.build.directory}/ontology.txt"
     * @required
     */
    private File ontologyFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if (!importedOboFile.exists())
        {
            throw new MojoExecutionException("OBO file to import does not exist: "+importedOboFile);
        }

        UpdateCVsReport report = null;

        PrintStream output = getOutputPrintStream();

        try
        {
            report = UpdateCVs.load(importedOboFile, output);
        }
        catch (PsiLoaderException e)
        {
            throw new MojoExecutionException("Problem importing OBO file", e);
        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        writeUpdatedTermsFile(report);
        writeCreatedTermsFile(report);
        writeObsoleteTermsFile(report);
        writeOrphanTermsFile(report);
        writeOntologyFile(report);
    }

    private void writeUpdatedTermsFile(UpdateCVsReport report) throws IOException
    {
        MojoUtils.prepareFile(updatedTermsFile);
        MojoUtils.writeStandardHeaderToFile("Updated terms", "CvObjects updated", getProject(), updatedTermsFile);

        Writer writer = new FileWriter(updatedTermsFile, true);

        writer.write("# Terms: "+report.getUpdatedTerms().size()+NEW_LINE+NEW_LINE);

        for (CvObject cv : report.getUpdatedTerms())
        {
            writer.write(cv.getAc()+"\t"+cv.getShortLabel()+NEW_LINE);
        }

        writer.close();
    }

    private void writeCreatedTermsFile(UpdateCVsReport report) throws IOException
    {
        MojoUtils.prepareFile(createdTermsFile);
        MojoUtils.writeStandardHeaderToFile("Created terms", "New CvObjects from the OBO file that has been created",
                getProject(), createdTermsFile);

        Writer writer = new FileWriter(createdTermsFile, true);

        writer.write("# Terms: "+report.getCreatedTerms().size()+NEW_LINE+NEW_LINE);

        for (CvObject cv : report.getCreatedTerms())
        {
            writer.write(cv.getAc()+"\t"+cv.getShortLabel()+NEW_LINE);
        }

        writer.close();
    }

    private void writeObsoleteTermsFile(UpdateCVsReport report) throws IOException
    {
        MojoUtils.prepareFile(obsoleteTermsFile);
        MojoUtils.writeStandardHeaderToFile("Obsolete terms", "Obsolete terms", getProject(), obsoleteTermsFile);

        Writer writer = new FileWriter(obsoleteTermsFile, true);

        writer.write("# Terms: "+report.getObsoleteTerms().size()+NEW_LINE+NEW_LINE);

        for (CvTerm cv : report.getObsoleteTerms())
        {
            writer.write(cv.getId()+"\t"+cv.getShortName()+NEW_LINE);
        }

        writer.close();
    }
    
    private void writeOrphanTermsFile(UpdateCVsReport report) throws IOException
    {
        MojoUtils.prepareFile(orphanTermsFile);
        MojoUtils.writeStandardHeaderToFile("Orphan terms", "The list of terms below could not be added to your IntAct node. " +
                "These terms are obsolete in PSI-MI and the ontology doesn't keep track of the root of obsolete terms." +
                " Solution: if you really want to add these terms into IntAct, you will have to do it manually and make " +
                "sure that they get their MI:xxxx.", getProject(), orphanTermsFile);

        Writer writer = new FileWriter(orphanTermsFile, true);

        writer.write("# Terms: "+report.getOrphanTerms().size()+NEW_LINE+NEW_LINE);

        for (CvTerm cv : report.getOrphanTerms())
        {
            writer.write(cv.getId()+"\t"+cv.getShortName()+NEW_LINE);
        }

        writer.close();
    }

    private void writeOntologyFile(UpdateCVsReport report) throws IOException
    {
        MojoUtils.prepareFile(ontologyFile);
        MojoUtils.writeStandardHeaderToFile("Ontology", "List of terms from the OBO file: "+importedOboFile,
                getProject(), ontologyFile);

        PrintStream ps = new PrintStream(ontologyFile);
        report.getOntology().print(ps);
    }

    public File getImportedOboFile()
    {
        return importedOboFile;
    }

    public void setImportedOboFile(File importedOboFile)
    {
        this.importedOboFile = importedOboFile;
    }

    public MavenProject getProject()
    {
        return project;
    }

    public File getHibernateConfig()
    {
        return hibernateConfig;
    }

    public File getUpdatedTermsFile()
    {
        return updatedTermsFile;
    }

    public File getCreatedTermsFile()
    {
        return createdTermsFile;
    }

    public File getObsoleteTermsFile()
    {
        return obsoleteTermsFile;
    }

    public File getOrphanTermsFile()
    {
        return orphanTermsFile;
    }

    public File getOntologyFile()
    {
        return ontologyFile;
    }
}
