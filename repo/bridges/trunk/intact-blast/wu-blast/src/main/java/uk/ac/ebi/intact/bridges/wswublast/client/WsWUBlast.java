package uk.ac.ebi.intact.bridges.wswublast.client;

import uk.ac.ebi.jdispatcher.soap.*;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12-Apr-2010</pre>
 */

public class WsWUBlast {

    private JDispatcherService_Service service;

    public WsWUBlast(){
        this("http://www.ebi.ac.uk/Tools/services/soap/wublast?wsdl");
    }

    public WsWUBlast(String wsdlUrl){
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
        WsRawOutputParameters parameters = new WsRawOutputParameters();
        WsResultTypes types = getJDispatcherPort().getResultTypes(jobId);

        for (WsResultType t : types.getType()){
            System.out.println("Result label" + t.getLabel());
            System.out.println("Result Identifier" + t.getIdentifier());
            System.out.println("Result Description" + t.getDescription());
        }
        return getJDispatcherPort().getResult(jobId, type, parameters);
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
