/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import java.util.Map;
import java.util.HashMap;

/**
 * Common properties for objects in the SimpleGraph package.
 * Extends HashMap to allow easy implementation of key-value functionality.
 */
public abstract class BasicGraph extends HashMap implements BasicGraphI {

    ///////////////////////////////////////
    // attributes
    private String id;

    private String label;

    ///////////////////////////////////////
    // access methods for attributes
    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

 }

