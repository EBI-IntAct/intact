package uk.ac.ebi.intact.psixml.converter.shared;

import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.Organism;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;
import uk.ac.ebi.intact.psixml.converter.annotation.PsiConverter;
import uk.ac.ebi.intact.psixml.converter.util.ConverterUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@PsiConverter(intactObjectType = BioSource.class, psiObjectType = Organism.class)
public class OrganismConverter extends AbstractIntactPsiConverter<BioSource, Organism> {

    public OrganismConverter(IntactContext intactContext, Entry parentEntry) {
        super(intactContext, parentEntry);
    }

    public BioSource psiToIntact(Organism psiObject) {
        if (psiObject == null) return null;

        String shortLabel = psiObject.getNames().getShortLabel();
        int taxId = psiObject.getNcbiTaxId();

        BioSource bioSource = new BioSource(getInstitution(), shortLabel, String.valueOf(taxId));
        ConverterUtils.populateNames(psiObject.getNames(), bioSource);

        return bioSource;
    }

    public Organism intactToPsi(BioSource intactObject) {
        throw new UnsupportedOperationException();
    }
}
