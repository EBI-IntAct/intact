package uk.ac.ebi.intact.util.uniprotExport.exporters;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.Interactor;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteractionForScoring;
import uk.ac.ebi.intact.util.uniprotExport.UniprotExportException;
import uk.ac.ebi.intact.util.uniprotExport.results.ExportedClusteredInteractions;
import uk.ac.ebi.intact.util.uniprotExport.results.UniprotExportResults;
import uk.ac.ebi.intact.util.uniprotExport.results.contexts.ExportContext;

/**
 * Interface to implement for classes charged to apply rules for uniprot export.
 * The information about the interactions are in the MiClusterScoreResults. It is up to the class to sort out
 * which interaction can be exported and then fill the list of interactions to export in the result
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/02/11</pre>
 */

public interface InteractionExporter {

    /**
     * select the interactions in the results which can be exported and set the list of interactions to export in the results.
     * This list will contain the Encore identifier of the interactions which were selected for uniprot export
     * @param results : contains the results of the clustering and information about the interactions
     * @throws UniprotExportException
     */
    public void exportInteractionsFrom(UniprotExportResults results) throws UniprotExportException;

    /**
     *
     * @param interaction
     * @param context
     * @return true if the encore interaction pass the uniprot export rules
     * @throws UniprotExportException
     */
    public boolean canExportEncoreInteraction(EncoreInteractionForScoring interaction, ExportContext context) throws UniprotExportException;

    /**
     *
     * @param interaction
     * @param context
     * @return true if the binary interaction can be exported
     * @throws UniprotExportException
     */
    public boolean canExportBinaryInteraction(BinaryInteraction<Interactor> interaction, ExportContext context) throws UniprotExportException;

     /**
     *
     * @param interaction
     * @param context
     * @param positiveInteractions : the results of the clustering and uniprot export for positive interactions
     * @return true if the negative encore interaction pass the uniprot export rules
     * @throws UniprotExportException
     */
    public boolean canExportNegativeEncoreInteraction(EncoreInteractionForScoring interaction, ExportContext context, ExportedClusteredInteractions positiveInteractions) throws UniprotExportException;

    /**
     *
     * @param interaction
     * @param context
     * @param positiveInteractions : the results of the clustering and uniprot export for positive interactions
     * @return true if the negative binary interaction can be exported
     * @throws UniprotExportException
     */
    public boolean canExportNegativeBinaryInteraction(BinaryInteraction<Interactor> interaction, ExportContext context, ExportedClusteredInteractions positiveInteractions) throws UniprotExportException;
}
