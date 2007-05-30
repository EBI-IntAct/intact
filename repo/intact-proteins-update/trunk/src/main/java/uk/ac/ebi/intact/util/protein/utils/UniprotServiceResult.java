/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein.utils;

import uk.ac.ebi.intact.model.Protein;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class UniprotServiceResult {

    /**
     * A collection of retrieved proteins.
     */
    Collection<Protein> proteins = new ArrayList<Protein>();
    /**
     * A collection of information messages (ex : searching for P12345, filtering on taxid 6409...).
     */
    private Collection<String> messages = new ArrayList<String>();
    /**
     * A collection of errors (ex : Could not update P12345, more than one protein found in IntAct for the uniprot
     * primary ac P12345).
     */
    private Collection<String> errors = new ArrayList<String>();

    /**
     * The query sent to the UniprotService for protein update(ex : P12345).
     */
    private String querySentToService = new String();

    /**
     * Constructor put private so that when you create a UniprotServiceResult you have at least to give the query sent
     * to the UniprotService for protein update.
     */
    private UniprotServiceResult() {
    }

    /**
     * Public constructor.
     * @param querySentToService the query sent to the UniprotService to update a protein (ex : P12345).
       If querySentToService is null, it will send a NullPointerException.
     */
    public UniprotServiceResult(String querySentToService) {
        if(querySentToService == null){
            throw new NullPointerException("querySentToService parameter can not be null");
        }
        this.querySentToService = querySentToService;
    }

    /**
     * Add a message to the messages collection.
     * @param message
     * If message is null, it will send a NullPointerException.
     */
    public void addMessage(String message){
        if(message == null){
            throw new NullPointerException("message argument can not be null");
        }
        messages.add(message);
    }

    /**
     * Get the messages collection.
     * @return
     */
    public Collection<String> getMessages() {
        return messages;
    }

    /**
     * Get the querySentToService string.
     * @return
     */
    public String getQuerySentToService() {
        return querySentToService;
    }

    /**
     * Add an error to the errors collection.
     * If error is null, it will send a NullPointerException.
     * @param error
     */
    public void addError(String error){
        if(error == null){
            throw new NullPointerException("error argument can not be null");   
        }
        errors.add(error);
    }

    /**
     * Get the errors collection.
     * @return
     */
    public Collection<String> getErrors() {
        return errors;
    }

    public void addAllToProteins(Collection<Protein> proteins){
        this.proteins.addAll(proteins);
    }


    public Collection<Protein> getProteins() {
        return proteins;
    }
}