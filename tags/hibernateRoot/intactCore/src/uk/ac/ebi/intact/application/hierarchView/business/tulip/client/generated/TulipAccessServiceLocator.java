/**
 * TulipAccessServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated;

public class TulipAccessServiceLocator extends org.apache.axis.client.Service implements uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessService {

    public TulipAccessServiceLocator() {
    }


    public TulipAccessServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TulipAccessServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for tulip
    private java.lang.String tulip_address = "http://localhost:8080/axis/services/tulip";

    public java.lang.String gettulipAddress() {
        return tulip_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String tulipWSDDServiceName = "tulip";

    public java.lang.String gettulipWSDDServiceName() {
        return tulipWSDDServiceName;
    }

    public void settulipWSDDServiceName(java.lang.String name) {
        tulipWSDDServiceName = name;
    }

    public uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess gettulip() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(tulip_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return gettulip(endpoint);
    }

    public uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess gettulip(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub _stub = new uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub(portAddress, this);
            _stub.setPortName(gettulipWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void settulipEndpointAddress(java.lang.String address) {
        tulip_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess.class.isAssignableFrom(serviceEndpointInterface)) {
                uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub _stub = new uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub(new java.net.URL(tulip_address), this);
                _stub.setPortName(gettulipWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("tulip".equals(inputPortName)) {
            return gettulip();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:tulip", "TulipAccessService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:tulip", "tulip"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("tulip".equals(portName)) {
            settulipEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
