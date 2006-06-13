/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.imex.psivalidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ActionEvent;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12-Jun-2006</pre>
 */
public class PsiValidatorBean
{

    private static final Log log = LogFactory.getLog(PsiValidatorBean.class);

    private boolean uploadLocalFile;

    private UploadedFile psiFile;
    private String psiUrl;

    private PsiReport currentPsiReport;

    public PsiValidatorBean()
    {
        this.uploadLocalFile = true;
    }

    public void uploadTypeChanged(ValueChangeEvent vce)
    {
        String type = (String) vce.getNewValue();
        uploadLocalFile = type.equals("local");

        if (log.isDebugEnabled())
            log.debug("Upload type changed, is local file? "+uploadLocalFile);
    }

    public void uploadFile(ActionEvent evt)
    {

        try
        {
            if (uploadLocalFile)
            {
                uploadFromLocalFile();
            }
            else
            {
                uploadFromUrl();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void uploadFromLocalFile() throws IOException
    {
        if (log.isInfoEnabled())
        {
           log.info("Uploading local file: "+psiFile.getName());
        }

        byte[] content = psiFile.getBytes();

        String name = psiFile.getName();
        InputStream is = new ByteArrayInputStream(content);

        PsiReportBuilder builder = new PsiReportBuilder(name, is);
        this.currentPsiReport = builder.createPsiReport();
    }

    private void uploadFromUrl() throws IOException
    {
        if (log.isInfoEnabled())
        {
           log.info("Uploading Url: "+psiUrl);
        }

        try
        {
            URL url = new URL(psiUrl);

            String name = psiUrl.substring(psiUrl.lastIndexOf("/")+1, psiUrl.length());

            PsiReportBuilder builder = new PsiReportBuilder(name, url);
            this.currentPsiReport = builder.createPsiReport();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

    }


    public void validateUrlFormat(FacesContext context,
                          UIComponent toValidate,
                          Object value)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Validating URL: "+value);
        }

        currentPsiReport = null;

        URL url = null;
        UIInput inputCompToValidate = (UIInput)toValidate;

        String toValidateClientId = inputCompToValidate.getClientId(context);

        try
        {
            url = new URL((String)value);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();

            inputCompToValidate.setValid(false);
            context.addMessage(toValidateClientId, new FacesMessage("Not a valid URL"));
            return;
        }

        try
        {
            url.openStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();

            inputCompToValidate.setValid(false);
            context.addMessage(toValidateClientId, new FacesMessage("Unknown URL"));
        }

    }

    // ACCESSORS

    public boolean isUploadLocalFile()
    {
        return uploadLocalFile;
    }

    public void setUploadLocalFile(boolean uploadLocalFile)
    {
        this.uploadLocalFile = uploadLocalFile;
    }

    public UploadedFile getPsiFile()
    {
        return psiFile;
    }

    public void setPsiFile(UploadedFile psiFile)
    {
        this.psiFile = psiFile;
    }

    public String getPsiUrl()
    {
        return psiUrl;
    }

    public void setPsiUrl(String psiUrl)
    {
        this.psiUrl = psiUrl;
    }

    public PsiReport getCurrentPsiReport()
    {
        return currentPsiReport;
    }

    public void setCurrentPsiReport(PsiReport currentPsiReport)
    {
        this.currentPsiReport = currentPsiReport;
    }
}
