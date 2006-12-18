/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.BioSource;

import java.util.Collection;

/**
 * To access to biosources
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Jun-2006</pre>
 */
public interface BioSourceDao extends AnnotatedObjectDao<BioSource> {

    BioSource getByTaxonIdUnique(String taxonId);

    Collection<BioSource> getByTaxonId(String taxonId);
}
