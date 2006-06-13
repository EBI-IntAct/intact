/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.imex.psivalidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.ArrayList;

import psidev.psi.mi.validator.util.UserPreferences;
import psidev.psi.mi.validator.extensions.mi25.Mi25Validator;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.MessageLevel;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12-Jun-2006</pre>
 */
public class PsiReportBuilder
{

    private static final Log log = LogFactory.getLog(PsiReportBuilder.class);

    private String name;
    private URL url;
    private InputStream inputStream;

    private enum SourceType { URL, INPUT_STREAM }

    private SourceType currentSourceType;

    public PsiReportBuilder(String name, URL url)
    {
        this.name = name;
        this.url = url;

        this.currentSourceType = SourceType.URL;
    }

    public PsiReportBuilder(String name, InputStream resettableInputStream)
    {
        this.name = name;
        this.inputStream = resettableInputStream;

        this.currentSourceType = SourceType.INPUT_STREAM;
    }

    public PsiReport createPsiReport() throws IOException
    {
        PsiReport report = new PsiReport(name);

        boolean xmlValid = validateXmlSyntax(report, getInputStream());

        if (xmlValid)
        {
            createHtmlView(report, getInputStream());

            validatePsiFileSemantics(report, getInputStream());
        }
        else
        {
            report.setSemanticsStatus("not checked, XML syntax needs to be valid first");
        }

        return report;

    }

    private InputStream getInputStream() throws IOException
    {
        if (currentSourceType == SourceType.URL)
        {
            return url.openStream();
        }
        else
        {
            inputStream.reset();
            return inputStream;
        }
    }

    private static boolean validateXmlSyntax(PsiReport report, InputStream is) throws IOException
    {
        //InputStream xsd = PsiReportBuilder.class.getResourceAsStream("/uk/ac/ebi/imex/psivalidator/resource/MIF25.xsd");

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        try
        {
            // parse an XML document into a DOM tree
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(is);

            // create a SchemaFactory capable of understanding WXS schemas
            //SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // load a WXS schema, represented by a Schema instance
            //Source schemaFile = new StreamSource(xsd);
            //Schema schema = factory.newSchema(schemaFile);

            // create a Validator instance, which can be used to validate an instance document
            //Validator validator = schema.newValidator();

            // validate the DOM tree
            //validator.validate(new DOMSource(document));
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace(writer);
        }
        catch (SAXException e)
        {
            e.printStackTrace(writer);
        }
        catch (IOException e)
        {
            e.printStackTrace(writer);
        }

        String output = sw.getBuffer().toString();

        if (log.isDebugEnabled())
            log.debug("XML Validation output: "+output);

        if (output.equals(""))
        {
            report.setXmlSyntaxStatus("valid");
            report.setXmlSyntaxReport("Document is valid");

            return true;
        }

        report.setXmlSyntaxStatus("invalid");
        report.setXmlSyntaxReport(output);

        return false;
    }



    private static void createHtmlView(PsiReport report, InputStream is)
    {
        String transformedOutput = null;
        try
        {
            transformedOutput = TransformationUtil.transformToHtml(is).toString();
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
        }
        report.setHtmlView(transformedOutput);
    }

    private static void validatePsiFileSemantics(PsiReport report, InputStream is)
    {

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        try
        {
            String expandedFile = TransformationUtil.transformToExpanded(is).toString();

            InputStream expandedStream = new ByteArrayInputStream(expandedFile.getBytes());

            InputStream configFile = PsiReportBuilder.class.getResourceAsStream("resource/config-mi-validator.xml");

            // set work directory
            UserPreferences preferences = new UserPreferences();
            preferences.setKeepDownloadedOntologiesOnDisk( true );
            preferences.setWorkDirectory(new File(System.getProperty("java.io.tmpdir")));
            preferences.setSaxValidationEnabled( false );


            psidev.psi.mi.validator.framework.Validator validator = new Mi25Validator( configFile, preferences );

            Collection<ValidatorMessage> messages = validator.validate( expandedStream );
            report.setValidatorMessages(new ArrayList<ValidatorMessage>(messages));
        }
        catch (Exception e)
        {
            e.printStackTrace(writer);
        }

        String output = sw.getBuffer().toString();

        if (!output.equals(""))
        {
            report.setSemanticsStatus("invalid");
            report.setSemanticsReport(output);
            return;
        }

        String status = "valid";
        report.setSemanticsReport("Document is valid");

        for (ValidatorMessage message : report.getValidatorMessages())
        {
            if (message.getLevel() == MessageLevel.WARN)
            {
                status = "warnings";
                report.setSemanticsReport("Validated with warnings");
            }

            if (message.getLevel().isHigher(MessageLevel.WARN))
            {
                status = "invalid";
                report.setSemanticsReport("Validation failed");
                break;
            }
        }

        report.setSemanticsStatus(status);

    }

}
