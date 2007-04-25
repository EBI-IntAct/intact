/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.psixml.tools.generator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import psidev.psi.mi.annotations.PsiXmlElement;
import uk.ac.ebi.intact.annotation.util.AnnotationUtil;
import uk.ac.ebi.intact.psixml.tools.generator.metadata.ModelClassMetadata;
import uk.ac.ebi.intact.psixml.tools.generator.metadata.ModelClassMetadataFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Creates source validator files using the Annotations in the PSI XML model classes
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:AnnotationSourceBuilder.java 8272 2007-04-25 10:20:12Z baranda $
 */
public class AnnotationSourceGenerator implements SourceGenerator {

    private File templateFile;

    /**
     * Constructor
     */
    public AnnotationSourceGenerator() {
    }

    public void generateClasses(SourceGeneratorContext sgContext) throws Exception {

        List<Class> modelClasses = getModelClassesFromJars(sgContext.getDependencyJars());

        SourceGeneratorHelper sourceBuilderHelper = new SourceGeneratorHelper(modelClasses, sgContext);

        for (Class modelClass : modelClasses) {
            create(sgContext, sourceBuilderHelper, modelClass);
        }
    }

    public void create(SourceGeneratorContext sgContext, SourceGeneratorHelper sbHelper, Class modelClass) throws Exception {
        String validatorClassName = sbHelper.getValidatorNameForClass(modelClass);

        VelocityContext context = sgContext.getVelocityContext();

        context.put("packageName", sgContext.getGeneratedPackage());
        context.put("modelClass", modelClass);
        context.put("type", validatorClassName);

        ModelClassMetadata modelClassMetadata = ModelClassMetadataFactory.createModelClassMetadata(sbHelper, modelClass);
        context.put("mcm", modelClassMetadata);

        File outputFile = sbHelper.getValidatorFileForClass(modelClass);

        // create a temporary copy of the template, so we avoid classpath issues later
        if (templateFile == null) {
            templateFile = createTempFileFromTemplate();
        }

        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class." + VelocityEngine.RESOURCE_LOADER + ".class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        Velocity.init(props);

        Template template = Velocity.getTemplate("ElementValidator.vm");

        // write the resulting file with velocity
        Writer writer = new FileWriter(outputFile);
        template.merge(context, writer);
        writer.close();
    }

    private File createTempFileFromTemplate() throws IOException {
        String templateFilename = "ElementValidator.vm";
        File temporaryFile = File.createTempFile("ElementValidator", ".vm");
        temporaryFile.deleteOnExit();

        FileWriter writer = null;

        InputStream is = AnnotationSourceGenerator.class.getResourceAsStream("/" + templateFilename);
        writer = new FileWriter(temporaryFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line + "\n");

        }

        writer.close();

        return temporaryFile;
    }

    protected List<Class> getModelClassesFromJars(File[] jarFiles) {
        List<Class> modelClasses = new ArrayList<Class>();

        for (File jarFile : jarFiles) {
            modelClasses.addAll(getModelClassesFromJar(jarFile));
        }

        return modelClasses;
    }

    protected List<Class> getModelClassesFromJar(File jarFile) {
        List<Class> modelClasses = null;

        try {
            // Looking for the annotation
            modelClasses = AnnotationUtil.getClassesWithAnnotationFromJar(PsiXmlElement.class, jarFile.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return modelClasses;
    }
}