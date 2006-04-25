/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.commons.search;

import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.InteractorImpl;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.CvObject;

/**
 * Enumeration used to map between the class to search as String (it comes from the GUI beans) to the
 * entity class (the class mapped with the ORM tool).
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Apr-2006</pre>
 */
public enum SearchClass
{
    EXPERIMENT("Experiment", Experiment.class),
    PROTEIN("Protein", ProteinImpl.class),
    INTERACTOR("Interactor", InteractorImpl.class),
    INTERACTION("Interaction", InteractionImpl.class),
    CVOBJECT("CvObject", CvObject.class),
    NOSPECIFIED("No specified", IntactObject.class);

    private Class<? extends IntactObject> mappedClass;
    private String shortName;

    SearchClass(String shortName, Class<? extends IntactObject> mappedClass)
    {
        this.shortName = shortName;
        this.mappedClass = mappedClass;
    }

    @Override
    public String toString()
    {
        return getShortName();
    }

    public static SearchClass valueOfShortName(String shortName)
    {
        if (shortName == null)
        {
            throw new NullPointerException("shortName");
        }

        if (shortName.trim().equals(""))
        {
            return NOSPECIFIED;
        }

        for (SearchClass sc : SearchClass.values())
        {
            if (sc.getShortName().equals(shortName))
            {
                return sc;
            }
        }

        throw new IllegalArgumentException("There is no SearchClass with name: "+shortName);
    }

    public static SearchClass valueOfMappedClass(Class<? extends IntactObject> mappedClass)
    {
        for (SearchClass sc : SearchClass.values())
        {
            if (sc.getMappedClass().equals(mappedClass))
            {
                return sc;
            }
        }

        throw new IllegalArgumentException("There is no SearchClass with class: "+mappedClass);
    }

    public Class<? extends IntactObject> getMappedClass()
    {
        return mappedClass;
    }

    public String getShortName()
    {
        return shortName;
    }

    public boolean isSpecified()
    {
        return this != NOSPECIFIED;
    }
}
