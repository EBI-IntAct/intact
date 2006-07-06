/**
 * TulipAccess.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated;

public interface TulipAccess extends java.rmi.Remote {
    public uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate[] getComputedTlpContent(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
    public java.lang.String[] getErrorMessages(boolean in0) throws java.rmi.RemoteException;
    public void cleanErrorMessages() throws java.rmi.RemoteException;
}
