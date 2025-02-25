package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implementation of xref for resulting sequences attached to model ranges
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table( name = "ia_modelled_resulting_sequence_xref" )
public class ModelledResultingSequenceXref extends AbstractIntactXref{

    public ModelledResultingSequenceXref() {
    }

    public ModelledResultingSequenceXref(CvTerm database, String id, CvTerm qualifier) {
        super(database, id, qualifier);
    }

    public ModelledResultingSequenceXref(CvTerm database, String id, String version, CvTerm qualifier) {
        super(database, id, version, qualifier);
    }

    public ModelledResultingSequenceXref(CvTerm database, String id, String version) {
        super(database, id, version);
    }

    public ModelledResultingSequenceXref(CvTerm database, String id) {
        super(database, id);
    }
}
