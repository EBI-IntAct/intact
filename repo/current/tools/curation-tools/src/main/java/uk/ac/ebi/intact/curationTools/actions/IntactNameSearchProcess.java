package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.IntactReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.InteractorImpl;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public class IntactNameSearchProcess extends IdentificationActionImpl {

    private IntactContext intactContext;
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotNameSearchProcess.class );

    public IntactNameSearchProcess(){
        super();
        intactContext = null;
    }

    public IntactNameSearchProcess(IntactContext context){
        super();
        intactContext = context;
    }

    public IntactContext getIntactContext() {
        return intactContext;
    }

    public void setIntactContext(IntactContext intactContext) {
        this.intactContext = intactContext;
    }

    private Collection<String> checkOrganism(Collection<InteractorImpl> interactors, String organism, IntactReport report, String name){
        Collection <String> interactorsAc = new ArrayList<String>();

        for (InteractorImpl interactor : interactors){
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

    private Collection<String> processNameSearch(String name, String organism, IntactReport report){

        final DataContext dataContext = this.intactContext.getDataContext();
        final DaoFactory daoFactory = dataContext.getDaoFactory();

        Collection<InteractorImpl> interactors = daoFactory.getInteractorDao().getByShortLabelLike(name);
        Collection <String> interactorsAc = checkOrganism(interactors, organism, report, name);

        if (interactors.isEmpty() && interactorsAc.isEmpty()){
            Status status = new Status(StatusLabel.FAILED, "No IntAct entries are matching the exact shortlabel " + name + " with the organism " + organism);
            report.setStatus(status);

            IntactReport report2 = new IntactReport(ActionName.SEARCH_intact_shortLabel);
            this.listOfReports.add(report2);

            interactors = daoFactory.getInteractorDao().getByShortLabelLike(name + '%');
            interactorsAc = checkOrganism(interactors, organism, report2, name);

            if (interactors.isEmpty() && interactorsAc.isEmpty()){
                Status status2 = new Status(StatusLabel.FAILED, "No IntAct entries are matching the shortlabel %" + name + "%");
                report2.setStatus(status2);

                IntactReport report3 = new IntactReport(ActionName.SEARCH_intact_fullName);
                this.listOfReports.add(report3);

                Query query = daoFactory.getEntityManager().createQuery("select p from InteractorImpl p "+
                        "where p.objClass = 'uk.ac.ebi.intact.model.ProteinImpl' "+
                        "and p.fullName like '%"+ name +"%'");
                interactors = query.getResultList();
                interactorsAc = checkOrganism(interactors, organism, report3, name);

                if (!interactors.isEmpty() && !interactorsAc.isEmpty()){
                    report3.addWarning("The matching Intact entries have a fullName containing the name " + name);
                }
            }
        }
        return interactorsAc;
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        if (this.intactContext == null){
            throw new ActionProcessingException("To be able to search if the Intact database contains a protein with a matching name, we need a non null IntactContext instance. Please, set the IntactContext instance.");
        }

        String geneName = context.getGene_name();
        String protein_name = context.getProtein_name();
        String organism = null;
        if (context.getOrganism() != null){
            organism = context.getOrganism().getTaxId();
        }
        String globalName = context.getGlobalName();

        IntactReport report = new IntactReport(ActionName.SEARCH_intact_exact_shortLabel);
        this.listOfReports.add(report);

        if (organism == null){
            report.addWarning("No organism was given for the protein with : name =  " + context.getGlobalName() != null ? context.getGlobalName() : (context.getGene_name()!= null ? context.getGene_name() : (context.getProtein_name() != null ? context.getProtein_name() : "")) + ". We will process the identification without looking at the organism and choose the entry with the longest sequence.");
        }

        ArrayList<String> intactAccessions = new ArrayList<String>();

        if (geneName != null){
            intactAccessions.addAll(processNameSearch(geneName, organism, report));
        }
        if (protein_name != null){
            intactAccessions.addAll(processNameSearch(protein_name, organism, report));
        }
        if (globalName != null){
            intactAccessions.addAll(processNameSearch(globalName, organism, report));
        }

        IntactReport ir = (IntactReport) this.listOfReports.get(this.listOfReports.size() - 1);

        if (intactAccessions.isEmpty()){
            Status status = new Status(StatusLabel.FAILED, "There is no Intact entry matching the names : " + (geneName != null ? geneName : "no gene name") + (protein_name != null ? " and " + protein_name : " and no protein name") + (globalName != null ? " and " + globalName : "and no other name"));
            ir.setStatus(status);
        }
        else if (intactAccessions.size() == 1){
            Status status = new Status(StatusLabel.COMPLETED, "One Intact entry "+ intactAccessions.get(0) +" is matching the names : " + (geneName != null ? geneName : "no gene name") + (protein_name != null ? " and " + protein_name : " and no protein name") + (globalName != null ? " and " + globalName : "and no other name"));
            ir.setStatus(status);

            ir.setIntactid(intactAccessions.get(0));
        }
        else {
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, intactAccessions.size() +" IntAct entries are matching the names : " + (geneName != null ? geneName : "no gene name") + (protein_name != null ? " and " + protein_name : " and no protein name") + (globalName != null ? " and " + globalName : "and no other name"));
            ir.setStatus(status);

            for (String ac : intactAccessions){
                ir.addPossibleIntactid(ac);
            }
        }
        return null;
    }

}
