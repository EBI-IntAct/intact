package uk.ac.ebi.intact.bridges.wswublast.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private JDispatcherService service;
    private uk.ac.ebi.jdispatcher.soap.ObjectFactory objFactory;
    public static final Log log = LogFactory.getLog( WUBlast.class );

    private final static String wsdlFile = "http://www.ebi.ac.uk/Tools/services/soap/wublast?wsdl";
    private final static String jDispatcherURL = "http://soap.jdispatcher.ebi.ac.uk";
    private final static String jDispatcherName = "JDispatcherService";

    private final static String jobName = "WUBlast";

    public WUBlast(){
        this(wsdlFile);
    }

    public WUBlast(String wsdlUrl){
        this.objFactory = new uk.ac.ebi.jdispatcher.soap.ObjectFactory();

        JDispatcherService_Service service_service;

        if (wsdlUrl == null){
            service_service = new JDispatcherService_Service();
        }
        else {
            try{
                service_service = new JDispatcherService_Service(new URL(wsdlUrl), new QName(jDispatcherURL, jDispatcherName));
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
                log.error("Warning: problem with specified endpoint URL. Default endpoint used.");
                service_service = new JDispatcherService_Service();
            }
        }

        service = service_service.getJDispatcherServiceHttpPort();
    }

    public String runWUBlast(String email, InputParameters params){
        return this.service.run(email, jobName, params);
    }

    public String checkStatus(String jobId){
        return this.service.getStatus(jobId);
    }

    /** Get details of the available result types for a job.
     *
     * @param jobId Job identifier to check for results types.
     * @return Array of objects describing result types.
     * @throws WuBlastClientException
     */
    public WsResultType[] getResultTypes(String jobId) throws WuBlastClientException {

        WsResultType[] retVal = null;
        WsResultTypes resultTypes = this.service.getResultTypes(jobId);
        if(resultTypes != null) {
            retVal = resultTypes.getType().toArray(new WsResultType[0]);
        }
        return retVal;
    }

    public byte[] poll(String jobId, String type) throws WuBlastClientException {

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
        WsParameters parameters = this.service.getParameters();

        for (String id : parameters.getId()){
            WsParameterDetails details = this.service.getParameterDetails(id);
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
