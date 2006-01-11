/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import uk.ac.ebi.intact.util.sanityChecker.model.*;
import uk.ac.ebi.intact.util.Crc64;
import uk.ac.ebi.intact.application.commons.util.AnnotationSection;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

import javax.mail.MessagingException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.io.IOException;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.ojb.broker.accesslayer.LookupException;



/**
 * TODO comment it.
 *
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class
        SanityChecker {
    private int duplicProt = 0;
    private SanityCheckerHelper sch;

    private EditorUrlBuilder editorUrlBuilder;

    /*
    * Those are the several SanityCheckerHelper to help retrieving the data in the database
    * I have done several sanityCheckerHelper object because you can associate only one sql request to each type of bean
    * (InteractorBean, ExperimentBean ...etc). But for the same type of Bean the different checkMethod will need
    * different type of sql request. So in order to avoid confusion I have created several Sanity check helper.
    * If the name of the sanityCheckHelper is onHoldSch if means that it is used in the method ExperimentIsOnHold and
    * InteractionIsOnHold. If the name is sch12 it means that it is used to check the rule 12.
    */
    private SanityCheckerHelper featureSch;
    private SanityCheckerHelper superCuratedSch;
    private SanityCheckerHelper deletionFeatureSch;
    private SanityCheckerHelper onHoldSch;
    private SanityCheckerHelper toBeReviewedSch;
    private SanityCheckerHelper noUniprotUpdateSch;
    private SanityCheckerHelper sch12;
    private SanityCheckerHelper sch13;
    private SanityCheckerHelper retrieveObjectSch;
    private SanityCheckerHelper oneIntOneExpSch;
    private SanityCheckerHelper hasValidPrimaryIdSch;
    private SanityCheckerHelper annotationTopic;
    private SanityCheckerHelper rangeSeqSch;
    private SanityCheckerHelper objectFromAc;
    /* ControlledvocabBean */
    private static ControlledvocabBean onHoldCvBean;
    private static ControlledvocabBean toBeReviewedCvBean;
    private static ControlledvocabBean acceptedCvBean;
    private static ControlledvocabBean noUniprotUpdateCvBean;

    private static ControlledvocabBean neutralCvBean;
    private static ControlledvocabBean baitCvBean;
    private static ControlledvocabBean fluorophoreAcceptorCvBean;
    private static ControlledvocabBean fluorophoreDonorCvBean;
    private static ControlledvocabBean electronAcceptorCvBean;
    private static ControlledvocabBean electronDonorCvBean;
    private static ControlledvocabBean preyCvBean;
    private static ControlledvocabBean enzymeCvBean;
    private static ControlledvocabBean enzymeTargetCvBean;
    private static ControlledvocabBean selfCvBean;
    private static ControlledvocabBean unspecifiedCvBean;
    private static ControlledvocabBean cTerminalCvBean;
    private static ControlledvocabBean nTerminalCvBean;
    private static ControlledvocabBean undeterminedCvBean;

    /**
     * Xref databases
     */
    private static ControlledvocabBean uniprotDatabaseCvBean;

    private static ControlledvocabBean pubmedDatabaseCvBean;

    private static ControlledvocabBean newtDatabaseCvBean;

    /**
     * Describe wether an Xref is related the primary SPTR AC (identityCrefQualifier) or not (secondaryXrefQualifier)
     */
    private static ControlledvocabBean primaryReferenceXrefQualifierCvBean;

    private static ControlledvocabBean identityXrefQualifierCvBean;

    /**
     * Is in charge to send the message to the curators and the admin
     */
    private MessageSender messageSender;

    private AnnotationSection annotationSection;

    private Map cvTopics;

    private IntactHelper helper;


    public SanityChecker() throws IntactException, SQLException {

        editorUrlBuilder = new EditorUrlBuilder();

        helper = new IntactHelper();

        featureSch = new SanityCheckerHelper(helper);
        featureSch.addMapping(FeatureBean.class, "select ac, shortlabel, fullname, userstamp, updated from ia_feature where ac like ? ");
        featureSch.addMapping(RangeBean.class, "select ac from ia_range where feature_ac = ? ");


        objectFromAc = new SanityCheckerHelper(helper);
        objectFromAc.addMapping(ExperimentBean.class, "select ac, shortlabel, fullname, updated, userstamp, detectmethod_ac, identmethod_ac, biosource_ac from ia_experiment where ac = ?");
        objectFromAc.addMapping(InteractorBean.class, "select ac, shortlabel, fullname, crc64, updated, userstamp, biosource_ac, interactiontype_ac, objclass from ia_interactor where ac = ? ");


        deletionFeatureSch = new SanityCheckerHelper(helper);
        deletionFeatureSch.addMapping(RangeBean.class, " select i.userstamp, i.updated, c.interaction_ac, c.interactor_ac, r.feature_ac , r.ac , r.fromintervalend, r.tointervalstart "+
                                                       " from ia_interactor i, ia_feature f, ia_range r, ia_controlledvocab ident, ia_component c, ia_controlledvocab type "+
                                                       " where i.ac = c.interaction_ac and "+
                                                             " c.ac=f.component_ac and " +
	                                                         " f.identification_ac = ident.ac and " +
                                                             " f.featuretype_ac = type.ac and " +
                                                             " ident.shortlabel = 'deletion analysis' and " +
                                                             " (type.shortlabel = 'mutation' or type.shortlabel = 'mutation decreasing' or type.shortlabel = 'mutation increasing') and " +
                                                             " f.ac = r.feature_ac and "+
                                                             " (r.tointervalstart - r.fromintervalend) > ? ");

        rangeSeqSch = new SanityCheckerHelper(helper);
        rangeSeqSch.addMapping(RangeBean.class,"select r.ac, fromintervalstart, fromfuzzytype_ac, sequence, f.component_ac " +
                                               "from ia_range r, ia_feature f, ia_component c, ia_interactor i " +
                                               "where i.ac=c.interactor_ac and " +
                                               "c.ac=f.component_ac and " +
                                               "f.ac=r.feature_ac and " +
                                               "sequence is not null and "+
                                               "i.ac = ?");

        rangeSeqSch.addMapping(ComponentBean.class, "select interaction_ac from ia_component where ac=?");
        rangeSeqSch.addMapping(InteractorBean.class, "select ac, userstamp, updated, objclass from ia_interactor where ac=?");

        this.retrieveObjectSch = new SanityCheckerHelper(helper);

        retrieveObjectSch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac FROM ia_exp2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(Bs2AnnotBean.class, "SELECT biosource_ac FROM ia_biosource2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(Int2AnnotBean.class, "SELECT interactor_ac FROM ia_int2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(CvObject2AnnotBean.class, "SELECT cvobject_ac FROM ia_cvobject2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(Feature2AnnotBean.class, "SELECT feature_ac FROM ia_feature2annot WHERE annotation_ac=?");

        this.hasValidPrimaryIdSch=new SanityCheckerHelper(helper);
        hasValidPrimaryIdSch.addMapping(AnnotationBean.class, "select a.description " +
                                                              "from ia_annotation a, ia_cvobject2annot c2a " +
                                                              "where c2a.cvobject_ac = ? and "+//in (select ac from ia_controlledvocab where objclass like '" + CvDatabase.class.getName() + "') and " +
                                                              "c2a.annotation_ac=a.ac and " +
                                                              "a.topic_ac=(select ac from ia_controlledvocab where shortlabel='" + CvTopic.XREF_VALIDATION_REGEXP + "')");

        this.oneIntOneExpSch = new SanityCheckerHelper(helper);
        /*oneIntOneExpSch.addMapping(Int2ExpBean.class,"select interaction_ac, experiment_ac "+
        "from ia_int2exp "+
        "where interaction_ac = ? and "+
        "interaction_ac in ( select interaction_ac "+
        "from ia_int2exp "+
        "group by interaction_ac "+
        "having count(experiment_ac) > 1)");  */

        oneIntOneExpSch.addMapping(Int2ExpBean.class,"select interaction_ac, experiment_ac "+
                                                     "from ia_int2exp "+
                                                     "where interaction_ac like ? and "+
                                                     "interaction_ac in ( select interaction_ac "+
                                                     "from ia_int2exp "+
                                                     "group by interaction_ac "+
                                                     "having count(experiment_ac) > 1)");

        //oneIntOneExpSch.addMapping(Int2ExpBean.class, )
        oneIntOneExpSch.addMapping(ExperimentBean.class, "select ac, shortlabel, updated, userstamp "+
                                                         "from ia_experiment "+
                                                         "where ac = ? ");
        oneIntOneExpSch.addMapping(InteractorBean.class, "select ac, objclass, shortlabel, updated, userstamp "+
                                                         "from ia_interactor "+
                                                         "where ac = ? ");

        this.sch12=new SanityCheckerHelper(helper);
        sch12.addMapping(ExperimentBean.class,"select e.ac " +
                                              "from ia_controlledvocab c, ia_experiment e " +
                                              "where c.ac=e.detectmethod_ac and  " +
                                              "e.ac = ? and " +
                                              "c.ac in (select ac " +
                                              "from ia_controlledvocab " +
                                              "where objclass = '" + CvInteraction.class.getName() + "')");

        this.sch13=new SanityCheckerHelper(helper);
        sch13.addMapping(ExperimentBean.class,"select e.ac " +
                                              "from ia_controlledvocab c, ia_experiment e " +
                                              "where c.ac=e.identmethod_ac and  " +
                                              "e.ac = ? and " +
                                              "c.ac in (select ac " +
                                              "from ia_controlledvocab " +
                                              "where objclass = '" + CvIdentification.class.getName() + "')");

        cvTopics = new HashMap();
        this.annotationTopic = new SanityCheckerHelper(helper);
        annotationTopic.addMapping(ControlledvocabBean.class,"select shortlabel, ac, objclass from ia_controlledvocab where objclass = '"+ CvTopic.class.getName() +"' and ac like ?");

        List cvTopicBeans = annotationTopic.getBeans(ControlledvocabBean.class,"%");
        for (int i = 0; i < cvTopicBeans.size(); i++) {
            ControlledvocabBean cvBean =  (ControlledvocabBean) cvTopicBeans.get(i);
            cvTopics.put(cvBean.getAc(), cvBean.getShortlabel());
        }

        this.sch = new SanityCheckerHelper(helper);
        sch.addMapping(ControlledvocabBean.class, "SELECT ac, objclass FROM ia_controlledvocab WHERE shortlabel = ?");
        List cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.ON_HOLD);
        this.onHoldCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.TO_BE_REVIEWED);
        toBeReviewedCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.ACCEPTED);
        acceptedCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvDatabase.NEWT);
        newtDatabaseCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.NON_UNIPROT);
        noUniprotUpdateCvBean = (ControlledvocabBean) cvs.get(0);

        sch.addMapping(ControlledvocabBean.class, "SELECT ac, objclass FROM ia_controlledvocab WHERE ac IN ( SELECT parent_ac FROM ia_xref WHERE primaryid = ? )" );
        neutralCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0497").get(0);
        baitCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0496" ).get(0);
        preyCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0498" ).get(0);
        fluorophoreAcceptorCvBean =(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0584" ).get(0);
        fluorophoreDonorCvBean =(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0583" ).get(0);
        electronAcceptorCvBean =(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0580" ).get(0);
        electronDonorCvBean =(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0579" ).get(0);
        enzymeCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0501" ).get(0);
        enzymeTargetCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0502" ).get(0);
        selfCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0503" ).get(0);
        unspecifiedCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0499" ).get(0);

        uniprotDatabaseCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class,"MI:0486" ).get(0);

        pubmedDatabaseCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class,"MI:0446" ).get(0);

        primaryReferenceXrefQualifierCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class,"MI:0358" ).get(0);

        identityXrefQualifierCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0356" ).get(0);

        cTerminalCvBean=(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0334" ).get(0);

        nTerminalCvBean=(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0340" ).get(0);

        undeterminedCvBean=(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0339" ).get(0);


        this.onHoldSch = new SanityCheckerHelper(helper);
        onHoldSch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac "+
                                                  "FROM ia_exp2annot where experiment_ac = ? "+
                                                  "AND annotation_ac IN "+
                                                  "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");
        onHoldSch.addMapping(Int2AnnotBean.class, "SELECT interactor_ac "+
                                                  "FROM ia_int2annot where interactor_ac = ? "+
                                                  "AND annotation_ac IN "+
                                                  "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");

        this.toBeReviewedSch =  new SanityCheckerHelper(helper);
        toBeReviewedSch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac "+
                                                        "FROM ia_exp2annot where experiment_ac = ? "+
                                                        "AND annotation_ac IN "+
                                                        "(SELECT ac FROM ia_annotation WHERE topic_ac='" + toBeReviewedCvBean.getAc() +"')");

        noUniprotUpdateSch= new SanityCheckerHelper(helper);
        noUniprotUpdateSch.addMapping(Exp2AnnotBean.class, "SELECT interactor_ac "+
                                                           "FROM ia_int2annot where interactor_ac = ? "+
                                                           "AND annotation_ac IN "+
                                                           "(SELECT ac FROM ia_annotation WHERE topic_ac='" + noUniprotUpdateCvBean.getAc() +"')");

        sch.addMapping(Int2AnnotBean.class, "SELECT interactor_ac "+
                                            "FROM ia_int2annot where interactor_ac = ? "+
                                            "AND annotation_ac IN "+
                                            "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");

        sch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac "+
                                            "FROM ia_exp2annot where experiment_ac = ? "+
                                            "AND annotation_ac IN "+
                                            "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");

        sch.addMapping(Int2ExpBean.class, "SELECT experiment_ac " +
                                          "FROM ia_int2exp " +
                                          "WHERE interaction_ac = ?");

        sch.addMapping(InteractorBean.class, "SELECT ac, objclass, userstamp, updated " +
                                             "FROM ia_interactor " +
                                             "WHERE ac = ? " +
                                             "AND interactiontype_ac IS NULL");

        sch.addMapping(ComponentBean.class,"SELECT interactor_ac, role, stoichiometry " +
                                           "FROM ia_component " +
                                           "WHERE interaction_ac = ? ");

        sch.addMapping(ExperimentBean.class,"SELECT biosource_ac, ac, shortlabel, userstamp, updated " +
                                            "FROM ia_experiment " +
                                            "WHERE ac like ?");

        sch.addMapping(XrefBean.class, "SELECT ac, database_ac, qualifier_ac, userstamp, updated "+
                                       "FROM ia_xref "+
                                       "WHERE parent_ac = ?");

        sch.addMapping(BioSourceBean.class,"SELECT ac, shortlabel, taxid, userstamp, updated " +
                                           "FROM ia_biosource " +
                                           "WHERE ac like ?");

        sch.addMapping(SequenceChunkBean.class, "select sequence_chunk, sequence_index " +
                                                "from ia_sequence_chunk " +
                                                "where parent_ac = ? " +
                                                "order by sequence_index");

        superCuratedSch = new SanityCheckerHelper(helper);
        superCuratedSch.addMapping(ExperimentBean.class,"select ac, userstamp, updated, shortlabel from ia_experiment where ac not in " +
	                                                        "(select e.ac " +
	                                                            "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
	                                                            "where e.ac=e2a.experiment_ac and " +
	                                                            "e2a.annotation_ac=a.ac and " +
	                                                            "a.topic_ac in ('"+acceptedCvBean.getAc()+"','"+toBeReviewedCvBean.getAc()+"')) "+
                                                                "and to_date(created,'DD-MON-YYYY HH24:MI:SS') >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS') and ac like ? ");


        messageSender = new MessageSender();

        annotationSection = new AnnotationSection(helper);


    }

    public boolean interactionIsOnHold(String ac ) throws SQLException {
        boolean onHold=false;
        List int2AnnotBeans = onHoldSch.getBeans(Int2AnnotBean.class,ac);
        if(!int2AnnotBeans.isEmpty()){
            onHold=true;
        }

        return onHold;
    }

    /**
     * This method check if every feature are associated to at list one range
     * @throws SQLException
     * @throws IntactException
     */



    public void featureWithoutRange() throws SQLException, IntactException {
       List featureBeans = featureSch.getBeans(FeatureBean.class,"%");
       for (int i = 0; i < featureBeans.size(); i++) {
           FeatureBean feature =  (FeatureBean) featureBeans.get(i);
           List rangeBeans = featureSch.getBeans(RangeBean.class, feature.getAc());
           if (rangeBeans.isEmpty()){
               AnnotatedBean annotatedBean = feature;
               messageSender.addMessage(annotatedBean, ReportTopic.FEATURE_WITHOUT_A_RANGE);
           }
       }
   }


    /**
     * This function search if this experiment is associated to any on-hold annotation. If any, onHold is set to true
     *
     * @param ac The string corresponding to the ac of an experiment
     * @return Return the boolean true if the experiment is on hold and false if it is not on hold
     * @throws SQLException
     */
    public boolean experimentIsOnHold(String ac ) throws SQLException {
        boolean onHold=false;
        List exp2AnnotBeans = onHoldSch.getBeans(Exp2AnnotBean.class,ac);
        if(!exp2AnnotBeans.isEmpty()){
            onHold=true;
        }
        return onHold;
    }

    /**
     * This function search if a protein (interactor having ProteinImpl as objclass) is associated to a no-uniprot-update
     * annotation. If it is, the boolean noUniprotUpdate is set to true
     * @param ac corresponding to a protein
     * @return Return the boolean true if the protein is a no-uniprot-update and false if it is not
     * @throws SQLException
     */

    public boolean noUniprotUpdate(String ac) throws SQLException {
        boolean noUniprotUpdate=false;
        List exp2AnnotBeans = noUniprotUpdateSch.getBeans(Exp2AnnotBean.class,ac);
        if(!exp2AnnotBeans.isEmpty()){
            noUniprotUpdate=true;
        }
        return noUniprotUpdate;

    }

    /**
     * This function search if this experiment is associated to any to-be-reviewed annotation. If any, onHold is set
     * to true
     *
     * @param ac The string corresponding to the ac of an experiment
     * @return Return the boolean true if the experiment is to-be-reviewed and false if it is not.
     * @throws SQLException
     */

    public boolean experimentToBeReviewed(String ac ) throws SQLException {
        boolean toBeReviewed=false;
        List exp2AnnotBeans = toBeReviewedSch.getBeans(Exp2AnnotBean.class,ac);
        if(!exp2AnnotBeans.isEmpty()){
            toBeReviewed=true;
        }
        return toBeReviewed;
    }


    /**
     * Performs Interaction checks
     *
     * @param interactorBeans List containing interactorBean having objclass equal to InteractionImpl
     * @throws IntactException if there was a search problem
     * @throws SQLException
     */

    public void checkInteractionsBaitAndPrey( List interactorBeans ) throws IntactException, SQLException {
        //System.out.println( "Checking on Interactions (rule 6) ..." );

        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactionBean =  (InteractorBean) interactorBeans.get(i);
            String interactionAc =  interactionBean.getAc();

            if(false == interactionIsOnHold(interactionAc)){
                List componentBeans = sch.getBeans(ComponentBean.class,interactionAc);
                int preyCount = 0,
                        baitCount = 0,
                        enzymeCount = 0,
                        enzymeTargetCount = 0,
                        neutralCount = 0,
                        selfCount = 0,
                        unspecifiedCount = 0,
                        fluorophoreAcceptorCount = 0,
                        fluorophoreDonorCount = 0,
                        electronAcceptorCount = 0,
                        electronDonorCount = 0;
                float selfStoichiometry = 0;
                float neutralStoichiometry = 0;

                for (int j = 0; j < componentBeans.size(); j++) {
                    ComponentBean componentBean =  (ComponentBean) componentBeans.get(j);

                    if ( baitCvBean.getAc().equals( componentBean.getRole() ) ) {
                        baitCount++;
                    } else if ( preyCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        preyCount++;
                    } else if ( enzymeCvBean.getAc().equals( componentBean.getRole() ) ) {
                        enzymeCount++;
                    } else if ( enzymeTargetCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        enzymeTargetCount++;
                    } else if ( neutralCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        neutralCount++;
                        neutralStoichiometry = componentBean.getStoichiometry();
                    } else if ( selfCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        selfCount++;
                        selfStoichiometry = componentBean.getStoichiometry();
                    } else if ( unspecifiedCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        unspecifiedCount++;
                    } else if ( fluorophoreDonorCvBean.getAc().equals( componentBean.getRole() )){
                        fluorophoreDonorCount++;
                    } else if (fluorophoreAcceptorCvBean.getAc().equals( componentBean.getRole() )){
                        fluorophoreAcceptorCount++;
                    } else if ( electronDonorCvBean.getAc().equals( componentBean.getRole() )){
                        electronDonorCount++;
                    } else if (electronAcceptorCvBean.getAc().equals( componentBean.getRole() )){
                        electronAcceptorCount++;
                    }
                }

                int baitPrey = ( baitCount + preyCount > 0 ? 1 : 0 );
                int enzymeTarget = ( enzymeCount + enzymeTargetCount > 0 ? 1 : 0 );
                int neutral = ( neutralCount > 0 ? 1 : 0 );
                int self = ( selfCount > 0 ? 1 : 0 );
                int unspecified = ( unspecifiedCount > 0 ? 1 : 0 );
                int fluorophoreAcceptorDonor = ( fluorophoreAcceptorCount + fluorophoreDonorCount > 0 ? 1 : 0);
                int electronAcceptorDonor = ( electronAcceptorCount + electronDonorCount > 0 ? 1 : 0);
                // count the number of categories used.
                int categoryCount = baitPrey + neutral + enzymeTarget + self + unspecified + fluorophoreAcceptorDonor + electronAcceptorDonor;

                switch ( categoryCount ) {
                    case 0:
                        // none of those categories
                        //System.out.println("Interaction " +interactionAc + " with no categories");
                        messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_CATEGORIES, interactionBean);
                        break;

                    case 1:
                        // exactly 1 category
                        if ( baitPrey == 1 ) {
                            // bait-prey
                            if ( baitCount == 0 ) {
                                //System.out.println("Interaction " +interactionAc + "  with no bait");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_BAIT, interactionBean);
                            } else if ( preyCount == 0 ) {
                                //System.out.println("Interaction " +interactionAc + "  with no prey");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_PREY, interactionBean);
                            }

                        }else if ( fluorophoreAcceptorDonor == 1 ) {
                            if ( fluorophoreAcceptorCount == 0){
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_FLUOROPHORE_ACCEPTOR, interactionBean);
                            }else if (fluorophoreDonorCount == 0 ){
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_FLUOROPHORE_DONOR, interactionBean);
                            }
                        }else if ( electronAcceptorDonor == 1 ) {
                            if ( electronAcceptorCount == 0){
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_ELECTRON_ACCEPTOR, interactionBean);
                            }else if (electronDonorCount == 0 ){
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_ELECTRON_DONOR, interactionBean);
                            }
                        }
                        else if ( enzymeTarget == 1 ) {
                            // enzyme - enzymeTarget
                            if ( enzymeCount == 0 ) {
                                //System.out.println("Interaction  " +interactionAc + " with no enzyme");

                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_ENZYME, interactionBean);
                            } else if ( enzymeTargetCount == 0 ) {
                                //System.out.println("Interaction " +interactionAc + "  with no enzyme target");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_ENZYME_TARGET, interactionBean);
                            }

                        } else if ( self == 1 ) {
                            // it has to be > 1
                            if ( selfCount > 1 ) {
                                //System.out.println("Interaction " +interactionAc + "  with more then 2 self protein");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN, interactionBean);
                            } else { // = 1
                                if ( selfStoichiometry < 1F ) {
                                    //System.out.println("Interaction " +interactionAc + "  self protein and stoichiometry lower than 2");
                                    //  messageSender.addMessage( ReportTopic.INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2, interactionBean);
                                }
                            }

                        } else {
                            // neutral
                            if ( neutralCount == 1 ) {
                                if ( neutralStoichiometry == 1 ) {
                                    //System.out.println("Interaction  " +interactionAc + "  with only one neutral");
                                    messageSender.addMessage( ReportTopic.INTERACTION_WITH_ONLY_ONE_NEUTRAL, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean));
                                }
                            }
                        }
                        break;

                    default:
                        // > 1 : mixed up categories !
                        //System.out.println("Interaction  " +interactionAc + "  with mixed component categories");
                        messageSender.addMessage( ReportTopic.INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean));
                } // switch
            }
        }
        //check 7
    }



    public void experimentNotSuperCurated() throws SQLException, IntactException {
        List ExperimentBeans = superCuratedSch.getBeans(ExperimentBean.class, "%" );
        for (int i = 0; i < ExperimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) ExperimentBeans.get(i);
            messageSender.addMessage(ReportTopic.EXPERIMENT_NOT_ACCEPTED_NOT_TO_BE_REVIEWED,experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean));
    }
    }

    /**
     * Performs Interaction checks (Check if the interaction has components)
     * @param interactorBeans List containing interactorBean having objclass equal to InteractionImpl
     * @throws SQLException
     */
    public void checkComponentOfInteractions( List interactorBeans ) throws SQLException, IntactException {
        //System.out.println( "Checking on Components (rules 5 and 6) ..." );

        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactionBean =  (InteractorBean) interactorBeans.get(i);
            String interactionAc =  interactionBean.getAc();


            if(false == interactionIsOnHold(interactionAc)){
                List componentBeans = sch.getBeans(ComponentBean.class,interactionAc);

                if ( componentBeans.size() == 0 ) {
                    //Interaction has no Components!! This is in fact test 5...
                    //System.out.println("Interaction has no component");
                    messageSender.addMessage( ReportTopic.NO_PROTEIN_CHECK, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                }
            }
        }
    }


    /**
     * Check if an interactor is a Protein and not an Interaction
     * @param interactorAc String corresponding to an interactor ac
     * @return True if the interactor is a protein, false if it is not
     * @throws SQLException
     */

    public boolean isInstanceOfProtein(String interactorAc) throws SQLException {
        boolean instanceOfProtein = false;
        InteractorBean interactorBean = (InteractorBean) sch.getBeans(InteractorBean.class,interactorAc).get(0);
        if(Protein.class.equals(interactorBean.getObjclass())){
            instanceOfProtein=true;
        }
        return instanceOfProtein;
    }

    /**
     * Performs protein check
     * @param interactorBeans List containing interactorBean having objclass equal to ProteinImpl
     * @throws SQLException
     */

    public void checkProtein( List interactorBeans ) throws SQLException, IntactException {

        //System.out.println( "Checking on Proteins (rules 14 and 16) ..." );

        //checks 14
        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean proteinBean =  (InteractorBean) interactorBeans.get(i);
            List xrefBeans = sch.getBeans(XrefBean.class, proteinBean.getAc());
            int count=0;
            for (int j = 0; j < xrefBeans.size(); j++) {
                XrefBean xrefBean = (XrefBean) xrefBeans.get(j);
                if ( uniprotDatabaseCvBean.getAc().equals( xrefBean.getDatabase_ac() ) && identityXrefQualifierCvBean.getAc().equals(xrefBean.getQualifier_ac())) {
                    count++;
                }
            }
            if (count == 0){
                if(!noUniprotUpdate(proteinBean.getAc())){
                    messageSender.addMessage( ReportTopic.PROTEIN_WITH_NO_UNIPROT_IDENTITY, proteinBean);//, editorUrlBuilder.getEditorUrl(proteinBean) );
                }
            } else if ( count > 1 ) {
                messageSender.addMessage( ReportTopic.PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY, proteinBean);//, editorUrlBuilder.getEditorUrl(proteinBean) );
            }
        }
    }

    /**
     * Performs protein check
     * @param interactorBeans List containing interactorBean having objclass equal to ProteinImpl
     * @throws SQLException
     */

    public void checkInteractionsComplete(List interactorBeans) throws SQLException, IntactException {
        //System.out.println( "Checking on Interactions (rule 7) ..." );

        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactionBean =  (InteractorBean) interactorBeans.get(i);
            String interactionAc =  interactionBean.getAc();

            if(false == interactionIsOnHold(interactionAc)){

                //check 7
                if ( sch.getBeans(Int2ExpBean.class,interactionAc).isEmpty() ) {
                    //record it.....
                    //System.out.println("Interaction "+interactionAc + " with no experiment");
                    messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_EXPERIMENT, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                }
                //check 9
                if (false == sch.getBeans(InteractorBean.class, interactionAc).isEmpty() ) {
                    //System.out.println("Interaction "+interactionAc + " has no interaction type");

                    messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_CVINTERACTIONTYPE, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                }
                //check 10
                // 2005-04-14: on-hold ... might be re-introduced later.
//                if( interaction.getBioSource() == null ) {
//                    addMessage( INTERACTION_WITH_NO_ORGANISM, interaction );
//                }

            }

        }

    }


    /**
     * Performs checks on Experiments.
     * @param experimentBeans  List containing experiment beans
     * @throws IntactException
     * @throws SQLException
     */
    public void checkExperiment( List experimentBeans ) throws IntactException, SQLException {

        for (int i = 0; i < experimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) experimentBeans.get(i);

            if ( !experimentIsOnHold( experimentBean.getAc() ) ) {

                sch.addMapping(ExperimentBean.class, "select e.ac from ia_experiment e, ia_int2exp i  where e.ac = ? and e.ac=i.experiment_ac");// in (SELECT experiment_ac FROM ia_int2exp)");

                //check 8
                if(sch.getBeans(ExperimentBean.class, experimentBean.getAc()).isEmpty()){
                    messageSender.addMessage(ReportTopic.EXPERIMENT_WITHOUT_INTERACTIONS, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }

                //check 11
                if (experimentBean.getBiosource_ac()==(null)){
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_ORGANISM, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }

                //check 12
                if(sch12.getBeans(ExperimentBean.class, experimentBean.getAc()).isEmpty()){
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_CVINTERACTION, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }
                //check 13
                if(sch13.getBeans(ExperimentBean.class, experimentBean.getAc()).isEmpty()){
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_CVIDENTIFICATION, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }
            }
        }

    }

    /**
     * Performs check on experiments
     * @param experimentBeans List containing experiment beans
     * @throws IntactException
     * @throws SQLException
     */
    public void checkExperimentsPubmedIds( List experimentBeans ) throws IntactException, SQLException {

        for (int i = 0; i < experimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) experimentBeans.get(i);
            if ( !experimentIsOnHold(experimentBean.getAc()) ) {
                int pubmedCount = 0;
                int pubmedPrimaryCount = 0;
                List xrefBeans = sch.getBeans(XrefBean.class,experimentBean.getAc());
                for (int j = 0; j < xrefBeans.size(); j++) {
                    XrefBean xrefBean = (XrefBean) xrefBeans.get(j);
//                    System.out.println("xref cvDb = " + xrefBean.getDatabase_ac());
//                    System.out.println("xref qualifier = " + xrefBean.getQualifier_ac());
//                    System.out.println();

                    if ( pubmedDatabaseCvBean.getAc().equals( xrefBean.getDatabase_ac() ) ) {
                        pubmedCount++;
                        if ( primaryReferenceXrefQualifierCvBean.getAc().equals( xrefBean.getQualifier_ac() ) ) {
                            pubmedPrimaryCount++;
                        }
                    }
                }
                if ( pubmedCount == 0 ) {
                    //record it.....
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_PUBMED, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }

                if ( pubmedPrimaryCount < 1 ) {
                    //record it.....
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }
            }// experimentIsOnHold
        }

    }

    /**
     * Checks We have so far: -----------------------
     * <p/>
     * 1.  Any Experiment lacking a PubMed ID 2.  Any PubMed ID in Experiment DBXref without qualifier=Primary-reference
     * 3.  Any Interaction containing a bait but not a prey protein 4.  Any Interaction containing a prey but not a bait
     * protein 5.  Any interaction with no protein attached 6.  Any interaction with 1 protein attached, stoichiometry=1
     * 7.  Any Interaction missing a link to an Experiment 8.  Any experiment (not on hold) with no Interaction linked
     * to it 9.  Any interaction missing CvInteractionType 10. Any interaction missing Organism 11. Any experiment (not
     * on hold) missing Organism 12. Any experiment (not on hold) missing CvInteraction 13. Any experiment (not on hold)
     * missing CvIdentification 14. Any proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot) 15.
     * Any BioSource with a NULL or empty taxid. 16. Any proteins with more than one Xref with XrefQualifier(identity)
     * and CvDatabase(uniprot)
     * <p/>
     * To perform these checks we need to enhance the Helper/persistence code to handle more complex queries, ie to be
     * able to build Criteria and Query objects probably used in OJB (easiest to do). This is going to be needed anyway
     * so that we can handle more complex search queries later....
     */

    public void checkBioSource( List bioSourceBeans ) throws IntactException, SQLException {

        //check 15
        for (int i = 0; i < bioSourceBeans.size(); i++) {
            BioSourceBean bioSourceBean =  (BioSourceBean) bioSourceBeans.get(i);
            if ( bioSourceBean.getTaxid() == null || "".equals( bioSourceBean.getTaxid() ) ) {
                messageSender.addMessage( ReportTopic.BIOSOURCE_WITH_NO_TAXID, bioSourceBean);//, editorUrlBuilder.getEditorUrl(bioSourceBean) );
            }
        }

    }


    public void duplicatedProtein (XrefBean xrefBean) throws SQLException {

        List interactorBeans = sch.getBeans(InteractorBean.class, xrefBean.getPrimaryid());
        List bioSourceAc = new ArrayList();
        List duplicatedInteractorBeans = new ArrayList();


        InteractorBean firstInteractor =  (InteractorBean) interactorBeans.get(0);
        bioSourceAc.add(firstInteractor.getBiosource_ac());
        duplicatedInteractorBeans.add(firstInteractor);

        for (int i = 1; i < interactorBeans.size(); i++) {
            InteractorBean interactorBean =  (InteractorBean) interactorBeans.get(i);
            if(bioSourceAc.contains(interactorBean.getBiosource_ac())){
                duplicatedInteractorBeans.add(interactorBean);
//                System.out.println("Duplicated prot, interactor_ac = " + interactorBean.getAc());
            }else{
                bioSourceAc.add(interactorBean.getBiosource_ac());
            }
        }

        if(duplicatedInteractorBeans.size()>1){
            messageSender.addMessage(ReportTopic.DUPLICATED_PROTEIN,duplicatedInteractorBeans);
            duplicProt = duplicProt+duplicatedInteractorBeans.size();
        }


    }

    /**
     * This method check whether the url contained in the annotation are valide. To do so, it uses the HttpClient
     * library (commons-httpclient-3.0-rc2.ja, http://jakarta.apache.org/commons/httpclient). This library depends on
     * two other libraries ( commons-codec-1.4-dev.jar and junit.jar). If the url is not valide then it retrieves the
     * first object this annotation is linked to and send the message to the owner of this object. This is to avoid 100
     * errors to be sent if the same url is linked to 100 objects.
     * @param annotationBeans List of annotationBean linked to the cvTopic with shortlabel "url"
     * @throws SQLException
     * @throws IntactException
     */

    public void checkURL(List annotationBeans) throws SQLException, IntactException {//Collection annotations){

        for (int i = 0; i < annotationBeans.size(); i++) {
            AnnotationBean annotationBean =  (AnnotationBean) annotationBeans.get(i);

            String urlString=annotationBean.getDescription();

            HttpURL httpUrl= null;


            //Creating the httpUrl object corresponding to the the url string contained in the annotation
            try {
                httpUrl = new HttpURL(urlString);
            } catch (URIException e) {
                // e.printStackTrace();
                //retrieveObject(annotationBean);
                messageSender.addMessage(annotationBean);
                //System.out.println("couldn't create httpURL uri" + urlString);
            }

            // If httpUrl is not null, get the method corresponding to the uri, execute if and analyze the
            // status code to know whether the url is valide or not.
            if(httpUrl!=null){
                HttpClient client = new HttpClient();
                HttpMethod method=null;
                try{
                    method = new GetMethod(urlString);
                }catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                    //System.out.println("Couldn't get method uri" + urlString);
                    //retrieveObject(annotationBean);
                    messageSender.addMessage(annotationBean);
                }
                int statusCode = -1;
                if(method!=null){
                    try {
                        statusCode = client.executeMethod(method);
                    } catch (IOException e) {
                        //retrieveObject(annotationBean);
                        messageSender.addMessage(annotationBean);
                    }
                    if(statusCode!=-1){
                        if(statusCode >= 300 && statusCode <600) {
                            //retrieveObject(annotationBean);
                            messageSender.addMessage(annotationBean);
                        }
                    }
                }
            }
        }
    }


    /**
     * This function retrieve all the sequence chunk corresponding to a protein and associate them in the good order to
     * rebuild the sequence
     *
     * @param proteinAc String ==> ac of a protein
     * @return
     * @throws SQLException
     */

    public String getProteinSequence(String proteinAc) throws SQLException {
        String sequence = new String();
        List sequenceChunkBeans = sch.getBeans(SequenceChunkBean.class,proteinAc);
        for (int i = 0; i < sequenceChunkBeans.size(); i++) {
            SequenceChunkBean sequenceChunkBean =  (SequenceChunkBean) sequenceChunkBeans.get(i);
            sequence=sequence + sequenceChunkBean.getSequence_chunk();
        }
        return sequence;
    }

    /**
     * Performs test on experiment (check if an experiment is to-be-reviewed or not)
     * @param experimentBeans
     * @throws SQLException
     */

    public void checkReviewed(List experimentBeans) throws SQLException, IntactException {

        for (int i = 0; i < experimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) experimentBeans.get(i);
            if(experimentIsOnHold(experimentBean.getAc())){
                messageSender.addMessage( ReportTopic.EXPERIMENT_ON_HOLD, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean));
            }
            if(experimentToBeReviewed(experimentBean.getAc())){
                messageSender.addMessage(ReportTopic.EXPERIMENT_TO_BE_REVIEWED, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean));
            }
        }
    }

    /**
     * Check if the crc64 stored in the database still corresponds to the crc64 calculated from the protein sequence
     * stored in the database
     * @param interactorBeans
     * @throws SQLException
     */
    public void checkCrc64(List interactorBeans) throws SQLException, IntactException {
        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactorBean =  (InteractorBean) interactorBeans.get(i);
            String sequence = getProteinSequence(interactorBean.getAc());
            String crc64Stored=interactorBean.getCrc64();
            if(sequence!=null && false==sequence.trim().equals("")){
                String crc64Calculated=Crc64.getCrc64(sequence);
                if(!crc64Calculated.equals(crc64Stored)){
                    messageSender.addMessage(ReportTopic.PROTEIN_WITH_WRONG_CRC64, interactorBean);//, editorUrlBuilder.getEditorUrl(interactorBean));
                }
            }
            else{
                if(crc64Stored!=null){
                    messageSender.addMessage(ReportTopic.PROTEIN_WITHOUT_A_SEQUENCE_BUT_WITH_AN_CRC64,interactorBean);//, editorUrlBuilder.getEditorUrl(interactorBean));
                }
            }

        }

    }

    /**
     * This method check that all biosource have at least one xref with a "Newt" taxid AND
     * with the xref qualifier equal to "identity"
     * It will not send any message when the taxid is < 0 because those are particular biosources
     * to tell that those experiments were done "in vitro" or that it was a "chemical synthesis"
     * (EBI-1318 or EBI-350168)
     *
     * @param bioSourceBeans A collection containing bioSources
     * @throws IntactException
     * @throws SQLException
     */
    public void checkNewt(List bioSourceBeans) throws IntactException, SQLException {

//        System.out.println("Checking bioSource (existing newt identity xref) :");

        for (int i = 0; i < bioSourceBeans.size(); i++) {
            boolean hasNewtXref = false;
            BioSourceBean bioSourceBean = (BioSourceBean) bioSourceBeans.get(i);
            List xrefs = sch.getBeans(XrefBean.class, bioSourceBean.getAc());
            for (int j = 0; j < xrefs.size(); j++) {
                XrefBean xrefBean =  (XrefBean) xrefs.get(j);
                String xrefQualifier = xrefBean.getQualifier_ac().trim();
                String databaseAc = xrefBean.getDatabase_ac().trim();
                if(newtDatabaseCvBean.getAc().equals(databaseAc) && identityXrefQualifierCvBean.getAc().equals(xrefQualifier)){
                    hasNewtXref=true;
                }
            }
            if(false==hasNewtXref){
                int taxid = Integer.parseInt(bioSourceBean.getTaxid());
                if(taxid>=1){
                    messageSender.addMessage(ReportTopic.BIOSOURCE_WITH_NO_NEWT_XREF,bioSourceBean, editorUrlBuilder.getEditorUrl(bioSourceBean));
                }
            }
        }

    }

    /**
     * This check if there is any interaction linked to more then one experiment if any it will send a message
     * The sql request is the following one :
     *      select interaction_ac, experiment_ac
     *      from ia_int2exp
     *      where interaction_ac like '%' and
     *      interaction_ac in ( select interaction_ac
     *                          from ia_int2exp
     *                          group by interaction_ac
     *                          having count(experiment_ac) > 1)
     *
     * For exemple it can return a list of Int2ExpBean like that :
     *      INTERACTION_AC          EXPERIMENT_AC
     *      EBI-367255              EBI-367251
     *      EBI-367255              EBI-79369
     *      EBI-520663              EBI-495409
     *      EBI-520663              EBI-495685
     *
     * For each line you check if the interaction_ac was the same then for the previous line, if it was you add the
     * the experimentBean to the experimentBeans list corresponding to this interaction_ac.
     * If it is not the same (and if it is not the first line), you ask the messageSender to send an error message giving
     * in parameter the interactionBean and the list of all ExperimentBeans.
     *
     * You obtain this kind of message :
     *
     *      Interaction linked to more then one experiment
     *      ----------------------------------------------
     *      AC: EBI-367255	 Shortlabel: tra_2-tra_4	 User: INTACT	 When: 2004-06-15 15:37:50.0
     *          AC: EBI-79369		 Shortlabel: wang-2000-3		 User: SKERRIEN		 When: 2003-12-05 14:26:41.0
     *          AC: EBI-367251		 Shortlabel: wang-2001-3		 User: SMUDALI		 When: 2004-06-15 15:34:38.0
     *      AC: EBI-520663	 Shortlabel: mdc1-brca1-1	 User: INTACT	 When: 2005-03-21 10:08:52.0
     *          AC: EBI-495685		 Shortlabel: stewart-2003-4		 User: SMUDALI		 When: 2005-02-24 16:16:11.0
     *          AC: EBI-495409		 Shortlabel: stewart-2003-1		 User: ABRIDGE		 When: 2005-02-24 14:23:33.0
     *
     *
     * @throws SQLException
     */

    public void checkOneIntOneExp() throws SQLException {
        List int2ExpBeans = oneIntOneExpSch.getBeans(Int2ExpBean.class, "%");

        List experimentBeans = new ArrayList();
        InteractorBean interactionBean=new InteractorBean();
        String interactionAc="";

        for (int i = 0; i < int2ExpBeans.size(); i++) {
            Int2ExpBean int2ExpBean =  (Int2ExpBean) int2ExpBeans.get(i);
            String currentInteractionAc = int2ExpBean.getInteraction_ac();
            String currentExperimentAc = int2ExpBean.getExperiment_ac();
            if(!interactionAc.equals(currentInteractionAc)){
                if(!interactionAc.equals("")){
                    messageSender.addMessage(ReportTopic.INTERACTION_LINKED_TO_MORE_THEN_ONE_EXPERIMENT,interactionBean,experimentBeans);
                }
                interactionBean=retrieveInteractorFromAc(oneIntOneExpSch,currentInteractionAc);
                experimentBeans.clear();
                experimentBeans.add(retrieveExperimentFromAc(oneIntOneExpSch, currentExperimentAc));
                interactionAc=currentInteractionAc;
            }
            else{
                experimentBeans.add(retrieveExperimentFromAc(oneIntOneExpSch, currentExperimentAc));
            }
        } //end of for on int2ExpBeans list
        if(!int2ExpBeans.isEmpty()){
            messageSender.addMessage(ReportTopic.INTERACTION_LINKED_TO_MORE_THEN_ONE_EXPERIMENT,interactionBean,experimentBeans);
        }
    }

    public ExperimentBean retrieveExperimentFromAc (SanityCheckerHelper helper, String ac) throws SQLException {
        ExperimentBean experimentBean = new ExperimentBean();

        List experimentBeans = objectFromAc.getBeans(ExperimentBean.class, ac);
        for (int i = 0; i < experimentBeans.size(); i++) {
            experimentBean =  (ExperimentBean) experimentBeans.get(i);
        }
        return experimentBean;
    }

    public InteractorBean retrieveInteractorFromAc (SanityCheckerHelper helper, String ac) throws SQLException {
        InteractorBean interactorBean = new InteractorBean();
        List interactorBeans = objectFromAc.getBeans(InteractorBean.class, ac);
        for (int i = 0; i < interactorBeans.size(); i++) {
            interactorBean =  (InteractorBean) interactorBeans.get(i);
        }
        return interactorBean;
    }

     /**
     * For each XrefBean this method will verify that the primaryId is valide
     *
     * @param xrefBeans a List containing all the xref of the database
     * @throws SQLException
     */

    public void hasValidPrimaryId(List xrefBeans) throws SQLException {

        for (int i = 0; i < xrefBeans.size(); i++) {
            XrefBean xrefBean =  (XrefBean) xrefBeans.get(i);
            if (xrefBean.getPrimaryid().equals("MA:1234")){
                String temporaryBreak = "";
            }


            //Get the annotationBean containing the regular expression wich describe the primaryid of the database ac
            List annotationBeans = hasValidPrimaryIdSch.getBeans(AnnotationBean.class,xrefBean.getDatabase_ac());

            for (int j = 0; j < annotationBeans.size(); j++) {

                AnnotationBean annotationBean =  (AnnotationBean) annotationBeans.get(j);
                // Get the regular expression (stored in the field description of the annotation
                String regexp=annotationBean.getDescription();
                // PrimaryId to check
                String primaryId = xrefBean.getPrimaryid();

                try {
                    // TODO escape special characters !!
                    Pattern pattern = Pattern.compile( regexp );

                    // validate the primaryId against that regular expression
                    Matcher matcher = pattern.matcher( primaryId );
                    // If the primaryId hadn't been validate against that regular expression send a message
                    if( false == matcher.matches() ) {
                        messageSender.addMessage(ReportTopic.XREF_WITH_NON_VALID_PRIMARYID,xrefBean);
                    }

                } catch ( Exception e ) {
                    // if the RegExp engine thrown an Exception, that may happen if the format is wrong.
                    // we just display it for debugging sake, but the Xref is declared valid.
                    //e.printStackTrace();
                }
            }
        }
    }
    public void checkAnnotations (List annotatedBeans, String annotatedType,List usableTopic) throws SQLException, IntactException {
        for (int i = 0; i < annotatedBeans.size(); i++) {
            AnnotatedBean annotatedBean =  (AnnotatedBean) annotatedBeans.get(i);

            if(Protein.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.updated, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_int2annot i2a " +
                                                                  "where a.ac=i2a.annotation_ac and "+
                                                                  "i2a.interactor_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, Protein.class.getName(),usableTopic);
            }
            else if(Interaction.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.updated, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_int2annot i2a " +
                                                                  "where a.ac=i2a.annotation_ac and "+
                                                                  "i2a.interactor_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, Interaction.class.getName(),usableTopic);
            }
            else if (Experiment.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.updated, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_exp2annot e2a " +
                                                                  "where a.ac=e2a.annotation_ac and "+
                                                                  "e2a.experiment_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, Experiment.class.getName(),usableTopic);
            }
            else if (BioSource.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.updated, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_biosource2annot bs2a " +
                                                                  "where a.ac=bs2a.annotation_ac and "+
                                                                  "bs2a.biosource_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, BioSource.class.getName(),usableTopic);

            } if (CvObject.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.updated, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_cvobject2annot cv2a " +
                                                                  "where a.ac=cv2a.annotation_ac and "+
                                                                  "cv2a.cvobject_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, CvObject.class.getName(),usableTopic);
            }

        }
    }


    public void checkAnnotationTopic (List annotations, AnnotatedBean annotatedBean, String annotatedType, List usableTopic) throws SQLException, IntactException {

        for (int i = 0; i < annotations.size(); i++) {
            AnnotationBean annotationBean =  (AnnotationBean) annotations.get(i);
            String topicShortlabel = (String) cvTopics.get(annotationBean.getTopic_ac());
//            System.out.println("topicShortLabel " + topicShortlabel);
            for (int j = 0; j < usableTopic.size(); j++) {
                String s = (String) usableTopic.get(j);
//                System.out.println("usable topic " +j + " = "+ s);
            }

            if(!usableTopic.contains(topicShortlabel)){
                messageSender.addMessage(ReportTopic.TOPICAC_NOT_VALID,annotationBean, topicShortlabel);

            }
        }
    }

    public void checkDeletionFeature(List rangeBeans ) throws SQLException, IntactException {
        for (int i = 0; i < rangeBeans.size(); i++) {
            RangeBean rangeBean =  (RangeBean) rangeBeans.get(i);
            messageSender.addMessage(ReportTopic.DELETION_INTERVAL_TO_LONG_TO_BE_CARACTERIZED_BY_DELETION_ANALYSIS_FEATURE_TYPE, rangeBean);
        }
    }

    public static void main(String[] args) throws SQLException, IntactException, LookupException {


        SanityChecker scn = new SanityChecker();
        Set key = scn.cvTopics.keySet();

        System.out.println( "Helper created (User: " + scn.helper.getDbUserName() + " " +
                            "Database: " + scn.helper.getDbName() + ")" );


        List expUsableTopic = scn.annotationSection.getUsableTopics(Experiment.class.getName());
        expUsableTopic.add(CvTopic.ACCEPTED);
        expUsableTopic.add(CvTopic.TO_BE_REVIEWED);

        List intUsableTopic = scn.annotationSection.getUsableTopics(Interaction.class.getName());
        List protUsableTopic = scn.annotationSection.getUsableTopics(Protein.class.getName());
        List cvUsableTopic=scn.annotationSection.getUsableTopics(CvObject.class.getName());
        List bsUsableTopic=scn.annotationSection.getUsableTopics(BioSource.class.getName());

        /*
        *     Check on feature
        */
//        scn.featureWithoutRange();

        /*
        *     Check on interactor
        */
        SanityCheckerHelper schIntAc = new SanityCheckerHelper(scn.helper);
        schIntAc.addMapping(InteractorBean.class,"SELECT ac, shortlabel, userstamp, updated, objclass "+
                                                 "FROM ia_interactor "+
                                                 "WHERE objclass = '"+InteractionImpl.class.getName()+ "'"  +
                                                " AND ac like ?");


        List interactorBeans = schIntAc.getBeans(InteractorBean.class, "EBI-%");
//        scn.checkInteractionsComplete(interactorBeans);
        scn.checkInteractionsBaitAndPrey(interactorBeans);
//        scn.checkComponentOfInteractions(interactorBeans);
//        scn.checkOneIntOneExp();
//        scn.checkAnnotations(interactorBeans, Interaction.class.getName(),intUsableTopic);

        /*
        *     Check on xref
        */
//        schIntAc.addMapping(XrefBean.class, "select distinct primaryId "+
//                                            "from ia_xref, ia_controlledvocab db, ia_controlledvocab q " +
//                                            "where database_ac = db.ac and " +
//                                            "db.shortlabel = ? and "+
//                                            "qualifier_ac = q.ac and "+
//                                            "q.shortlabel = 'identity' "+
//                                            "group by primaryId "+
//                                            "having count(primaryId) > 1");
//
//
//        List xrefBeans = schIntAc.getBeans(XrefBean.class,CvDatabase.UNIPROT);
//
//        scn.sch.addMapping(InteractorBean.class,"SELECT i.ac,i.objclass, i.shortlabel, i.biosource_ac, i.userstamp, i.updated "+
//                                                "FROM ia_interactor i, ia_xref x "+
//                                                "WHERE i.ac = x.parent_ac AND " +
//                                                "0 = ( SELECT count(1) " +
//                                                "FROM ia_annotation a, ia_int2annot i2a, ia_controlledvocab topic "+
//                                                "WHERE i.ac = i2a.interactor_ac AND "+
//                                                "i2a.annotation_ac = a.ac AND " +
//                                                "a.topic_ac = topic.ac AND " +
//                                                "topic.shortlabel = 'no-uniprot-update' ) AND "+
//                                                "x.qualifier_ac = '" +identityXrefQualifierCvBean.getAc()+"' AND "+
//                                                "x.primaryid=?");
//
//        for (int i = 0; i < xrefBeans.size(); i++) {
//            XrefBean xrefBean =  (XrefBean) xrefBeans.get(i);
//            scn.duplicatedProtein(xrefBean);
//        }
//
//        schIntAc.addMapping(XrefBean.class,"select ac, userstamp, updated, database_ac, primaryid,parent_ac "+
//                                           "from ia_xref "+
//                                            "where ac like ?");
//                                            //"where ac ='EBI-695273' and ac like ?");
//        xrefBeans = schIntAc.getBeans(XrefBean.class, "%");
//        scn.hasValidPrimaryId(xrefBeans);

        /*
        *     Check on Experiment
        */
//        List experimentBeans = scn.sch.getBeans(ExperimentBean.class,"EBI-%");
//        scn.checkReviewed(experimentBeans);
//        scn.checkExperiment(experimentBeans);
//        scn.checkExperimentsPubmedIds(experimentBeans);
//        scn.checkAnnotations(experimentBeans, Experiment.class.getName(), expUsableTopic);
//        scn.experimentNotSuperCurated();

        /*
        *     Check on BioSource
        */

//        List bioSourceBeans = scn.sch.getBeans(BioSourceBean.class,"EBI-%");
        //System.out.println("The size of bioSource list is " + bioSourceBeans.size());
//        scn.checkBioSource(bioSourceBeans);
//        scn.checkNewt(bioSourceBeans);
//        scn.checkAnnotations(bioSourceBeans, BioSource.class.getName(),bsUsableTopic);

        /*
        *     Check on protein
        */
       //right now not actual using, as concerning checks appear to commented out

//        schIntAc.addMapping(InteractorBean.class,"SELECT ac, crc64, shortlabel, userstamp, updated, objclass "+
//                                                 "FROM ia_interactor "+
//                                                 "WHERE objclass = '"+ProteinImpl.class.getName()+
//                                                 "' AND ac like ?");
//
//        List proteinBeans = schIntAc.getBeans(InteractorBean.class,"%");
//
//        scn.checkProtein(proteinBeans);
//        scn.checkCrc64(proteinBeans);
//        scn.checkAnnotations(proteinBeans, EditorMenuFactory.PROTEIN,protUsableTopic);
//
//        //already working
//        List ranges = scn.deletionFeatureSch.getBeans(RangeBean.class,"2");
//        scn.checkDeletionFeature(ranges);

        /*
        *     Check on annotation
        */

            //tested
//             schIntAc.addMapping(AnnotationBean.class, "SELECT ac, description, updated, userstamp "+
//                                                       "FROM ia_annotation "+
//                                                       "WHERE topic_ac = 'EBI-18' and ac like ?"
//                                                        );
//
//            List annotationBeans = schIntAc.getBeans(AnnotationBean.class,"EBI-%");
//            scn.checkURL(annotationBeans);

            /*
            *    Check on controlledvocab
            */

//             schIntAc.addMapping(ControlledvocabBean.class,"SELECT ac, objclass, shortlabel, updated, userstamp "+
//                                                           "FROM ia_controlledvocab "+
//                                                           "WHERE ac = ?");
//            List controlledvocabBeans = schIntAc.getBeans(ControlledvocabBean.class, "%");

//        scn.checkAnnotations(controlledvocabBeans, CvObject.class.getName(),cvUsableTopic);

        // try to send emails
        try {
            scn.messageSender.postEmails("SANITY CHECK");

        } catch ( MessagingException e ) {
            // scould not send emails, then how error ...
            //e.printStackTrace();

        }


        schIntAc=null;

        scn.helper.closeStore();
        scn=null;
    }

}