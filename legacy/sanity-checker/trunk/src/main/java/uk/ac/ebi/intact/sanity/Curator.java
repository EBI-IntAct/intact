/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Jul-2006</pre>
 */
public class Curator
{

    /**
    * Id of the curator
    */
    private String id;

    /**
    * Mail of the curator
    */
    private String mail;

    /**
    * Whether the curator has administration capabilities or not
    */
    private boolean admin;

    public Curator()
    {

    }


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMail()
    {
        if (mail == null)
        {
            return id+"@ebi.ac.uk";
        }
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }
}

