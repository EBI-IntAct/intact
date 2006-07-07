/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.util.sanityChecker.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

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

    private Connection getJdbcConnection() {
        return DaoFactory.connection();
    }

    public void addMapping( Class beanClass, String sql ) throws SQLException {
        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }



        // We test that the sql is valid.
        Connection conn = getJdbcConnection();
        PreparedStatement preparedStatement = conn.prepareStatement( sql );
        preparedStatement.close();

        

        // Store the association
        bean2sql.put( beanClass, sql );
    }

    public List getBeans( Class beanClass, String param ) throws SQLException {
        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        List resultList = null;

        Connection conn = getJdbcConnection();
        resultList = (List) queryRunner.query( conn,
                                               (String) bean2sql.get( beanClass ),
                                               param,
                                               new BeanListHandler( beanClass ) );
        return resultList;
    }

    public IntactBean getFirstBean( Class beanClass, String param ) throws SQLException {
        IntactBean intactBean = null;

        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        Connection conn = getJdbcConnection();
        List resultList = (List) queryRunner.query( conn,
                                                    (String) bean2sql.get( beanClass ),
                                                    param,
                                                    new BeanListHandler( beanClass ) );

        if ( false == resultList.isEmpty() ) {
            intactBean = (IntactBean) resultList.get( 0 );
        }

        return intactBean;
    }


    public AnnotatedBean getAnnotatedBeanFromAnnotation( String annotationAc ) throws IntactException, SQLException {

        AnnotatedBean annotatedBean = null;

        addMapping( Int2AnnotBean.class, "select interactor_ac " +
                                                 "from ia_int2annot " +
                                                 "where annotation_ac = ?" );
        List annotatedBeans = getBeans( Int2AnnotBean.class, annotationAc );
        if ( annotatedBeans.isEmpty() ) {
            addMapping( Exp2AnnotBean.class, "select experiment_ac " +
                                                     "from ia_exp2annot " +
                                                     "where annotation_ac = ?" );
            annotatedBeans = getBeans( Exp2AnnotBean.class, annotationAc );
            if ( annotatedBeans.isEmpty() ) {
                addMapping( CvObject2AnnotBean.class, "select cvobject_ac " +
                                                              "from ia_cvobject2annot " +
                                                              "where annotation_ac = ?" );
                annotatedBeans = getBeans( CvObject2AnnotBean.class, annotationAc );
                if ( annotatedBeans.isEmpty() ) {
                    addMapping( Bs2AnnotBean.class, "select biosource_ac " +
                                                            "from ia_biosource2annot " +
                                                            "where annotation_ac = ?" );
                    annotatedBeans = getBeans( Bs2AnnotBean.class, annotationAc );
                    if ( annotatedBeans.isEmpty() ) {
                        addMapping( Feature2AnnotBean.class, "select feature_ac " +
                                                                     "from ia_feature2annot " +
                                                                     "where annotation_ac = ?" );
                        annotatedBeans = getBeans( Feature2AnnotBean.class, annotationAc );
                        if ( annotatedBeans.isEmpty() ) {
//                           LOGGER.info("Annotation having ac equal to " + annotationAc + " is not annotated any object int the database.");
//                            System.err.println("Annotation having ac equal to " + annotationAc + " is not annotated any object int the database.");
                        } else {//The annotation is on a Feature
                            Feature2AnnotBean feature2AnnotBean = (Feature2AnnotBean) annotatedBeans.get( 0 );
                            annotatedBean = getFeatureBeanFromAc( feature2AnnotBean.getFeature_ac() );
                        }
                    } else {//The annotation is on a BioSource
                        Bs2AnnotBean bs2AnnotBean = (Bs2AnnotBean) annotatedBeans.get( 0 );
                        annotatedBean = getBioSourceBeanFromAc( bs2AnnotBean.getBiosource_ac() );
                    }
                } else {//The annotation is on a CvObject
                    CvObject2AnnotBean cvObject2AnnotBean = (CvObject2AnnotBean) annotatedBeans.get( 0 );
                    annotatedBean = getCvBeanFromAc( cvObject2AnnotBean.getCvobject_ac() );
                }
            } else { //The annotation is on an Experiment
                Exp2AnnotBean exp2AnnotBean = (Exp2AnnotBean) annotatedBeans.get( 0 );
                annotatedBean = getExperimentBeanFromAc( exp2AnnotBean.getExperiment_ac() );
            }
        } else {//The Annotation is on an Interactor
            Int2AnnotBean int2AnnotBean = (Int2AnnotBean) annotatedBeans.get( 0 );
            annotatedBean = getInteractorBeanFromAc( int2AnnotBean.getInteractor_ac() );
        }
        return annotatedBean;
    }

    public IntactBean getXreferencedObject( XrefBean xrefBean ) throws SQLException, IntactException {

        IntactBean intactBean = null;

        String parentAc = xrefBean.getParent_ac();

        intactBean = getInteractorBeanFromAc( parentAc );
        if ( intactBean == null ) {
            intactBean = getCvBeanFromAc( parentAc );
            if ( intactBean == null ) {
                intactBean = getBioSourceBeanFromAc( parentAc );
            }
            if ( intactBean == null ) {
                intactBean = getExperimentBeanFromAc( parentAc );
            }
            if ( intactBean == null ) {
                intactBean = getFeatureBeanFromAc( parentAc );
            }
        }

        return intactBean;
    }

    public InteractorBean getInteractorBeanFromAc( String ac ) throws IntactException, SQLException {
        InteractorBean interactorBean = null;

        addMapping( InteractorBean.class, "select ac, objclass, updated, userstamp, crc64, biosource_ac, fullname, interactiontype_ac, shortlabel " +
                                                  "from ia_interactor " +
                                                  "where ac=?" );
        interactorBean = (InteractorBean) getFirstBean( InteractorBean.class, ac );

        return interactorBean;
    }

    public ControlledvocabBean getCvBeanFromAc( String ac ) throws IntactException, SQLException {
        ControlledvocabBean cvBean;

        addMapping( ControlledvocabBean.class, "select ac, objclass, updated, userstamp, fullname, shortlabel " +
                                                       "from ia_controlledvocab " +
                                                       "where ac=?" );
        cvBean = (ControlledvocabBean) getFirstBean( ControlledvocabBean.class, ac );
        return cvBean;
    }

    public BioSourceBean getBioSourceBeanFromAc( String ac ) throws IntactException, SQLException {
        BioSourceBean bsBean;

        addMapping( BioSourceBean.class, "select ac, taxid, tissue_ac, celltype_ac, updated, userstamp, fullname, shortlabel " +
                                                 "from ia_biosource " +
                                                 "where ac=?" );
        bsBean = (BioSourceBean) getFirstBean( BioSourceBean.class, ac );
        return bsBean;
    }

    public ExperimentBean getExperimentBeanFromAc( String ac ) throws IntactException, SQLException {
        ExperimentBean expBean;

        addMapping( ExperimentBean.class, "select ac, biosource_ac, detectmethod_ac, identmethod_ac, relatedexperiment_ac, updated, userstamp, fullname, shortlabel " +
                                                  "from ia_experiment " +
                                                  "where ac=?" );
        expBean = (ExperimentBean) getFirstBean( ExperimentBean.class, ac );
        return expBean;
    }

    public FeatureBean getFeatureBeanFromAc( String ac ) throws IntactException, SQLException {
        FeatureBean featureBean;
        addMapping( FeatureBean.class, "select ac, component_ac, identification_ac, featuretype_ac, linkedfeature_ac, updated, userstamp, fullname, shortlabel " +
                                               "from ia_feature " +
                                               "where ac=?" );
        featureBean = (FeatureBean) getFirstBean( FeatureBean.class, ac );
        return featureBean;
    }

    public List getBeans( Class beanClass, List params ) throws SQLException {

        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        Connection conn = getJdbcConnection();

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


            SanityCheckerHelper sch = new SanityCheckerHelper();

            sch.addMapping( BioSourceBean.class, "SELECT taxid, shortlabel, fullname " +
                                                         "FROM IA_BIOSOURCE " +
                                                         "WHERE shortlabel like ?" );

            for ( Iterator iterator = sch.getBeans( BioSourceBean.class, "h%" ).iterator(); iterator.hasNext(); )
            {
                BioSourceBean bioSourceBean = (BioSourceBean) iterator.next();
                System.out.println( bioSourceBean );
            }


            System.out.println( "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" );

            sch.addMapping( BioSourceBean.class, "SELECT taxid, shortlabel, fullname " +
                                                         "FROM IA_BIOSOURCE " +
                                                         "WHERE shortlabel like ?" );

            for ( Iterator iterator = sch.getBeans( BioSourceBean.class, "%" ).iterator(); iterator.hasNext(); )
            {
                BioSourceBean bioSourceBean = (BioSourceBean) iterator.next();
                System.out.println( bioSourceBean );
            }


            sch.addMapping( InteractorBean.class, "SELECT ac, shortlabel, userstamp, updated, objclass " +
                                                          "FROM ia_interactor " +
                                                          "WHERE objclass = '" + InteractionImpl.class.getName() +
                                                          "' AND ac like ?" );

            List interactorBeans = sch.getBeans( InteractorBean.class, "EBI-%" );
            for ( int i = 0; i < interactorBeans.size(); i++ ) {
                InteractorBean interactorBean = (InteractorBean) interactorBeans.get( i );
                System.out.println( "interactor ac = " + interactorBean.getAc() );


                sch.addMapping( Int2AnnotBean.class, "SELECT annotation_ac FROM ia_int2annot WHERE interactor_ac = ?" );
                List int2AnnotBeans = sch.getBeans( Int2AnnotBean.class, interactorBean.getAc() );
            }
    }
}
