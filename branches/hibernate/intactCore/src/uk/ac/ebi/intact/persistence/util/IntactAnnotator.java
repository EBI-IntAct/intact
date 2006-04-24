/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.util;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvInteraction;

import javax.persistence.Entity;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.File;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Mar-2006</pre>
 */
public class IntactAnnotator
{
    /**
     * Retrieves a list of the annotated classes to use. This methods look for classes annotated with
     * <code>@javax.persistence.Entity</code> in the uk.ac.ebi.intact.model package
     * @return  The list of hibernate annotated classes
     */
    public static List<Class> getAnnotatedClasses() {
        List<Class> classes = new ArrayList<Class>();

        String packageName = "uk.ac.ebi.intact.model";

         // Translate the package name into an absolute path
        String name = packageName ;
        if (!name.startsWith("/")) {
            name = "/" + name;
        }

        name = name.replace('.','/');

        // Get a File object for the package
        URL url = IntactAnnotator.class.getResource(name);
        File directory = new File(url.getFile());

        if (directory.exists()) {
            // Get the list of the files contained in the package
            for (String file : directory.list()) {

                // we are only interested in .class files
                if (file.endsWith(".class")) {
                    // removes the .class extension
                    String classname = file.substring(0,file.length()-6);
                    try {
                        // Try to create an instance of the object
                        Class clazz = Class.forName(packageName+"."+classname);

                        // check for the @Entity annotation
                        if (clazz.isAnnotationPresent(Entity.class)) {
                            classes.add(clazz);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return classes;
    }
}
