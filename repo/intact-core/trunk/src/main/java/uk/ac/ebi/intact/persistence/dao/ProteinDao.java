/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.ProteinImpl;

import java.util.List;
import java.util.Map;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface ProteinDao extends InteractorDao<ProteinImpl>
{
    String getIdentityXrefByProteinAc(String proteinAc);

    String getUniprotAcByProteinAc(String proteinAc);

    String getUniprotUrlTemplateByProteinAc(String proteinAc);

    Map<String,Integer> getPartnersCountingInteractionsByProteinAc(String proteinAc);

    Map<String, List<String>> getPartnersWithInteractionAcsByProteinAc(String proteinAc);

    List<String> getPartnersUniprotIdsByProteinAc(String proteinAc);

    Integer countPartnersByProteinAc(String proteinAc);

    List<ProteinImpl> getUniprotProteins(Integer firstResult, Integer maxResults);

    List<ProteinImpl> getUniprotProteinsInvolvedInInteractions(Integer firstResult, Integer maxResults);

    Integer countUniprotProteins();

    Integer countUniprotProteinsInvolvedInInteractions();

    List<ProteinImpl> getByUniprotId(String uniprotId);
}
