/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEventListener;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.event.IntactObjectEventListener;
import uk.ac.ebi.intact.model.meta.DbInfo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public class StandardCoreDataConfig extends AbstractHibernateDataConfig {

    private static final Log log = LogFactory.getLog( StandardCoreDataConfig.class );

    public static final String NAME = "uk.ac.ebi.intact.config.STANDARD_CORE";

    private boolean listenersRegistered;

    public StandardCoreDataConfig( IntactSession session ) {
        super( session );
    }

    public String getName() {
        return NAME;
    }

    protected List<String> getPackagesWithEntities() {
        List<String> packages = new ArrayList<String>( 1 );

        // /uk/ac/ebi/intact/model
        packages.add( Interactor.class.getPackage().getName() );
        packages.add( DbInfo.class.getPackage().getName() );

        return packages;
    }

    public List<String> getExcludedEntities()
    {
        return Arrays.asList("uk.ac.ebi.intact.model.SearchItem");
    }

    @Override
    public Ejb3Configuration getConfiguration() {
        Ejb3Configuration configuration = super.getConfiguration();

        if ( !isListenersRegistered() ) {
            if ( log.isDebugEnabled() ) {
                log.info( "Registering core EventListeners:" );
                log.debug( "\tRegistering: " + IntactObjectEventListener.class );
            }

            List<PreInsertEventListener> preInserts = new ArrayList<PreInsertEventListener>(Arrays.asList(configuration.getEventListeners().getPreInsertEventListeners()));
            preInserts.add(new IntactObjectEventListener());

            List<PreUpdateEventListener> preUpdates = new ArrayList<PreUpdateEventListener>(Arrays.asList(configuration.getEventListeners().getPreUpdateEventListeners()));
            preUpdates.add(new IntactObjectEventListener());

            configuration.getEventListeners().setPreInsertEventListeners(preInserts.toArray(new PreInsertEventListener[preInserts.size()]));
            configuration.getEventListeners().setPreUpdateEventListeners(preUpdates.toArray(new PreUpdateEventListener[preUpdates.size()]));

        }

        return configuration;
    }

    protected File getConfigFile() {
        URL resource = StandardCoreDataConfig.class.getResource("/hibernate.cfg.xml");
        File file = (resource == null)? null : new File(resource.getFile());
        return file;
    }

    protected void setListenersRegistered( boolean listenersRegistered ) {
        //getSession().setApplicationAttribute(LISTENERS_REGISTERED_FLAG, listenersRegistered);
        this.listenersRegistered = listenersRegistered;
    }

    protected boolean isListenersRegistered() {
        return listenersRegistered;

        //Object listenersRegistered = getSession().getApplicationAttribute(LISTENERS_REGISTERED_FLAG);
        //return (listenersRegistered == null)? Boolean.FALSE :(Boolean) listenersRegistered;
    }
}
