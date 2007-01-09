package uk.ac.ebi.intact.site.util;

/**
 * @author Bruno Aranda (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class DataLoadingException extends Exception
{
    public DataLoadingException()
    {
        super();
    }

    public DataLoadingException(String message)
    {
        super(message);
    }

    public DataLoadingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DataLoadingException(Throwable cause)
    {
        super(cause);
    }
}
