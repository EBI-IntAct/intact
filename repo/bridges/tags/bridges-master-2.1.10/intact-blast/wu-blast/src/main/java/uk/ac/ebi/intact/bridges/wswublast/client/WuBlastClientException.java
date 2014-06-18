/**
 * 
 */
package uk.ac.ebi.intact.bridges.wswublast.client;

/**
 * Exception for BlastClient
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id: BlastClient.java 9577 2007-08-24 14:34:55Z irina-armean $
 */
public class WuBlastClientException extends Exception {

	public WuBlastClientException(){
        super();
    }

	public WuBlastClientException(Throwable cause){
        super(cause);
    }

    public WuBlastClientException(String message) {
        super(message);
    }

    public WuBlastClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
