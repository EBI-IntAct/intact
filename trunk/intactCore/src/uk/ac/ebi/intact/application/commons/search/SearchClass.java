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
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvAliasType;
import uk.ac.ebi.intact.model.CvFeatureIdentification;
import uk.ac.ebi.intact.model.CvCellType;
import uk.ac.ebi.intact.model.CvInteractionType;
import uk.ac.ebi.intact.model.CvFuzzyType;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvIdentification;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTissue;
import uk.ac.ebi.intact.model.CvComponentRole;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.CvFeatureType;
import uk.ac.ebi.intact.model.NucleicAcidImpl;
import uk.ac.ebi.intact.model.BioSource;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

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
    BIOSOURCE("BioSource", BioSource.class),
    PROTEIN("Protein", ProteinImpl.class),
    NUCLEIC_ACID("NucleicAcid", NucleicAcidImpl.class),
    INTERACTOR("Interactor", InteractorImpl.class),
    INTERACTION("Interaction", InteractionImpl.class),

    CV_OBJECT("CvObject", CvObject.class),
    CV_ALIAS_TYPE("CvAliasType", CvAliasType.class),
    CV_CELL_TYPE("CvCellType", CvCellType.class),
    CV_COMPONENT_ROLE("CvComponentRole", CvComponentRole.class),
    CV_DATABASE("CvDatabase", CvDatabase.class),
    CV_FEATURE_IDENTIFICATION("CvFeatureIdentification", CvFeatureIdentification.class),
    CV_FEATURE_TYPE("CvFeatureType", CvFeatureType.class),
    CV_FUZZY_TYPE("CvFuzzyType", CvFuzzyType.class),
    CV_IDENTIFICATION("CvIdentification", CvIdentification.class),
    CV_INTERACTION("CvInteraction", CvInteraction.class),
    CV_INTERACTION_TYPE("CvInteractionType", CvInteractionType.class),
    CV_INTERACTOR_TYPE("CvInteractorType", CvInteractorType.class),
    CV_TISSUE("CvTissue", CvTissue.class),
    CV_TOPIC("CvTopic", CvTopic.class),
    CV_XREF_QUALIFIER("CvXrefQualifier", CvXrefQualifier.class),

    NOSPECIFIED("No specified", AnnotatedObject.class);

    private Class<? extends AnnotatedObject> mappedClass;
    private String shortName;

    SearchClass(String shortName, Class<? extends AnnotatedObject> mappedClass)
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

        if (shortName.contains("$$"))
        {
            shortName = shortName.substring(0, shortName.indexOf("$$"));
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
        // remove any potential subclass/proxy (due to the use of cglib)
        if (mappedClass.getName().contains("$$"))
        {
            String className = mappedClass.getName();
            String trimmedClass = className.substring(0, className.indexOf("$$"));
            try
            {
                mappedClass = (Class<? extends IntactObject>) Class.forName(trimmedClass);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        for (SearchClass sc : SearchClass.values())
        {
            if (sc.getMappedClass().equals(mappedClass))
            {
                return sc;
            }
        }

        throw new IllegalArgumentException("There is no SearchClass with class: "+mappedClass);
    }

    public Class<? extends AnnotatedObject> getMappedClass()
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

    public boolean isCvObjectSubclass()
    {
        return CvObject.class.isAssignableFrom(mappedClass);
    }

    public static SearchClass[] cvObjectClasses()
    {
        List<SearchClass> cvClasses = new ArrayList<SearchClass>();

        for (SearchClass sc : SearchClass.values())
        {
           if (sc.getShortName().startsWith("Cv"))
           {
               cvClasses.add(sc);
           }
        }

        return cvClasses.toArray(new SearchClass[cvClasses.size()]);

    }

    public static String[] cvObjectClassesAsStringArray()
    {
        List<String> cvClasses = new ArrayList<String>();

        for (SearchClass sc : SearchClass.values())
        {
           if (sc.getShortName().startsWith("Cv"))
           {
               cvClasses.add(sc.getMappedClass().getName());
           }
        }

        return cvClasses.toArray(new String[cvClasses.size()]);

    }

}
