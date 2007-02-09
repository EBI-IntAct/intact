/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import uk.ac.ebi.intact.bridge.adapters.UniprotBridgeAdapter;

/**
 * Factory allowing to instanciate a ProteinLoaderService.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Feb-2007</pre>
 */
public class ProteinLoaderServiceFactory {

    public static final String SPRING_CONFIG_FILE = "/spring-config.xml";

    //////////////////////
    // Singleton

    private static ProteinLoaderServiceFactory ourInstance = new ProteinLoaderServiceFactory();

    public static ProteinLoaderServiceFactory getInstance() {
        return ourInstance;
    }

    private ProteinLoaderServiceFactory() {
    }

    //////////////////////
    // Instance methods

    private UniprotBridgeAdapter getUniprotBridgeAdapter() {
        ClassPathResource resource = new ClassPathResource( SPRING_CONFIG_FILE );
        BeanFactory factory = new XmlBeanFactory( resource );
        UniprotBridgeAdapter bridge = ( UniprotBridgeAdapter ) factory.getBean( "UniprotBridgeAdapter" );
        return bridge;
    }

    public ProteinLoaderService buildProteinLoaderService() {
        return buildProteinLoaderService( getUniprotBridgeAdapter() );
    }

    public ProteinLoaderService buildProteinLoaderService( UniprotBridgeAdapter adapter ) {
        if( adapter == null ) {
            throw new IllegalArgumentException( "You must give a non null implementation of UniprotBridgeAdapter." );
        }
        return new ProteinLoaderServiceImpl( adapter );
    }
}