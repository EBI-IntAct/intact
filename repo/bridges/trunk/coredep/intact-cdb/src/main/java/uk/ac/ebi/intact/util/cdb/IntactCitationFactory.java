/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import uk.ac.ebi.cdb.webservice.Citation;
import uk.ac.ebi.cdb.webservice.Author;
import uk.ac.ebi.intact.bridges.citexplore.CitexploreClient;

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

    private static ThreadLocal<IntactCitationFactory> ourInstance = new ThreadLocal<IntactCitationFactory>() {
        @Override
        protected IntactCitationFactory initialValue() {
            return new IntactCitationFactory();
        }
    };
    ///////////////////////////
    // Singleton's methods

    public static IntactCitationFactory getInstance() {
        return ourInstance.get();
    }

    private IntactCitationFactory() {
    }

    ///////////////////////////
    // Business

    /**
     * CitExplore web service
     */
    private static CitexploreClient citProxy = new CitexploreClient();

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

    private String lastPubmedId = null;
    private IntactCitation lastCitation = null;

    /**
     * Based upon a pubmed Id, collect from CitExplore a subset of the information related to that publication.
     *
     * @param pubmedId the pubmed ID of the publication we want information on.
     *
     * @return a citation corresponding to the given pubmed id.
     *
     * @throws PublicationNotFoundException
     * @throws UnexpectedException
     */
    public IntactCitation buildCitation( String pubmedId ) throws UnexpectedException,
                                                                  PublicationNotFoundException {

        if ( pubmedId == null ) {
            throw new IllegalArgumentException( "You must give a non null pubmed id." );
        }

        // shortcut in case we keep asking for the same pubmed id.
        if ( pubmedId.equals( lastPubmedId ) ) {
            return lastCitation;
        }

        Citation c = null;
        try {
            c = citProxy.getCitationById( pubmedId );
        } catch ( Exception e ) {

            final String msg = e.getMessage();
            if ( msg != null && msg.indexOf( "uk.ac.ebi.cdb.webservice.DataNotFoundException" ) != -1 ) {
                throw new PublicationNotFoundException( "The PubMed ID " + pubmedId + " could not be found." );
            }

            // otherwise throw the original exception
            throw new UnexpectedException( "An unexpected error occured (ie. " + msg + ").", e );
        }

        // retreive information

        int year = ( c.getJournalIssue().getYearOfPublication() ).intValue();

        // build journal name as 'isoname (issn)' or 'isoname' if the issn is not available.
        String journalName = c.getJournalIssue().getJournal().getISOAbbreviation();
        if ( journalName == null ) {
            journalName = c.getJournalIssue().getJournal().getMedlineAbbreviation();
            if ( journalName == null ) {
                throw new UnexpectedException( "PubMed id " + pubmedId + "'s journal doesn't have an isoAbreviation or medlineAbreviation.", null );
            }
        }
        String issn = c.getJournalIssue().getJournal().getISSN();
        String journal = journalName + ( issn != null && issn.trim().length() > 0 ? " (" + issn + ")" : "" );

        String title = c.getTitle();
        String email = getEmail( c );

        // get the first author last name
        String authorLastName = null;

        // list of all the authors
        String authors = null;

        List authorList = c.getAuthorCollection();

        if ( ! authorList.isEmpty() ) {

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
            if ( authorList.size() > 1 ) {

                StringBuffer authorsBuffer = new StringBuffer( 128 );

                for ( Iterator iterator = authorList.iterator(); iterator.hasNext(); ) {
                    Author anAuthor = (Author) iterator.next();

                    authorsBuffer.append( anAuthor.getLastName() ).append( ' ' ).append( anAuthor.getInitials() ).append( '.' );

                    if ( iterator.hasNext() ) {
                        authorsBuffer.append( ',' ).append( ' ' );
                    }
                }

                authors = authorsBuffer.toString();
            }

        }

        IntactCitation citation = new IntactCitation( authorLastName, year, journal, title, authors, email );

        // cache last request.
        lastPubmedId = pubmedId;
        lastCitation = citation;

        return citation;


    }
}