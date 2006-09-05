/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Experiment;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface ExperimentDao extends AnnotatedObjectDao<Experiment>
{
    Integer countInteractionsForExperimentWithAc(String ac);

    List<Interaction> getInteractionsForExperimentWithAc(String ac, int firstResult, int maxResults);

    List<Interaction> getInteractionsForExperimentWithAcExcluding(String ac, String[] excludedAcs, int firstResult, int maxResults);
}
