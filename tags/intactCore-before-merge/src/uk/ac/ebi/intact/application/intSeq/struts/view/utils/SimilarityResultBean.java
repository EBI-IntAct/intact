/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.view.utils;

 /**
 * Bean to display a result object from the aligment. This bean is used by the SimilarityDecorator
 * to decorate the data before its display in the similaritySearchResults.jsp page
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class SimilarityResultBean {

    /**
     * 6 items which will represent one line in the displayed table.
     */
    private String ac;
    private String percentage;
    private int beginQuery;
    private int endQuery;
    private int beginSubject;
    private int endSubject;

    /**
     * constructor by default.
     */
    public SimilarityResultBean () {

    }

    /**
     * constructs an instance of this class for a given object, composed with 6 items.
     *
     * @param ac the accession number
     * @param perc the percentage identity
     * @param begQue the start of the query fragment
     * @param endQue the end of the query fragment
     * @param begSub the start of the subject fragment
     * @param endSub the end of the subject fragment
     */
    public SimilarityResultBean (String ac, String perc, int begQue, int endQue, int begSub, int endSub) {
       this.ac = ac;
       this.percentage = perc;
       this.beginQuery = begQue;
       this.endQuery = endQue;
       this.beginSubject = begSub;
       this.endSubject = endSub;
    }


    /**
     * Set the ac column.
     * @param identifier represents the accession number
     *
     */
    public void setAc (String identifier) {
        this.ac = identifier;
    }

     /**
     * returns the accession number
     * @return String
     *
     */
    public String getAc() {
        return (this.ac);
    }


     /**
     * Set the percentage identity column.
     * @param perc represents the percentage identity
     *
     */
     public void setPercentage (String perc) {
        this.percentage = perc;
    }

    /**
     * returns the percentage identity
     * @return String
     *
     */
    public String getPercentage() {
        return (this.percentage);
    }

     /**
     * Set the start of the query fragment.
     * @param begin
     *
     */
     public void setQueryFragmentBegin (int begin) {
        this.beginQuery = begin;
    }

    /**
     * returns the start of the query fragment
     * @return int
     *
     */
    public int getQueryFragmentBegin () {
        return (this.beginQuery);
    }

     /**
     * Set the end of the query fragment.
     * @param end
     *
     */
     public void setQueryFragmentEnd (int end) {
        this.endQuery = end;
    }

    /**
     * returns the end of the query fragment.
     * @return int
     *
     */
    public int getQueryFragmentEnd () {
        return (this.endQuery);
    }


     /**
     * Set the start of the subject fragment.
     * @param begin
     *
     */
     public void setSubjectFragmentBegin (int begin) {
        this.beginSubject = begin;
    }

    /**
     * returns the start of the subject fragment
     * @return int
     *
     */
    public int getSubjectFragmentBegin() {
        return (this.beginSubject);
    }

     /**
     * Set the end of the subject fragment.
     * @param end
     *
     */
     public void setSubjectFragmentEnd (int end) {
        this.endSubject = end;
    }

    /**
     * returns the end of the subject fragment.
     * @return int
     *
     */
    public int getSubjectFragmentEnd () {
        return (this.endSubject);
    }


}
