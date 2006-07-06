/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang.SerializationUtils;
import uk.ac.ebi.intact.application.editor.business.EditUser;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.DataSourceException;

import java.util.Collection;

/**
 * Tests the user instance which is stored in a session.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */ 
public class SessionSerializationTest extends TestCase  {

    /**
     * The handler to the Intact helper
     */
    private IntactHelper myHelper;

    public SessionSerializationTest(String name) {
        super(name);
    }

    public void setUp() throws IntactException {
        myHelper = new IntactHelper();
    }

    public void tearDown() {
        try {
            myHelper.closeStore();
        }
        catch (IntactException ie) {}
    }

    /**
     * Returns this test suite.
     */
    public static Test suite() {
        return new TestSuite(SessionSerializationTest.class);
    }

    /**
     * An eperiment without adding annotations or xrefs.
     * @throws DataSourceException for setting up the user.
     */
    public void atestExperiment1() throws DataSourceException {
        try {
            // Create the user we want to serialize.
            EditUserI preUser = new EditUser();

            // The experiment we are dealing at the moment.
            Experiment exp = (Experiment) myHelper.getObjectByLabel(
                    Experiment.class, "gavin");

            // Set this experiment as the current view.
            preUser.setView(exp);

            // Verify the view before serializing it.
            assertEquals(exp, preUser.getView().getAnnotatedObject());

            byte[] bytes = SerializationUtils.serialize(preUser);
            System.out.println("Number of bytes is: " + bytes.length);

            // Deserialize it
            EditUserI postUser = (EditUserI) SerializationUtils.deserialize(bytes);

            // They are two different objects.
            assertNotSame(preUser, postUser);

            // User names are equal.
            assertEquals(preUser.getUserName(), postUser.getUserName());

            // Database should be same.
            assertEquals(preUser.getDatabaseName(), postUser.getDatabaseName());

            // Views should be same.
            assertEquals(preUser.getView(), postUser.getView());

            // Users are same.
            assertEquals(preUser, postUser);
        }
        catch (IntactException ie) {
           fail(ie.getMessage());
        }
    }

    /**
     * An experiment with annotations and xrefs.
     * @throws DataSourceException for setting up the user.
     */
    public void atestExperiment2() throws DataSourceException {
        try {
            // Create the user we want to serialize.
            EditUserI preUser = new EditUser();

            // The experiment we are dealing at the moment.
            Experiment exp = (Experiment) myHelper.getObjectByLabel(
                    Experiment.class, "gavin");

            // Institution and topic for annotations.
            Institution inst = EditorService.getInstance().getOwner();
            CvTopic comment = (CvTopic) myHelper.getObjectByLabel(
                    CvTopic.class, "comment");

            // Add annotations.
            Annotation annot1 = new Annotation(inst, comment);
            annot1.setAnnotationText("comment 1");
            exp.addAnnotation(annot1);

            // Add another annotation.
            Annotation annot2 = new Annotation(inst, comment);
            annot2.setAnnotationText("comment 2");
            exp.addAnnotation(annot2);

            // Database and Xref qualifier for Xrefs.
            CvDatabase uniprot = (CvDatabase) myHelper.getObjectByLabel(
                    CvDatabase.class, "uniprot");
            CvXrefQualifier xqualifier = (CvXrefQualifier)
                    myHelper.getObjectByLabel(CvXrefQualifier.class, "identity");

            // Add a xref.
            Xref xref1 = new Xref(inst, uniprot, "aaa", "bbb", "xxx", xqualifier);
            exp.addXref(xref1);

            // Add another xref.
            Xref xref2 = new Xref(inst, uniprot, "xxx", "yyy", "aaa", xqualifier);
            exp.addXref(xref2);

            // Set this experiment as the current view.
            preUser.setView(exp);

            // Verify the viewbefore serializing it.
            assertEquals(exp, preUser.getView().getAnnotatedObject());

            byte[] bytes = SerializationUtils.serialize(preUser);
            System.out.println("Number of bytes is: " + bytes.length);

            // Deserialize it
            EditUserI postUser = (EditUserI) SerializationUtils.deserialize(bytes);

            // They are two different objects.
            assertNotSame(preUser, postUser);

            // User names are equal.
            assertEquals(preUser.getUserName(), postUser.getUserName());

            // Database should be same.
            assertEquals(preUser.getDatabaseName(), postUser.getDatabaseName());

            // Query the view bean.
            AbstractEditViewBean preView = preUser.getView();
            AbstractEditViewBean postView = postUser.getView();

            // Annotations must match.
            assertTrue(preView.getAnnotations().equals(postView.getAnnotations()));

            // Xrefs must match.
            assertTrue(preView.getXrefs().equals(postView.getXrefs()));

            // Views should be same.
            assertEquals(preView, postView);

            // Users are same.
            assertEquals(preUser, postUser);
        }
        catch (IntactException ie) {
           fail(ie.getMessage());
        }
    }

    /**
     * Adding new annotations and xrefs to an experiment.
     * @throws DataSourceException for setting up the user.
     */
    public void testExperiment3() throws DataSourceException {
        try {
            // Create the user we want to serialize.
            EditUserI preUser = new EditUser();

            // The experiment we are dealing at the moment.
            Experiment exp = (Experiment) myHelper.getObjectByLabel(
                    Experiment.class, "gavin");

            // Set this experiment as the current view.
            preUser.setView(exp);

            // Query the view bean.
            AbstractEditViewBean preView = preUser.getView();

            // Institution and topic for annotations.
            Institution inst = EditorService.getInstance().getOwner();
            CvTopic comment = (CvTopic) myHelper.getObjectByLabel(
                    CvTopic.class, "comment");

            // Add annotations to the view.
            Annotation annot1 = new Annotation(inst, comment);
            annot1.setAnnotationText("comment 1");
            preView.addAnnotation(new CommentBean(annot1));

            // Add another annotation to the view.
            Annotation annot2 = new Annotation(inst, comment);
            annot2.setAnnotationText("comment 2");
            preView.addAnnotation(new CommentBean(annot2));

            // Database and Xref qualifier for Xrefs.
            CvDatabase sptr = (CvDatabase) myHelper.getObjectByLabel(
                    CvDatabase.class, "sptr");
            CvXrefQualifier xqualifier = (CvXrefQualifier)
                    myHelper.getObjectByLabel(CvXrefQualifier.class, "identity");

            // Add a xref to the view.
            Xref xref1 = new Xref(inst, sptr, "aaa", "bbb", "xxx", xqualifier);
            preView.addXref(new XreferenceBean(xref1));

            // Add another xref to the view.
            Xref xref2 = new Xref(inst, sptr, "xxx", "yyy", "aaa", xqualifier);
            preView.addXref(new XreferenceBean(xref2));

            // Verify the view before serializing it.
            assertEquals(exp, preUser.getView().getAnnotatedObject());

            // Serialize it.
            byte[] bytes = SerializationUtils.serialize(preUser);

            // Deserialize it
            EditUserI postUser = (EditUserI) SerializationUtils.deserialize(bytes);

            // They are two different objects.
            assertNotSame(preUser, postUser);

            // User names are equal.
            assertEquals(preUser.getUserName(), postUser.getUserName());

            // Database should be same.
            assertEquals(preUser.getDatabaseName(), postUser.getDatabaseName());

            // View from the serialized object.
            AbstractEditViewBean postView = postUser.getView();

            // Two annotations/xrefs in either pre or post views.
            assertTrue(preView.getAnnotations().size() == 2);
            assertTrue(postView.getAnnotations().size() == 2);
            assertTrue(preView.getXrefs().size() == 2);
            assertTrue(postView.getXrefs().size() == 2);

            // Views should be same.
            assertEquals(preView, postView);

            // Users are same.
            assertEquals(preUser, postUser);
        }
        catch (IntactException ie) {
           fail(ie.getMessage());
        }
    }

    /**
     * Updating/Deleting annotations and xrefs.
     * @throws DataSourceException for setting up the user.
     */
    public void testExperiment4() throws DataSourceException {
        try {
            // Create the user we want to serialize.
            EditUserI preUser = new EditUser();

            // The experiment we are dealing at the moment.
            Experiment exp = (Experiment) myHelper.getObjectByLabel(
                    Experiment.class, "gavin");

            // Set this experiment as the current view.
            preUser.setView(exp);

            // Query the view bean.
            AbstractEditViewBean preView = preUser.getView();

            // Institution and topic for annotations.
            Institution inst = EditorService.getInstance().getOwner();
            CvTopic comment = (CvTopic) myHelper.getObjectByLabel(
                    CvTopic.class, "comment");

            // Add annotations to the view.
            Annotation annot1 = new Annotation(inst, comment);
            annot1.setAnnotationText("comment 1");
            CommentBean cb1 = new CommentBean(annot1);
            preView.addAnnotation(cb1);

            // Add another annotation to the view.
            Annotation annot2 = new Annotation(inst, comment);
            annot2.setAnnotationText("comment 2");
            CommentBean cb2 = new CommentBean(annot2);
            preView.addAnnotation(cb2);

            // Delete the cb1.
            preView.delAnnotation(cb1);

            // Update the cb2.
            cb2.setTopic("remark");
            // This method is no longer public. Instead:
            // CommentBean cb22 = new CommentBean(cb2.getAnnotation(user), cb.getKey());
            // view.saveComment(cb2, cb22);
//            preView.addAnnotationToUpdate(cb2);

            // Database and Xref qualifier for Xrefs.
            CvDatabase sptr = (CvDatabase) myHelper.getObjectByLabel(
                    CvDatabase.class, "sptr");
            CvXrefQualifier xqualifier = (CvXrefQualifier)
                    myHelper.getObjectByLabel(CvXrefQualifier.class, "identity");

            // Add a xref to the view.
            Xref xref1 = new Xref(inst, sptr, "aaa", "bbb", "xxx", xqualifier);
            XreferenceBean xb1 = new XreferenceBean(xref1);
            preView.addXref(xb1);

            // Add another xref to the view.
            Xref xref2 = new Xref(inst, sptr, "xxx", "yyy", "aaa", xqualifier);
            XreferenceBean xb2 = new XreferenceBean(xref2);
            preView.addXref(xb2);

            // Delete xb1.
            preView.delXref(xb1);

            // Update xb2.
            xb2.setDatabase("go");
            // See the comments made under preView.addAnnotationToUpdate(cb2).
//            preView.addXrefToUpdate(xb2);

            // Serialize it.
            byte[] bytes = SerializationUtils.serialize(preUser);

            // Deserialize it
            EditUserI postUser = (EditUserI) SerializationUtils.deserialize(bytes);

            // They are two different objects.
            assertNotSame(preUser, postUser);

            // User names are equal.
            assertEquals(preUser.getUserName(), postUser.getUserName());

            // Database should be same.
            assertEquals(preUser.getDatabaseName(), postUser.getDatabaseName());

            // View from the serialized object.
            AbstractEditViewBean postView = postUser.getView();

            // Single annotation/xref in either pre or post views.
            assertTrue(preView.getAnnotations().size() == 1);
            assertTrue(postView.getAnnotations().size() == 1);
            assertTrue(preView.getXrefs().size() == 1);
            assertTrue(postView.getXrefs().size() == 1);

            // Views should be same.
            assertEquals(preView, postView);

            // Users are same.
            assertEquals(preUser, postUser);
        }
        catch (IntactException ie) {
           fail(ie.getMessage());
        }
    }

    /**
     * Test for a user.
     * @throws DataSourceException
     */
    public void testUser1() throws DataSourceException {
        try {
            // Create the user we want to serialize.
            EditUserI preUser = new EditUser();

            // Serialize it.
            byte[] bytes0 = SerializationUtils.serialize(preUser);
            // Deserialize it
            EditUserI postUser0 = (EditUserI) SerializationUtils.deserialize(bytes0);

            // They are two different objects.
            assertNotSame(preUser, postUser0);

            // Views should be same.
            assertEquals(preUser.getView(), postUser0.getView());

            // Users are same.
            assertEquals(preUser, postUser0);

            // No search query.
            assertNull(preUser.getSearchCriteria());
            assertNull(postUser0.getSearchCriteria());

            // Do a search query.
            Collection results = preUser.lookup("CvTopic", "*", 20).getResult();
            // There should be a search query.
            assertNotNull(preUser.getSearchCriteria());

            // Seralize it.
            byte[] bytes1 = SerializationUtils.serialize(preUser);
            // Deserialize it
            EditUserI postUser1 = (EditUserI) SerializationUtils.deserialize(bytes1);

            // Same search queery.
            assertNotNull(postUser1.getSearchCriteria());
            assertEquals(preUser.getSearchCriteria(), postUser1.getSearchCriteria());

            // Do a search.
            Collection results1 = myHelper.search(CvTopic.class.getName(), "ac", "*");
            // Set the search cache.
            preUser.addToSearchCache( results1 );
            assertEquals(preUser.getSearchResult().size(), results1.size());

            // Seralize it.
            byte[] bytes2 = SerializationUtils.serialize(preUser);
            // Deserialize it
            EditUserI postUser2 = (EditUserI) SerializationUtils.deserialize(bytes2);

            // Should retrieve the same search result cache.
            assertEquals(postUser2.getSearchResult().size(), results1.size());
            assertEquals(postUser2.getSearchResult(), preUser.getSearchResult());

            // No editing as we haven't set view yet.
            assertFalse(preUser.isEditing());

            // Set the CvTopic as the editing type.
            preUser.setView(CvTopic.class);
            assertTrue(preUser.isEditing());

            // Seralize it.
            byte[] bytes3 = SerializationUtils.serialize(preUser);
            // Deserialize it
            EditUserI postUser3 = (EditUserI) SerializationUtils.deserialize(bytes3);
            // Should maintain the editing mode.
            assertTrue(postUser3.isEditing());
        }
        catch (IntactException ie) {
           fail(ie.getMessage());
        }
    }
}
