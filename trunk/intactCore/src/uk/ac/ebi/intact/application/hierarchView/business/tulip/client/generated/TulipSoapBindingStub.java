/**
 * TulipSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated;

public class TulipSoapBindingStub extends org.apache.axis.client.Stub implements uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    public TulipSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public TulipSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public TulipSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        try {
            if (service == null) {
                super.service = new org.apache.axis.client.Service();
            } else {
                super.service = service;
            }
            Class cls;
            javax.xml.rpc.namespace.QName qName;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            Class simplesf = org.apache.axis.encoding.ser.SimpleNonPrimitiveSerializerFactory.class;
            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            qName = new javax.xml.rpc.namespace.QName("urn:uk.ac.ebi.intact.application.hierarchView.business.tulip.webService", "ProteinCoordinate");
            cachedSerQNames.add(qName);
            cls = uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.rpc.namespace.QName("urn:tulip", "ArrayOf_tns1_ProteinCoordinate");
            cachedSerQNames.add(qName);
            cls = uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate[].class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(arraysf);
            cachedDeserFactories.add(arraydf);

        }
        catch(java.lang.Exception t) {
            throw org.apache.axis.AxisFault.makeFault(t);
        }
    }

    private org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call call =
                    (org.apache.axis.client.Call) super.service.createCall();
            if (super.maintainSessionSet) {
                call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                call.setTimeout(super.cachedTimeout);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            if (firstCall()) {
                // must set encoding style before registering serializers
                call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP_ENC);
                for (int i = 0; i < cachedSerFactories.size(); ++i) {
                    Class cls = (Class) cachedSerClasses.get(i);
                    javax.xml.rpc.namespace.QName qName =
                            (javax.xml.rpc.namespace.QName) cachedSerQNames.get(i);
                    Class sf = (Class)
                             cachedSerFactories.get(i);
                    Class df = (Class)
                             cachedDeserFactories.get(i);
                    call.registerTypeMapping(cls, qName, sf, df, false);
                }
            }
            return call;
        }
        catch (Throwable t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", t);
        }
    }

    public uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate[] getComputedTlpContent(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = createCall();
        javax.xml.rpc.namespace.QName p0QName = new javax.xml.rpc.namespace.QName("", "in0");
        call.addParameter(p0QName, new javax.xml.rpc.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, javax.xml.rpc.ParameterMode.IN);
        javax.xml.rpc.namespace.QName p1QName = new javax.xml.rpc.namespace.QName("", "in1");
        call.addParameter(p1QName, new javax.xml.rpc.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, javax.xml.rpc.ParameterMode.IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("urn:tulip", "ArrayOf_tns1_ProteinCoordinate"));
        call.setUseSOAPAction(true);
        call.setSOAPActionURI("");
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("getComputedTlpContent", "getComputedTlpContent"));

        Object resp = call.invoke(new Object[] {in0, in1});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
            try {
                return (uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate[]) resp;
            } catch (java.lang.Exception e) {
                return (uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate[]) org.apache.axis.utils.JavaUtils.convert(resp, uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate[].class);
            }
        }
    }

}
