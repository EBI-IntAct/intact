/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.annotation.AnnotationUtil;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Mar-2006</pre>
 */
public class IntactAnnotator
{

    public static final Log log = LogFactory.getLog(IntactAnnotator.class);

    private IntactAnnotator()
    {
    }

    /**
     * Retrieves a list of the annotated classes to use. This methods look for classes annotated with
     * <code>@javax.persistence.Entity</code> in the uk.ac.ebi.intact.model package
     * @return  The list of hibernate annotated classes
     */
    public static List<Class> getAnnotatedClasses() {
        List<Class> annotatedClasses = new ArrayList<Class>();

        String packageName = "/uk/ac/ebi/intact/model";

        // Get a File object for the package
        URL url = IntactAnnotator.class.getResource(packageName);
        File directory = new File(url.getFile());

        if (directory.exists()) {
            log.debug("Reading annotated classes from directory: "+directory);

            // Get the list of the files contained in the package
            for (String file : directory.list()) {
                Class clazz = AnnotationUtil.getAnnotatedClass(Entity.class, packageName+"/"+file);

                if (clazz != null)
                {

                    annotatedClasses.add(clazz);
                }
            }
        }
        else
        {
            log.warn("Directory not found: "+directory);

            // probably directory points inside a jar file, we get the jar name
            // and will look for annotated classes inside
            String jarPath = directory.toString().substring(5, directory.toString().indexOf(".jar")+4);

            log.info("Searching classes in jar: "+jarPath);
            try
            {
                annotatedClasses.addAll(AnnotationUtil.getClassesWithAnnotationFromJar(Entity.class, jarPath));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return annotatedClasses;
    }



}
