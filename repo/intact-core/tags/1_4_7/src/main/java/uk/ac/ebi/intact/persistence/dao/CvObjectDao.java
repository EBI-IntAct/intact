/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.CvObject;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface CvObjectDao<T extends CvObject> extends AnnotatedObjectDao<T>
{
     List<T> getByPsiMiRefCollection(Collection<String> psiMis);
//    List<T> getAll(boolean excludeObsolete, boolean excludeHidden);
}
