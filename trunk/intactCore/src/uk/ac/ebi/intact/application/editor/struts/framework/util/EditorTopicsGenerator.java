/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import java.io.*;
import java.util.Properties;

/**
 * This utility class generates EditorTopics.properties file using the
 * classes in uk.ac.ebi.intact.model package. The algorithm as follows.
 * First, the program searches for all the classes in the
 * uk.ac.ebi.intact.model package.
 * If a class has CvObject as its parent class then it is saved to a properties
 * file under an abbreviated class name. For example,
 * uk.ac.ebi.intact.model.CvTopic saved under CvTopic.
 * All decendents of CvObject apart from CvObject itself are stored. In addition
 * to CvObject subclasses, Experiment and BioSource are also added as topics.
 *
 * <b>Note</b> This program assumes that all the decendents of CvObject
 * are in the package uk.ac.ebi.intact.model and starts with 'Cv'. It also
 * assumes that classes already exists in this location (ie., compile sources
 * before running this tool).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorTopicsGenerator {

    /**
     * The package name to search for topics.
     */
    private static String TOPICS_PACKAGE = "uk.ac.ebi.intact.model";

    /**
     * The root of the classpath. The package uk.ac.ebi.intact.model
     * should be able to access from this value.
     */
    private String myRoot;

    /**
     * The name of the resource file to write Intact types.
     */
    private String myResourceName;

    /**
     * Constructor that accepts jar filename.
     *
     * @param root the root of the classpath to access uk.ac.ebi.intact.model.
     * @param dest the name of the resource file to write Intact types. The
     * postfix '.properties' is appended to the name if it is not present. This
     * shouldn't be null.
     */
    public EditorTopicsGenerator(String root, String dest) {
        myRoot = root;
        // Check for properties extension.
        myResourceName = dest.endsWith(".properties") ? dest :
                dest + ".properties";
    }


    /**
     * This method does the actual writing of the resource file.
     */
    public void doIt() {
        // The name of the superclass of CV objects.
        final String superCN = "CvObject";

        // The relative path to package containing CvObject.
        String path = myRoot + packageToFile(TOPICS_PACKAGE);

        // Filter to filter out what we want; an annoymous inner class.
        FilenameFilter filter = new FilenameFilter() {
            // The name of the super class.
            private String mySuperName = superCN + ".class";

            // Implements FilenameFiler interface.
            public boolean accept(File dir, String name) {
                // Only the name beginning with Cv and classes are accepted;
                // don't inlcude CvObject as it is the super class.
                if (name.startsWith("Cv") && name.endsWith(".class") &&
                        !name.equals(mySuperName)) {
                    return true;
                }
                return false;
            }
        };
        // List of files that match the filter.
        String[] files = (new File(path)).list(filter);

        // The proprties to hold CV class names; the key is the CV class name
        // and the value is the full classpath name.
        Properties props = new Properties();

        for (int i = 0; i < files.length; i++) {
            // The class name without the file extension.
            addTopic(props, stripExtension(files[i]), superCN);
        }
        // Add other two additional classes.
        addTopic(props, "Experiment", "AnnotatedObject");
        addTopic(props, "BioSource", "AnnotatedObject");
        try {
            writeToProperties(props);
        }
        catch (IOException ex) {
            assert false;
        }
    }

    /**
     * Converts a given package name to the platform dependent file path.
     * @param packName the name of the package.
     * @return the patform dependent file path.
     */
    private static String packageToFile(String packName) {
        // The platform dependent file separator.
        char sep = System.getProperty("file.separator").charAt(0);

        // Replace all the package separators with file separators.
        return sep + packName.replace(".".charAt(0), sep) + sep;
    }

    /**
     * Strips extension bit from a filename.
     * @param filename the name of the file.
     * @return the filename after removing the extension; if there is no
     * extension then this equals to <tt>filename</tt>.
     */
    private static String stripExtension(String filename) {
        // The index position to chop off the .class extension.
        int pos = filename.indexOf('.');

        // Only remove an extension if we have one.
        if (pos != -1) {
            return filename.substring(0, pos);
        }
        return filename;
    }

    /**
     * Adds a topic to given <code>props</code> container only if
     * <code>className</code> exists and it is a super class of
     * <code>superName</code>
     * @param props the properties to add the editor topic.
     * @param className the name of the class to add (name only).
     * @param superName the name of the supper class of <code>className</code>
     * (name only).
     */
    private void addTopic(Properties props, String className, String superName) {
        // The class name with the package.
        String fullPath = TOPICS_PACKAGE + "." + className;

        // The class for the name.
        Class clazz = null;
        try {
            clazz = Class.forName(fullPath);
        }
        catch (ClassNotFoundException cnfe) {
            // This shouldn't happen as the class is already there.
            assert false;
        }
        // Get the super class.
        Class superclass = clazz.getSuperclass();

        if (superclass.getName().equals(TOPICS_PACKAGE + "." + superName)) {
            // Only consider CvObject as the super class.
            props.put(className, fullPath);
        }
    }

    /**
     * Writes the given properties contents to a properties file.
     * @param props the properties file to write contents.
     * @exception IOException throws for any I/O errors.
     */
    private void writeToProperties(Properties props) throws IOException {
        // The output stream to write the props contents.
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(myResourceName));
            props.store(out, "Editor Topics");
        }
        finally {
            // Ensure that we close the stream regardless.
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ioe) {
                }
            }
        }
    }
}
