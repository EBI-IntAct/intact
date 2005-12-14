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
 * To change this template use File | Settings | File Templates.
 */
public class propagateTest extends TestCase {
        IntactHelper helper = new IntactHelper();
        Connection conn = helper.getJDBCConnection();
        Statement st = conn.createStatement();


    public propagateTest( String name ) throws IntactException, SQLException{
        super( name );
    }
    private void cleanupTestdata() throws SQLException{
        st.execute("DELETE IA_EXPERIMENT      WHERE ac ='TST-1'");
        st.execute("DELETE IA_INTERACTOR      WHERE ac IN ('TST-2','TST-104') ");
        st.execute("DELETE IA_INTERACTOR      WHERE ac IN ('TST-3','TST-107','TST-109') ");
        st.execute("DELETE IA_COMPONENT       WHERE ac IN ('TST-4','TST-108','TST-110')");
        st.execute("DELETE IA_ALIAS           WHERE ac IN ('TST-5','TST-10','TST-14','TST-18','TST-101','TST-105','TST-111','TST-18','TST-107') ");
        st.execute("DELETE IA_ANNOTATION      WHERE ac IN ('TST-7','TST-102','TST-15','TST-9','TST-104','TST-19','TST-108')  ");
        st.execute("DELETE IA_XREF            WHERE ac IN ('TST-8','TST-11','TST-20','TST-16','TST-103','TST-106','TST-109') ");
        st.execute("DELETE IA_controlledvocab WHERE ac IN ('TST-6','TST-12') ");
        st.execute("DELETE IA_feature         WHERE ac IN ('TST-13','TST-107','TST-109') ");
        st.execute("DELETE IA_range           WHERE ac IN ('TST-21','TST-110') ");

    }
    private void insertTestdata () throws SQLException{
        st.execute(" INSERT INTO IA_EXPERIMENT      ( ac )                               VALUES ('TST-1') ");
        st.execute(" INSERT INTO IA_INTERACTOR      ( ac,objclass )                      VALUES ('TST-2','uk.ac.ebi.intact.model.InteractionImpl') ");
        st.execute(" INSERT INTO IA_INT2EXP         ( interaction_ac, experiment_ac)     VALUES ('TST-2','TST-1') ");
        st.execute(" INSERT INTO IA_INTERACTOR      ( ac, objclass )                     VALUES ('TST-3','uk.ac.ebi.intact.model.proteinimpl') ");
        st.execute(" INSERT INTO IA_COMPONENT       ( ac ,interaction_ac ,interactor_ac) VALUES ('TST-4','TST-2','TST-3') ");
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
    private Timestamp getUpdated() throws SQLException {
        ResultSet rs = st.executeQuery("select updated from ia_experiment where ac= 'TST-1'");
        Timestamp timestampUpdated= null;
         // Get the data from the row using the column index
         while (rs.next()) timestampUpdated = rs.getTimestamp(1);
         return timestampUpdated;
    }
    public void evaluateTestStatement(String sql) throws  SQLException,InterruptedException {
        Timestamp updatedBefore = getUpdated();
        Thread.sleep( 1000 );   /* because in database, fields are of type Date not Timestamp  */
        st.execute(sql);
        Timestamp updatedAfter= getUpdated();
        assertTrue(updatedAfter.after(updatedBefore));
    }
    public void experimentAlias()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-101','TST-1') ");
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-5' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-5' ");
        cleanupTestdata();
    }
    public void experimentAnnotation()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-102','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_exp2annot  (experiment_ac,annotation_ac ) VALUES ('TST-1','TST-102') ");
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-7' ");
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-7' ");    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_exp2annot WHERE experiment_ac='TST-1' and annotation_ac='TST-7' ");    /*delete test 2 */
        cleanupTestdata();
    }
    public void experimentXref()        throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-103','TST-1') ");
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-8' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-8' ");
        cleanupTestdata();
    }
    public void experimentInteraction() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO IA_INTERACTOR ( ac,objclass ) VALUES ('TST-104','uk.ac.ebi.intact.model.InteractionImpl') ");
        evaluateTestStatement(" INSERT INTO ia_int2exp (interaction_ac,experiment_ac) VALUES ('TST-104','TST-1') ");
        evaluateTestStatement(" DELETE ia_int2exp WHERE interaction_ac='TST-2' AND experiment_ac= 'TST-1' ");
        cleanupTestdata();
        insertTestdata ();
        evaluateTestStatement(" DELETE ia_interactor WHERE ac='TST-2'");
        cleanupTestdata();
    }
    public void interactionAttributes() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" UPDATE ia_interactor SET shortlabel='x' WHERE ac='TST-2'");
        cleanupTestdata();
    }
    public void interactionAlias()      throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-105','TST-2') ");
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-10' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-10' ");
        cleanupTestdata();
    }
    public void interactionAnnotation() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-104','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_int2annot  (interactor_ac,annotation_ac ) VALUES ('TST-2','TST-104') ");
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-9' ");
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-9' ");    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_int2annot WHERE interactor_ac='TST-2' and annotation_ac='TST-9' ");
        cleanupTestdata();
    }
    public void interactionXref()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-106','TST-2') ");
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-11' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-11' ");
        cleanupTestdata();
    }
    public void interactionComponent()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO IA_INTERACTOR ( ac, objclass ) VALUES ('TST-107','uk.ac.ebi.intact.model.proteinimpl') ");
        evaluateTestStatement(" INSERT INTO ia_component(ac, interaction_ac, interactor_ac) VALUES ('TST-108','TST-2','TST-107')");
        evaluateTestStatement(" UPDATE      ia_component set role='TST-12' where ac='TST-4'"); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_component where ac='TST-4' ");
        cleanupTestdata();
    }
    /* ComponentAtttributes() see interactionComponent  update*/
    public void componentFeature()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_feature (ac, component_ac) VALUES ('TST-108','TST-4') ");
        evaluateTestStatement(" UPDATE      ia_feature set shortlabel='xxx' where ac= 'TST-13'  "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_feature where ac= 'TST-13'");
        cleanupTestdata();
    }
    public void componentProtein()  throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        /* is actually an update of the field interactor_ac of the component record */
        st.execute(" INSERT INTO IA_INTERACTOR ( ac, objclass ) VALUES ('TST-109','uk.ac.ebi.intact.model.proteinimpl') ");
        st.execute(" INSERT INTO IA_COMPONENT ( ac ,interaction_ac) VALUES ('TST-110','TST-2') ");
        evaluateTestStatement(" UPDATE ia_component set interactor_ac = 'TST-109' where ac='TST-110'  "); /* updates and deletes tested on full tree*/
        cleanupTestdata();
    }
    public void proteinCRC64()    throws IntactException, SQLException,InterruptedException {

        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" UPDATE IA_INTERACTOR SET CRC64='X' WHERE AC='TST-3' ");
        cleanupTestdata();
    }
    public void proteinAlias()      throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-111','TST-3') ");
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-14' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-14' ");
        cleanupTestdata();
    }
    public void proteinAnnotation() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-104','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_int2annot  (interactor_ac,annotation_ac ) VALUES ('TST-3','TST-104') ");
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-15' ");
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-15' ");    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_int2annot WHERE interactor_ac='TST-3' and annotation_ac='TST-15' ");
        cleanupTestdata(); /* clean up possible still existing testdata */
    }
    public void proteinXref()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-106','TST-3') ");
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-16' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-16' ");
        cleanupTestdata();
    }
    public void featureAlias()      throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO IA_ALIAS (ac, parent_ac) VALUES ('TST-107','TST-13') ");
        evaluateTestStatement(" UPDATE      IA_ALIAS SET NAME = 'X' WHERE AC='TST-18' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      IA_ALIAS WHERE AC='TST-18' ");
        cleanupTestdata();
    }
    public void featureAnnotation() throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        st.execute(" INSERT INTO ia_annotation (ac, topic_ac) VALUES ('TST-108','TST-6') "); /*annotation seq not leading to propagate*/
        evaluateTestStatement(" INSERT INTO ia_feature2annot  (feature_ac,annotation_ac ) VALUES ('TST-13','TST-108') ");
        evaluateTestStatement(" UPDATE      ia_annotation SET description = 'X' WHERE ac='TST-19' ");
        evaluateTestStatement(" DELETE      ia_annotation WHERE ac='TST-19' ");    /*delete test 1 */
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata (); /* test  */
        evaluateTestStatement(" DELETE      ia_feature2annot WHERE feature_ac='TST-13' and annotation_ac='TST-19' ");
        cleanupTestdata();
    }
    public void featureXref()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_xref (ac, parent_ac) VALUES ('TST-109','TST-13') ");
        evaluateTestStatement(" UPDATE      ia_xref SET primaryid = '123' WHERE AC='TST-20' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_xref WHERE AC='TST-20' ");
        cleanupTestdata();
    }
    public void featureRange()       throws IntactException, SQLException,InterruptedException {
        cleanupTestdata(); /* clean up possible still existing testdata */
        insertTestdata ();
        evaluateTestStatement(" INSERT INTO ia_range (ac, feature_ac) VALUES ('TST-110','TST-13') ");
        evaluateTestStatement(" UPDATE      ia_range SET fromintervalstart = '1' WHERE AC='TST-21' "); /* updates and deletes tested on full tree*/
        evaluateTestStatement(" DELETE      ia_range WHERE AC='TST-21' ");
        cleanupTestdata();
    }


    public void allTests ()           throws IntactException, SQLException,InterruptedException{
        experimentAlias();
        experimentAnnotation();
        experimentXref();
        experimentInteraction ();
        interactionAttributes();
        interactionAlias();
        interactionAnnotation();
        interactionXref();
        interactionComponent();
        componentFeature();
        componentProtein();
        proteinCRC64();
        proteinAlias();
        proteinAnnotation();
        proteinXref();
        featureAlias();
        featureAnnotation();
        featureXref();
        featureRange();
    }


}
