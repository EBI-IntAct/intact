/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.adapters;

import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.Organism;

import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This adapter should be solely used for testing purpose. It creates proteins in a predictable manner without ever
 * relying on the network.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Feb-2007</pre>
 */
public class DummyUniprotService extends AbstractUniprotService {

    // TODO build a static list of proteins that the service can serve.
    // TODO each protein will have specific properties such as given ac being a secondary AC, having splice variant...

    public Collection<UniprotProtein> retreive( String ac ) {
        if ( ac == null ) {
            throw new NullPointerException( "ac must not be null." );
        }
        Collection<UniprotProtein> proteins = new ArrayList<UniprotProtein>( 2 );

        Organism o = new Organism( 9606, "human");
        UniprotProtein protein = new UniprotProtein( ac, ac, o, "ac" );

        proteins.add( protein );

        return proteins;
    }

    public Map<String, Collection<UniprotProtein>> retreive( Collection<String> acs ) {
        Map<String, Collection<UniprotProtein>> map = new HashMap<String, Collection<UniprotProtein>>( acs.size() );

        for ( String ac : acs ) {
            map.put( ac, retreive( ac ) );
        }

        return map;
    }
}