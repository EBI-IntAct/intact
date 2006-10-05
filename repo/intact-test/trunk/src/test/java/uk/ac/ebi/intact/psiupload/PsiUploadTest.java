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
package uk.ac.ebi.intact.psiupload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.AbstractIntactTest;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.util.UpdateProteinsI;
import uk.ac.ebi.intact.util.UpdateProteins;
import uk.ac.ebi.intact.util.BioSourceFactory;
import uk.ac.ebi.intact.util.controlledVocab.PSILoader;
import uk.ac.ebi.intact.util.controlledVocab.UpdateCVs;
import uk.ac.ebi.intact.util.controlledVocab.SequenceManager;
import uk.ac.ebi.intact.util.controlledVocab.model.IntactOntology;
import uk.ac.ebi.intact.util.controlledVocab.model.CvTerm;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.EntrySetTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.EntrySetParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.persister.EntrySetPersister;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.ControlledVocabularyRepository;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.EntrySetChecker;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Sep-2006</pre>
 */
public class PsiUploadTest  extends AbstractIntactTest
{

    private static final Log log = LogFactory.getLog(PsiUploadTest.class);

    public void testImportCVs() throws Exception
    {
        File oboFile = new File(PsiUploadTest.class.getResource("psi-binder-4intact.obo").getFile());

        UpdateCVs.loadFile(oboFile);
    }

    public void testUpload() throws Exception
    {
        File xmlFile = new File(PsiUploadTest.class.getResource("10206957.xml").getFile());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        MessageHolder messages = MessageHolder.getInstance();

        // Parse the PSI file
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse( xmlFile );
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
}
