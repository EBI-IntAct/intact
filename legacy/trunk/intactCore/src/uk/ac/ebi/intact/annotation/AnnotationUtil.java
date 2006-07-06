/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Entity;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Utilities to deal with annotations
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Jun-2006</pre>
 */
public class AnnotationUtil
{

    private static final Log log = LogFactory.getLog(AnnotationUtil.class);

    /**
     * Gathers a list of classes with a defined Annotation
     * @param annotationClass The annotation to look for
     * @param jarPath The path to the jar
     * @return  The list of classes with the annotation
     * @throws IOException thrown if something goes wrong when reading the jar
     */
    public static List<Class> getClassesWithAnnotationFromJar(Class<? extends Annotation> annotationClass, String jarPath) throws IOException
    {

        List<Class> annotatedClasses = new ArrayList<Class>();

        JarFile jarFile = new JarFile(jarPath);

        Enumeration<JarEntry> e = jarFile.entries();

        while (e.hasMoreElements())
        {
            JarEntry entry = e.nextElement();

            Class clazz = getAnnotatedClass(annotationClass,entry.getName());

            if (clazz != null)
            {
                annotatedClasses.add(clazz);
            }
        }

        jarFile.close();

        return annotatedClasses;
    }

    /**
     * Returns the Class if the provided String is a FQN class that contains the annotation
     * @param annotationClass The annotation to look for
     * @param classFilename The fully qualified name of the class as a String
     * @return the Class if contains the annotation, otherwise returns null.
     */
    public static Class getAnnotatedClass(Class<? extends Annotation> annotationClass, String classFilename)
    {
        if (classFilename.endsWith(".class")) {

            String fileDir = classFilename.substring(0, classFilename.lastIndexOf("/"));
            String className = classFilename.substring(classFilename.lastIndexOf("/")+1,classFilename.indexOf(".class") );

            String packageName = fileDir.replaceAll("/",".");

            if (packageName.startsWith("."))
                    packageName = packageName.substring(1, fileDir.length());

           // removes the .class extension
            try {
                // Try to create an instance of the object
                Class clazz = Class.forName(packageName+"."+className);

                // check for the annotation is present, and if present, return the class
                if (clazz.isAnnotationPresent(annotationClass)) {
                    return clazz;
                }

            } catch (Throwable e) {
                log.debug("Error loading class "+packageName+"."+className+": "+e);
            }
        }

        // if the file does not have the annotation return null
        return null;
    }
}
