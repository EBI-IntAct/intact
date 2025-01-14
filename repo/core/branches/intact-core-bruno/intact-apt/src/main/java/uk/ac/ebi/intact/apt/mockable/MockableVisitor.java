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
package uk.ac.ebi.intact.apt.mockable;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MockableVisitor extends SimpleDeclarationVisitor {

    private static final String MOCK_TEMPLATE = "Mock.vm";

    private String generatedPackage = "uk.ac.ebi.intact.core.unit.mock";

    private AnnotationProcessorEnvironment env;
    private File targetDir;
    private MockInfo mockInfo;


    public MockableVisitor(AnnotationProcessorEnvironment env, File targetDir) {
        super();
        this.env = env;
        this.targetDir = targetDir;
    }

    @Override
    public void visitInterfaceDeclaration(InterfaceDeclaration interfaceDeclaration) {
        this.mockInfo = new MockInfo(interfaceDeclaration, generatedPackage);
    }

    @Override
    public void visitMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.mockInfo.addMethodSignature(methodDeclaration);
    }


    public void generateMock() throws Exception {
        // create velocity context
        VelocityContext context = new VelocityContext();
        context.put("mockInfo", mockInfo);

        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class." + VelocityEngine.RESOURCE_LOADER + ".class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        Velocity.init(props);

        Template template = Velocity.getTemplate(MOCK_TEMPLATE);

        // write the resulting file with velocity
        File mockFile = mockInfo.getFileName(targetDir);
        Writer writer = new FileWriter(mockFile);
        template.merge(context, writer);
        writer.close();

        mockFile.setReadOnly();
    }


    public String getGeneratedPackage()
    {
        return generatedPackage;
    }
}