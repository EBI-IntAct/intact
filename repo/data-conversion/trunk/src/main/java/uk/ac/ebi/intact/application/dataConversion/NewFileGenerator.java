/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.CvMapping;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlI;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.dao.ExperimentListGeneratorDao;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.NucleicAcid;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-Aug-2006</pre>
 */
public abstract class NewFileGenerator
{

    private static final Log log = LogFactory.getLog(NewFileGenerator.class);

    public static Document generatePsiData(ExperimentListItem eli, PsiVersion psiVersion, CvMapping cvMapping)
    {
        UserSessionDownload session = new UserSessionDownload(psiVersion);
        
        if (cvMapping != null)
        {
            session.setReverseCvMapping(cvMapping);
        }

        Integer firstResult = null;
        Integer maxResults = null;

        if (eli.getLargeScaleChunkSize() != null)
        {
            firstResult = (eli.getChunkNumber()-1)*eli.getLargeScaleChunkSize();
            maxResults = eli.getLargeScaleChunkSize();
        }

        return generateData(eli.getExperimentLabels(), session, firstResult, maxResults);
    }

    private static Document generateData(Collection<String> experimentLabels, UserSessionDownload session, Integer firstInteraction, Integer maxInteractions)
    {
        Collection<Interaction> interactions = ExperimentListGeneratorDao
                .getInteractionByExperimentShortLabel(experimentLabels.toArray(new String[experimentLabels.size()]),
                                            firstInteraction, maxInteractions);

        return generateData(interactions, session);
    }

     /**
     * Convert a list of experiment into PSI XML
     *
     * @param interactions a list of interactions to export in PSI XML
     * @param session     the PSI doanload session.
     *
     * @return the generated XML Document
     */
    private static Document generateData( Collection<Interaction> interactions, UserSessionDownload session ) {

        Interaction2xmlI interaction2xml = Interaction2xmlFactory.getInstance(session);


         // Psi 1 do not tolerate Nucleic Acid as Participant of an Interaction. So if psi verstion is psi1 we
         // filter out all the Interactions having a Nucleic Acid as a participant. Then we check, if there is no
         // any more interactions linked to the experiment, we do not process it.

         if (PsiVersion.getVersion1().getVersion().equals(session.getPsiVersion().getVersion()))
         {
             filterInteractions(interactions);
         }

         if (interactions.size() != 0)
         {
             // in order to have them in that order, experimentList, then interactorList, at last interactionList.
             session.getExperimentListElement();
             session.getInteractorListElement();

             int count = 0;
             for (Interaction interaction : interactions)
             {

                 interaction2xml.create(session, session.getInteractionListElement(), interaction);

                 count++;

                 if ((count % 50) == 0)
                 {
                     log.debug("Interaction: " + count);
                 }
             } // interactions

         }

         return session.getPsiDocument();
    }

    /**
     * It take an interactions Collection and remove from it all the interactions having a NucleicAcid as component.
     * This is used in case psi version is psi1 as psi1 do not allow Nucleic Acid as Interaction's participant.
     * @param interactions Collection of interactions
     */

    public static void filterInteractions(Collection interactions){

        for (Iterator iterator = interactions.iterator(); iterator.hasNext();) {
            Interaction interaction =  (Interaction) iterator.next();
            Collection components = interaction.getComponents();
            for (Iterator iterator1 = components.iterator(); iterator1.hasNext();) {
                Component component =  (Component) iterator1.next();
                if ( component.getInteractor() instanceof NucleicAcid){
                    iterator.remove();
                    break;
                }
            }
        }

    }

}
