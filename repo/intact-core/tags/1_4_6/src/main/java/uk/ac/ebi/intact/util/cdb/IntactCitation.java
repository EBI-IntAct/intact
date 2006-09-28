/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Keeps hold of a subset of a PubMed publication.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Aug-2005</pre>
 */
public class IntactCitation {

    //////////////////////
    // Constants

    // max length of an IntAct fullname
    public static final int FULLNAME_MAX_LENGTH = 250;

    // max length of the author last name in the experiment shortlabel (author-2004a-10)
    public static final int AUTHOR_NAME_MAX_LENGTH = 11;

    // flag to be looked after in case the title is longer than FULLNAME_MAX_LENGTH.
    public static final Set TITLE_SEPARATORS = new HashSet();

    static {
        TITLE_SEPARATORS.add( ": " );
        TITLE_SEPARATORS.add( ". " );
    }

    ///////////////////////
    // Instance variables

    private int year;
    private String journal;
    private String title;
    private String authorLastName;
    private String authorList;
    private String email;

    //////////////////////
    // Constructors

    public IntactCitation( String authorLastName,
                           int year,
                           String journal,
                           String title,
                           String authorList,
                           String email ) {
        this.year = year;

        if ( title != null ) {
            title = title.trim();
        }

        if ( title.length() > FULLNAME_MAX_LENGTH ) {
            // truncate after the first ': ' or '. '

            for ( Iterator iterator = TITLE_SEPARATORS.iterator(); iterator.hasNext()
                                                                   && title.length() > FULLNAME_MAX_LENGTH; ) {
                String separator = (String) iterator.next();

                int index = title.indexOf( separator );
                if ( index != -1 ) {
                    title = title.substring( 0, index );
                }
            }

            if ( title.length() < FULLNAME_MAX_LENGTH ) {
                title += ".";
            }
        }
        this.title = title;

        if ( authorLastName != null ) {
            authorLastName = authorLastName.trim();
        }
        // truncate it if required
        if ( authorLastName.length() > AUTHOR_NAME_MAX_LENGTH ) {
            // truncate then
            authorLastName = authorLastName.substring( 1, AUTHOR_NAME_MAX_LENGTH );
        }
        this.authorLastName = authorLastName;

        if ( email != null ) {
            email = email.trim();
        }
        this.email = email;

        this.authorList = authorList;

        this.journal = journal;
    }

    /////////////////////
    // Getters

    public int getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public boolean hasAuthorLastName() {
        return authorLastName != null;
    }

    public String getEmail() {
        return email;
    }

    public boolean hasEmail() {
        return email != null;
    }

    public String getAuthorList() {
        return authorList;
    }

    public boolean hasAuthorList() {
        return authorList != null;
    }

    public String getJournal() {
        return journal;
    }


    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append( "IntactCitation" );
        sb.append( "{year=" ).append( year );
        sb.append( ", journal='" ).append( journal ).append( '\'' );
        sb.append( ", title='" ).append( title ).append( '\'' );
        sb.append( ", authorLastName='" ).append( authorLastName ).append( '\'' );
        sb.append( ", authorList='" ).append( authorList ).append( '\'' );
        sb.append( ", email='" ).append( email ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}