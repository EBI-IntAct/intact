/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvDagObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Auxiliary class to insert GO Dag nodes from a GO dag formatted flat file.
 */
public class DagNode {

    /** The indentation level in the GO file
     */
    int indentLevel;

    /** The primary parent of the current node
     *
     */
    DagNode parent;

    /** The additional parents of the current node.
     */
    Hashtable additionalParents;

    /** GO id of the current node.
     */
    String goid;

    /** The Go term
     *
     */
    String goterm;


    /** Default constructor
     *
     */
    private DagNode(){
        super();
        parent = null;
        additionalParents = new Hashtable();
        indentLevel = 0;
        goid = null;
    }

    /**  Read new nodes from the input, add nodes to the aParent node.
     * @param in Go Dag format input source
     * @param aParent The node to which new nodes will be added.
     *
     * @exception IOException
     * @exception RESyntaxException
     * @exception Exception
     */
    public static DagNode addNodes(BufferedReader in,
                                   DagNode aParent,
                                   Class aTargetClass,
                                   IntactHelper helper,
				   String goidDatabase,
                                   int count)
            throws IOException, RESyntaxException, Exception {

        DagNode current = null;

        /* The while loop adds all nodes of the current level to the current aParent.
        Return from the loop happens if one of the parsed nodes is on the same or lower
        indentation level as the current aParent.
        */
        while (true) {
            if (null == current) {
                // Read the next node from flat file
                current = nextDagNode(in);
                if (null == current) {

                    // The end of the flat file has been reached.
                    return null;
                }  else {
                    // Progress report
                    System.err.print (".");
                }
            }

            if (current.isParent(aParent)) {
                if (null != aParent) {
                    current.parent = aParent;
                }
                current.storeDagNode(aTargetClass,helper, goidDatabase);
                current = addNodes(in, current, aTargetClass, helper, goidDatabase, count);
            } else {
                if (aParent == null) {
                    // Special case for the root node. It is the only node without a aParent.
                    // make node persistent
                    current.storeDagNode(aTargetClass,helper, goidDatabase);
                    // Recurse into the dag.
                    return addNodes(in, current, aTargetClass, helper, goidDatabase, count);
                } else {
                    // A node has been found which has a lower indentation level than the current aParent.
                    // Return from the current level to the next higher node.
                    return current;
                }
            }
        }
    }

    /**
       Returns true if aNode is a parent of the current node. Note:
       This relies on the GO flatfile structure, it is not a general
       purpose method.
    */
    private boolean isParent(DagNode aNode) {
        if (aNode == null) {
            return false;
        }
        return (aNode.indentLevel == this.indentLevel - 1);
    }


    /**
     * Returns the next DagNode from a GO DAG file
     * @param in the flat file data source
     * @throws IOException
     * @throws RESyntaxException
     */
    private static DagNode nextDagNode(BufferedReader in)
	throws IOException, RESyntaxException {

        RE dagLinePat =  new RE("^([:blank:]*)" +        // leading blanks
                            "([\\%\\$\\<].*)");

        //removed space which causes problems at go-flatfile import! It seems to work like this.
        RE goIdPat = new RE("[\\%\\$\\<][:blank:]*" +
                            "([^\\;]*)" +                // term
                            "\\; *" +
                            "([^\\s\\,]*)"               // GO id
                            );

	DagNode node = null;
	String nextLine = null;

        while (null != (nextLine = in.readLine())) {
            if (dagLinePat.match(nextLine)) {

                node = new DagNode();

                String leadingBlanks = dagLinePat.getParen(1);
                String termLine = dagLinePat.getParen(2);
                node.indentLevel = leadingBlanks.length();
                int lastEndPos = 0;

                /* A Go term may have more than one parent. Each line
                   of a Go Dag file contains the term,
                   and optinal additional additionalParents. The "main" parent
                   is defined by a preceding line, the additional
                   additionalParents are defined in the current line and are
                   stored in the additionalParents vector.
                */
                while (goIdPat.match(termLine, lastEndPos)) {
                    int currentStartPos = goIdPat.getParenStart(2);
                    int currentEndPos = goIdPat.getParenEnd(2);

                    /* The first Go id is the id of the current term,
                       subsequent ones are ids of additional Parents.
                    */
                    if (null == node.goid) {
                        // Store goid
                        node.goid = termLine.substring(currentStartPos,
                                currentEndPos);
                        // Store goterm
                        node.goterm = termLine.substring(goIdPat.getParenStart(1), goIdPat.getParenEnd(1));
                    } else {
                        node.additionalParents.put(termLine.substring(currentStartPos, currentEndPos),
                                                   termLine.substring(goIdPat.getParenStart(1), goIdPat.getParenEnd(1)));
                    }
                    lastEndPos = currentEndPos;
                }
            }
            // ignore comments and non-match lines, do nothing
            if (node != null) {
                return node;
            }
        }
        return node;
    };

    /** Create a hashtable which contains temporary data defining a GO term.
     *
     * @param goid
     * @param goterm
     * @return A Hashtable containing a minimal definition of a go term.
     */
    private Hashtable newDefinitionHash(String goid, String goterm){
        Hashtable definition = new Hashtable();
        Vector goids = new Vector();
        Vector goterms = new Vector();

        goids.add(goid);
        goterms.add(goterm);
        definition.put("goid", goids);
        definition.put("term", goterms);
        return definition;
    }

    /** Save the current DAG node to the database.
     *
     * @param targetClass The class into which the node will be inserted.
     * @param helper      Database access object.
     * @param goidDatabase The database xref to use to identify the current node from the goid.
     * @throws Exception
     */
    private void storeDagNode(Class targetClass, 
			      IntactHelper helper, 
			      String goidDatabase) throws Exception {

        CvDagObject targetNode = null;
        CvDagObject directParent = null;

        System.out.println(goid);
        // Get parent and child (targetNode) from the database
	if (goidDatabase.equals("-")){
	    throw new IntactException("Can't store DAG node without goidDatabase and goid");
	} else {       
	    targetNode = (CvDagObject) GoTools.selectCvObject(helper, goidDatabase, goid, null, targetClass);
	}

        // if the target node is not defined, create it.
        if (targetNode == null) {
            targetNode = (CvDagObject) GoTools.insertDefinition(newDefinitionHash(goid, goterm),
                                                                helper,
								goidDatabase, 
                                                                targetClass,
                                                                false);
        }

        // Insert the direct parent
        if (parent != null) {	
	    if (goidDatabase.equals("-")){		
		throw new IntactException("Can't store DAG node without goidDatabase and goid");
	    } else {
		directParent =  (CvDagObject) GoTools.selectCvObject(helper, goidDatabase, parent.goid, null, targetClass);
	    } 

            if (directParent == null) {
                directParent = (CvDagObject) GoTools.insertDefinition(newDefinitionHash(goid, goterm),
                                                                      helper,
								      goidDatabase,
                                                                      targetClass,
                                                                      false);
            }

            // Add the link between parent and child
            targetNode.addParent(directParent);
            helper.update(directParent);
            helper.update(targetNode);
        }

        // Insert additional parents
        for (Enumeration e = additionalParents.keys(); e.hasMoreElements();) {
            String nextGoid = (String) e.nextElement();
            String nextGoterm = (String) additionalParents.get(nextGoid);
            CvDagObject additionalParent = null;
	    if (goidDatabase.equals("-")){		
		throw new IntactException("Can't store DAG node without goidDatabase and goid");
	    } else {
		additionalParent =  (CvDagObject) GoTools.selectCvObject(helper, goidDatabase, nextGoid, null, targetClass);
;
	    } 

            if (additionalParent == null) {
                additionalParent = (CvDagObject) GoTools.insertDefinition(newDefinitionHash(nextGoid, nextGoterm),
									  helper,
									  goidDatabase,
									  targetClass,
									  false);
            }

            // Add the link between parent and child
            targetNode.addParent(additionalParent);
            helper.update(additionalParent);
        }
        helper.update(targetNode);
    }

    public String toString(){
        return goid + ": " + parent + ": " + additionalParents;
    }

}
