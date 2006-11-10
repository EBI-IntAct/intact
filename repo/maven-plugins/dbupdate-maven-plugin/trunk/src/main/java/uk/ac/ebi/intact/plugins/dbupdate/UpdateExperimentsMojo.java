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
package uk.ac.ebi.intact.plugins.dbupdate;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.dbutil.update.UpdateExperiments;
import uk.ac.ebi.intact.dbutil.update.UpdateSingleExperimentReport;
import uk.ac.ebi.intact.dbutil.update.UpdatedValue;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Updates the experiments short labels and more
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @goal experiments
 *
 * @phase process-resources
 */
public class UpdateExperimentsMojo extends IntactHibernateMojo
{
    /**
     * @parameter default-value="${project.build.directory}/update-experiments-invalid.txt"
     */
    private File invalidFile;

    /**
     * @parameter default-value="${project.build.directory}/update-experiments-updated.txt"
     */
    private File updatedFile;

    private Writer invalidExpWriter;
    private Writer updatedExpWriter;

     /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
     {
         PrintStream ps = new PrintStream(getOutputFile());

         List<UpdateSingleExperimentReport> expReports = null;
         try
         {
             expReports = UpdateExperiments.startUpdate(ps);
         }
         catch (SQLException e)
         {
             e.printStackTrace();
         }

         // write experiments to files
         MojoUtils.prepareFile(invalidFile, true);
         MojoUtils.writeStandardHeaderToFile("Invalid", "Invalid experiments", getProject(), invalidFile);

         MojoUtils.prepareFile(updatedFile, true);
         MojoUtils.writeStandardHeaderToFile("Updated", "Updated experiments", getProject(), updatedFile);

         int i = 0;

         for (UpdateSingleExperimentReport report : expReports)
         {
             if (report.isInvalid())
             {
                 writeInvalidExp(report);
             }

             if (report.isUpdated())
             {
                 writeUpdatedExp(report);
             }

             // flush from time to time
             if (i > 0 && i % 20 == 0)
             {
                 getInvalidExpWriter().flush();
                 getUpdatedExpWriter().flush();
             }

             i++;
         }

         getInvalidExpWriter().close();
         getUpdatedExpWriter().close();
     }

    private Writer getInvalidExpWriter() throws IOException
    {
        if (invalidExpWriter == null)
        {
            invalidExpWriter = new FileWriter(invalidFile);
        }

        return invalidExpWriter;
    }

    private Writer getUpdatedExpWriter() throws IOException
    {
        if (updatedExpWriter == null)
        {
            updatedExpWriter = new FileWriter(updatedFile);
        }

        return updatedExpWriter;
    }

    private void writeInvalidExp(UpdateSingleExperimentReport report) throws IOException
    {
        String line = report.getExperimentAc()+"\t"+report.getInvalidMessage()+NEW_LINE;

        getInvalidExpWriter().write(line);
    }

    private void writeUpdatedExp(UpdateSingleExperimentReport report) throws IOException
    {
        String line = report.getExperimentAc()+"\t"+report.getInvalidMessage()+NEW_LINE;

        getUpdatedExpWriter().write(report.getExperimentAc()+" - "+report.getExperimentLabel()+NEW_LINE);

        if (report.isShortLabelUpdated())
            writeUpdatedLine("short-label", report.getShortLabelValue());
        if (report.isFullNameUpdated())
            writeUpdatedLine("full-name", report.getFullNameValue());
        if (report.isAuthorListUpdated())
            writeUpdatedLine("author-list", report.getAuthorListValue());
        if (report.isAuthorEmailUpdated())
            writeUpdatedLine("author-list-email", report.getAuthorEmailValue());
        if (report.isContactUpdated())
            writeUpdatedLine("contact", report.getContactListValue());
        if (report.isJournalUpdated())
            writeUpdatedLine("journal", report.getContactListValue());
        if (report.isYearUpdated())
            writeUpdatedLine("year", report.getContactListValue());
    }

    private void writeUpdatedLine(String item, UpdatedValue value) throws IOException
    {
        getUpdatedExpWriter().write("\t"+item+": "+value.toString()+NEW_LINE);
    }
}
