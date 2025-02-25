package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.*;
import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.listener.InteractionParameterListener;

import javax.persistence.*;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Intact implementation of interaction evidence parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@javax.persistence.Entity
@Table( name = "ia_interaction_evidence_parameter" )
public class InteractionEvidenceParameter extends AbstractIntactParameter{

    private Experiment experiment;

    protected InteractionEvidenceParameter() {
        super();
    }

    public InteractionEvidenceParameter(CvTerm type, ParameterValue value) {
        super(type, value);
    }

    public InteractionEvidenceParameter(CvTerm type, ParameterValue value, CvTerm unit) {
        super(type, value, unit);
    }

    public InteractionEvidenceParameter(CvTerm type, ParameterValue value, CvTerm unit, BigDecimal uncertainty) {
        super(type, value, unit, uncertainty);
    }

    public InteractionEvidenceParameter(CvTerm type, ParameterValue value, BigDecimal uncertainty) {
        super(type, value, uncertainty);
    }

    public InteractionEvidenceParameter(CvTerm type, String value) throws IllegalParameterException {
        super(type, value);
    }

    public InteractionEvidenceParameter(CvTerm type, String value, CvTerm unit) throws IllegalParameterException {
        super(type, value, unit);
    }

    /**
     *
     * @param experiment
     * @deprecated Only kept for backward compatibility with intact core.
     * we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    @Deprecated
    public void setExperiment( Experiment experiment ) {
        this.experiment = experiment;
    }

    @ManyToOne(targetEntity = IntactExperiment.class)
    @JoinColumn( name = "experiment_ac", referencedColumnName = "ac" )
    @Target(IntactExperiment.class)
    /**
     * @deprecated we don't need this as we have a back reference to the participant, interaction which has a reference to the experiment
     */
    @Deprecated
    private Experiment getExperiment() {
        return this.experiment;
    }
}
