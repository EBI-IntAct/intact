/*
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 12-Nov-2002
 * Time: 15:02:03
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.application.search.struts.view;

import java.lang.Object;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

public class JAXPTransformer extends java.lang.Object implements java.io.Serializable {

    JAXPTransformerResources source;

    public JAXPTransformer() {}

    public JAXPTransformer(JAXPTransformerResources source) throws TransformerException {

        transform(source);
    }

    public void transform(JAXPTransformerResources source) throws TransformerException {

        Object XMLSource = source.getXMLSource();
        Object XSLSource = source.getXSLSource();
        Object transformResult = source.getTransformResult();

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        //determine which type of XSL source we have
        if(XSLSource.getClass().getName().compareTo("javax.xml.transform.stream.StreamSource") == 0) {

        transformer = tFactory.newTransformer((StreamSource)XSLSource);
        }

        if(XSLSource.getClass().getName().compareTo("javax.xml.transform.dom.DOMSource") == 0) {

        transformer = tFactory.newTransformer((DOMSource)XSLSource);
        }

        if(XSLSource.getClass().getName().compareTo("javax.xml.transform.sax.SAXSource") == 0) {

        transformer = tFactory.newTransformer((SAXSource)XSLSource);
        }

        //now check the XML source (stream, dom or sax) so we can apply the correct transformer


        //stream
        if(XMLSource.getClass().getName().compareTo("javax.xml.transform.stream.StreamSource") == 0) {

            //now check the class of the transform result
            if(transformResult.getClass().getName().compareTo("javax.xml.stream.StreamResult") == 0) {

                transformer.transform((StreamSource)XMLSource, (StreamResult)transformResult);
            }

            if(transformResult.getClass().getName().compareTo("javax.xml.dom.DOMResult") == 0) {

                transformer.transform((StreamSource)XMLSource, (DOMResult)transformResult);
            }

            if(transformResult.getClass().getName().compareTo("javax.xml.sax.SAXResult") == 0) {

                transformer.transform((StreamSource)XMLSource, (SAXResult)transformResult);
            }
        }

        //DOM
        if(XMLSource.getClass().getName().compareTo("javax.xml.transform.dom.DOMSource") == 0) {

            //now check the class of the transform result
            if(transformResult.getClass().getName().compareTo("javax.xml.stream.StreamResult") == 0) {

                transformer.transform((DOMSource)XMLSource, (StreamResult)transformResult);
            }

            if(transformResult.getClass().getName().compareTo("javax.xml.dom.DOMResult") == 0) {

                transformer.transform((DOMSource)XMLSource, (DOMResult)transformResult);
            }

            if(transformResult.getClass().getName().compareTo("javax.xml.sax.SAXResult") == 0) {

                transformer.transform((DOMSource)XMLSource, (SAXResult)transformResult);
            }
        }

        //SAX
        if(XMLSource.getClass().getName().compareTo("javax.xml.transform.sax.SAXSource") == 0) {

            //now check the class of the transform result
            if(transformResult.getClass().getName().compareTo("javax.xml.stream.StreamResult") == 0) {

                transformer.transform((SAXSource)XMLSource, (StreamResult)transformResult);
            }

            if(transformResult.getClass().getName().compareTo("javax.xml.dom.DOMResult") == 0) {

                transformer.transform((SAXSource)XMLSource, (DOMResult)transformResult);
            }

            if(transformResult.getClass().getName().compareTo("javax.xml.sax.SAXResult") == 0) {

                transformer.transform((SAXSource)XMLSource, (SAXResult)transformResult);
            }
        }
    }

    public void setTransformerResources(JAXPTransformerResources new_resources) {

        source = new_resources;
    }

    public JAXPTransformerResources getTransformerResources() {

        return source;
    }
}
