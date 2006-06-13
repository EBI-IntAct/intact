/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.imex.psivalidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.validator.framework.ValidatorMessage;

import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12-Jun-2006</pre>
 */
public class PsiReport
{

    private static final Log log = LogFactory.getLog(PsiReport.class);

    private String name;
    private String xmlSyntaxStatus;
    private String xmlSyntaxReport;
    private String semanticsStatus;
    private String semanticsReport;

    private List<ValidatorMessage> validatorMessages;

    private String htmlView;

    public PsiReport(String name) {
         this.name = name;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getXmlSyntaxStatus()
    {
        return xmlSyntaxStatus;
    }

    public void setXmlSyntaxStatus(String xmlSyntaxStatus)
    {
        this.xmlSyntaxStatus = xmlSyntaxStatus;
    }

    public String getXmlSyntaxReport()
    {
        return xmlSyntaxReport;
    }

    public void setXmlSyntaxReport(String xmlSyntaxReport)
    {
        this.xmlSyntaxReport = xmlSyntaxReport;
    }

    public String getSemanticsStatus()
    {
        return semanticsStatus;
    }

    public void setSemanticsStatus(String semanticsStatus)
    {
        this.semanticsStatus = semanticsStatus;
    }

    public String getSemanticsReport()
    {
        return semanticsReport;
    }

    public void setSemanticsReport(String semanticsReport)
    {
        this.semanticsReport = semanticsReport;
    }

    public String getHtmlView()
    {
        return htmlView;
    }

    public void setHtmlView(String htmlView)
    {
        this.htmlView = htmlView;
    }


    public List<ValidatorMessage> getValidatorMessages()
    {
        return validatorMessages;
    }

    public void setValidatorMessages(List<ValidatorMessage> validatorMessages)
    {
        this.validatorMessages = validatorMessages;
    }
}
