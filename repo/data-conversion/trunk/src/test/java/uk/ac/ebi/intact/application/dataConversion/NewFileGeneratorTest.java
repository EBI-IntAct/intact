/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import junit.framework.TestCase;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.CvMapping;
import uk.ac.ebi.intact.application.dataConversion.util.DisplayXML;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2006</pre>
 */
public class NewFileGeneratorTest extends TestCase
{

    private static final Log log = LogFactory.getLog(NewFileGeneratorTest.class);

    public void testGenerateListGavin() throws Exception
    {
        File reverseMappingFile = new File(NewFileGeneratorTest.class.getResource("/reverseMapping.txt").getFile());

        CvMapping mapping = new CvMapping();
        mapping.loadFile(reverseMappingFile);


        ExperimentListGenerator gen = new ExperimentListGenerator("mahajan-2000-1");
        //gen.setLargeScaleChunkSize(150);

        List<ExperimentListItem> eliSpecies = gen.generateClassificationBySpecies();

        ExperimentListItem eli = eliSpecies.get(0);
        log.info("Experiment List Item: "+eli);

        Document doc = NewFileGenerator.generatePsiData(eliSpecies.get(0), PsiVersion.getVersion1(), mapping);

        Writer writer = new StringWriter();
        DisplayXML.write(doc, writer, "   ");

        String xmlDoc = writer.toString();
        assertEquals(25939, xmlDoc.length());

        // 2.5
        doc = NewFileGenerator.generatePsiData(eliSpecies.get(0), PsiVersion.getVersion25(), mapping);

        writer = new StringWriter();
        DisplayXML.write(doc, writer, "   ");

        xmlDoc = writer.toString();
        assertEquals(128792, xmlDoc.length());

    }

    public void testGenerateXmlFiles() throws Exception
    {
        File reverseMappingFile = new File(NewFileGeneratorTest.class.getResource("/reverseMapping.txt").getFile());

        CvMapping mapping = new CvMapping();
        mapping.loadFile(reverseMappingFile);

        ExperimentListGenerator gen = new ExperimentListGenerator("ab%");

        List<ExperimentListItem> allItems = gen.generateAllClassifications();

        for (ExperimentListItem item : allItems)
        {
            NewFileGenerator.writePsiData(item, PsiVersion.VERSION_25, mapping, new File("target/psi25"), true);
        }

        // check if the files exist and are not empty
        for (ExperimentListItem item : allItems)
        {
            File xmlFile = new File("target/psi25", item.getFilename());

            assertTrue(xmlFile.exists());
            assertTrue(xmlFile.length() > 0);
        }
    }

}
