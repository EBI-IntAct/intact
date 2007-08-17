package uk.ac.ebi.intact.sanity.check;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleAdminReport
{
    private Map<ReportTopic, List<ReportMessage>> messagesByTopic;
    private Map<ReportTopic, String> headersByTopic;

    public SimpleAdminReport()
    {
        this.messagesByTopic = new HashMap<ReportTopic, List<ReportMessage>>();
        this.headersByTopic = new HashMap<ReportTopic, String>();
    }

    public void addMessage(String ac, ReportTopic topic, String message) {
        addMessage(new ReportMessage(ac, topic, message));
    }

    public void addMessage(ReportMessage message) {
        if (messagesByTopic.containsKey(message.getTopic())) {
            messagesByTopic.get(message.getTopic()).add(message);
        } else {
            List<ReportMessage> messages = new ArrayList<ReportMessage>();
            messages.add(message);
            messagesByTopic.put(message.getTopic(), messages);
        }
    }

    public Map<ReportTopic, List<ReportMessage>> getMessages()
    {
        return messagesByTopic;
    }

    public List<ReportMessage> getMessagesByTopic(ReportTopic topic)
    {
        return messagesByTopic.get(topic);
    }

    public void addHeader(ReportTopic topic, String header) {
        headersByTopic.put(topic, header);
    }

    public String getHeaderByTopic(ReportTopic topic) {
        return headersByTopic.get(topic);
    }

    public Set<ReportTopic> getTopicsWithMessages() {
        return messagesByTopic.keySet();
    }
}
