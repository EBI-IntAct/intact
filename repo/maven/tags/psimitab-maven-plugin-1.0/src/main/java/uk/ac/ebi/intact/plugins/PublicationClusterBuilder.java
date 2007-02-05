/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-Jan-2007</pre>
 */
public class PublicationClusterBuilder {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( PublicationClusterBuilder.class );

    public static final Pattern PUBLICATION_PATTERN = Pattern.compile( "(\\d+).*\\.xml" );


    private File directory;

    public PublicationClusterBuilder( File directory ) {

        if ( directory == null ) {
            throw new IllegalArgumentException( "You must give a non null directory." );
        }

        if ( !directory.exists() ) {
            throw new IllegalArgumentException( "You must give an existing directory." );
        }

        if ( !directory.isDirectory() ) {
            throw new IllegalArgumentException( "You must give a valid directory." );
        }

        if ( !directory.canRead() ) {
            throw new IllegalArgumentException( "You must give a readable directory." );
        }

        this.directory = directory;
    }

    public Map<Integer, Collection<File>> build() {
        Map<Integer, Collection<File>> map = new HashMap<Integer, Collection<File>>();

        // 10688190-01.xml
        // 10688190_negative.xml
        // 11283351_ito-2001-1_01.xml

        Stack<File> stack = new Stack<File>();
        stack.push( directory );

        while ( !stack.isEmpty() ) {
            File dir = stack.pop();

            File[] files = dir.listFiles();
            for ( int i = 0; i < files.length; i++ ) {
                File file = files[i];

                if ( file.isDirectory() ) {
                    stack.push( file ); // process later
                } else {
                    // this is a file, check it out
                    Matcher matcher = PUBLICATION_PATTERN.matcher( file.getName() );
                    if ( !matcher.matches() ) {
                        if ( !file.getName().endsWith( ".zip" ) ) {
                            log.error( "Could not match: " + file.getAbsolutePath() );
                        }
                    } else {
                        Integer pmid = Integer.parseInt( matcher.group( 1 ) );
                        Collection<File> associatedFiles = map.get( pmid );
                        if ( associatedFiles == null ) {
                            associatedFiles = new ArrayList<File>( 2 );
                            map.put( pmid, associatedFiles );
                        }
                        associatedFiles.add( file );
                    }
                }
            }
        }

        return map;
    }
}