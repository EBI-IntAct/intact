/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: AliasBean.java,v 1.1 2006/01/12 16:32:26 catherineleroy Exp $
 */
public class AliasBean extends IntactBean {

    private String aliastypeAc;

    private String parent_ac;

    private String name;

    public String getAliastypeAc() {
        return aliastypeAc;
    }

    public void setAliastypeAc(String aliastypeAc) {
        this.aliastypeAc = aliastypeAc;
    }

    public String getParent_ac() {
        return parent_ac;
    }

    public void setParent_ac(String parent_ac) {
        this.parent_ac = parent_ac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
