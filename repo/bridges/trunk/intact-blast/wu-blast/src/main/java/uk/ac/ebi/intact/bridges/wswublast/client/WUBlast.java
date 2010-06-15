package uk.ac.ebi.intact.bridges.wswublast.client;

import uk.ac.ebi.jdispatcher.soap.*;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12-Apr-2010</pre>
 */

public class WUBlast {

    private JDispatcherService_Service service;

    public WUBlast(){
        this("http://www.ebi.ac.uk/Tools/services/soap/wublast?wsdl");
    }

    public WUBlast(String wsdlUrl){
        try {
            service = new JDispatcherService_Service(new URL(wsdlUrl), new QName("http://soap.jdispatcher.ebi.ac.uk", "JDispatcherService"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public JDispatcherService getJDispatcherPort() {
        return this.service.getJDispatcherServiceHttpPort();
    }

    public String runWUBlast(String email, InputParameters params){
        return getJDispatcherPort().run(email, "WU wswublast", params);
    }

    public String checkStatus(String jobId){
        return getJDispatcherPort().getStatus(jobId);
    }

    public byte[] poll(String jobId, String type){

        // Get result types
        WsResultType[] resultTypes = getResultTypes(jobId);
        int retValN = 0;

        for(int i = 0; i < resultTypes.length; i++) {
            // Get the results
            if(resultTypes[i].getIdentifier().equals(type)) {
                byte[] resultbytes = null;
                try {
                    resultbytes = this.service.getResult(jobId, resultTypes[i].getIdentifier(), null);
                } catch (SOAPFaultException e){
                    resultbytes = null;
                    log.warn("A SOAP exception has been thrown for this job and we couldn't get any results.", e);
                }

                if(resultbytes == null) {
                    System.err.println("Null result for " + resultTypes[i].getIdentifier() + "!");
                }

                return resultbytes;
            }
        }
        return null;
    }

    public void getParametersValues(){
        WsParameters parameters = getJDispatcherPort().getParameters();

        for (String id : parameters.getId()){
            WsParameterDetails details = getJDispatcherPort().getParameterDetails(id);
            System.out.println("parameter name : " + details.getName());

            System.out.println("parameter type : " + details.getType());
            System.out.println("parameter description : " + details.getDescription());

            WsParameterValues values = details.getValues();
            System.out.println("parameter values : ");

            if (values != null){
                for (WsParameterValue value : values.getValue()){
                    System.out.println("value label : " + value.getLabel());
                    System.out.println("value : " + value.getValue());
                }
            }

        }
    }

    
}
