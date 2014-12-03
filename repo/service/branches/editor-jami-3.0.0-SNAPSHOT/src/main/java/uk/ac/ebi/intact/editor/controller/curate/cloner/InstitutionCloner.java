package uk.ac.ebi.intact.editor.controller.curate.cloner;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

/**
 * Cv cloner
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/12/14</pre>
 */

public class InstitutionCloner extends AbstractCvTermCloner<Source, IntactSource>{
    @Override
    protected Annotation instantiateAnnotation(CvTerm topic, String value) {
        return new CvTermAnnotation(topic, value);
    }

    @Override
    protected Xref instantiateXref(CvTerm database, String id, String version, CvTerm qualifier) {
        return new CvTermXref(database, id, version, qualifier);
    }

    @Override
    protected Alias instantiateAliase(CvTerm type, String name) {
        return new CvTermAlias(type, name);
    }

    @Override
    protected IntactSource instantiateNewCloneFrom(Source cv) {
        return new IntactSource(cv.getShortName());
    }
}
