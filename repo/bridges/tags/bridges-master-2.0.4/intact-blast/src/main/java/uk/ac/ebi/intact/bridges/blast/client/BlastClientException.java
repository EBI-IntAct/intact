/**
 * 
 */
package uk.ac.ebi.intact.bridges.blast.client;

/**
 * Exception for BlastClient
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id: BlastClient.java 9577 2007-08-24 14:34:55Z irina-armean $
 */
public class BlastClientException extends Exception {

	public BlastClientException(){
        super();
    }

	public BlastClientException(Throwable cause){
        super(cause);
    }

    public BlastClientException(String message) {
        super(message);
    }

    public BlastClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
