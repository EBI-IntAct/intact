/*
 * Copyright 2006 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.context.IntactContext;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Util methods for annotatedObject.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public class AnnotatedObjectUtils {

    private AnnotatedObjectUtils() {
    }

    /**
     * Trims a shortlabel if it is too long to be inserted in the database.
     *
     * @return the shortlabel.
     */
    public static String prepareShortLabel( String shortLabel ) {
        boolean modified = false;

        if ( shortLabel == null ) {

            throw new NullPointerException( "Must define a non null short label" );

        } else {
            // delete leading and trailing spaces.
            shortLabel = shortLabel.trim();

            if ( "".equals( shortLabel ) ) {
                throw new IllegalArgumentException(
                        "Must define a non empty short label" );
            }

            if ( shortLabel.length() >= AnnotatedObject.MAX_SHORT_LABEL_LEN ) {
                shortLabel = shortLabel.substring( 0, AnnotatedObject.MAX_SHORT_LABEL_LEN );
                modified = true;
            }
        }

        return shortLabel;
    }

    /**
     * Search for all Xrefs having Xref with the given CvDatabase.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param db the non null CvDatabase filter.
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs( AnnotatedObject ao, CvDatabase db ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( db == null ) {
            throw new NullPointerException( "CvDatabase must not be null." );
        }

        Collection<Xref> xrefs = new ArrayList<Xref>( ao.getXrefs().size() );

        for ( Iterator<Xref> iterator = ao.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = iterator.next();
            if ( db.equals( xref.getCvDatabase() ) ) {
                xrefs.add( xref );
            }
        }

        return xrefs;
    }

    /**
     * Search for all Xrefs having Xref with both the given CvDatabase and CvXrefQualifier.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param db the non null CvDatabase filter.
     * @param qu the non null CvXrefQualifier filter.
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs( AnnotatedObject ao, CvDatabase db, CvXrefQualifier qu ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( db == null ) {
            throw new NullPointerException( "CvDatabase must not be null." );
        }
        if ( qu == null ) {
            throw new NullPointerException( "CvXrefQualifier must not be null." );
        }

        Collection<Xref> xrefs = new ArrayList<Xref>( ao.getXrefs().size() );

        for ( Iterator<Xref> iterator = ao.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = iterator.next();
            if ( db.equals( xref.getCvDatabase() ) && qu.equals( xref.getCvXrefQualifier() ) ) {
                xrefs.add( xref );
            }
        }

        return xrefs;
    }

    /**
     * Search for all Xrefs having Xref with the given CvXrefQualifier.
     *
     * @param ao the non null AnnotatedObject to search on.
     * @param qu the non null CvXrefQualifier filter.
     *
     * @return a non null Collection of Xref, may be empty.
     */
    public static Collection<Xref> searchXrefs( AnnotatedObject ao, CvXrefQualifier qu ) {

        if ( ao == null ) {
            throw new NullPointerException( "AnnotatedObject must not be null." );
        }
        if ( qu == null ) {
            throw new NullPointerException( "CvXrefQualifier must not be null." );
        }

        Collection<Xref> xrefs = new ArrayList<Xref>( ao.getXrefs().size() );

        for ( Iterator<Xref> iterator = ao.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = iterator.next();
            if ( qu.equals( xref.getCvXrefQualifier() ) ) {
                xrefs.add( xref );
            }
        }

        return xrefs;
    }

    /**
     * Gets the generic Xref type for an AnnotatedObject class
     * @param clazz an AnnotatedObject class
     * @return the Xref type used in the class
     */
    public static Class<? extends Xref> getXrefClassType(Class<? extends AnnotatedObject> clazz)  {
        clazz = CgLibUtil.removeCglibEnhanced(clazz);

        PropertyDescriptor propDesc = null;
        try {
            propDesc = new PropertyDescriptor("xrefs", clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        Method method = propDesc.getReadMethod();

        return getParameterizedType(method.getGenericReturnType());
    }


    /**
     * Gets the generic Xref type for an AnnotatedObject class
     * @param clazz an AnnotatedObject class
     * @return the Xref type used in the class
     *
     * @since 1.6.1
     */
    public static Class<? extends Alias> getAliasClassType(Class<? extends AnnotatedObject> clazz)  {
        clazz = CgLibUtil.removeCglibEnhanced(clazz);

        PropertyDescriptor propDesc = null;
        try {
            propDesc = new PropertyDescriptor("aliases", clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        Method method = propDesc.getReadMethod();

        return getParameterizedType(method.getGenericReturnType());
    }

    /**
     * Finds an Annotations with a topic that has an MI or label equal to the value provided
     * @param annotatedObject The annotatedObject to find the annotation
     * @param miOrLabel The MI (use it when possible) or the shortLabel
     * @return The annotation with that CvTopic. Null if no annotation for that CV is found
     *
     * @since 1.8.0
     */
    public static Annotation findAnnotationByTopicMiOrLabel(AnnotatedObject<?, ?> annotatedObject, String miOrLabel) {
        for (Annotation annotation : annotatedObject.getAnnotations()) {
            final CvTopic topic = annotation.getCvTopic();
            if (topic != null && (miOrLabel.equals(topic.getMiIdentifier()) || miOrLabel.equals(topic.getShortLabel()))) {
                return annotation;
            }
        }
        return null;
    }

    private static Class getParameterizedType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            return (Class) paramType.getActualTypeArguments()[0];
        }
        return null;
    }

    /**
     * Find all annotations having any of the provided CvTopics.
     *
     * @param annotatedObject the object to serch on.
     * @param topics          the topics we are searching.
     * @return a non null collection of Annotation.
     */
    public static Collection<Annotation> findAnnotationsByCvTopic( AnnotatedObject<?, ?> annotatedObject,
                                                                   Collection<CvTopic> topics ) {
        if ( annotatedObject == null ) {
            throw new NullPointerException( "You must give a non null annotatedObject" );
        }

        if ( topics == null ) {
            throw new NullPointerException( "You must give a non null collection of CvTopic" );
        }

        Collection<Annotation> annotations = new ArrayList<Annotation>();
        if( ! topics.isEmpty() ) {
            for ( Annotation annotation : annotatedObject.getAnnotations() ) {
                if( topics.contains( annotation.getCvTopic() )) {
                    annotations.add( annotation );
                }
            }
        }
        return annotations;
    }

     /**
     * Check if the object state is "new" or "managed". This check is useful in those
     * cases where we need to check if the collections (annotations, aliases and xrefs) are
     * accessible and won't throw a LazyInitializationException if accessed.
     * @param annotatedObject The AnnotatedObject to check
     * @return True if is new or managed
     */
    public static boolean isNewOrManaged(AnnotatedObject annotatedObject) {
        // is it new?
        if (annotatedObject.getAc() == null) return true;

        // is it transient? (as in opposition to managed)
        if ( IntactContext.currentInstanceExists() &&
            IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().isTransient(annotatedObject)) {
            return false;
        }

        return true;
    }



    /**
     * Checks if two given annotated objects contain the same set of annotations, xrefs and aliases
     * @param ao1 Annotated object 1
     * @param ao2 Annotated object 2
     * @return if the two annotated objects contain the same set of annnotations, xrefs and aliases
     */
    public static boolean containSameCollections(AnnotatedObject ao1, AnnotatedObject ao2) {
        if (!containSameXrefs(ao1, ao2)) {
            return false;
        }
        if (!containSameAnnotations(ao1, ao2)) {
            return false;
        }
        if (!containSameAliases(ao1, ao2)) {
            return false;
        }
        return true;
    }


     public static boolean containSameAnnotations(AnnotatedObject ao1, AnnotatedObject ao2) {
        return areCollectionEqual(ao1.getAnnotations(), ao2.getAnnotations());
    }

    public static boolean containSameXrefs(AnnotatedObject ao1, AnnotatedObject ao2) {
        return areCollectionEqual(ao1.getXrefs(), ao2.getXrefs());
    }

    public static boolean containSameAliases(AnnotatedObject ao1, AnnotatedObject ao2) {
        return areCollectionEqual(ao1.getAliases(), ao2.getAliases());
    }


    /**
     * Method to compare Annotation, Xref and Aliases collections
     * @param intactObjects1 Annotations, Xrefs or Aliases
     * @param intactObjects2 Annotations, Xrefs or Aliases
     * @return true if the collections are equal
     */
    private static boolean areCollectionEqual(Collection<? extends IntactObject> intactObjects1, Collection<? extends IntactObject> intactObjects2) {
        if (intactObjects1.size() != intactObjects2.size()) {
            return false;
        }

        List<String> uniqueStrings1 = new ArrayList<String>();

        for (IntactObject io1 : intactObjects1) {
            uniqueStrings1.add(createUniqueString(io1));
        }

        for (IntactObject io2 : intactObjects2) {
            String unique2 = createUniqueString(io2);

            if (!uniqueStrings1.contains(unique2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates unique strings for Annotations,Xrefs and aliases.
     * @param io the object to use
     * @return a unique string for that object
     */
    protected static String createUniqueString(IntactObject io) {
        if (io == null) throw new NullPointerException("IntactObject cannot be null to create a unique String");

        if (io instanceof Annotation) {
            Annotation annot = (Annotation)io;
            String cvId = (annot.getCvTopic() != null)? annot.getCvTopic().getMiIdentifier() : "";
            return annot.getAnnotationText()+"__"+cvId;
        } else if (io instanceof Xref) {
            Xref xref = (Xref)io;
            String qualId = (xref.getCvXrefQualifier() != null)? xref.getCvXrefQualifier().getMiIdentifier() : "";
            return xref.getPrimaryId()+"__"+xref.getCvDatabase().getMiIdentifier()+"__"+qualId;
        } else if (io instanceof Alias) {
            Alias alias = (Alias)io;
            String typeId = (alias.getCvAliasType() != null)? alias.getCvAliasType().getMiIdentifier() : "";
            return alias.getName()+"__"+typeId;
        }
        return io.toString();
    }

}
