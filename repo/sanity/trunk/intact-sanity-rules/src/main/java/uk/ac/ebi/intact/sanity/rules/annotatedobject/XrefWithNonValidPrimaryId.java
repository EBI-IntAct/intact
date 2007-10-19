package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.Field;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.*;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Xref's primaryId checker.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */

@SanityRule (target = AnnotatedObject.class, group = { RuleGroup.INTACT, RuleGroup.IMEX })
public class XrefWithNonValidPrimaryId implements Rule<AnnotatedObject<?,?>> {

    public Collection<GeneralMessage> check(AnnotatedObject<?,?> intactObject) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (Xref xref : intactObject.getXrefs()) {
            String primaryId = xref.getPrimaryId();

            String idValidationRegexp = getIdValidationRegexp(xref.getCvDatabase());

            if (idValidationRegexp != null && !primaryId.matches(idValidationRegexp)) {
                XrefMessage xrefMessage = new XrefMessage( MessageDefinition.XREF_INVALID_PRIMARYID, intactObject, xref);

                Field regexField = new Field();
                regexField.setName("Regexp");
                regexField.setValue(idValidationRegexp);
                xrefMessage.getInsaneObject().getField().add(regexField);

                messages.add(xrefMessage);
            }
        }

        return messages;
    }

    protected String getIdValidationRegexp(AnnotatedObject<?,?> annotatedObject) {
        String regexp = null;

        for (Annotation annotation : annotatedObject.getAnnotations()) {
            if (annotation.getCvTopic() != null) {
                CvObjectXref idXref = CvObjectUtils.getPsiMiIdentityXref(annotation.getCvTopic());
                if (idXref != null &&
                    idXref.getPrimaryId().equals(CvTopic.XREF_VALIDATION_REGEXP_MI_REF)) {
                     regexp = annotation.getAnnotationText();
                }
            }
        }

        return regexp;
    }
}