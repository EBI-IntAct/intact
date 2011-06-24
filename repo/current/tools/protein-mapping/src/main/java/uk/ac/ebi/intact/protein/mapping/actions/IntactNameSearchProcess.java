package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.InteractorImpl;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.IntactReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultIntactReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is looking into the IntAct database for proteins with a shortlabel or fullname  which could match the name of the protein
 * to identify
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public class IntactNameSearchProcess extends IdentificationActionImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotNameSearchProcess.class );

    /**
     * Create an IntactNameSearchProcess with an Intact context which is null and should be set later using the setIntactContext method
     */
    public IntactNameSearchProcess(ReportsFactory factory){
        super(factory);
    }

    /**
     * Check that the organisms of the matching interactors in Intact are matching the organism of the protein to identify
     * @param interactors : the interactors in IntAct which are matching the name of the protein to identify
     * @param organism : the organism of the protein to identify
     * @param report : the current report of this object
     * @param name : the name of the protein to identify
     * @return the list of matching interactors in Intact which are matching the organism of the protein to identify
     */
    private Collection<String> checkOrganism(Collection<InteractorImpl> interactors, String organism, IntactReport report, String name){
        Collection <String> interactorsAc = new ArrayList<String>();

        for (InteractorImpl interactor : interactors){
            // The organism of the matching interactor in Intact
            BioSource interactorBiosource = interactor.getBioSource();

            if (interactorBiosource == null && organism != null){
                report.addWarning("The interactor " + interactor.getAc() + " was matching the name " + name + ". As the organism of the matching protein is null we cannot take into account this protein.");
            }
            else if (organism != null && interactorBiosource != null){
                if (interactorBiosource.getTaxId() != null){
                    if (organism.equals(interactorBiosource.getTaxId())){
                        interactorsAc.add(interactor.getAc());
                    }
                    else {
                        report.addWarning("The interactor " + interactor.getAc() + " was matching the name " + name + " but its organism " + interactorBiosource.getAc() + " is not matching " + organism);
                    }
                }
                else {
                    report.addWarning("The interactor " + interactor.getAc() + " was matching the name " + name + " but its organism ("+interactorBiosource.getAc()+") doesn't have a valid taxId and is not matching " + organism);
                }
            }
            else if (organism == null){
                interactorsAc.add(interactor.getAc());
            }
            else {
                report.addWarning("The interactor " + interactor.getAc() + " was matching the name " + name + " but its organism is null and is not matching " + organism);
            }
        }

        return interactorsAc;
    }

    /**
     * Process the name search into Intact as following :
     * 1) search exact shortlabel
     * 2) search shortlabel like 'name%'
     * 3) search fullname like '%name%'
     * @param name : the name of the protein to identify
     * @param organism : the organism of the protein to identify
     * @param report : the report
     * @return The list of Intact accessions which are matching the name of the protein as well as the organism
     */
    private Collection<String> processNameSearch(String name, String organism, IntactReport report){
        IntactContext intactContext = IntactContext.getCurrentInstance();

        // create the data context
        final DataContext dataContext = intactContext.getDataContext();
        final DaoFactory daoFactory = dataContext.getDaoFactory();

        // the list of interactors with this exact shortlabel
        Collection<InteractorImpl> interactors = daoFactory.getInteractorDao().getByShortLabelLike(name);

        // check if the matching interactors are also matching the organism
        Collection <String> interactorsAc = checkOrganism(interactors, organism, report, name);

        // No interactors matching exact shortlabel and organism
        if (interactors.isEmpty() && interactorsAc.isEmpty()){
            Status status = new Status(StatusLabel.FAILED, "No IntAct entries are matching the exact shortlabel " + name + " with the organism " + organism);
            report.setStatus(status);

            // New search = new DefaultIntactReport
            IntactReport report2 = getReportsFactory().getIntactReport(ActionName.SEARCH_intact_shortLabel);
            this.listOfReports.add(report2);

            // the list of interactors with shortlabel like 'name%'
            interactors = daoFactory.getInteractorDao().getByShortLabelLike(name + '%');
            // check if the matching interactors are also matching the organism
            interactorsAc = checkOrganism(interactors, organism, report2, name);

            // No interactors matching shortlabel like 'name%' and organism
            if (interactors.isEmpty() && interactorsAc.isEmpty()){
                Status status2 = new Status(StatusLabel.FAILED, "No IntAct entries are matching the shortlabel %" + name + "%");
                report2.setStatus(status2);

                // New search = new DefaultIntactReport
                DefaultIntactReport report3 = new DefaultIntactReport(ActionName.SEARCH_intact_fullName);
                this.listOfReports.add(report3);

                // get the list of interactors with fullname like '%name%'
                Query query = daoFactory.getEntityManager().createQuery("select p from InteractorImpl p "+
                        "where p.objClass = 'uk.ac.ebi.intact.model.ProteinImpl' "+
                        "and p.fullName like '%"+ name +"%'");
                interactors = query.getResultList();
                // check if the matching interactors are also matching the organism
                interactorsAc = checkOrganism(interactors, organism, report3, name);

                if (!interactors.isEmpty() && !interactorsAc.isEmpty()){
                    report3.addWarning("The matching Intact entries have a fullName containing the name " + name);
                }
            }
        }
        return interactorsAc;
    }

    /**
     * It will look if the gene name, protein name and/or general name of the protein to identify is matching a shortlabel of fullname of an Intact entry
     * @param context : the context of the protein
     * @return Always null as the process doesn't aimed at finding an unique uniprot entry but aimed at finding an unique IntAct entry. It Will add the results of the process
     * (Intact accession, possible intact entries, etc.) on an DefaultIntactReport which will be added to the list of reports of this object.
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {

        // Always clear the previous report of this object
        this.listOfReports.clear();

        String geneName = context.getGene_name();
        String protein_name = context.getProtein_name();
        String organism = null;

        if (context.getOrganism() != null){
            organism = context.getOrganism().getTaxId();
        }
        String globalName = context.getGlobalName();

        // create an DefaultIntactReport
        IntactReport report = getReportsFactory().getIntactReport(ActionName.SEARCH_intact_exact_shortLabel);
        this.listOfReports.add(report);

        if (organism == null){
            report.addWarning("No organism was given for the protein with : name =  " + context.getGlobalName() != null ? context.getGlobalName() : (context.getGene_name()!= null ? context.getGene_name() : (context.getProtein_name() != null ? context.getProtein_name() : "")) + ". We will process the identification without looking at the organism and choose the entry with the longest sequence.");
        }

        // the list will contain all the possible intact accessions
        ArrayList<String> intactAccessions = new ArrayList<String>();

        // if the gene name is not null, query Intact with the gene name
        if (geneName != null){
            intactAccessions.addAll(processNameSearch(geneName, organism, report));
        }
        // if the protein name is not null, query Intact with the protein name
        if (protein_name != null){
            intactAccessions.addAll(processNameSearch(protein_name, organism, report));
        }
        // if the general name is not null, query Intact with the general name
        if (globalName != null){
            intactAccessions.addAll(processNameSearch(globalName, organism, report));
        }

        // finish to write the status of the last report
        IntactReport ir = (IntactReport) this.listOfReports.get(this.listOfReports.size() - 1);

        if (intactAccessions.isEmpty()){
            Status status = new Status(StatusLabel.FAILED, "There is no Intact entry matching the names : " + (geneName != null ? geneName : "no gene name") + (protein_name != null ? " and " + protein_name : " and no protein name") + (globalName != null ? " and " + globalName : "and no other name"));
            ir.setStatus(status);
        }
        else if (intactAccessions.size() == 1){
            Status status = new Status(StatusLabel.COMPLETED, "One Intact entry "+ intactAccessions.get(0) +" is matching the names : " + (geneName != null ? geneName : "no gene name") + (protein_name != null ? " and " + protein_name : " and no protein name") + (globalName != null ? " and " + globalName : "and no other name"));
            ir.setStatus(status);

            ir.setIntactAc(intactAccessions.get(0));
        }
        else {
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, intactAccessions.size() +" IntAct entries are matching the names : " + (geneName != null ? geneName : "no gene name") + (protein_name != null ? " and " + protein_name : " and no protein name") + (globalName != null ? " and " + globalName : "and no other name"));
            ir.setStatus(status);

            for (String ac : intactAccessions){
                ir.addPossibleIntactAc(ac);
            }
        }
        return null;
    }

}
