/**
 * RegIMExSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package uk.ac.ebi.intact.application.imex.keyassigner.generated;

public class RegIMExSoapBindingStub extends org.apache.axis.client.Stub
        implements uk.ac.ebi.intact.application.imex.keyassigner.generated.RegIMEx_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[2];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1() {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName( "newSubmission" );
        oper.setReturnType( new javax.xml.namespace.QName( "http://imex.org/registry", "SubmissionAxis" ) );
        oper.setReturnClass( uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis.class );
        oper.setReturnQName( new javax.xml.namespace.QName( "", "newSubmissionReturn" ) );
        oper.setStyle( org.apache.axis.constants.Style.RPC );
        oper.setUse( org.apache.axis.constants.Use.ENCODED );
        _operations[ 0 ] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName( "newSubmission" );
        param = new org.apache.axis.description.ParameterDesc( new javax.xml.namespace.QName( "", "in0" ), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName( "http://www.w3.org/2001/XMLSchema", "int" ), int.class, false, false );
        oper.addParameter( param );
        oper.setReturnType( new javax.xml.namespace.QName( "http://imex.org/registry", "SubmissionAxis" ) );
        oper.setReturnClass( uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis.class );
        oper.setReturnQName( new javax.xml.namespace.QName( "", "newSubmissionReturn" ) );
        oper.setStyle( org.apache.axis.constants.Style.RPC );
        oper.setUse( org.apache.axis.constants.Use.ENCODED );
        _operations[ 1 ] = oper;

    }

    public RegIMExSoapBindingStub() throws org.apache.axis.AxisFault {
        this( null );
    }

    public RegIMExSoapBindingStub( java.net.URL endpointURL, javax.xml.rpc.Service service ) throws org.apache.axis.AxisFault {
        this( service );
        super.cachedEndpoint = endpointURL;
    }

    public RegIMExSoapBindingStub( javax.xml.rpc.Service service ) throws org.apache.axis.AxisFault {
        if ( service == null ) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ( (org.apache.axis.client.Service) super.service ).setTypeMappingVersion( "1.2" );
        java.lang.Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        qName = new javax.xml.namespace.QName( "http://imex.org/registry", "SubmissionAxis" );
        cachedSerQNames.add( qName );
        cls = uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis.class;
        cachedSerClasses.add( cls );
        cachedSerFactories.add( beansf );
        cachedDeserFactories.add( beandf );

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if ( super.maintainSessionSet ) {
                _call.setMaintainSession( super.maintainSession );
            }
            if ( super.cachedUsername != null ) {
                _call.setUsername( super.cachedUsername );
            }
            if ( super.cachedPassword != null ) {
                _call.setPassword( super.cachedPassword );
            }
            if ( super.cachedEndpoint != null ) {
                _call.setTargetEndpointAddress( super.cachedEndpoint );
            }
            if ( super.cachedTimeout != null ) {
                _call.setTimeout( super.cachedTimeout );
            }
            if ( super.cachedPortName != null ) {
                _call.setPortName( super.cachedPortName );
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while ( keys.hasMoreElements() ) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty( key, super.cachedProperties.get( key ) );
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized ( this ) {
                if ( firstCall() ) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion( org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS );
                    _call.setEncodingStyle( org.apache.axis.Constants.URI_SOAP11_ENC );
                    for ( int i = 0; i < cachedSerFactories.size(); ++i ) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get( i );
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get( i );
                        java.lang.Object x = cachedSerFactories.get( i );
                        if ( x instanceof Class ) {
                            java.lang.Class sf = (java.lang.Class)
                                    cachedSerFactories.get( i );
                            java.lang.Class df = (java.lang.Class)
                                    cachedDeserFactories.get( i );
                            _call.registerTypeMapping( cls, qName, sf, df, false );
                        } else if ( x instanceof javax.xml.rpc.encoding.SerializerFactory ) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                    cachedSerFactories.get( i );
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                    cachedDeserFactories.get( i );
                            _call.registerTypeMapping( cls, qName, sf, df, false );
                        }
                    }
                }
            }
            return _call;
        }
        catch ( java.lang.Throwable _t ) {
            throw new org.apache.axis.AxisFault( "Failure trying to get the Call object", _t );
        }
    }

    public uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis newSubmission() throws java.rmi.RemoteException {
        if ( super.cachedEndpoint == null ) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation( _operations[ 0 ] );
        _call.setUseSOAPAction( true );
        _call.setSOAPActionURI( "" );
        _call.setSOAPVersion( org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS );
        _call.setOperationName( new javax.xml.namespace.QName( "http://imex.org/registry", "newSubmission" ) );

        setRequestHeaders( _call );
        setAttachments( _call );
        try {
            java.lang.Object _resp = _call.invoke( new java.lang.Object[]{ } );

            if ( _resp instanceof java.rmi.RemoteException ) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments( _call );
                try {
                    return (uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis) _resp;
                } catch ( java.lang.Exception _exception ) {
                    return (uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis) org.apache.axis.utils.JavaUtils.convert( _resp, uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis.class );
                }
            }
        } catch ( org.apache.axis.AxisFault axisFaultException ) {
            throw axisFaultException;
        }
    }

    public uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis newSubmission( int in0 ) throws java.rmi.RemoteException {
        if ( super.cachedEndpoint == null ) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation( _operations[ 1 ] );
        _call.setUseSOAPAction( true );
        _call.setSOAPActionURI( "" );
        _call.setSOAPVersion( org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS );
        _call.setOperationName( new javax.xml.namespace.QName( "http://imex.org/registry", "newSubmission" ) );

        setRequestHeaders( _call );
        setAttachments( _call );
        try {
            java.lang.Object _resp = _call.invoke( new java.lang.Object[]{ new java.lang.Integer( in0 ) } );

            if ( _resp instanceof java.rmi.RemoteException ) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments( _call );
                try {
                    return (uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis) _resp;
                } catch ( java.lang.Exception _exception ) {
                    return (uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis) org.apache.axis.utils.JavaUtils.convert( _resp, uk.ac.ebi.intact.application.imex.keyassigner.generated.SubmissionAxis.class );
                }
            }
        } catch ( org.apache.axis.AxisFault axisFaultException ) {
            throw axisFaultException;
        }
    }

}
