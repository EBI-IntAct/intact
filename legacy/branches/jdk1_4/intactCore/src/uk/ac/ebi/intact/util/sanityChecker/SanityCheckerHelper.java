/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.util.sanityChecker.model.*;
import uk.ac.ebi.intact.model.InteractionImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckerHelper {

//    protected static final Logger LOGGER = Logger.getLogger("sanitycheck");

    private Connection conn;

    private Map bean2sql = new HashMap();

    private QueryRunner queryRunner;

    public SanityCheckerHelper(IntactHelper helper) throws IntactException {
        if (helper == null) {
            throw new IllegalArgumentException("Helper should not be null");
        }
        conn = helper.getJDBCConnection();
        queryRunner = new QueryRunner();
    }

    public void addMapping(Class beanClass, String sql) throws SQLException {
        if (beanClass == null) {
            throw new IllegalArgumentException("beanClass should not be null");
        }

        if (bean2sql.containsKey(beanClass)) {
//            LOGGER.info("The beanClass: " + beanClass.getName() + ", has already been mapped");
//            LOGGER.info("The previous associated sql request was : " + bean2sql.get(beanClass));
//            LOGGER.info("The new associated sql request will be : " + sql);

//            System.err.println("The beanClass: " + beanClass.getName() + ", has already been mapped");
//            System.err.println("The previous associated sql request was : " + bean2sql.get(beanClass));
//            System.err.println("The new associated sql request will be : " + sql);
        }

        // We test that the sql is valid.
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.close();

        // Store the association
        bean2sql.put(beanClass, sql);
    }

    public List getBeans(Class beanClass, String param) throws SQLException {
        if (beanClass == null) {
            throw new IllegalArgumentException("beanClass should not be null");
        }

        if (false == bean2sql.containsKey(beanClass)) {
            throw new IllegalArgumentException("The beanClass :" + beanClass.getName() + " does not have known sql association");
        }

        List resultList = null;
        try {
            resultList= (List) queryRunner.query(conn,
                    (String) bean2sql.get(beanClass),
                    param,
                    new BeanListHandler(beanClass));
        } catch ( OutOfMemoryError aome ) {

            aome.printStackTrace();
//            LOGGER.info( "" );
//            LOGGER.info( "SanityCheckerHelper ran out of memory." );
//            LOGGER.info( "Please run it again and change the JVM configuration." );
//            LOGGER.info( "Here are some the options: http://java.sun.com/docs/hotspot/VMOptions.html" );
//            LOGGER.info( "Hint: You can use -Xms -Xmx to specify respectively the minimum and maximum" );
//            LOGGER.info( "      amount of memory that the JVM is allowed to allocate." );
//            LOGGER.info( "      eg. java -Xms128m -Xmx512m <className>" );
//            LOGGER.info( "      you can set it up in scripts/javaRun.sh" );

//            System.err.println( "" );
//            System.err.println( "SanityCheckerHelper ran out of memory." );
//            System.err.println( "Please run it again and change the JVM configuration." );
//            System.err.println( "Here are some the options: http://java.sun.com/docs/hotspot/VMOptions.html" );
//            System.err.println( "Hint: You can use -Xms -Xmx to specify respectively the minimum and maximum" );
//            System.err.println( "      amount of memory that the JVM is allowed to allocate." );
//            System.err.println( "      eg. java -Xms128m -Xmx512m <className>" );
//            System.err.println( "      you can set it up in scripts/javaRun.sh" );

            System.exit( 1 );

        } catch ( Exception e ) {

            e.printStackTrace();

            Throwable t = e;
            while ( t.getCause() != null ) {

                t = e.getCause();

//                LOGGER.info( "" );
//                LOGGER.info( "================== ROOT CAUSE ==========================" );
//                LOGGER.info( "" );

//                System.err.println( "" );
//                System.err.println( "================== ROOT CAUSE ==========================" );
//                System.err.println( "" );

                t.printStackTrace( System.err );
            }

            System.exit( 1 );
        }

        return resultList;
    }

    public IntactBean getFirstBean(Class beanClass, String param) throws SQLException {
        IntactBean intactBean = null;

        if (beanClass == null) {
            throw new IllegalArgumentException("beanClass should not be null");
        }

        if (false == bean2sql.containsKey(beanClass)) {
            throw new IllegalArgumentException("The beanClass :" + beanClass.getName() + " does not have known sql association");
        }

        List resultList = (List) queryRunner.query(conn,
                (String) bean2sql.get(beanClass),
                param,
                new BeanListHandler(beanClass));

        if( false == resultList.isEmpty() ){
            intactBean = (IntactBean) resultList.get(0);
        }

        return intactBean;
    }



    public static AnnotatedBean getAnnotatedBeanFromAnnotation(String annotationAc) throws IntactException, SQLException {

        AnnotatedBean annotatedBean = null;

        IntactHelper intactHelper = new IntactHelper();

        SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
        sch.addMapping(Int2AnnotBean.class, "select interactor_ac "+
                                            "from ia_int2annot "+
                                            "where annotation_ac = ?");
        List annotatedBeans = sch.getBeans(Int2AnnotBean.class, annotationAc);
        if(annotatedBeans.isEmpty()){
            sch.addMapping(Exp2AnnotBean.class, "select experiment_ac "+
                                                "from ia_exp2annot "+
                                                "where annotation_ac = ?");
            annotatedBeans = sch.getBeans(Exp2AnnotBean.class, annotationAc);
            if(annotatedBeans.isEmpty()){
                sch.addMapping(CvObject2AnnotBean.class, "select cvobject_ac "+
                                                         "from ia_cvobject2annot "+
                                                         "where annotation_ac = ?");
                annotatedBeans = sch.getBeans(CvObject2AnnotBean.class, annotationAc);
                if(annotatedBeans.isEmpty()){
                    sch.addMapping(Bs2AnnotBean.class, "select biosource_ac "+
                                                       "from ia_biosource2annot "+
                                                       "where annotation_ac = ?");
                    annotatedBeans = sch.getBeans(Bs2AnnotBean.class, annotationAc);
                    if(annotatedBeans.isEmpty()){
                        sch.addMapping(Feature2AnnotBean.class, "select feature_ac "+
                                                                "from ia_feature2annot "+
                                                                "where annotation_ac = ?");
                        annotatedBeans = sch.getBeans(Feature2AnnotBean.class, annotationAc);
                        if(annotatedBeans.isEmpty()){
//                           LOGGER.info("Annotation having ac equal to " + annotationAc + " is not annotated any object int the database.");
//                            System.err.println("Annotation having ac equal to " + annotationAc + " is not annotated any object int the database.");
                        }else{//The annotation is on a Feature
                            Feature2AnnotBean feature2AnnotBean = (Feature2AnnotBean) annotatedBeans.get(0);
                            annotatedBean = sch.getFeatureBeanFromAc(feature2AnnotBean.getFeature_ac());
                        }
                    }else{//The annotation is on a BioSource
                        Bs2AnnotBean bs2AnnotBean = (Bs2AnnotBean) annotatedBeans.get(0);
                        annotatedBean = sch.getBioSourceBeanFromAc(bs2AnnotBean.getBiosource_ac());
                    }
                }else{//The annotation is on a CvObject
                    CvObject2AnnotBean cvObject2AnnotBean = (CvObject2AnnotBean) annotatedBeans.get(0);
                    annotatedBean = sch.getCvBeanFromAc(cvObject2AnnotBean.getCvobject_ac());
                }
            }else{ //The annotation is on an Experiment
                Exp2AnnotBean exp2AnnotBean = (Exp2AnnotBean) annotatedBeans.get(0);
                annotatedBean = sch.getExperimentBeanFromAc(exp2AnnotBean.getExperiment_ac());
            }
        }else{//The Annotation is on an Interactor
            Int2AnnotBean int2AnnotBean = (Int2AnnotBean) annotatedBeans.get(0);
            annotatedBean = sch.getInteractorBeanFromAc(int2AnnotBean.getInteractor_ac());
        }

        intactHelper.closeStore();

        return annotatedBean;
    }

    public static IntactBean getXreferencedObject(XrefBean xrefBean) throws SQLException, IntactException {
        IntactHelper intactHelper = new IntactHelper();
        SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);

        IntactBean intactBean = null;

        String parentAc = xrefBean.getParent_ac();

        intactBean = sch.getInteractorBeanFromAc( parentAc );
        if( intactBean == null ){
            intactBean = sch.getCvBeanFromAc( parentAc );
            if (intactBean == null){
                intactBean = sch.getBioSourceBeanFromAc( parentAc );
            }
            if (intactBean == null){
                intactBean = sch.getExperimentBeanFromAc( parentAc );
            }
            if (intactBean == null ){
                intactBean = sch.getFeatureBeanFromAc( parentAc);
            }
        }

        return intactBean;
    }

    public InteractorBean getInteractorBeanFromAc(String ac) throws IntactException, SQLException {
        IntactHelper intactHelper = new IntactHelper();
        SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
        sch.addMapping(InteractorBean.class,"select ac, objclass, updated, userstamp, crc64, biosource_ac, fullname, interactiontype_ac, shortlabel " +
                                            "from ia_interactor " +
                                            "where ac=?");
        InteractorBean interactorBean = (InteractorBean) sch.getFirstBean(InteractorBean.class,ac);
        intactHelper.closeStore();
        return interactorBean;
    }

    public ControlledvocabBean getCvBeanFromAc(String ac) throws IntactException, SQLException {
        IntactHelper intactHelper = new IntactHelper();
        SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
        sch.addMapping(ControlledvocabBean.class,"select ac, objclass, updated, userstamp, fullname, shortlabel " +
                                            "from ia_controlledvocab " +
                                            "where ac=?");
        ControlledvocabBean cvBean = (ControlledvocabBean) sch.getFirstBean(ControlledvocabBean.class,ac);
        intactHelper.closeStore();
        return cvBean;
    }

    public BioSourceBean getBioSourceBeanFromAc(String ac) throws IntactException, SQLException {
        IntactHelper intactHelper = new IntactHelper();
        SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
        sch.addMapping(BioSourceBean.class,"select ac, taxid, tissue_ac, celltype_ac, updated, userstamp, fullname, shortlabel " +
                                            "from ia_biosource " +
                                            "where ac=?");
        BioSourceBean bsBean = (BioSourceBean) sch.getFirstBean(BioSourceBean.class,ac);
        intactHelper.closeStore();
        return bsBean;
    }
    public ExperimentBean getExperimentBeanFromAc(String ac) throws IntactException, SQLException {
        IntactHelper intactHelper = new IntactHelper();
        SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
        sch.addMapping(ExperimentBean.class,"select ac, biosource_ac, detectmethod_ac, identmethod_ac, relatedexperiment_ac, updated, userstamp, fullname, shortlabel " +
                                            "from ia_experiment " +
                                            "where ac=?");
        ExperimentBean expBean = (ExperimentBean) sch.getFirstBean(ExperimentBean.class,ac);
        intactHelper.closeStore();
        return expBean;
    }

    public FeatureBean getFeatureBeanFromAc(String ac) throws IntactException, SQLException {
           IntactHelper intactHelper = new IntactHelper();
           SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
           sch.addMapping(FeatureBean.class,"select ac, component_ac, identification_ac, featuretype_ac, linkedfeature_ac, updated, userstamp, fullname, shortlabel " +
                                               "from ia_feature " +
                                               "where ac=?");
           FeatureBean featureBean = (FeatureBean) sch.getFirstBean(FeatureBean.class,ac);
           intactHelper.closeStore();
           return featureBean;
       }


    public List getBeans(Class beanClass, List params) throws SQLException {

        if (beanClass == null) {
            throw new IllegalArgumentException("beanClass should not be null");
        }

        if (false == bean2sql.containsKey(beanClass)) {
            throw new IllegalArgumentException("The beanClass :" + beanClass.getName() + " does not have known sql association");
        }

        List resultList = new ArrayList();

        for(int i=0; i < params.size(); i++ ){
            List list = (List) queryRunner.query(conn,
                                                 (String) bean2sql.get(beanClass),
                                                 (String) params.get(i),
                                                 new BeanListHandler(beanClass));
            resultList.addAll(list);
        }

        return resultList;
    }


    public static void main(String[] args) throws IntactException, SQLException {

        IntactHelper helper = new IntactHelper();
        SanityCheckerHelper sch = new SanityCheckerHelper( helper );

        sch.addMapping( BioSourceBean.class, "SELECT taxid, shortlabel, fullname " +
                                             "FROM IA_BIOSOURCE " +
                                             "WHERE shortlabel like ?" );

        for (Iterator iterator = sch.getBeans(BioSourceBean.class, "h%").iterator(); iterator.hasNext();) {
            BioSourceBean bioSourceBean = (BioSourceBean) iterator.next();
            System.out.println(bioSourceBean);
        }


        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");

        sch.addMapping( BioSourceBean.class, "SELECT taxid, shortlabel, fullname " +
                                             "FROM IA_BIOSOURCE " +
                                             "WHERE shortlabel like ?" );

        for (Iterator iterator = sch.getBeans(BioSourceBean.class, "%").iterator(); iterator.hasNext();) {
            BioSourceBean bioSourceBean = (BioSourceBean) iterator.next();
            System.out.println(bioSourceBean);
        }


        sch.addMapping(InteractorBean.class,"SELECT ac, shortlabel, userstamp, updated, objclass "+
                                                "FROM ia_interactor "+
                                                "WHERE objclass = '"+InteractionImpl.class.getName()+
                                                "' AND ac like ?");

        List interactorBeans = sch.getBeans(InteractorBean.class, "EBI-%");
        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactorBean =  (InteractorBean) interactorBeans.get(i);
            System.out.println("interactor ac = " + interactorBean.getAc());


            sch.addMapping(Int2AnnotBean.class, "SELECT annotation_ac FROM ia_int2annot WHERE interactor_ac = ?" );
            List int2AnnotBeans = sch.getBeans(Int2AnnotBean.class,interactorBean.getAc());

        }


    }
}
