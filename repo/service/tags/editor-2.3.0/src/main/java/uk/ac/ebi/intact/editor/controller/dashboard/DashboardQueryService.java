/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.editor.controller.dashboard;

import org.primefaces.model.LazyDataModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.editor.controller.JpaAwareController;
import uk.ac.ebi.intact.editor.controller.UserSessionController;
import uk.ac.ebi.intact.editor.util.LazyDataModelFactory;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.service.ComplexService;
import uk.ac.ebi.intact.model.Publication;

import javax.faces.context.FacesContext;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "session" )
public class DashboardQueryService extends JpaAwareController {

    public static final String[] DEFAULT_STATUS_SHOWN = new String[]{"PL:0004", "PL:0007", "PL:0008"};
    private LazyDataModel<Publication> allPublications;
    private LazyDataModel<Publication> ownedByUser;
    private LazyDataModel<Publication> reviewedByUser;

    private LazyDataModel<IntactComplex> allComplexes;
    private LazyDataModel<IntactComplex> complexesOwnedByUser;
    private LazyDataModel<IntactComplex> complexesReviewedByUser;

    private boolean hideAcceptedAndReleased;
    private String[] statusToShow;

    private boolean isPublicationTableEnabled = false;
    private boolean isComplexTableEnabled = false;

    public DashboardQueryService() {
        hideAcceptedAndReleased = true;

        statusToShow = DEFAULT_STATUS_SHOWN;
    }

    @SuppressWarnings("unchecked")
    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public void loadData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            UserSessionController userSessionController = ApplicationContextProvider.getBean("userSessionController");
            if (userSessionController.hasRole("CURATOR") || userSessionController.hasRole("REVIEWER") ){
                isPublicationTableEnabled = true;
            }
            if (userSessionController.hasRole("COMPLEX_CURATOR") || userSessionController.hasRole("COMPLEX_REVIEWER") ){
                isComplexTableEnabled = true;
            }
            refreshTables();
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public void loadJamiData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            UserSessionController userSessionController = ApplicationContextProvider.getBean("userSessionController");
            if (userSessionController.hasRole("CURATOR") || userSessionController.hasRole("REVIEWER") ){
                isPublicationTableEnabled = true;
            }
            if (userSessionController.hasRole("COMPLEX_CURATOR") || userSessionController.hasRole("COMPLEX_REVIEWER") ){
                isComplexTableEnabled = true;
            }
            refreshJamiTables();
        }
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public void refreshTables() {
        if (isPublicationTableEnabled){
            UserSessionController userSessionController = (UserSessionController) getSpringContext().getBean("userSessionController");
            final String userId = userSessionController.getCurrentUser().getLogin().toUpperCase();

            if (statusToShow.length == 0) {
                addWarningMessage("No statuses selected", "Using default status selection");
                statusToShow = DEFAULT_STATUS_SHOWN;
            }

            StringBuilder statusToShowSql = new StringBuilder();

            for (int i=0; i<statusToShow.length; i++) {
                if (i>0) {
                    statusToShowSql.append(" or");
                }
                statusToShowSql.append(" p.status.identifier = '").append(statusToShow[i]).append("'");
            }

            String additionalSql = statusToShowSql.toString();

            allPublications = LazyDataModelFactory.createLazyDataModel(getCoreEntityManager(),
                    "select p from Publication p left join fetch p.xrefs as x where " + additionalSql,
                    "select count(distinct p.ac) from Publication p where " + additionalSql,"p", "updated", false);

            ownedByUser = LazyDataModelFactory.createLazyDataModel( getCoreEntityManager(),
                    "select p from Publication p left join fetch p.xrefs as x where upper(p.currentOwner.login) = '"+userId+"'"+
                            " and ("+additionalSql+")",
                    "select count(distinct p.ac) from Publication p where upper(p.currentOwner.login) = '"+userId+"'"+
                            " and ("+additionalSql+")","p", "updated", false);

            reviewedByUser = LazyDataModelFactory.createLazyDataModel( getCoreEntityManager(),
                    "select p from Publication p left join fetch p.xrefs as x where upper(p.currentReviewer.login) = '"+userId+"'"+
                            " and ("+additionalSql+")",
                    "select count(distinct p.ac) from Publication p where upper(p.currentReviewer.login) = '"+userId+"'"+
                            " and ("+additionalSql+")","p", "updated", false);
        }
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public void refreshJamiTables() {
        if (isComplexTableEnabled){
            UserSessionController userSessionController = (UserSessionController) getSpringContext().getBean("userSessionController");
            final String userId = userSessionController.getCurrentUser().getLogin().toUpperCase();

            if (statusToShow.length == 0) {
                addWarningMessage("No statuses selected", "Using default status selection");
                statusToShow = DEFAULT_STATUS_SHOWN;
            }
            StringBuilder statusToShowSql2 = new StringBuilder();

            for (int i=0; i<statusToShow.length; i++) {
                if (i>0) {
                    statusToShowSql2.append(" or");
                }
                statusToShowSql2.append(" p.cvStatus.identifier = '").append(statusToShow[i]).append("'");
            }

            String additionalSql2 = statusToShowSql2.toString();
            ComplexService complexService = getComplexService();

            allComplexes = LazyDataModelFactory.createLazyDataModel(complexService,
                    "select p from IntactComplex p where " + additionalSql2,
                    "select count(distinct p.ac) from IntactComplex p where " + additionalSql2, "p", "updated", false);

            complexesOwnedByUser = LazyDataModelFactory.createLazyDataModel( complexService,
                    "select p from IntactComplex p where upper(p.currentOwner.login) = '"+userId+"'"+
                            " and ("+additionalSql2+")",
                    "select count(distinct p.ac) from IntactComplex p where upper(p.currentOwner.login) = '"+userId+"'"+
                            " and ("+additionalSql2+")", "p", "updated", false);

            complexesReviewedByUser = LazyDataModelFactory.createLazyDataModel( complexService,
                    "select p from IntactComplex p where upper(p.currentReviewer.login) = '"+userId+"'"+
                            " and ("+additionalSql2+")",
                    "select count(distinct p.ac) from IntactComplex p where upper(p.currentReviewer.login) = '"+userId+"'"+
                            " and ("+additionalSql2+")", "p", "updated", false);
        }
    }

    public LazyDataModel<Publication> getAllPublications() {
        return allPublications;
    }

    public LazyDataModel<Publication> getOwnedByUser() {
        return ownedByUser;
    }

    public LazyDataModel<Publication> getReviewedByUser() {
        return reviewedByUser;
    }

    public boolean isHideAcceptedAndReleased() {
        return hideAcceptedAndReleased;
    }

    public void setHideAcceptedAndReleased(boolean hideAcceptedAndReleased) {
        this.hideAcceptedAndReleased = hideAcceptedAndReleased;
    }

    public String[] getStatusToShow() {
        return statusToShow;
    }

    public void setStatusToShow(String[] statusToShow) {
        this.statusToShow = statusToShow;
    }

    public LazyDataModel<IntactComplex> getAllComplexes() {
        return allComplexes;
    }

    public LazyDataModel<IntactComplex> getComplexesOwnedByUser() {
        return complexesOwnedByUser;
    }

    public LazyDataModel<IntactComplex> getComplexesReviewedByUser() {
        return complexesReviewedByUser;
    }

    public boolean isPublicationTableEnabled() {
        return isPublicationTableEnabled;
    }

    public boolean isComplexTableEnabled() {
        return isComplexTableEnabled;
    }
}
