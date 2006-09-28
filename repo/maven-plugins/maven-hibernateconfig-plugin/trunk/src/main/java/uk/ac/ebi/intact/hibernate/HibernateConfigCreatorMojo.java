/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.hibernate;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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
     * @required
     */
    private String targetPath;

    /**
     * Name of the config filename
     * @parameter default-value="hibernate.cfg.xml"
     * @required
     */
    private String filename;

    /**
     * Whether SQL will be shown when the application is executed or not
     * @parameter default-value="false"
     * @required
     */
    private boolean showSql;

    /**
     * Whether the SQL statements will be formatted, if shown
     * @parameter default-value="true"
     * @required
     */
    private boolean formatSql;

    /**
     * Ddl behaviour. Possible values are: "create-drop", "create", "update" and "none"
     * @parameter default-value="none"
     * @required
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
     * @required
     */
    private String connectionProviderClass;

    /**
     * The scope where the file will be placed (possible values are "runtime" and "test").
     * If using "runtime" the config file will be available for runtime and tests.
     * @parameter default-value="runtime"
     * @required
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

    public void execute() throws MojoExecutionException
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

        if (hibernateEvents != null && !hibernateEvents.isEmpty())
        {
            createHibernateEventsString();
        }

        File tempDir = new File(project.getBasedir(), targetPath);

        if (!tempDir.exists())
        {
            tempDir.mkdirs();
        }

        File outputFile = new File(tempDir, filename);

        try
        {
            Writer writer = new FileWriter(outputFile);

            InputStream is = getClass().getResourceAsStream("hibernate.cfg.xml.template");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null)
            {
                line = filterLine(line);
                writer.write(line+"\n");
            }

            writer.close();

            getLog().debug("File created: "+outputFile);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Could create file", e);
        }

         // Adding the resources
         List includes = Collections.singletonList(filename);
         List excludes = null;

        if (scope.equalsIgnoreCase("test"))
        {
            // add the config only in test
            getLog().debug("Adding hibernate config file to tests");
            helper.addTestResource(project, outputFile.getParent(), includes, excludes);
        }
        else
        {
            getLog().debug("Adding hibernate config file to output classes: "+outputFile.getParent()+" "+filename);
            helper.addResource(project, outputFile.getParent(), includes, excludes);
        }

    }

    private void createHibernateEventsString()
    {
        StringBuffer sb = new StringBuffer();

        for (HibernateEvent hibernateEvent : hibernateEvents)
        {
            sb.append("        <event type=\""+hibernateEvent.getType()+"\">\n" +
                    "            <listener class=\""+hibernateEvent.getClassName()+"\"/>\n" +
                    "        </event>\n");
        }

        hibernateEventsAsString = sb.toString();
    }

    private String filterLine(String line)
    {
        line = line.replaceAll("\\$\\{db\\.dialect\\}", dialect);
        line = line.replaceAll("\\$\\{db\\.driver\\}", driver);
        line = line.replaceAll("\\$\\{db\\.url\\}", url);
        line = line.replaceAll("\\$\\{db\\.user\\}", user);
        line = line.replaceAll("\\$\\{db\\.password\\}", password);
        line = line.replaceAll("\\$\\{connection\\.provider_class\\}", connectionProviderClass);
        line = line.replaceAll("\\$\\{format_sql\\}", String.valueOf(formatSql));
        line = line.replaceAll("\\$\\{show_sql\\}", String.valueOf(showSql));
        line = line.replaceAll("\\$\\{hbm2ddl\\.auto\\}", hbm2ddlAuto);
        line = line.replaceAll("\\$\\{hibernateEvents\\}", hibernateEventsAsString);

        line = line.replaceAll("\\$\\{lucene.index_dir\\}",
               createPropertyLine("lucene.index_dir", luceneIndexDir, "Lucene index dir"));
        line = line.replaceAll("\\$\\{lucene.analyzer\\}",
               createPropertyLine("lucene.analyzer", luceneAnalyzer, "Lucene analyser") );

        if (sessionFactoryName == null || sessionFactoryName.trim().equals(""))
        {
            line = line.replaceAll("\\$\\{session_factory_name\\}", "name=\""+sessionFactoryName+"\"");
        }
        else
        {
            line.replaceAll("\\$\\{session_factory_name\\}", "");
        }

        return line;
    }

    private String createPropertyLine(String name, String value, String comments)
    {
        if (value == null) return "";

        return "<!-- "+comments+" -->\n        <property name=\""+name+"\">"+value+"</property>";
    }

}
