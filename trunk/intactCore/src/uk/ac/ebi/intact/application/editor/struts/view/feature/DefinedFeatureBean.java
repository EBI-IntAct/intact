/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Bean to store a defined feature.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class DefinedFeatureBean extends FeatureBean {
    // Class Data

    private static String DEFAULT_SOURCE = "-";
    private static String EXISTING_SOURCE = "Existing Feature";

    // Instance Data

    /**
     * Where is coming from. Init with the default source.
     */
    private String mySource = DEFAULT_SOURCE;

    /**
     * Creates an instance of undefined feature type. The source is set to
     * {@link #DEFAULT_SOURCE}.
     */
    public DefinedFeatureBean() {
        // Sets the range as undefined.
        RangeBean rb = new RangeBean();
        rb.setFromRange("?");
        rb.setToRange("?");

        // Sets the fuzzy type as undetermined types.
        rb.setFromFuzzyTypeAsUndetermined();
        rb.setToFuzzyTypeAsUndetermined();

        addRange(rb);
        setShortLabel("undetermined");
        setFullName("Undetermined feature position");
    }

    /**
     * Instantiate an instance of existing Feature from a given Feature.
     * @param feature the feature to be created as an existing feature. The
     * source is set to {@link #EXISTING_SOURCE}.
     */
    public DefinedFeatureBean(Feature feature) {
        this(feature, EXISTING_SOURCE);
    }

    /**
     * Instantiate an object of this class from a Feature instance and its
     * source.
     *
     * @param feature the <code>Feature</code> object.
     * @param source  where this feature from.
     */
    public DefinedFeatureBean(Feature feature, String source) {
        super(feature);
        mySource = source;
    }

    // Read only properties.

    public String getSource() {
        return mySource;
    }
}
