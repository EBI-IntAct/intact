package uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.iterators;

import psidev.psi.mi.tab.model.builder.Row;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.util.Iterator;

/**
 * Interface defining the methods of Iterators
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public interface SolrIterator<SolrDocument> extends Iterator {

    SolrDocument next();
    String nextMitabLine();
    Row nextRow();
    BinaryInteraction nextIntactBinaryInteraction();

    void remove();
    boolean hasNext();

    //boolean hasNextWithSameRig(String rig);
}
