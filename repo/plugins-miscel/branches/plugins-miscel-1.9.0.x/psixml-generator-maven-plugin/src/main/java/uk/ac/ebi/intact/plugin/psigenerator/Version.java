/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:Version.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since <pre>11-Aug-2006</pre>
 */
public class Version
{

    private static final Log log = LogFactory.getLog(Version.class);

    private String number;
    private String folderName;
    private boolean validate;

    public Version()
    {
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }

    public String toString()
    {
        return getNumber();
    }

    public boolean isValidate()
    {
        return validate;
    }

    public void setValidate(boolean validate)
    {
        this.validate = validate;
    }
}
