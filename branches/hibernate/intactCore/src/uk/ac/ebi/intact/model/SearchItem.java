/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

/**
 * For an item in the ia_search table
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Apr-2006</pre>
 */
@Entity
@Table(name = "ia_search")
public class SearchItem extends IntactObjectImpl
{

    private String ac;
    private String value;
    private String objClass;
    private String type;

    public SearchItem()
    {

    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getObjClass()
    {
        return objClass;
    }

    public void setObjClass(String objClass)
    {
        this.objClass = objClass;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

}
