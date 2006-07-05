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
public class AnnotatedBean extends IntactBean {

    private String shortlabel;

    private String fullname;

    public AnnotatedBean() {
    }

    public String getShortlabel() {
        return shortlabel;
    }

    public void setShortlabel(String shortlabel) {
        this.shortlabel = shortlabel;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
