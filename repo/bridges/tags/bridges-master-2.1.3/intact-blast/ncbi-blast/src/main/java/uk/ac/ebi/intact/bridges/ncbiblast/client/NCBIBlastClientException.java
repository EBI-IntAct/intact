/**
 * 
 */
package uk.ac.ebi.intact.bridges.ncbiblast.client;

/**
 * Exception for BlastClient
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id: BlastClient.java 9577 2007-08-24 14:34:55Z irina-armean $
 */
public class NCBIBlastClientException extends Exception {

	public NCBIBlastClientException(){
        super();
    }

	public NCBIBlastClientException(Throwable cause){
        super(cause);
    }

    public NCBIBlastClientException(String message) {
        super(message);
    }

    public NCBIBlastClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
