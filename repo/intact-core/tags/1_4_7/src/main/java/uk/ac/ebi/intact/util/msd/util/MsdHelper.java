package uk.ac.ebi.intact.util.msd.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.msd.model.PdbBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA. User: krobbe Date: 22-Mar-2006 Time: 16:39:56 To change this template use File | Settings |
 * File Templates.
 */
public class MsdHelper {

    private Connection conn;
    private Map bean2sql = new HashMap();
    private QueryRunner queryRunner;

    /**
     * Call a connection to MSD database and instatiate a queryRunner
     *
     * @throws Exception
     */
    public MsdHelper() throws Exception {

        conn = MsdConnection.getMsdConnection();
        queryRunner = new QueryRunner();
    }

    /**
     * Close the connection to MSD database
     *
     * @throws IntactException
     */
    public void close() throws IntactException {
        MsdConnection.closeConnection();
    }

    /**
     * Enable the mapping between a SQL result and a Bean Object The Bean Class and the sql String need to be added in
     * argument.
     *
     * @param beanClass
     * @param sql
     *
     * @throws SQLException
     */
    public void addMapping( Class beanClass, String sql ) throws SQLException {
        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }
        //TODO : add a check to make sure sql is not null.

        if ( bean2sql.containsKey( beanClass ) ) {
//            LOGGER.info("The beanClass: " + beanClass.getName() + ", has already been mapped");
//            LOGGER.info("The previous associated sql request was : " + bean2sql.get(beanClass));
//            LOGGER.info("The new associated sql request will be : " + sql);

//            System.err.println("The beanClass: " + beanClass.getName() + ", has already been mapped");
//            System.err.println("The previous associated sql request was : " + bean2sql.get(beanClass));
//            System.err.println("The new associated sql request will be : " + sql);
        }

        // We test that the sql is valid.
        PreparedStatement preparedStatement = conn.prepareStatement( sql );
        preparedStatement.close();

        // Store the association
        bean2sql.put( beanClass, sql );
        System.out.println( beanClass + " added" );
        System.out.println( false == bean2sql.containsKey( beanClass ) );
    }

    /**
     * @param beanClass
     * @param param
     *
     * @return List
     *
     * @throws SQLException
     */
    public List getBeans( Class beanClass, String param ) throws SQLException {
        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        List resultList = null;
        try {
            resultList = (List) queryRunner.query( conn,
                                                   (String) bean2sql.get( beanClass ),
                                                   param,
                                                   new BeanListHandler( beanClass ) );
        } catch ( OutOfMemoryError aome ) {

            aome.printStackTrace();
//            LOGGER.info( "" );


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

    public PdbBean getFirstBean( Class beanClass, String param ) throws SQLException {
        PdbBean pdbBean = null;

        if ( beanClass == null ) {
            throw new IllegalArgumentException( "beanClass should not be null" );
        }

        if ( false == bean2sql.containsKey( beanClass ) ) {
            throw new IllegalArgumentException( "The beanClass :" + beanClass.getName() + " does not have known sql association" );
        }

        List resultList = (List) queryRunner.query( conn,
                                                    (String) bean2sql.get( beanClass ),
                                                    param,
                                                    new BeanListHandler( beanClass ) );
        //verify that resultList is not bigger then one, and send back the appropriate error message.
        if ( false == resultList.isEmpty() ) {
            pdbBean = (PdbBean) resultList.get( 0 );
        }

        return pdbBean;
    }

    /*public List getPDBBeansfromPDBcode (String pdbCode) throws Exception, SQLException {
       MsdHelper helper = new MsdHelper();
       List  resultList;
       helper.addMapping( PdbBean.class, "SELECT entry_id as PDBcode, title,  " +
                                          "experiment_type as experimentType, res_val as resolution, "+
                                          "r_work as rWork, " +"r_free as rFree, "+
                                          "oligomeric_state as oligomericStateList, "+"pubmedid as pmid, "+
                                          "comp_list as moleculeList " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE entry_id =?");

        resultList=helper.getBeans(PdbBean.class,pdbCode);
        helper.close();
        return resultList;

    }*/

    public static void main( String[] args ) throws Exception, SQLException {

        MsdHelper helper = new MsdHelper();


        helper.addMapping( PdbBean.class, "SELECT entry_id as pdbCcode, " + //title as structureTitle,  " +
                                          "experiment_type as experimentType, res_val as resolution, " +
                                          "r_work as rWork, " +
                                          "comp_list as moleculeList " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE entry_id =?" );

        for ( Iterator iterator = helper.getBeans( PdbBean.class, "1B7R" ).iterator(); iterator.hasNext(); ) {
            PdbBean pdbBean = (PdbBean) iterator.next();
            System.out.println( pdbBean );
            System.out.println( pdbBean.getClass() );
            System.out.println( "experiment:" + pdbBean.getExperimentType() );
            System.out.println( "pdb : " + pdbBean.getPdbCode() );
            System.out.println( "resolution : " + pdbBean.getResolution() );
            System.out.println( "moleculeList : " + pdbBean.getMoleculeList() );
            System.out.println( "RFactor : " + pdbBean.getrWork() );

        }
        helper.close();
    }
}
