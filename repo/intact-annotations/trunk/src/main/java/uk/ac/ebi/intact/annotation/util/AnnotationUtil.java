/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.annotation.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utilities to deal with annotations
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Jun-2006</pre>
 */
public class AnnotationUtil {

    private static final Log log = LogFactory.getLog(AnnotationUtil.class);

    /**
     * Gathers a list of classes with a defined Annotation
     *
     * @param annotationClass The annotation to look for
     * @param jarPath         The path to the jar
     *
     * @return The list of classes with the annotation
     *
     * @throws IOException thrown if something goes wrong when reading the jar
     */
    public static List<Class> getClassesWithAnnotationFromJar(Class<? extends Annotation> annotationClass, String jarPath) throws IOException {
        return getClassesWithAnnotationFromJar(annotationClass, jarPath, null);
    }

    /**
     * Gathers a list of classes with a defined Annotation in a package
     *
     * @param annotationClass The annotation to look for
     * @param jarPath         The path to the jar
     *
     * @return The list of classes with the annotation
     *
     * @throws IOException thrown if something goes wrong when reading the jar
     */
    public static List<Class> getClassesWithAnnotationFromJar(Class<? extends Annotation> annotationClass, String jarPath, String packageName) throws IOException {
        List<Class> annotatedClasses = new ArrayList<Class>();

        JarFile jarFile = new JarFile(jarPath);

        Enumeration<JarEntry> e = jarFile.entries();

        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();

            Class clazz = getAnnotatedClass(annotationClass, entry.getName());

            if (clazz != null) {

                if (packageName != null && clazz.getPackage().getName().equals(packageName)) {
                    annotatedClasses.add(clazz);
                } else if (packageName == null) {
                    annotatedClasses.add(clazz);
                }
            }
        }

        jarFile.close();

        return annotatedClasses;
    }

    /**
     * Returns the classes contained in those directories present in the classpath
     *
     * @param annotationClass annotation to look for
     *
     * @return Classes containing the annotation
     */
    public static List<Class> getClassesWithAnnotationFromClasspathDirs(Class<? extends Annotation> annotationClass) {
        //recover the classpath
        String classPath = System.getProperty("java.class.path");

        String[] classpathItems = classPath.split(":");

        if (classpathItems.length == 0) {
            classPath.split(";");
        }

        List<Class> annotatedClasses = new ArrayList<Class>();

        for (String classpathItem : classpathItems) {
            File ciFile = new File(classpathItem);

            if (ciFile.isDirectory()) {
                annotatedClasses.addAll(getClassesWithAnnotationFromDir(annotationClass, ciFile));
            }
        }

        return annotatedClasses;
    }

    /**
     * Returns the classes contained in the classpath
     * NOTE: this method is not recommended as it can take a long time
     *
     * @param annotationClass annotation to look for
     *
     * @return Classes containing the annotation
     */
    public static List<Class> getClassesWithAnnotationFromClasspath(Class<? extends Annotation> annotationClass) throws IOException {
        List<Class> annotatedClasses = new ArrayList<Class>();
        annotatedClasses.addAll(getClassesWithAnnotationFromClasspathJars(annotationClass));
        annotatedClasses.addAll(getClassesWithAnnotationFromClasspathDirs(annotationClass));
        return annotatedClasses;
    }

    /**
     * Returns the classes contained in those jars present in the classpath
     * NOTE: this method is not recommended as it can take a long time
     *
     * @param annotationClass annotation to look for
     *
     * @return Classes containing the annotation
     */
    public static List<Class> getClassesWithAnnotationFromClasspathJars(Class<? extends Annotation> annotationClass) throws IOException {
        //recover the classpath
        String classPath = System.getProperty("java.class.path");

        String[] classpathItems = classPath.split(":");

        if (classpathItems.length == 0) {
            classPath.split(";");
        }

        List<Class> annotatedClasses = new ArrayList<Class>();

        for (String classpathItem : classpathItems) {
            File ciFile = new File(classpathItem);

            if (!ciFile.isDirectory() && classpathItem.endsWith(".jar")) {
                annotatedClasses.addAll(getClassesWithAnnotationFromJar(annotationClass, ciFile.toString()));
            }
        }

        return annotatedClasses;
    }

    /**
     * Returns the classes contained the annotation in a directory. It searches the subdirectories recursively
     *
     * @param annotationClass annotation to look for
     * @param dir             Directory to use, recursive search in its subdirectories
     *
     * @return Classes containing the annotation
     */
    public static List<Class> getClassesWithAnnotationFromDir(Class<? extends Annotation> annotationClass, File dir) {
        return getClassesWithAnnotationFromDir(annotationClass, dir, dir);
    }

    private static List<Class> getClassesWithAnnotationFromDir(Class<? extends Annotation> annotationClass, File dir, File parentDir) {
        List<Class> classesFromDir = new ArrayList<Class>();

        File[] classFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });

        for (File classFile : classFiles) {
            String classFileWithoutDir = classFile.toString().substring(parentDir.toString().length());

            Class annotatedClass = AnnotationUtil.getAnnotatedClass(annotationClass, classFileWithoutDir);

            if (annotatedClass != null) {
                classesFromDir.add(annotatedClass);
            }
        }

        File[] subdirs = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        for (File subdir : subdirs) {
            List<Class> classesFromSubdir = getClassesWithAnnotationFromDir(annotationClass, subdir, parentDir);
            classesFromDir.addAll(classesFromSubdir);
        }

        return classesFromDir;
    }


    /**
     * Returns the Class if the provided String is a FQN class that contains the annotation
     *
     * @param annotationClass The annotation to look for
     * @param classFilename   The fully qualified name of the class as a String
     *
     * @return the Class if contains the annotation, otherwise returns null.
     */
    public static Class getAnnotatedClass(Class<? extends Annotation> annotationClass, String classFilename) {
        if (classFilename.endsWith(".class")) {

            String fileDir;
            String className;

            if (classFilename.contains("/")) {
                fileDir = classFilename.substring(0, classFilename.lastIndexOf("/"));
                className = classFilename.substring(classFilename.lastIndexOf("/") + 1, classFilename.indexOf(".class"));
            } else {
                fileDir = "";
                className = classFilename;
            }

            String packageName = fileDir.replaceAll("/", ".");

            if (packageName.startsWith("."))
                packageName = packageName.substring(1, fileDir.length());

            // removes the .class extension
            try {
                // Try to create an instance of the object
                Class clazz = Class.forName(packageName + "." + className);

                // check for the annotation is present, and if present, return the class
                if (clazz.isAnnotationPresent(annotationClass)) {
                    return clazz;
                }

            } catch (Throwable e) {
                log.debug("Error loading class " + packageName + "." + className + ": " + e);
            }
        }

        // if the file does not have the annotation return null
        return null;
    }
}
