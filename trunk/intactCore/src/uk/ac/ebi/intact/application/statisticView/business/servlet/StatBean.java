/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.servlet;

/**
 * Use for the &lt;display:&gt; tag in order to display one line of a table.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class StatBean {
    private String object;
    private int    count;
    private String description;

    public StatBean(String object, int count, String description) {
        this.object = object;
        this.count = count;
        this.description = description;
    }


    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
