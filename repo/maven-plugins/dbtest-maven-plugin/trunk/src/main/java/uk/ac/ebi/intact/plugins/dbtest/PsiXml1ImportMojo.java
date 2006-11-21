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
package uk.ac.ebi.intact.plugins.dbtest;

import uk.ac.ebi.intact.plugin.IntactAbstractMojo;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugins.hibernateconfig.HibernateConfigCreatorMojo;
import uk.ac.ebi.intact.plugins.dbtest.xmlimport.Imports;
import uk.ac.ebi.intact.plugins.dbtest.xmlimport.XmlFileset;
import uk.ac.ebi.intact.util.Utilities;
import uk.ac.ebi.intact.util.protein.UpdateProteins;
import uk.ac.ebi.intact.util.protein.UpdateProteinsI;
import uk.ac.ebi.intact.util.protein.BioSourceFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.ControlledVocabularyRepository;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.EntrySetChecker;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.EntrySetParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.persister.EntrySetPersister;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.EntrySetTag;
import uk.ac.ebi.intact.context.IntactContext;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.hibernate.SessionFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.net.URL;
import java.sql.SQLException;

/**
 * Import a psi xml 1 into the database
 * @goal import-psi1
 */
public class PsiXml1ImportMojo
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
     * Files to import
     *
     * @parameter
     * @required
     */
    private Imports imports;

    protected void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException
    {
        for (XmlFileset fileset : imports.getXmlFilesets())
        {
            if (fileset.getVersion() == null)
            {
                throw new MojoExecutionException("All xmlFilesets must contain a <version> element");
            }

            if (fileset.getVersion().equals("1"))
            {
                for (String url : fileset.getUrls())
                {
                    getLog().debug("To import: "+url);

                    try
                    {
                        importUrl(url);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new MojoExecutionException("Could import file: "+url, e);
                    }
                }
            }

        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        // close the connection
        // TODO to be replaced by  IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().closeSessionFactory();
        SessionFactory sf = (SessionFactory) IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().getSessionFactory();
        sf.close();
    }

    private void importUrl(String str) throws Exception
    {
        URL url = new URL(str);
        InputStream xmlStream = url.openStream();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        MessageHolder messages = MessageHolder.getInstance();

        // Parse the PSI file
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse( xmlStream );
        Element rootElement = document.getDocumentElement();

        EntrySetParser entrySetParser = new EntrySetParser();
        EntrySetTag entrySet = entrySetParser.process( rootElement );

        UpdateProteinsI proteinFactory = new UpdateProteins();
        BioSourceFactory bioSourceFactory = new BioSourceFactory();

        ControlledVocabularyRepository.check( );

        // check the parsed model
        EntrySetChecker.check(entrySet, proteinFactory, bioSourceFactory);

        if (messages.checkerMessageExists())
        {
            // display checker messages.
            MessageHolder.getInstance().printCheckerReport(System.err);
        }
        else
        {
            EntrySetPersister.persist(entrySet);

            if (messages.checkerMessageExists())
            {
                // display persister messages.
                MessageHolder.getInstance().printPersisterReport(System.err);
            }
            else
            {
                System.out.println("The data have been successfully saved in your Intact node.");
            }
        }
    }

    public MavenProject getProject()
    {
        return project;
    }

    public Imports getImports()
    {
        return imports;
    }

    public void setImports(Imports imports)
    {
        this.imports = imports;
    }
}
