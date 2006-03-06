/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

//import HTTPClient.*;
//import org.apache.soap.transport.*;
//import oracle.soap.transport.*;
import uk.ac.ebi.cdb.webservice.proxy.WSCitationProxy;
import uk.ac.ebi.cdb.bean.Author;
import uk.ac.ebi.cdb.bean.Citation;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class handling the collection of data from CitExplre Web Service based on a pubmed ID.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Aug-2005</pre>
 */
public class IntactCitationFactory {

    ///////////////////////////
    // Singleton's methods

    private static IntactCitationFactory ourInstance = new IntactCitationFactory();

    public static IntactCitationFactory getInstance() {
        return ourInstance;
    }

    private IntactCitationFactory() {
    }


    ///////////////////////////
    // Business

    /**
     * CitExplore web service
     */
    private static WSCitationProxy citProxy = new WSCitationProxy();

    /**
     * Set the email pattern string
     */
    private static Pattern emailCheckerPattern = Pattern.compile( ".+@.+\\.[a-z]+" );

    /**
     * If available from pubmed return the email of the first author.
     *
     * @param citation the data collected from CitExplore.
     *
     * @return a valid email or null.
     */
    private String getEmail( Citation citation ) {

        String affiliation = citation.getAffiliation();

        if ( affiliation == null ) {
            return null;
        }

        // 1. extract the email address
        int spacePos = affiliation.lastIndexOf( " " );

        // email shows up last in the affiliation
        if ( spacePos != -1 ) {
            affiliation = citation.getAffiliation().substring( spacePos + 1, citation.getAffiliation().length() );
        }

        int atPos = affiliation.indexOf( "@" );

        if ( atPos != -1 ) {
            return affiliation;
        }

        // 2. validate email
        //Match the given string with the pattern
        Matcher m = emailCheckerPattern.matcher( affiliation );

        //check whether match is found
        boolean matchFound = m.matches();

        if ( matchFound ) {
            // this was a valid email
            return affiliation;
        }

        // email was not valid.
        return null;
    }

    /**
     * Based upon a pubmed Id, collect from CitExplore a subset of the information related to that publication.
     *
     * @param pubmedId the pubmed ID of the publication we want information on.
     * @return
     * @throws Exception
     */
    public IntactCitation buildCitation( String pubmedId ) throws UnexpectedException,
                                                                  PublicationNotFoundException {

        Citation c = null;
        try {
            c = citProxy.getMedlineCitation( new Integer( pubmedId ) );
        } catch( Exception e ) {

            if( e.getMessage().indexOf( "uk.ac.ebi.cdb.webservice.DataNotFoundException" ) != -1 ) {
                throw new PublicationNotFoundException( "The PubMed ID "+ pubmedId +" could not be found." );
            }

            // otherwise throw the original exception
            e.printStackTrace( );
            throw new UnexpectedException( "An unexpected error occured (ie. "+ e.getMessage() +").", e );
        }

        // retreive information

        int year = (c.getJournalIssue().getYearOfPublication()).intValue();
        String title = c.getTitle();
        String email = getEmail( c );

        // get the first author last name
        String authorLastName = null;

        // list of all the authors
        String authors = null;

        List authorList = c.getAuthorCollection();

        if ( authorList.isEmpty() ) {

//            System.err.println( experiment.getShortLabel() + ", " + pubmedId + ": Could not find an author name." );

        } else {

            Author author = (Author) authorList.iterator().next();

            // it has to be lowercase
            authorLastName = author.getLastName().toLowerCase();

            // 11 characters maximum
            authorLastName = authorLastName.substring( 0, Math.min( 11, authorLastName.length() ) );

            // replace everything that is not in [a-z] by _
            StringBuffer sb = new StringBuffer( authorLastName );
            for ( int i = 0; i < sb.length(); i++ ) {
                char ch = sb.charAt( i );

                // if current char is not in [a-z], replace it
                if ( !( ch >= 'a' && ch <= 'z' ) ) {
                    sb.setCharAt( i, '_' );
                }
            }

            authorLastName = sb.toString();

            // build the list of authors
            if( authorList.size() > 1 ) {

                StringBuffer authorsBuffer = new StringBuffer( 128 );

                for ( Iterator iterator = authorList.iterator(); iterator.hasNext(); ) {
                    Author anAuthor = (Author) iterator.next();

                    authorsBuffer.append( anAuthor.getLastName() ).append( ' ' ).append( anAuthor.getInitials() ).append( '.' );

                    if( iterator.hasNext() ) {
                        authorsBuffer.append( ',' ).append( ' ' );
                    }
                }

                authors = authorsBuffer.toString();
            }

        }

        return new IntactCitation( authorLastName, year, title, authors, email );
    }
}