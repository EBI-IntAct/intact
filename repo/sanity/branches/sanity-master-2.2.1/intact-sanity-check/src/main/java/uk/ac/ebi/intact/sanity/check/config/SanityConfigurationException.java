package uk.ac.ebi.intact.sanity.check.config;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityConfigurationException extends RuntimeException
{
    public SanityConfigurationException()
    {
        super();
    }

    public SanityConfigurationException(String s)
    {
        super(s);
    }

    public SanityConfigurationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public SanityConfigurationException(Throwable throwable)
    {
        super(throwable);
    }
}
