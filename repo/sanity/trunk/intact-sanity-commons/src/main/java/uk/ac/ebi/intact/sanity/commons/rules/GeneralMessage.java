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

    @Deprecated
    public static final MessageLevel LOW_LEVEL = MessageLevel.MINOR;

    @Deprecated
    public static final MessageLevel AVERAGE_LEVEL = MessageLevel.NORMAL;

    @Deprecated
    public static final MessageLevel HIGH_LEVEL = MessageLevel.MAJOR;

    public static final String NO_IDEA = "";
    /*
    The description of the rule.
    ex : An experiment should always be linked to an interaction.
     */
    private String description;
    /*
    The level of gravity of the rule.
     */
    private MessageLevel level;
    /*
    The proposed solution if the rule is transgressed
     */
    private String proposedSolution;
    /*
    The out-low object
     */
    private InsaneObject insaneObject;

    @Deprecated
    public GeneralMessage(String description, int level, String proposedSolution, IntactObject insaneObject) {
        this.description = description;
        this.proposedSolution = proposedSolution;
        this.insaneObject = toInsaneObject(insaneObject);

        switch (level) {
            case 2:
                this.level = MessageLevel.MAJOR;
                break;
            case 1:
                this.level = MessageLevel.NORMAL;
                break;
            case 0:
                this.level = MessageLevel.MINOR;
                break;
        }
    }

    public GeneralMessage(String description, MessageLevel level, String proposedSolution, IntactObject insaneObject) {
        this.description = description;
        this.level = level;
        this.proposedSolution = proposedSolution;
        this.insaneObject = toInsaneObject(insaneObject);
    }

    public GeneralMessage(String description, MessageLevel level, String proposedSolution, InsaneObject insaneObject) {
        this.description = description;
        this.level = level;
        this.proposedSolution = proposedSolution;
        this.insaneObject = insaneObject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public void setLevel(MessageLevel level) {
        this.level = level;
    }

    public String getProposedSolution() {
        return proposedSolution;
    }

    public void setProposedSolution(String proposedSolution) {
        this.proposedSolution = proposedSolution;
    }


    public InsaneObject getInsaneObject() {
        return insaneObject;
    }

    public void setInsaneObject(InsaneObject insaneObject) {
        this.insaneObject = insaneObject;
    }

    @Override
    public String toString() {
        return "["+level+"] "+description+" (Tip: "+proposedSolution+")";
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