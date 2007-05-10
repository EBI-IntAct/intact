package uk.ac.ebi.intact.psixml.converter.shared;

import psidev.psi.mi.xml.model.Organism;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;
import uk.ac.ebi.intact.psixml.converter.annotation.PsiConverter;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@PsiConverter(intactObjectType = BioSource.class, psiObjectType = Organism.class)
public class OrganismConverter extends AbstractIntactPsiConverter<BioSource,Organism>
{
    public OrganismConverter(IntactContext intactContext)
    {
        super(intactContext);
    }

    public BioSource psiToIntact(Organism psiObject)
    {
        if (psiObject == null) return null;

        String shortLabel = psiObject.getNames().getShortLabel();
        String fullName = psiObject.getNames().getFullName();
        int taxId = psiObject.getNcbiTaxId();

        BioSource bioSource = new BioSource(getInstitution(), shortLabel, String.valueOf(taxId));
        bioSource.setFullName(fullName);
        
        return bioSource;
    }

    public Organism intactToPsi(BioSource intactObject)
    {
        throw new UnsupportedOperationException();
    }
}
