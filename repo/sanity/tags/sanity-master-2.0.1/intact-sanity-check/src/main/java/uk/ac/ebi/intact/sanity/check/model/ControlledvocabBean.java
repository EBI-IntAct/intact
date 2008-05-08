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
 * @version $Id: ControlledvocabBean.java,v 1.2 2005/10/13 10:52:04 catherineleroy Exp $
 */
public class ControlledvocabBean extends AnnotatedBean{
    private String objclass;

    public ControlledvocabBean() {
    }

    public String getObjclass() {
        return objclass;
    }

    public void setObjclass(String objclass) {
        this.objclass = objclass;
    }
}
