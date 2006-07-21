/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.SequenceGenerator;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Generates identifiers for IntAct objects
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jul-2006</pre>
 */
public class IntactIdGenerator extends SequenceGenerator
{

    private static final Log log = LogFactory.getLog(IntactIdGenerator.class);

    /**
     * The ID generation is handled by the class uk.ac.ebi.intact.model.IntactIdGeneration, which extends
     * Hibernate's SequenceGeneration. This class provides the new ID to the underlying hibernate layer.
     * This new ID is the concatenation of the prefix and a sequence provided by the database, separated
     * by a dash.
     * @param sessionImplementor a hibernate session implementor
     * @param object the object being persisted
     * @return the new generated ID
     * @throws HibernateException if something goes wrong
     */
    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object object) throws HibernateException
    {
        Properties props = new Properties();
        try
        {
            props.load(IntactIdGenerator.class.getResourceAsStream("ac.properties"));
        }
        catch (IOException e)
        {
            throw new HibernateException("Error loading default AC prefix", e);
        }

        String prefix = props.getProperty("ac.prefix");

        String id = prefix+"-"+super.generate(sessionImplementor, object);

        log.trace("Assigning Id: "+id);

        return id;
    }


    @Override
    public String getSequenceName()
    {
        return "intact_sequence";
    }


    @Override
    public Object generatorKey()
    {
        return "intact_generator";
    }
}
