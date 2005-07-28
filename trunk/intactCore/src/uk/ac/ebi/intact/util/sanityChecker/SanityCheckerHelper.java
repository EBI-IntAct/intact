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
import uk.ac.ebi.intact.util.sanityChecker.model.BioSourceBean;
import uk.ac.ebi.intact.util.sanityChecker.model.InteractorBean;
import uk.ac.ebi.intact.util.sanityChecker.model.Int2AnnotBean;
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
            System.err.println("The beanClass: " + beanClass.getName() + ", has already been mapped");
            System.err.println("The previous associated sql request was : " + bean2sql.get(beanClass));
            System.err.println("The new associated sql request will be : " + sql);
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

        List resultList = (List) queryRunner.query(conn,
                (String) bean2sql.get(beanClass),
                param,
                new BeanListHandler(beanClass));

        return resultList;
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


        sch.addMapping(InteractorBean.class,"SELECT ac, shortlabel, userstamp, timestamp, objclass "+
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
