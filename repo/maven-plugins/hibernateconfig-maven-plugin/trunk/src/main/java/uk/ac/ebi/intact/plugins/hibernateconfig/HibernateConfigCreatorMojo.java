/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins.hibernateconfig;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Generates a properties file with the topic names as keys and the classes as values
 *
 * @goal generate-config
 * 
 * @phase generate-sources
 */
public class HibernateConfigCreatorMojo
        extends AbstractMojo
{

    private static String NOT_DEFINED = "NOT_DEFINED";

    /**
    * Project instance, used to add new source directory to the build.
    * @parameter default-value="${project}"
    * @required
    * @readonly
    */
    private MavenProject project;

    /**
    * The set of dependencies required by the project
    * @parameter default-value="${project.artifacts}"
    * @required
    * @readonly
    */
    private java.util.Set<Artifact> dependencies;

    /**
    * project-helper instance, used to make addition of resources
    * simpler.
    * @component
    */
    private MavenProjectHelper helper;

    /**
     * The path where the file will be situated inside the classes build directory
     * @parameter default-value="target/hibernate/config"
     */
    private String targetPath;

    /**
     * Name of the config filename
     * @parameter default-value="hibernate.cfg.xml"
     */
    private String filename;

    /**
     * Whether SQL will be shown when the application is executed or not
     * @parameter default-value="false"
     */
    private boolean showSql;

    /**
     * Whether the SQL statements will be formatted, if shown
     * @parameter default-value="true"
     */
    private boolean formatSql;

    /**
     * Ddl behaviour. Possible values are: "create-drop", "create", "update" and "none"
     * @parameter default-value="none"
     */
    private String hbm2ddlAuto;

    /**
     * The hibernate dialect to use
     * @parameter
     * @required
     */
    private String dialect;

    /**
     * The JDBC driver class name
     * @parameter
     * @required
     */
    private String driver;

    /**
     * The URL to use for the connections
     * @parameter
     * @required
     */
    private String url;

    /**
     * The database user name
     * @parameter
     * @required
     */
    private String user;

    /**
     * The database user password
     * @parameter
     */
    private String password;

    /**
     * The hibernate connection provider class, if not using c3p0
     * @parameter default-value="org.hibernate.connection.C3P0ConnectionProvider"
     */
    private String connectionProviderClass;

    /**
     * The scope where the file will be placed (possible values are "runtime" and "test").
     * If using "runtime" the config file will be available for runtime and tests. Default "test"
     * @parameter default-value="runtime"
     */
    private String scope;

    /**
     * List of hibernate events
     * @parameter
     */
    private List<HibernateEvent> hibernateEvents;

    /**
     * The absolute path where the lucene index used can be found
     * @parameter
     */
    private String luceneIndexDir;

    /**
     * The lucene analyzer class (default, the class name of the @Indexed entity)
     * @parameter
     */
    private String luceneAnalyzer;

    /**
     * The name of the session factory. When deployed, the session factory will be put in the JNDI context
     * @parameter
     */
    private String sessionFactoryName;

    private String hibernateEventsAsString = "";

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("Hibernate Mojo in action");

        if (password == null)
        {
            password = "";
        }

        if (luceneIndexDir != null)
        {
             if (hibernateEvents == null)
             {
                 hibernateEvents = new ArrayList<HibernateEvent>();
             }
            hibernateEvents.add(new HibernateEvent("post-commit-update", "org.hibernate.lucene.event.LuceneEventListener"));
            hibernateEvents.add(new HibernateEvent("post-commit-insert", "org.hibernate.lucene.event.LuceneEventListener"));
            hibernateEvents.add(new HibernateEvent("post-commit-delete", "org.hibernate.lucene.event.LuceneEventListener"));
        }

        File tempDir = new File(targetPath);

        if (!tempDir.exists())
        {
            tempDir.mkdirs();
        }

        // create velocity context
        VelocityContext context = new VelocityContext();
        context.put("mojo", this);

        if (project != null)
        {
            context.put("artifactId", project.getArtifactId());
            context.put("version", project.getVersion());
        }

        File outputFile = new File(tempDir, filename);

        String templateFilename = "hibernateconfig.vm";
        File templateFile = new File(getClass().getResource(templateFilename).getFile());

        Template template = null;
        try
        {
            Properties props = new Properties();

            // when executing the test, the template is loaded from a file
            // however, the classpath will be used when calling the plugin
            if (isResourceInsideJar(templateFile))
            {
                props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
                props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
                      ClasspathResourceLoader.class.getName());
                templateFilename = "/uk/ac/ebi/intact/plugins/hibernateconfig/hibernateconfig.vm";
            }
            else
            {
                props.setProperty( VelocityEngine.RESOURCE_LOADER, "file" );
                props.setProperty( "file."+VelocityEngine.RESOURCE_LOADER+".path",
                        templateFile.getParent() );
                templateFilename = "hibernateconfig.vm";
            }

            Velocity.init(props);

            template = Velocity.getTemplate(templateFilename);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MojoExecutionException("Couldn't get template: " + templateFile);
        }

        try
        {
            Writer writer = new FileWriter(outputFile);
            template.merge(context, writer);
            writer.close();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Problem writing to file with name: " + outputFile, e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MojoExecutionException("Couldn't create hibernate.cfg.xml file from template", e);
        }

        // Adding the resources
         List includes = Collections.singletonList(filename);
         List excludes = null;

        if (scope.equalsIgnoreCase("test"))
        {
            // add the config only in test
            getLog().debug("Adding hibernate config file to tests");

            if (project != null)
                helper.addTestResource(project, outputFile.getParent(), includes, excludes);
        }
        else
        {
            getLog().debug("Adding hibernate config file to output classes: "+outputFile.getParent()+" "+filename);

            if (project != null)
            {
                if (!driver.equals(NOT_DEFINED))
                {
                    helper.addResource(project, outputFile.getParent(), includes, excludes);
                }
                else
                {
                    getLog().error(filename+" not included in the classes folder, because some of its values are "+NOT_DEFINED);
                }
            }

        }

    }

    private boolean isResourceInsideJar(File resourceFile)
    {
        return resourceFile.toString().contains(".jar!");
    }

    public String getTargetPath()
    {
        return targetPath;
    }

    public void setTargetPath(String targetPath)
    {
        this.targetPath = targetPath;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public boolean isShowSql()
    {
        return showSql;
    }

    public void setShowSql(boolean showSql)
    {
        this.showSql = showSql;
    }

    public boolean isFormatSql()
    {
        return formatSql;
    }

    public void setFormatSql(boolean formatSql)
    {
        this.formatSql = formatSql;
    }

    public String getHbm2ddlAuto()
    {
        return hbm2ddlAuto;
    }

    public void setHbm2ddlAuto(String hbm2ddlAuto)
    {
        this.hbm2ddlAuto = hbm2ddlAuto;
    }

    public String getDialect()
    {
        return dialect;
    }

    public void setDialect(String dialect)
    {
        this.dialect = dialect;
    }

    public String getDriver()
    {
        return driver;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getConnectionProviderClass()
    {
        return connectionProviderClass;
    }

    public void setConnectionProviderClass(String connectionProviderClass)
    {
        this.connectionProviderClass = connectionProviderClass;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public List<HibernateEvent> getHibernateEvents()
    {
        return hibernateEvents;
    }

    public void setHibernateEvents(List<HibernateEvent> hibernateEvents)
    {
        this.hibernateEvents = hibernateEvents;
    }

    public String getLuceneIndexDir()
    {
        return luceneIndexDir;
    }

    public void setLuceneIndexDir(String luceneIndexDir)
    {
        this.luceneIndexDir = luceneIndexDir;
    }

    public String getLuceneAnalyzer()
    {
        return luceneAnalyzer;
    }

    public void setLuceneAnalyzer(String luceneAnalyzer)
    {
        this.luceneAnalyzer = luceneAnalyzer;
    }

    public String getSessionFactoryName()
    {
        return sessionFactoryName;
    }

    public void setSessionFactoryName(String sessionFactoryName)
    {
        this.sessionFactoryName = sessionFactoryName;
    }
}
