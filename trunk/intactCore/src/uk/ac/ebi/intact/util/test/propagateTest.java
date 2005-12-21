package uk.ac.ebi.intact.util.test;

import junit.framework.TestCase;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.sql.*;
/**
 * Created by IntelliJ IDEA.
 * User: clief
 * Date: 07-Dec-2005
 * Time: 12:37:12
 * @author Cor Lieftink
 * @version $Id: PropagateTest.java
 */
public class propagateTest extends TestCase {
        private IntactHelper helper;
        private Statement st;

    protected void setUp() throws Exception {
        super.setUp();
        helper = new IntactHelper();
        Connection conn = helper.getJDBCConnection();
        st = conn.createStatement();
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        if (helper != null) {
            helper.closeStore();
        }
    }

    ///////////////////
    // Utility methods

    public propagateTest( String name ) throws IntactException, SQLException{
        super( name );
    }
    private void cleanupTestdata() throws SQLException{
        st.execute("DELETE IA_EXPERIMENT            WHERE ac IN ('TST-1','TST-24')");
        st.execute("DELETE IA_EXPERIMENT_AUDIT      WHERE ac IN ('TST-1','TST-24')");
        st.execute("DELETE IA_INTERACTOR            WHERE ac IN ('TST-2','TST-104', 'TST-22','TST-3','TST-107','TST-109') ");
        st.execute("DELETE IA_INTERACTOR_AUDIT      WHERE ac IN ('TST-2','TST-104', 'TST-22','TST-3','TST-107','TST-109') ");
        st.execute("DELETE IA_COMPONENT             WHERE ac IN ('TST-4','TST-108','TST-110','TST-23')");
        st.execute("DELETE IA_COMPONENT_AUDIT       WHERE ac IN ('TST-4','TST-108','TST-110','TST-23')");
        st.execute("DELETE IA_ALIAS                 WHERE ac IN ('TST-5','TST-10','TST-14','TST-18','TST-101','TST-105','TST-111','TST-18','TST-107') ");
        st.execute("DELETE IA_ALIAS_AUDIT           WHERE ac IN ('TST-5','TST-10','TST-14','TST-18','TST-101','TST-105','TST-111','TST-18','TST-107') ");
        st.execute("DELETE IA_ANNOTATION            WHERE ac IN ('TST-7','TST-102','TST-15','TST-9','TST-104','TST-19','TST-108')  ");
        st.execute("DELETE IA_ANNOTATION_AUDIT      WHERE ac IN ('TST-7','TST-102','TST-15','TST-9','TST-104','TST-19','TST-108')  ");
        st.execute("DELETE IA_XREF                  WHERE ac IN ('TST-8','TST-11','TST-20','TST-16','TST-103','TST-106','TST-109') ");
        st.execute("DELETE IA_XREF_AUDIT            WHERE ac IN ('TST-8','TST-11','TST-20','TST-16','TST-103','TST-106','TST-109') ");
        st.execute("DELETE IA_controlledvocab       WHERE ac IN ('TST-6','TST-12') ");
        st.execute("DELETE IA_controlledvocab_audit WHERE ac IN ('TST-6','TST-12') ");
        st.execute("DELETE IA_feature               WHERE ac IN ('TST-13','TST-107','TST-109') ");
        st.execute("DELETE IA_feature_audit         WHERE ac IN ('TST-13','TST-107','TST-109') ");
        st.execute("DELETE IA_range                 WHERE ac IN ('TST-21','TST-110') ");
        st.execute("DELETE IA_range_audit           WHERE ac IN ('TST-21','TST-110') ");

    }
    private void insertTestdata () throws SQLException{
        st.execute(" INSERT INTO IA_EXPERIMENT      ( ac )                               VALUES ('TST-24') ");
        st.execute(" INSERT INTO IA_EXPERIMENT      ( ac )                               VALUES ('TST-1') ");
        st.execute(" INSERT INTO IA_INTERACTOR      ( ac,objclass )                      VALUES ('TST-2','uk.ac.ebi.intact.model.InteractionImpl') ");
        st.execute(" INSERT INTO IA_INTERACTOR      ( ac,objclass )                      VALUES ('TST-22','uk.ac.ebi.intact.model.InteractionImpl') ");
        st.execute(" INSERT INTO IA_INT2EXP         ( interaction_ac, experiment_ac)     VALUES ('TST-2','TST-1') ");
        st.execute(" INSERT INTO IA_INT2EXP         ( interaction_ac, experiment_ac)     VALUES ('TST-22','TST-24') ");
        st.execute(" INSERT INTO IA_INTERACTOR      ( ac, objclass )                     VALUES ('TST-3','uk.ac.ebi.intact.model.proteinimpl') ");
        st.execute(" INSERT INTO IA_COMPONENT       ( ac ,interaction_ac ,interactor_ac) VALUES ('TST-4','TST-2','TST-3') ");
        st.execute(" INSERT INTO IA_COMPONENT       ( ac ,interaction_ac ,interactor_ac) VALUES ('TST-23','TST-22','TST-3') ");
        st.execute(" INSERT INTO ia_alias           ( ac ,parent_ac)                     VALUES ('TST-5','TST-1') ");
        st.execute(" INSERT INTO ia_controlledvocab ( ac)                                VALUES ('TST-6') ");
        st.execute(" INSERT INTO ia_annotation      ( ac, topic_ac)                      VALUES ('TST-7','TST-6') ");
        st.execute(" INSERT INTO ia_exp2annot       ( experiment_ac,annotation_ac )      VALUES ('TST-1','TST-7') ");
        st.execute(" INSERT INTO ia_xref            ( ac, parent_ac)                     VALUES ('TST-8','TST-1') ");
        st.execute(" INSERT INTO ia_annotation      ( ac, topic_ac)                      VALUES ('TST-9','TST-6') ");
        st.execute(" INSERT INTO ia_int2annot       ( interactor_ac,annotation_ac )      VALUES ('TST-2','TST-9') ");
        st.execute(" INSERT INTO IA_ALIAS           ( ac ,parent_ac)                     VALUES ('TST-10','TST-2') "); /* for interaction */
        st.execute(" INSERT INTO ia_xref            ( ac, parent_ac)                     VALUES ('TST-11','TST-2') ");
        st.execute(" INSERT INTO ia_controlledvocab ( ac)                                VALUES ('TST-12') "); /* for role */
        st.execute(" INSERT INTO ia_feature         ( ac, component_ac)                  VALUES ('TST-13','TST-4') ");
        st.execute(" INSERT INTO ia_alias           ( ac ,parent_ac)                     VALUES ('TST-14','TST-3') "); /* for protein */
        st.execute(" INSERT INTO ia_annotation      ( ac, topic_ac)                      VALUES ('TST-15','TST-6') ");
        st.execute(" INSERT INTO ia_int2annot       ( interactor_ac,annotation_ac )      VALUES ('TST-3','TST-15') ");
        st.execute(" INSERT INTO ia_xref            ( ac, parent_ac)                     VALUES ('TST-16','TST-3') ");
        st.execute(" INSERT INTO ia_alias           ( ac ,parent_ac)                     VALUES ('TST-18','TST-13') ");  /* for feature */
        st.execute(" INSERT INTO ia_annotation      ( ac, topic_ac)                      VALUES ('TST-19','TST-6') ");
        st.execute(" INSERT INTO ia_feature2annot   ( feature_ac,annotation_ac )         VALUES ('TST-13','TST-19') ");
        st.execute(" INSERT INTO ia_xref            ( ac, parent_ac)                     VALUES ('TST-20','TST-13') ");
        st.execute(" INSERT INTO ia_range           ( ac, feature_ac)                    VALUES ('TST-21','TST-13') ");
    }
    private Timestamp getUpdated(String testAc) throws SQLException {
        ResultSet rs = st.executeQuery("select updated from ia_experiment where ac= '" + testAc + "'");
        Timestamp timestampUpdated= null;
         // Get the data from the row using the column index
         while (rs.next()) timestampUpdated = rs.getTimestamp(1);
         return timestampUpdated;
    }
    public void evaluateTestStatement(String sql, boolean testBothExperiments) throws  SQLException,InterruptedException {
        Timestamp updatedBefore = getUpdated("TST-1");
        Timestamp updatedBefore2 = new Timestamp(1);
        if (testBothExperiments){
            updatedBefore2= getUpdated("TST-24");
        }
        Thread.sleep( 1000 );   /* because in database, fields are of type Date not Timestamp  */
        st.execute(sql);
        Timestamp updatedAfter2 = new Timestamp(1);
        if (testBothExperiments){
            updatedAfter2= getUpdated("TST-24");
        }
        Timestamp updatedAfter= getUpdated("TST-1");
        assertTrue(updatedAfter.after(updatedBefore));
        if (testBothExperiments) {
            assertTrue(updatedAfter2.after(updatedBefore2));
        }



    }

    /////////////////////
    // Tests

    public void testExperimentAlias()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-101','TST-1') ", false );
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-5' ", false); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-5' ", false);
        cleanupTestdata();
    }
    public void testExperimentAnnotation()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-102','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_exp2annot  (experiment_ac,annotation_ac ) VALUES ('TST-1','TST-102') ", false );
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-7' ", false );
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-7' ", false );    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_exp2annot WHERE experiment_ac='TST-1' and annotation_ac='TST-7' ", false );    /*delete test 2 */
        cleanupTestdata();
    }
    public void testExperimentXref()        throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-103','TST-1') ", false );
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-8' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-8' ", false );
        cleanupTestdata();
    }
    public void testExperimentInteraction() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO IA_INTERACTOR ( ac,objclass ) VALUES ('TST-104','uk.ac.ebi.intact.model.InteractionImpl') ");
        evaluateTestStatement(" INSERT INTO ia_int2exp (interaction_ac,experiment_ac) VALUES ('TST-104','TST-1') ", false );
        evaluateTestStatement(" DELETE ia_int2exp WHERE interaction_ac='TST-2' AND experiment_ac= 'TST-1' ", false );
        cleanupTestdata();
        insertTestdata ();
        evaluateTestStatement(" DELETE ia_interactor WHERE ac='TST-2'", false );
        cleanupTestdata();
    }
    public void testInteractionAttributes() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" UPDATE ia_interactor SET shortlabel='x' WHERE ac='TST-2'", false );
        cleanupTestdata();
    }
    public void testInteractionAlias()      throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-105','TST-2') ", false );
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-10' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-10' ", false );
        cleanupTestdata();
    }
    public void testInteractionAnnotation() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-104','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_int2annot  (interactor_ac,annotation_ac ) VALUES ('TST-2','TST-104') ", false );
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-9' ", false );
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-9' ", false );    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_int2annot WHERE interactor_ac='TST-2' and annotation_ac='TST-9' ", false );
        cleanupTestdata();
    }
    public void testInteractionXref()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-106','TST-2') ", false );
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-11' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-11' ", false );
        cleanupTestdata();
    }
    public void testInteractionComponent()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO IA_INTERACTOR ( ac, objclass ) VALUES ('TST-107','uk.ac.ebi.intact.model.proteinimpl') ");
        evaluateTestStatement(" INSERT INTO ia_component(ac, interaction_ac, interactor_ac) VALUES ('TST-108','TST-2','TST-107')", false );
        evaluateTestStatement(" UPDATE      ia_component set role='TST-12' where ac='TST-4'", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_component where ac='TST-4' ", false );
        cleanupTestdata();
    }
    /* ComponentAtttributes() see interactionComponent  update*/
    public void testComponentFeature()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_feature (ac, component_ac) VALUES ('TST-108','TST-4') ", false );
        evaluateTestStatement(" UPDATE      ia_feature set shortlabel='xxx' where ac= 'TST-13'  ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_feature where ac= 'TST-13'", false );
        cleanupTestdata();
    }
    public void testComponentProtein()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        /* is actually an update of the field interactor_ac of the component record */
        st.execute(" INSERT INTO IA_INTERACTOR ( ac, objclass ) VALUES ('TST-109','uk.ac.ebi.intact.model.proteinimpl') ");
        st.execute(" INSERT INTO IA_COMPONENT ( ac ,interaction_ac) VALUES ('TST-110','TST-2') ");
        evaluateTestStatement(" UPDATE ia_component set interactor_ac = 'TST-109' where ac='TST-110'  ", false ); /* updates and deletes tested on full tree*/
        cleanupTestdata();
    }
     public void testProtein()    throws IntactException, SQLException,InterruptedException {

        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" UPDATE IA_INTERACTOR SET CRC64='X' WHERE AC='TST-3' ",false);
        evaluateTestStatement(" DELETE IA_INTERACTOR WHERE AC='TST-3' ",false);
        cleanupTestdata();
    }
    public void testProteinAlias()      throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-111','TST-3') ", true );
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-14' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-14' ", false );
        cleanupTestdata();
    }
    public void testProteinAnnotation() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-104','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_int2annot  (interactor_ac,annotation_ac ) VALUES ('TST-3','TST-104') ", true );
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-15' ", false );
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-15' ", false );    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_int2annot WHERE interactor_ac='TST-3' and annotation_ac='TST-15' ", false );
        cleanupTestdata(); /* clean up possible still existing testdata */
    }
    public void testProteinXref()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-106','TST-3') ", false );
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-16' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-16' ", false );
        cleanupTestdata();
    }
    public void testFeatureAlias()      throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-107','TST-13') ", false );
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-18' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-18' ", false );
        cleanupTestdata();
    }
    public void testFeatureAnnotation() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-108','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_feature2annot  (feature_ac,annotation_ac ) VALUES ('TST-13','TST-108') ", false );
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-19' ", false );
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-19' ", false );    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_feature2annot WHERE feature_ac='TST-13' and annotation_ac='TST-19' ", false );
        cleanupTestdata();
    }
    public void testFeatureXref()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-109','TST-13') ", false );
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-20' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-20' ", false );
        cleanupTestdata();
    }
    public void testFeatureRange()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_range (ac, feature_ac) VALUES ('TST-110','TST-13') ", false );
        evaluateTestStatement(" UPDATE      ia_range SET fromintervalstart = '1' WHERE AC='TST-21' ", false ); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_range WHERE AC='TST-21' ", false );
        cleanupTestdata();
    }


}
