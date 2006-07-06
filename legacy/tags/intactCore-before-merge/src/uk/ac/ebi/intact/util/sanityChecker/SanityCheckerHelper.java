/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.util.sanityChecker.model.*;

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

    private Map bean2sql = new HashMap();

    private QueryRunner queryRunner;

    public SanityCheckerHelper() throws IntactException {
        queryRunner = new QueryRunner();
    }

    private Connection getJdbcConnection( IntactHelper helper ) {

        if ( helper == null ) {
            throw new IllegalArgumentException( "You must give a non null helper." );
        }

        try {
            return helper.getJDBCConnection();
        } catch ( IntactException e ) {
            throw new IllegalStateException( "IntactHelper held an invalid JDBC Connection (was null)." );
        }
    }

    public void addMapping( IntactHelper helper, Class beanClass, String sql ) throws SQLException {
        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        // We test that the sql is valid.
        Connection conn = getJdbcConnection( helper );
        PreparedStatement preparedStatement = conn.prepareStatement( sql );
        preparedStatement.close();

        // Store the association
        bean2sql.put( beanClass, sql );
    }

    public List getBeans( IntactHelper helper, Class beanClass, String param ) throws SQLException {
        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        List resultList = null;

        Connection conn = getJdbcConnection( helper );
        resultList = (List) queryRunner.query( conn,
                                               (String) bean2sql.get( beanClass ),
                                               param,
                                               new BeanListHandler( beanClass ) );
        return resultList;
    }

    public IntactBean getFirstBean( IntactHelper helper, Class beanClass, String param ) throws SQLException {
        IntactBean intactBean = null;

        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        Connection conn = getJdbcConnection( helper );
        List resultList = (List) queryRunner.query( conn,
                                                    (String) bean2sql.get( beanClass ),
                                                    param,
                                                    new BeanListHandler( beanClass ) );

        if ( false == resultList.isEmpty() ) {
            intactBean = (IntactBean) resultList.get( 0 );
        }

        return intactBean;
    }


    public AnnotatedBean getAnnotatedBeanFromAnnotation( IntactHelper helper, String annotationAc ) throws IntactException, SQLException {

        AnnotatedBean annotatedBean = null;

        addMapping( helper, Int2AnnotBean.class, "select interactor_ac " +
                                                 "from ia_int2annot " +
                                                 "where annotation_ac = ?" );
        List annotatedBeans = getBeans( helper, Int2AnnotBean.class, annotationAc );
        if ( annotatedBeans.isEmpty() ) {
            addMapping( helper, Exp2AnnotBean.class, "select experiment_ac " +
                                                     "from ia_exp2annot " +
                                                     "where annotation_ac = ?" );
            annotatedBeans = getBeans( helper, Exp2AnnotBean.class, annotationAc );
            if ( annotatedBeans.isEmpty() ) {
                addMapping( helper, CvObject2AnnotBean.class, "select cvobject_ac " +
                                                              "from ia_cvobject2annot " +
                                                              "where annotation_ac = ?" );
                annotatedBeans = getBeans( helper, CvObject2AnnotBean.class, annotationAc );
                if ( annotatedBeans.isEmpty() ) {
                    addMapping( helper, Bs2AnnotBean.class, "select biosource_ac " +
                                                            "from ia_biosource2annot " +
                                                            "where annotation_ac = ?" );
                    annotatedBeans = getBeans( helper, Bs2AnnotBean.class, annotationAc );
                    if ( annotatedBeans.isEmpty() ) {
                        addMapping( helper, Feature2AnnotBean.class, "select feature_ac " +
                                                                     "from ia_feature2annot " +
                                                                     "where annotation_ac = ?" );
                        annotatedBeans = getBeans( helper, Feature2AnnotBean.class, annotationAc );
                        if ( annotatedBeans.isEmpty() ) {
//                           LOGGER.info("Annotation having ac equal to " + annotationAc + " is not annotated any object int the database.");
//                            System.err.println("Annotation having ac equal to " + annotationAc + " is not annotated any object int the database.");
                        } else {//The annotation is on a Feature
                            Feature2AnnotBean feature2AnnotBean = (Feature2AnnotBean) annotatedBeans.get( 0 );
                            annotatedBean = getFeatureBeanFromAc( helper, feature2AnnotBean.getFeature_ac() );
                        }
                    } else {//The annotation is on a BioSource
                        Bs2AnnotBean bs2AnnotBean = (Bs2AnnotBean) annotatedBeans.get( 0 );
                        annotatedBean = getBioSourceBeanFromAc( helper, bs2AnnotBean.getBiosource_ac() );
                    }
                } else {//The annotation is on a CvObject
                    CvObject2AnnotBean cvObject2AnnotBean = (CvObject2AnnotBean) annotatedBeans.get( 0 );
                    annotatedBean = getCvBeanFromAc( helper, cvObject2AnnotBean.getCvobject_ac() );
                }
            } else { //The annotation is on an Experiment
                Exp2AnnotBean exp2AnnotBean = (Exp2AnnotBean) annotatedBeans.get( 0 );
                annotatedBean = getExperimentBeanFromAc( helper, exp2AnnotBean.getExperiment_ac() );
            }
        } else {//The Annotation is on an Interactor
            Int2AnnotBean int2AnnotBean = (Int2AnnotBean) annotatedBeans.get( 0 );
            annotatedBean = getInteractorBeanFromAc( helper, int2AnnotBean.getInteractor_ac() );
        }
        return annotatedBean;
    }

    public IntactBean getXreferencedObject( IntactHelper helper, XrefBean xrefBean ) throws SQLException, IntactException {

        IntactBean intactBean = null;

        String parentAc = xrefBean.getParent_ac();

        intactBean = getInteractorBeanFromAc( helper, parentAc );
        if ( intactBean == null ) {
            intactBean = getCvBeanFromAc( helper, parentAc );
            if ( intactBean == null ) {
                intactBean = getBioSourceBeanFromAc( helper, parentAc );
            }
            if ( intactBean == null ) {
                intactBean = getExperimentBeanFromAc( helper, parentAc );
            }
            if ( intactBean == null ) {
                intactBean = getFeatureBeanFromAc( helper, parentAc );
            }
        }

        return intactBean;
    }

    public InteractorBean getInteractorBeanFromAc( IntactHelper helper, String ac ) throws IntactException, SQLException {
        InteractorBean interactorBean = null;

        addMapping( helper, InteractorBean.class, "select ac, objclass, updated, userstamp, crc64, biosource_ac, fullname, interactiontype_ac, shortlabel " +
                                                  "from ia_interactor " +
                                                  "where ac=?" );
        interactorBean = (InteractorBean) getFirstBean( helper, InteractorBean.class, ac );

        return interactorBean;
    }

    public ControlledvocabBean getCvBeanFromAc( IntactHelper helper, String ac ) throws IntactException, SQLException {
        ControlledvocabBean cvBean;

        addMapping( helper, ControlledvocabBean.class, "select ac, objclass, updated, userstamp, fullname, shortlabel " +
                                                       "from ia_controlledvocab " +
                                                       "where ac=?" );
        cvBean = (ControlledvocabBean) getFirstBean( helper, ControlledvocabBean.class, ac );
        return cvBean;
    }

    public BioSourceBean getBioSourceBeanFromAc( IntactHelper helper, String ac ) throws IntactException, SQLException {
        BioSourceBean bsBean;

        addMapping( helper, BioSourceBean.class, "select ac, taxid, tissue_ac, celltype_ac, updated, userstamp, fullname, shortlabel " +
                                                 "from ia_biosource " +
                                                 "where ac=?" );
        bsBean = (BioSourceBean) getFirstBean( helper, BioSourceBean.class, ac );
        return bsBean;
    }

    public ExperimentBean getExperimentBeanFromAc( IntactHelper helper, String ac ) throws IntactException, SQLException {
        ExperimentBean expBean;

        addMapping( helper, ExperimentBean.class, "select ac, biosource_ac, detectmethod_ac, identmethod_ac, relatedexperiment_ac, updated, userstamp, fullname, shortlabel " +
                                                  "from ia_experiment " +
                                                  "where ac=?" );
        expBean = (ExperimentBean) getFirstBean( helper, ExperimentBean.class, ac );
        return expBean;
    }

    public FeatureBean getFeatureBeanFromAc( IntactHelper helper, String ac ) throws IntactException, SQLException {
        FeatureBean featureBean;
        addMapping( helper, FeatureBean.class, "select ac, component_ac, identification_ac, featuretype_ac, linkedfeature_ac, updated, userstamp, fullname, shortlabel " +
                                               "from ia_feature " +
                                               "where ac=?" );
        featureBean = (FeatureBean) getFirstBean( helper, FeatureBean.class, ac );
        return featureBean;
    }

    public List getBeans( IntactHelper helper, Class beanClass, List params ) throws SQLException {

        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        Connection conn = getJdbcConnection( helper );

        List resultList = new ArrayList();

        for ( int i = 0; i < params.size(); i++ ) {
            List list = (List) queryRunner.query( conn,
                                                  (String) bean2sql.get( beanClass ),
                                                  (String) params.get( i ),
                                                  new BeanListHandler( beanClass ) );
            resultList.addAll( list );
        }

        return resultList;
    }


    /**
     * M A I N
     */
    public static void main( String[] args ) throws IntactException, SQLException {

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            SanityCheckerHelper sch = new SanityCheckerHelper();

            sch.addMapping( helper, BioSourceBean.class, "SELECT taxid, shortlabel, fullname " +
                                                         "FROM IA_BIOSOURCE " +
                                                         "WHERE shortlabel like ?" );

            for ( Iterator iterator = sch.getBeans( helper, BioSourceBean.class, "h%" ).iterator(); iterator.hasNext(); )
            {
                BioSourceBean bioSourceBean = (BioSourceBean) iterator.next();
                System.out.println( bioSourceBean );
            }


            System.out.println( "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" );

            sch.addMapping( helper, BioSourceBean.class, "SELECT taxid, shortlabel, fullname " +
                                                         "FROM IA_BIOSOURCE " +
                                                         "WHERE shortlabel like ?" );

            for ( Iterator iterator = sch.getBeans( helper, BioSourceBean.class, "%" ).iterator(); iterator.hasNext(); )
            {
                BioSourceBean bioSourceBean = (BioSourceBean) iterator.next();
                System.out.println( bioSourceBean );
            }


            sch.addMapping( helper, InteractorBean.class, "SELECT ac, shortlabel, userstamp, updated, objclass " +
                                                          "FROM ia_interactor " +
                                                          "WHERE objclass = '" + InteractionImpl.class.getName() +
                                                          "' AND ac like ?" );

            List interactorBeans = sch.getBeans( helper, InteractorBean.class, "EBI-%" );
            for ( int i = 0; i < interactorBeans.size(); i++ ) {
                InteractorBean interactorBean = (InteractorBean) interactorBeans.get( i );
                System.out.println( "interactor ac = " + interactorBean.getAc() );


                sch.addMapping( helper, Int2AnnotBean.class, "SELECT annotation_ac FROM ia_int2annot WHERE interactor_ac = ?" );
                List int2AnnotBeans = sch.getBeans( helper, Int2AnnotBean.class, interactorBean.getAc() );
            }
        } finally {
            if ( helper != null ) {
                helper.closeStore();
            }
        }
    }
}
