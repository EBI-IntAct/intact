/**
 * TulipAccessServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated;

public class TulipAccessServiceLocator extends org.apache.axis.client.Service implements uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessService {

    // Use to get a proxy class for Tulip
    private final java.lang.String Tulip_address = "http://web7-node1.ebi.ac.uk:8160/axis/services/tulip";

    public String getTulipAddress() {
        return Tulip_address;
    }

    public uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess getTulip() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Tulip_address);
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getTulip(endpoint);
    }

    public uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess getTulip(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            return new uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub(portAddress, this);
        }
        catch (org.apache.axis.AxisFault e) {
            return null; // ???
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess.class.isAssignableFrom(serviceEndpointInterface)) {
                return new uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub(new java.net.URL(Tulip_address), this);
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

}
