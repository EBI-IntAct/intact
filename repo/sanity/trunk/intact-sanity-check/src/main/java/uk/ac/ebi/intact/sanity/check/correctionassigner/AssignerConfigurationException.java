package uk.ac.ebi.intact.sanity.check.correctionassigner;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AssignerConfigurationException extends RuntimeException
{
    public AssignerConfigurationException()
    {
        super();
    }

    public AssignerConfigurationException(String s)
    {
        super(s);
    }

    public AssignerConfigurationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public AssignerConfigurationException(Throwable throwable)
    {
        super(throwable);
    }
}
