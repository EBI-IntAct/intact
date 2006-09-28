/** Java class "CvDagObject.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.util.go;

import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.CvDagObject;

import java.io.PrintWriter;
import java.util.Iterator;

/**
 * This class is responsible for generating a Directed Acyclic Graph (DAG) for
 * given CvDag object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class DagGenerator {

    /**
     * The writer to write the output to.
     */
    private PrintWriter myWriter;

    /**
     * The database xref from which to generate the goid
     */
    private String myGoIdDatabase;

    /**
     * Constructs an instance of this class.
     * @param writer the writer to write to.
     * @param goidDatabase the name of the GO id database.
     */

    public DagGenerator(PrintWriter writer, String goidDatabase) {
        myWriter = writer;
        myGoIdDatabase = goidDatabase;
    }

    /**
     * Create the GO flat file representation of the current object and all its
     * descendants.
     * @param root the starting point.
     */
    public void toGoDag(CvDagObject root){
        toGoDag(0, root, root);
    }

    /**
     * This method is responsible for writing the DAG hierarchive. The method
     * is called recursively (starting from root).
     *
     * @param currentDepth The current depth in the DAG. Translated into leading
     * blanks.
     * @param current the current CvDag object
     * @param parent All parents exept for this are listed in the DAG line as
     *  additional parents.
     */
    private void toGoDag(int currentDepth, CvDagObject current, CvDagObject parent){
        // leading blanks
        for (int i = 0; i < currentDepth; i++) {
            myWriter.print(' ');
        }

        // The GO prefix
        if (currentDepth == 0) {
            myWriter.print("$");
        }
        else {
            myWriter.print("@is_a@");
        }
        // write the current term
        term2DagLine(current, true);

        // additional parents
        for (Iterator iter = current.getParents().iterator(); iter.hasNext();) {
            CvDagObject nextParent = (CvDagObject) iter.next();
            if (parent != nextParent) {
                myWriter.print(" @is_a@ ");
                term2DagLine(nextParent, false);
            }
        }
        // end term line
        myWriter.println();

        // Iterate over children to add subtrees.
        for (Iterator iter = current.getChildren().iterator(); iter.hasNext();) {
            CvDagObject nextCvDag = (CvDagObject) iter.next();
            toGoDag(currentDepth+1, nextCvDag, current);
        }
    }

    /**
     * Prints the term, goid and synonym info
     */
    private void term2DagLine(CvDagObject current, boolean printAlias) {
        // No need to print the short label if it is as same as the term.
        if (!current.getShortLabel().equals(current.getFullName())) {
            myWriter.print(current.getShortLabel() + "\\: ");
        }
        // The term itself
        myWriter.print(current.getFullName());

        // Write GO id
        String goid = GoUtils.getGoid(current, myGoIdDatabase);

        if (goid != null) {
            myWriter.print(" ; " + goid);
        }
        // Return if there is no need to print aliases.
        if (!printAlias) {
            return;
        }
        // Print aliases.
        for (Iterator iter = current.getAliases().iterator(); iter.hasNext(); ) {
            String alias = ((Alias) iter.next()).getName();
            int pos = alias.indexOf(":");
            if (pos != -1) {
                // Case: A\:ac
                myWriter.print(" ; synonym:[" + alias.substring(0, pos) + "\\"
                        + alias.substring(pos) + "]");
            }
            else {
                // Case: AAA
                myWriter.print(" ; synonym:" + alias);
            }
        }
    }
}
