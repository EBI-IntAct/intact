/**
 * TulipAccess.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated;

public interface TulipAccess extends java.rmi.Remote {
    public uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate[] getComputedTlpContent(java.lang.String tlpContent, java.lang.String optionMask) throws java.rmi.RemoteException;
    public java.lang.String[] getErrorMessages(boolean hasToBeCleaned) throws java.rmi.RemoteException;
    public void cleanErrorMessages() throws java.rmi.RemoteException;
}
