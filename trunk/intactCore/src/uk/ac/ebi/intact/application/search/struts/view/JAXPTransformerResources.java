/*
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 11-Nov-2002
 * Time: 16:33:59
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.application.search.struts.view;

//XML source types
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;
import java.lang.Object;

public class JAXPTransformerResources extends java.lang.Object implements java.io.Serializable {

    private Object XMLSource;
    private Object XSLSource;
    private Object transformResult;

    public JAXPTransformerResources() {}

    public Object getXMLSource() { return XMLSource; }
    public Object getXSLSource() { return XSLSource; }
    public Object getTransformResult() { return transformResult; }

    public void setXMLStreamSource(StreamSource newXMLStreamSource)
    { XMLSource = newXMLStreamSource; }

    public void setXMLDOMSource(DOMSource newXMLDOMSource)
    { XMLSource = newXMLDOMSource; }

    public void setXMLSAXSource(SAXSource newXMLSAXSource)
    { XMLSource = newXMLSAXSource; }

    public void setXSLStreamSource(StreamSource newXSLStreamSource)
    { XSLSource = newXSLStreamSource; }

    public void setXSLDOMSource(DOMSource newXSLDOMSource)
    { XSLSource = newXSLDOMSource; }

    public void setXSLSAXSource(SAXSource newXSLSAXSource)
    { XSLSource = newXSLSAXSource; }

    public void setStreamResult(StreamResult newStreamResult)
    { transformResult = newStreamResult; }

    public void setDOMResult(DOMResult newDOMResult)
    { transformResult = newDOMResult; }

    public void setXMLStreamSource(SAXResult newSAXResult)
    { transformResult = newSAXResult; }
}
