/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
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
