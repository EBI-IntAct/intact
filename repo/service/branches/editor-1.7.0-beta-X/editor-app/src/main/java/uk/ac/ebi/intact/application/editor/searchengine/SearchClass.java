/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.application.editor.searchengine;

import uk.ac.ebi.intact.model.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Enumeration used to map between the class to search as String (it comes from the GUI beans) to the
 * entity class (the class mapped with the ORM tool).
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
public enum SearchClass {

    EXPERIMENT( "Experiment", Experiment.class ),
    BIOSOURCE( "BioSource", BioSource.class ),
    PROTEIN( "Protein", ProteinImpl.class ),
    NUCLEIC_ACID( "NucleicAcid", NucleicAcidImpl.class ),
    INTERACTOR( "Interactor", InteractorImpl.class ),
    INTERACTION( "Interaction", InteractionImpl.class ),
    SMALL_MOLECULE( "SmallMolecule", SmallMoleculeImpl.class ),
    COMPONENT( "Component", Component.class ),
    FEATURE("Feature",Feature.class),

    CV_OBJECT( "CvObject", CvObject.class ),
    CV_ALIAS_TYPE( "CvAliasType", CvAliasType.class ),
    CV_CELL_TYPE( "CvCellType", CvCellType.class ),
    CV_COMPONENT_ROLE("CvComponentRole",CvComponentRole.class),
    CV_BIOLOGICAL_ROLE( "CvBiologicalRole", CvBiologicalRole.class ),
    CV_EXPERIMENTAL_ROLE( "CvExperimentalRole", CvExperimentalRole.class ),
    CV_DATABASE( "CvDatabase", CvDatabase.class ),
    CV_FEATURE_IDENTIFICATION( "CvFeatureIdentification", CvFeatureIdentification.class ),
    CV_FEATURE_TYPE( "CvFeatureType", CvFeatureType.class ),
    CV_FUZZY_TYPE( "CvFuzzyType", CvFuzzyType.class ),
    CV_IDENTIFICATION( "CvIdentification", CvIdentification.class ),
    CV_INTERACTION( "CvInteraction", CvInteraction.class ),
    CV_INTERACTION_TYPE( "CvInteractionType", CvInteractionType.class ),
    CV_INTERACTOR_TYPE( "CvInteractorType", CvInteractorType.class ),
    CV_TISSUE( "CvTissue", CvTissue.class ),
    CV_TOPIC( "CvTopic", CvTopic.class ),
    CV_XREF_QUALIFIER( "CvXrefQualifier", CvXrefQualifier.class ),
    NOSPECIFIED( "No specified", AnnotatedObject.class );

    private Class<? extends AnnotatedObject> mappedClass;
    private String shortName;

    SearchClass( String shortName, Class<? extends AnnotatedObject> mappedClass ) {
        this.shortName = shortName;
        this.mappedClass = mappedClass;
    }

    @Override
    public String toString() {
        return getShortName();
    }

    public static SearchClass valueOfShortName( String shortName ) {
        if ( shortName == null ) {
            throw new NullPointerException( "shortName" );
        }

        if ( shortName.contains( "$$" ) ) {
            shortName = shortName.substring( 0, shortName.indexOf( "$$" ) );
        }

        if ( shortName.trim().equals( "" ) ) {
            return NOSPECIFIED;
        }

        for ( SearchClass sc : SearchClass.values() ) {
            if ( sc.getShortName().equals( shortName ) ) {
                return sc;
            }
        }

        throw new IllegalArgumentException( "There is no SearchClass with name: " + shortName );
    }

    public static SearchClass valueOfMappedClass( Class<? extends IntactObject> mappedClass ) {
        // remove any potential subclass/proxy (due to the use of cglib)
        if ( mappedClass.getName().contains( "$$" ) ) {
            String className = mappedClass.getName();
            String trimmedClass = className.substring( 0, className.indexOf( "$$" ) );
            try {
                mappedClass = ( Class<? extends IntactObject> ) Class.forName( trimmedClass );
            }
            catch ( ClassNotFoundException e ) {
                e.printStackTrace();
            }
        }

        for ( SearchClass sc : SearchClass.values() ) {
            if ( sc.getMappedClass().equals( mappedClass ) ) {
                return sc;
            }
        }

        throw new IllegalArgumentException( "There is no SearchClass with class: " + mappedClass );
    }

    public Class<? extends AnnotatedObject> getMappedClass() {
        return mappedClass;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isSpecified() {
        return this != NOSPECIFIED;
    }

    public boolean isCvObjectSubclass() {
        return CvObject.class.isAssignableFrom( mappedClass );
    }

    public static SearchClass[] cvObjectClasses() {
        List<SearchClass> cvClasses = new ArrayList<SearchClass>();

        for ( SearchClass sc : SearchClass.values() ) {
            if ( sc.getShortName().startsWith( "Cv" ) ) {
                cvClasses.add( sc );
            }
        }

        return cvClasses.toArray( new SearchClass[cvClasses.size()] );

    }

    public static String[] cvObjectClassesAsStringArray() {
        List<String> cvClasses = new ArrayList<String>();

        for ( SearchClass sc : SearchClass.values() ) {
            if ( sc.getShortName().startsWith( "Cv" ) ) {
                cvClasses.add( sc.getMappedClass().getName() );
            }
        }

        return cvClasses.toArray( new String[cvClasses.size()] );

    }

    public static String[] annotatedObjectClassesAsStringArray() {
        List<String> aoClasses = new ArrayList<String>();

        for ( SearchClass sc : SearchClass.values() ) {
            if ( AnnotatedObject.class.isAssignableFrom( sc.getMappedClass() )
                 && sc != SearchClass.NOSPECIFIED ) {
                aoClasses.add( sc.getMappedClass().getName() );
            }
        }

        return aoClasses.toArray( new String[aoClasses.size()] );
    }

}
