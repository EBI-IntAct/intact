/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.util.go.GoUtils;

/**
 * Utilities to read and write files in GO format
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class GoTools {

    /**
     * Load or unload Controlled Vocabularies in GO format.
     * Usage:
     * GoTools upload   IntAct_classname goid_db Go_DefinitionFile [Go_DagFile] |
     * GoTools download[v14] IntAct_classname goid_db Go_DefinitionFile [Go_DagFile]
     * <p/>
     * goid_db is the shortLabel of the database which is to be used to establish
     * object identity by mapping it to goid: in the GO flat file.
     * Example: If goid_db is psi-mi, an CvObject with an xref "psi-mi; MI:123" is
     * considered to be the same object as an object from the flat file with goid MI:123.
     * If goid_db is '-', the shortLabel will be used if present.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String nl = System.getProperty("line.separator");

        String usage = "Usage: GoTools upload IntAct_classname goid_db "
                + "Go_DefinitionFile [Go_DagFile] OR" + nl
                + "GoTools download IntAct_classname goid_db "
                + "Go_DefinitionFile [Go_DagFile]" + nl + "goid_db is the "
                + "shortLabel of the database which is to be used to establish"
                + nl + "object identity by mapping it to goid: in the GO flat file."
                + nl +  "Example: If goid_db is psi-mi, an CvObject with an xref "
                + "psi-mi; MI:123 is" + nl + "considered to be the same object "
                + "as an object from the flat file with goid MI:123." + nl
                + "If goid_db is '-', the short label will be used if present.";

        Class targetClass = null;

        try {
            // Check parameters
            if ((args.length < 4) || (args.length > 5)) {
                System.out.println("Invalid number of arguments.\n" + usage);
                System.exit(1);
            }

            try {
                targetClass = Class.forName(args[1]);
            }
            catch (ClassNotFoundException e) {
                System.out.println("Class " + args[1] + " not found.\n" + usage);
                System.exit(1);
            }

            // Create database access object
            IntactHelper helper = new IntactHelper();

            // args[2] is the go id database.
            GoUtils goUtils = new GoUtils(helper, args[2], targetClass);

            if (args[0].equals("upload")) {
                // Insert definitions
                goUtils.insertGoDefinitions(args[3]);

                // Insert DAG
                if (args.length == 5) {
                    goUtils.insertGoDag(args[4]);
                }
            }
            else if (args[0].equals("download")) {
                // Write definitions
                System.out.println("Writing GO definitons to " + args[3] + " ...");
                goUtils.writeGoDefinitions(args[3]);

                // Write go dag format
                if (args.length == 5) {
                    System.out.println("Writing GO DAG to " + args[4] + " ...");
                    goUtils.writeGoDag(args[4]);
                    System.out.println("Done.");
                }
            }
            else {
                System.out.println("Invalid argument " + args[0] + "\n" + usage);
                System.exit(1);
            }

            if (helper != null) {
                helper.closeStore();
            }
        }
        catch (IntactException e) {
            System.out.println(e.getMessage());
        }
    }
}

