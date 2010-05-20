/*package uk.ac.ebi.intact.curationTools.io;

import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.contexts.UpdateContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.proteinIdentification.jaxb.*;

import java.util.List;*/

/**
 * The IdentificationResults converter into JAXB
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-May-2010</pre>
 */

/*public class IdentificationResultConverter {

    private ObjectFactory objectFactory;

    public IdentificationResultConverter(){
        objectFactory = new ObjectFactory();
    }

    private Results convertsResultIntoJAXB (String intActId, IdentificationResults result, IdentificationContext context){
        Results r = this.objectFactory.createResults();
        r.setProtein(intActId);
        r.setUniprotAccession(result.getFinalUniprotId());

        Context c = convertsContextIntoJAXB(context);
        r.setContext(c);

        return r;
    }

    private Context convertsContextIntoJAXB(IdentificationContext context){
        Context c = this.objectFactory.createContext();
        c.setGeneName(context.getGene_name());
        c.setGlobalName(context.getGlobalName());
        c.setProteinName(context.getProtein_name());
        c.setSequence(context.getSequence());
        c.setTaxId(context.getOrganism().getTaxId());

        ListOfIdentifiers listOfIdentifiers = this.objectFactory.createListOfIdentifiers();

        if (context instanceof UpdateContext){
            UpdateContext update = (UpdateContext) context ;

            //for (String id : update.getIdentifiers()){
                //listOfIdentifiers.getIdentifier().add(id);
            //}
        }

        c.setListOfIdentifiers(listOfIdentifiers);

        return c;
    }
}*/
