/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.

This class take care of all the actions when pressing on the auto-completion button on the Experiment-Editor page.

All experiment are linked to a pubmed Id. Once you have it, you can via a web service (cdbWebservices.jar developped by
Mark Rijnbeek markr@ebi.ac.uk) retrieve some information from pubmed.

Then, you can automatically :
- add the shortlabel of the experiment
- add the fullname of the experiment
- add an annotation with (cvTopic = author-list) and (description = [name of the author])
- add an annotation with (cvTopic = contact-email) and (description = [email]) (if possible, not all articles are
associated to a contact-email
- add a crossreference with (primary Id = pubmedId) and (qualifier = primary-reference)

If an anotation author-list already exists and is different, it will update the description with the new author list
If an xref exists with qualifier = primary-reference but with a different primaryId, it will update the primaryId
If an annotation contact-email already exists it will add a new annotation, with the new email. It will not erase the
previous one.


*/

package uk.ac.ebi.intact.application.editor.struts.action.experiment;

import org.apache.ojb.broker.query.Query;
import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorFormI;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.util.IntactHelperUtil;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.cdb.ExperimentAutoFill;
import uk.ac.ebi.intact.util.cdb.PublicationNotFoundException;
import uk.ac.ebi.intact.util.cdb.UnexpectedException;
import uk.ac.ebi.intact.util.cdb.UpdateExperiments;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * The action class to auto complete a part of the Experiment Editor form from a given pubmedId
 * (after pressing the auto-completion button on Editor - Experiment 
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: AutocompDispatchAction.java, 2005/08/24
 *
 * @struts.action
 *      path="/exp/autocomp"
 *      name="expForm"
 *      input="edit.layout"
 *      scope="session"
 *      validate="false"
 *      parameter="dispatch"
 */

public class AutocompDispatchAction extends AbstractEditorDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("exp.button.autocompletion", "autocomp");

        return map;
    }

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the action servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward autocomp(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.

        EditorFormI editorForm = (EditorFormI) form;

        EditUserI user = getIntactUser(request);
        //user.getUserName();
        //user.getIntactHelper();

        // The current view of the edit session.
        ExperimentViewBean view = (ExperimentViewBean) user.getView();

        String pubmedId=view.getPubmedId();
        if(pubmedId!=null){
            pubmedId = pubmedId.trim();
        }

        /*
        Instantiate the object ExperimentAutoFill (eaf), which use the webService.
        Once this object is created you can call some of its methods like :
        eaf.getFullname()           =====> it will return a valide fullname for the experiment
        eaf.getShortlabel(helper)   =====> it will return a valide shortlabel for the experiment
        eaf.getAuthorList()         =====> it will return the author list
        eaf.getAuthorEmail()        =====> it will return the author email
        */


        try{

            ExperimentAutoFill eaf = new ExperimentAutoFill(pubmedId);

            IntactHelper helper = user.getIntactHelper();

            // The ac of the experiment
            String expAc=view.getAc();

            /*********************************************************************************************
            C r e a t i n g   t h e   f u l l n a m e   a n d   a d d i n g   i t   t o   t h e   v i e w
            *********************************************************************************************/
            String fullname = eaf.getFullname();
            view.setFullName(fullname);

            /*************************************************************************************************
            C r e a t i n g   t h e   s h o r t l a b e l   a n d   a d d i n g   i t   t o   t h e   v i e w
            **************************************************************************************************/
            String shortlabel = eaf.getShortlabel(helper);
            view.setShortLabel(shortlabel);

            /******************************************************************************************
            C r e a t i n g   a u t h o r - l i s t   a n n o t a t i o n   a n d   a d d i n g   i t
            ******************************************************************************************/
            String authorList = eaf.getAuthorList();
            if(!("".equals(authorList) || null==authorList)){

                //Create an object Annotation with author-list cvTopic containing the list of authors
                Annotation authorListAnnotation = authorListAnnotation(authorList,helper);

                //Create a CommentBean from this Annotation
                CommentBean authorListCb = new CommentBean(authorListAnnotation);

                //Create the authorList CvTopic
                //CvTopic authorListTopic = (CvTopic) helper.getObjectByLabel( CvTopic.class, CvTopic.AUTHOR_LIST );

                /*
                Work to do on the view :
                If the view already contains an author-list CommentBean we have to update its description  with the new
                list of author
                */
                boolean annotationUpdated=false;
                List<CommentBean> annotsAlreadyInView=view.getAnnotations();
                for (int i = 0; i < annotsAlreadyInView.size(); i++) {
                    CommentBean cb =  annotsAlreadyInView.get(i);
                    /*
                    If cb's cvTopic is authorList cvTopic and if the list of authors is not the one corresponding to the
                    pubmed Id just entered then set the description of cb with the new author list
                    */
                    if(CvTopic.AUTHOR_LIST.equals(cb.getTopic()) && false==authorListCb.getDescription().equals(cb.getDescription())){
                        cb.setDescription(authorListCb.getDescription());
                        annotationUpdated=true;
                    }
                }

                /*
                Work to do on the database :
                If this experiment is already attached to an author-list annotation which is in the database, we
                update the annotationText of the annotation with the new list of author.
                */
                if(false=="".equals(expAc) && null != expAc){
                    Experiment exp = helper.getObjectByAc(Experiment.class, expAc);
                    //get all the annotations contained in the database linked to this experiment
                    Collection annotations = exp.getAnnotations();
                    for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
                        Annotation annot =  (Annotation) iterator.next();
                        if(CvTopic.AUTHOR_LIST.equals(annot.getCvTopic().getShortLabel()) && false==authorListCb.getDescription().equals(annot.getAnnotationText())){
                            if(helper.isPersistent(annot)){
                                annot.setAnnotationText(authorListCb.getDescription());
                                helper.update(annot);
                            }
                        }
                    }
                }
                /*
                If the authorListCb is not in the view and if the modification hadn't been done by update then add it
                properly to the view.
                */
                if(!view.annotationExists(authorListCb) && annotationUpdated==false){
                    view.addAnnotation(authorListCb);
                }

            }


            /*********************************************************************************************
            C r e a t i n g   e m a i l - c o n t a c t   a n n o t a t i o n   a n d   a d d i n g   i t
            **********************************************************************************************/

            /*
            An email is never deleted automatically, so we just add any new email which is not already in the view.
            */
            String authorEmail = eaf.getAuthorEmail();
            if(!("".equals(authorEmail) || null==authorEmail)){
                Annotation authorEmailAnnotation = authorEmailAnnotation(authorEmail, helper);
                CommentBean authorEmailCb = new CommentBean(authorEmailAnnotation);
                if(!view.annotationExists(authorEmailCb)) {
                    view.addAnnotation(authorEmailCb);
                }
            }

            /*********************************************************************************************
            C r e a t i n g   j o u r n a l   a n n o t a t i o n   a n d   a d d i n g   i t
            **********************************************************************************************/
            String journal = eaf.getJournal();
            if(!("".equals(journal) || null==journal)){
                Annotation journalAnnotation = createJournalAnnotation(journal, helper);
                CommentBean journalCb = new CommentBean(journalAnnotation);



                /*
                Work to do on the view :
                If the view already contains an author-list CommentBean we have to update its description  with the new
                list of author
                */
                boolean annotationUpdated=false;
                List<CommentBean> annotsAlreadyInView=view.getAnnotations();
                for (CommentBean cb : annotsAlreadyInView) {
                    /*
                    If cb's cvTopic is authorList cvTopic and if the list of authors is not the one corresponding to the
                    pubmed Id just entered then set the description of cb with the new author list
                    */
                    if(CvTopic.JOURNAL.equals(cb.getTopic()) && false==journalCb.getDescription().equals(cb.getDescription())){
                        cb.setDescription(journalCb.getDescription());
                        annotationUpdated=true;
                    }
                }

                if(!view.annotationExists(journalCb) && annotationUpdated==false){
                    view.addAnnotation(journalCb);
                }
            }
            /***************************************************************************************************
            C r e a t i n g   p u b l i c a t i o n   y e a r   a n n o t a t i o n   a n d   a d d i n g   i t
            ****************************************************************************************************/

            String pubYear = Integer.toString(eaf.getYear());
            if(!("".equals(pubYear) || null==pubYear)){
                Annotation pubYearAnnotation = createPubYearAnnotation(pubYear, helper);
                CommentBean pubYearCb = new CommentBean(pubYearAnnotation);



                /*
                Work to do on the view :
                If the view already contains an author-list CommentBean we have to update its description  with the new
                list of author
                */
                boolean annotationUpdated=false;
                List annotsAlreadyInView=view.getAnnotations();
                for (int i = 0; i < annotsAlreadyInView.size(); i++) {
                    CommentBean cb =  (CommentBean) annotsAlreadyInView.get(i);
                    /*
                    If cb's cvTopic is authorList cvTopic and if the list of authors is not the one corresponding to the
                    pubmed Id just entered then set the description of cb with the new author list
                    */
                    if(CvTopic.PUBLICATION_YEAR.equals(cb.getTopic()) && false==pubYearCb.getDescription().equals(cb.getDescription())){
                        cb.setDescription(pubYearCb.getDescription());
                        annotationUpdated=true;
                    }
                }

                /*
                Work to do on the database :
                If this experiment is already attached to an author-list annotation which is in the database, we
                update the annotationText of the annotation with the new list of author.
                */
                if(false=="".equals(expAc) && null != expAc){
                    Experiment exp = helper.getObjectByAc(Experiment.class, expAc);
                    //get all the annotations contained in the database linked to this experiment
                    Collection<Annotation> annotations = exp.getAnnotations();
                    for (Annotation annot : annotations){
                        if(CvTopic.PUBLICATION_YEAR.equals(annot.getCvTopic().getShortLabel()) && false==pubYearCb.getDescription().equals(annot.getAnnotationText())){
                            if(helper.isPersistent(annot)){
                                annot.setAnnotationText(pubYearCb.getDescription());
                                helper.update(annot);
                            }
                        }
                    }
                }

                if(!view.annotationExists(pubYearCb) && annotationUpdated==false){
                    view.addAnnotation(pubYearCb);
                }

            }

            /******************************************************************************
            C r e a t i n g   p u b m e d   x r e f e r e n ce   a n d   a d d i n g   i t
            ******************************************************************************/

            boolean xrefUpdated=false;
            //An xref object with primaryId = pubmedId and qualifier=primary-reference
            Xref pubmedXref = pubmedXref(pubmedId, helper);

            //The XreferenceBean corresponding to to the pubmedXref
            XreferenceBean pubmedXb = new XreferenceBean(pubmedXref);

            //The list of all the XreferenceBean contained in the view
            List<XreferenceBean> xrefsAlreadyInView = view.getXrefs();

            /*
            Work to do on the view :
            If the view already contains an xreferenceBean with database=pubmed and qualifier=primary-reference but with
            primaryId !=pubmedId we update its primaryid  with the new pubmed Id
            */
            for (XreferenceBean xrefBean : xrefsAlreadyInView) {
                if(CvDatabase.PUBMED.equals(xrefBean.getDatabase()) && CvXrefQualifier.PRIMARY_REFERENCE.equals(xrefBean.getQualifier()) && false==pubmedId.equals(xrefBean.getPrimaryId())){
                    xrefBean.setPrimaryId(pubmedId);
                    xrefUpdated=true;
                }
            }
            /*
            Work to do on the database :
            If this experiment is already attached to a pubmed xreference (with qualifier = primary-reference) which is
            in the database, we update the primaryId with the new one.
            */
            if(false=="".equals(expAc) && null != expAc){
                Experiment exp = helper.getObjectByAc(Experiment.class, expAc);
                Collection<Xref> xrefs = exp.getXrefs();

                for (Xref xref : xrefs) {
                    if(CvDatabase.PUBMED.equals(xref.getCvDatabase().getShortLabel()) && CvXrefQualifier.PRIMARY_REFERENCE.equals(xref.getCvXrefQualifier().getShortLabel()) && false==pubmedId.equals(xref.getPrimaryId())){
                        if(helper.isPersistent(xref)){
                            xref.setPrimaryId(pubmedId);
                            helper.update(xref);
                        }
                    }
                }
            }
            if(!view.xrefExists(pubmedXb) && xrefUpdated==false){
                view.addXref(pubmedXb);
            }

            view.copyPropertiesTo(editorForm);

        }catch (NumberFormatException e){  //If the pubmed Id do not have the good format
            LOGGER.error("The given pubmed id is not an integer : ", e);
            ActionErrors errors = new ActionErrors();
            errors.add("autocomp", new ActionError("error.exp.autocomp.wrong.format"));
            saveErrors(request, errors);
            setAnchor(request, editorForm);
            // Display the error in the edit page.
            return mapping.getInputForward();
        }catch (PublicationNotFoundException e){  //If the publication is not found
            LOGGER.error(" The publication corresponding to pubmedId " + pubmedId + "couldn't be found : ", e);
            ActionErrors errors = new ActionErrors();
            errors.add("autocomp", new ActionError("error.exp.autocomp.publication.not.found"));
            saveErrors(request, errors);
            setAnchor(request, editorForm);
            // Display the error in the edit page.
            return mapping.getInputForward();
        }catch(UnexpectedException e){ //Unexpected exception
            LOGGER.error("", e);
            ActionErrors errors = new ActionErrors();
            errors.add("autocomp", new ActionError("error.exp.autocomp"));
            saveErrors(request, errors);
            setAnchor(request, editorForm);
            // Display the error in the edit page.
            return mapping.getInputForward();
        }catch(Throwable t){ //Any other kind of exception
            LOGGER.error("",t);
            ActionErrors errors = new ActionErrors();
            errors.add("autocomp", new ActionError("error.exp.autocomp"));
            saveErrors(request, errors);
            setAnchor(request, editorForm);
            // Display the error in the edit page.
            return mapping.getInputForward();
        }

        return mapping.getInputForward();
    }

    /**
     * Given an authorList and an intact helper, it creates an "author-list" Annotation
     *
     * @param authorList a String containing the name of the author separated by a coma
     * ex : Ho Y., Gruhler A., Heilbut A., Bader GD., Moore L., Adams SL., Millar A., Taylor P., Bennett K.
     * @param helper an IntactHelper object
     * @return The author-list Annotation
     * @throws IntactException
     */
    public Annotation authorListAnnotation(String authorList, IntactHelper helper) throws IntactException {

        Annotation authorListAnnot;

        CvTopic authorListTopic = helper.getObjectByLabel( CvTopic.class, CvTopic.AUTHOR_LIST );
        if ( authorListTopic == null ) {
            System.err.println( "Could not find CvTopic(" + CvTopic.AUTHOR_LIST +
                                ")... no author list will be attached/updated to the experiment." );
        }

        authorListAnnot = new Annotation(getService().getOwner(), authorListTopic ,authorList);

        return authorListAnnot;
    }

    /**
     * Given an publication year String and an intact helper, it creates an "author-list" Annotation
     *
     * @param pubYear a String containing the year of publication of the article
     * @param helper an IntactHelper object
     * @return The publication-year Annotation
     * @throws IntactException
     */
    public Annotation createPubYearAnnotation (String pubYear, IntactHelper helper) throws IntactException {
        Annotation pubYearAnnot;

        CvTopic authorListTopic = helper.getObjectByLabel( CvTopic.class, CvTopic.PUBLICATION_YEAR );
        if ( authorListTopic == null ) {
            System.err.println( "Could not find CvTopic(" + CvTopic.PUBLICATION_YEAR +
                                ")... no author list will be attached/updated to the experiment." );
        }

        pubYearAnnot = new Annotation(getService().getOwner(), authorListTopic ,pubYear);

        return pubYearAnnot;

    }

    /**
         * Given an authorList and an intact helper, it creates an "author-list" Annotation
         *
         * @param journal a String containing the name of the journ
         * @param helper an IntactHelper object
         * @return The journal Annotation
         * @throws IntactException
         */
        public Annotation createJournalAnnotation(String journal, IntactHelper helper) throws IntactException {

            Annotation journalAnnot;

            CvTopic authorListTopic = helper.getObjectByLabel( CvTopic.class, CvTopic.JOURNAL );
            if ( authorListTopic == null ) {
                System.err.println( "Could not find CvTopic(" + CvTopic.JOURNAL +
                                    ")... no author list will be attached/updated to the experiment." );
            }

            journalAnnot = new Annotation(getService().getOwner(), authorListTopic ,journal);

            return journalAnnot;
        }

    /**
     * Given an authorEmail and an intact helper, it creates an "contact-email" Annotation
     * @param authorEmail a String containing the email of the author
     * ex : bcrosby@uwindsor.ca
     * @param helper an IntactHelper object
     * @return The contact-email annotation
     * @throws IntactException
     */
    public Annotation authorEmailAnnotation(String authorEmail, IntactHelper helper) throws IntactException {

        Annotation authorEmailAnnot;

        CvTopic authorEmailTopic = helper.getObjectByLabel( CvTopic.class, CvTopic.CONTACT_EMAIL );
        if ( authorEmailTopic == null ) {
            System.err.println( "Could not find CvTopic(" + CvTopic.CONTACT_EMAIL +
                                ")... no email will be attached/updated to the experiments." );
        }
        authorEmailAnnot = new Annotation(getService().getOwner(), authorEmailTopic ,authorEmail);

        return authorEmailAnnot;
    }

    /**
     *  Given and Intact Helper and a pubmedId it create a Xref (pubmed, primary-reference)
     * @param pubmedId a pubmed Id
     * @param helper
     * @return the pubmed Xref
     * @throws IntactException
     */

    public Xref pubmedXref (String pubmedId, IntactHelper helper) throws IntactException {
        Xref pubmedXref;
        CvXrefQualifier primaryRefQualifier = helper.getObjectByLabel( CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE );
        CvDatabase pubmedDatabase= helper.getObjectByLabel(CvDatabase.class, CvDatabase.PUBMED);
        pubmedXref=new Xref(getService().getOwner(),pubmedDatabase,pubmedId,"","",primaryRefQualifier);
        return pubmedXref;
    }


}