/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.InteractorImpl;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface InteractorDao<T extends InteractorImpl> extends AnnotatedObjectDao<T>
{
    Integer countInteractionsForInteractorWithAc(String ac);

    List<String> getGeneNamesByInteractorAc(String proteinAc);
}
