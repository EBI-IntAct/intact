/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import java.util.Map;

/**
 * A simple node class for temporary processing, e.g. to prepare output for graph analysis packages.
 */
public class Node extends BasicGraph implements NodeI {

    ///////////////////////////////////////
    // attributes
    private String ac;

    ///////////////////////////////////////
    // access methods for attributes
    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }
}
