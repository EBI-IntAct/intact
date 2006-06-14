/**
 * Copyright (c) 2006 The European Bioinformatics Institute, and others.
 * All rights reserved. 
 */
package uk.ac.ebi.imex.psivalidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Jun-2006</pre>
 */
public class TransformationUtil
{

    private static final Log log = LogFactory.getLog(TransformationUtil.class);

    public static OutputStream transformToHtml(InputStream is) throws TransformerException
    {

        InputStream xslt = TransformationUtil.class.getResourceAsStream("resource/MIF25_view.xsl");
        return transform(is, xslt);
    }

    public static OutputStream transformToExpanded(InputStream is) throws TransformerException
    {
         InputStream xslt = TransformationUtil.class.getResourceAsStream("resource/MIF25_expand.xsl");
         return transform(is, xslt);
    }

    private static OutputStream transform(InputStream isToTransform, InputStream xslt) throws TransformerException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // JAXP reads data using the Source interface
        Source xmlSource = new StreamSource(isToTransform);
        Source xsltSource = new StreamSource(xslt);

        // the factory pattern supports different XSLT processors
        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);

        trans.transform(xmlSource, new StreamResult(outputStream));

        return outputStream;
    }

}
