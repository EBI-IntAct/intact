package uk.ac.ebi.intact.application.hierarchView.business.tulip.client;

import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.GVFImplService;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.GVFImplServiceLocator;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.GVFImpl;

import javax.xml.rpc.ServiceException;
import java.util.Properties;
import java.net.URL;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

/**
 * User: Bruno Aranda
 * Date: 04/07/2006
 * Time: 22:25:14
 */
public class TulipClient
{

     /**
     * Stub to handle the tulip web service
     */
    private GVFImpl tulip;


    ////////////////
    // Methods

    /**
     * Prepare the web service.
     */
    public TulipClient() {

        try
        {
            GVFImplServiceLocator serviceLocator = new GVFImplServiceLocator();
            serviceLocator.setMaintainSession(true);

            this.tulip = serviceLocator.gettulip(new URL("http://www.ebi.ac.uk/intact/axis/services/tulip"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    } // constructor

    public TulipClient(String tulipWsUrl) {

        try
        {
            GVFImplServiceLocator serviceLocator = new GVFImplServiceLocator();
            serviceLocator.setMaintainSession(true);

            this.tulip = serviceLocator.gettulip(new URL(tulipWsUrl));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    } // constructor



    /**
     * allows to compute a tlp content
     *
     * @param tlpContent the tlp content to compute
     * @return the collection of protein coordinates
     */
    public ProteinCoordinate[] getComputedTlpContent( String tlpContent )
            throws RemoteException
    {

        ProteinCoordinate[] pc = null;
        String mask = "0";


        if( null != tulip ) {
            try {
                pc = tulip.getComputedTlpContent( tlpContent, mask );

            } catch ( RemoteException re ) {
                throw re;
            }
        }

        return pc;
    } // getComputedTlpContent


    /**
     * Get the list of messages produce in the web service
     *
     * @param hasToBeCleaned delete all messages after sended them back to the client
     */
    public String[] getErrorMessages( boolean hasToBeCleaned ) {
        try {
            return tulip.getErrorMessages( hasToBeCleaned );
        } catch ( RemoteException re ) {
            // create an error message to display
            String[] errors = new String[1];
            errors[0] = "\n\nError while checking errors.";
            return errors;
        }
    } // getErrorMessages

}
