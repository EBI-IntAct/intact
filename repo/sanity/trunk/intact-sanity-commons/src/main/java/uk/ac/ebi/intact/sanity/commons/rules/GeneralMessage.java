/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons.rules;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BasicObject;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.sanity.commons.InsaneObject;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2007</pre>
 */
public class GeneralMessage {

    private MessageDefinition messageDefinition;

    /*
    The out-low object
     */
    private InsaneObject insaneObject;

    public GeneralMessage(MessageDefinition messageDefinition, IntactObject insaneObject) {
        this.messageDefinition = messageDefinition;
        this.insaneObject = toInsaneObject(insaneObject);
    }

    public MessageDefinition getMessageDefinition() {
        return messageDefinition;
    }

    public void setMessageDefinition(MessageDefinition messageDefinition) {
        this.messageDefinition = messageDefinition;
    }

    public InsaneObject getInsaneObject() {
        return insaneObject;
    }

    public void setInsaneObject(InsaneObject insaneObject) {
        this.insaneObject = insaneObject;
    }

    @Override
    public String toString() {
        return messageDefinition + " -> "+insaneObject.getShortlabel();
    }

    public static InsaneObject toInsaneObject(IntactObject intactObject) {
        InsaneObject insaneObject = new InsaneObject();
        insaneObject.setAc(intactObject.getAc());

        if (intactObject instanceof AnnotatedObject) {
            insaneObject.setShortlabel(((AnnotatedObject)intactObject).getShortLabel());
        }

        if (intactObject instanceof BasicObject) {
            insaneObject.setOwner(((BasicObject)intactObject).getOwner().getShortLabel());
        } else {
            insaneObject.setOwner(IntactContext.getCurrentInstance().getInstitution().getShortLabel());
        }

        if (intactObject.getCreated() != null) {
            insaneObject.setCreated(toXmlGregorianCalendar(intactObject.getCreated()));
        }

        if (intactObject.getUpdated() != null) {
            insaneObject.setUpdated(toXmlGregorianCalendar(intactObject.getUpdated()));
        }

        insaneObject.setCreator(intactObject.getCreator());
        insaneObject.setUpdator(intactObject.getUpdator());
        insaneObject.setObjclass(intactObject.getClass().getSimpleName().replaceAll("Impl", ""));

        return insaneObject;
    }

    protected static XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTime(date);

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new SanityRuleException("Problem converting to InsaneObject date: "+date);
        }
    }
}