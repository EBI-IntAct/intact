/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein;

import uk.ac.ebi.intact.model.Protein;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Feb-2007</pre>
 */
public interface ProteinLoaderService {

    // Requirements:
    // ------------
    //                insert by uniprot AC (single AC or List)
    //                filtering on taxid
    // [low priority] get stats of object created
    //                allow personalized configuration:
    //                      - DR line filter
    //                      - Shortlabel / Fullname
    //                      - Feature chain loading (true/false)
    //                      - Splice variant loading (true/false)

    // [low priority] cache AC -> UniprotProtein with ehcache


    public Collection<Protein> retrieve( String uniprotId );

    public Collection<Protein> retrieve( String uniprotId, int taxidFilter );

    public Collection<Protein> retrieve( String uniprotId, Collection<Integer> taxidFilters );



    public Collection<Protein> retrieve( Collection<String> uniprotIds );

    public Collection<Protein> retrieve( Collection<String> uniprotIds, int taxidFilter );

    public Collection<Protein> retrieve( Collection<String> uniprotIds, Collection<Integer> taxidFilters );

}