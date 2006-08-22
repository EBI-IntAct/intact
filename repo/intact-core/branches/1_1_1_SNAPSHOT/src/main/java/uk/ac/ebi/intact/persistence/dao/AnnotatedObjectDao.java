/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.criterion.MatchMode;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvTopic;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface AnnotatedObjectDao<T extends AnnotatedObject> extends IntactObjectDao<T>
{
    T getByShortLabel(String value);

    T getByShortLabel(String value, boolean ignoreCase);

    Collection<T> getByShortLabelLike(String value);

    Collection<T> getByShortLabelLike(String value, boolean ignoreCase);

    T getByXref(String primaryId);

    List<T> getByXrefLike(String primaryId);

    List<T> getByXrefLike(CvDatabase database, String primaryId);

    List<T> getByXrefLike(CvDatabase database, CvXrefQualifier qualifier, String primaryId);

    List<T> getByAnnotationAc(String ac);

    List<T> getByAnnotationTopicAnDescription(CvTopic topic, String description);
}
