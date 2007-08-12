package uk.ac.ebi.intact.sanity.check;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ReportMessage
{
    private ReportTopic topic;
    private String message;
    private String ac;

    public ReportMessage(String ac, ReportTopic topic, String message)
    {
        this.ac = ac;
        this.topic = topic;
        this.message = message;
    }

    public String getAc()
    {
        return ac;
    }

    public void setAc(String ac)
    {
        this.ac = ac;
    }

    public ReportTopic getTopic()
    {
        return topic;
    }

    public void setTopic(ReportTopic topic)
    {
        this.topic = topic;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return getAc()+"\t"+getMessage();
    }
}
