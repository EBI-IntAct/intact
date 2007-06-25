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
package uk.ac.ebi.intact.unitdataset.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.commons.dataset.DbUnitTestDataset;
import uk.ac.ebi.intact.commons.dataset.TestDataset;
import uk.ac.ebi.intact.commons.dataset.TestDatasetProvider;
import uk.ac.ebi.intact.commons.lang.CommonURLClassLoader;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @goal import
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class UnitDatasetImportMojo extends IntactHibernateMojo {

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${intact.dataset.id}"
     * @required
     */
    private String id;

    /**
     * @parameter expression="${intact.dataset.provider}"
     * @required
     */
    private String provider;


    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    protected void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException {
        commitTransaction();

        getLog().info("Importing DBUnit dataset: '" + id + "' in " + provider);

        ClassLoader classLoader = getClassLoader();
        Class providerClazz = null;
        try {
            providerClazz = classLoader.loadClass(provider);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Could not find class " + provider, e);
        }

        if (TestDatasetProvider.class.isAssignableFrom(providerClazz)) {
            TestDatasetProvider providerInstance = null;
            try {
                providerInstance = (TestDatasetProvider) providerClazz.newInstance();
            } catch (Exception e) {
                throw new MojoExecutionException("Exception instantiating: " + providerClazz);
            }

            TestDataset testDataset = providerInstance.getTestDataset(id);

            if (testDataset instanceof DbUnitTestDataset) {
                DbUnitTestDataset dbUnitTestDataset = (DbUnitTestDataset) testDataset;
                IntactUnit iu = new IntactUnit();
                try {
                    iu.createSchema(false);
                } catch (IntactTransactionException e) {
                    throw new MojoExecutionException("Exception creating schema", e);
                }
                beginTransaction();

                if (IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInstitutionDao().countAll() != 0) {
                    throw new IllegalStateException("The database should be empty at this point");
                }

                iu.importTestDataset(dbUnitTestDataset);
                
                commitTransaction();
            } else {
                throw new MojoExecutionException("The dataset provided must be a: " + DbUnitTestDataset.class);
            }

        } else {
            throw new MojoExecutionException("Provider should be a subclass of " + TestDatasetProvider.class.getName() + "; provided: " + provider);
        }

    }

    protected ClassLoader getClassLoader() throws MojoExecutionException {
        CommonURLClassLoader classLoader = new CommonURLClassLoader(getDependencies(), UnitDatasetImportMojo.class.getClassLoader());
        return classLoader;
    }

    protected URL[] getDependencies() throws MojoExecutionException {
        List<URL> urls = new ArrayList<URL>();

        try {
            for (String strFile : (Collection<String>) getProject().getCompileClasspathElements()) {
                File file = new File(strFile);
                urls.add(file.toURL());
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Exception processing compile dependencies", e);
        }

        return urls.toArray(new URL[urls.size()]);
    }

    protected void beginTransaction() {
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
    }

    protected void commitTransaction() throws MojoExecutionException {
        if (IntactContext.getCurrentInstance().getDataContext().isTransactionActive()) {
            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            } catch (IntactTransactionException e) {
                throw new MojoExecutionException("Problem initialising", e);
            }
        }
    }

    /**
     * Implementation of abstract method from superclass
     */
    public MavenProject getProject() {
        return project;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
