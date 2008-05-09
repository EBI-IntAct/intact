/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

/**
 * Classification to be run by the plugin.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Sep-2006</pre>
 */
public class Classification {

    /**
     * Name of the classification.
     * Allowed values: classifications, species, datasets.
     */
    String name;

    /**
     * Is the processing requested.
     */
    boolean enabled;

    public Classification() {
    }

    /**
     * Returns name of the classification. Allowed values: classifications, species, datasets.
     *
     * @return name of the classification.
     */
    public String getName() {
        return (name != null ? name.trim() : name );
    }

    /**
     * Sets name of the classification. Allowed values: classifications, species, datasets.
     *
     * @param name name of the classification.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Returns is the processing requested.
     *
     * @return is the processing requested.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets is the processing requested.
     *
     * @param enabled is the processing requested.
     */
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }
}