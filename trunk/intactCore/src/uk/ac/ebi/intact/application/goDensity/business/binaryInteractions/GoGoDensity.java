/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.goDensity.business.binaryInteractions;

/**
 * GoGoDensity represents a repository for a set of data, which consist of:<br>
 * - goId1 <br>
 * - goId2 <br>
 * - possible Interactions between and under these two goIds <br>
 * - existing Interactions between and under these two goIds <br>
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class GoGoDensity {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    /**
     * GO:ID 1 for this group
     */
    String _goId1;

    /**
     * GO:ID 2 for this group
     */
    String _goId2;

    /**
     * existing Interactions
     */
    int _countInteractions;

    /**
     * possible Interactions
     */
    int _countPossibleInteractions;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * Constructor
     * @param goId1 1st goId for this group
     * @param goId2 2nd goId
     * @param countPossibleInteractions all possible interactions between the
     * two goIds and all theire children
     * @param countInteractions all real existing interactions in data between
     * the two goIds and all theire children
     */
    public GoGoDensity(
            String goId1,
            String goId2,
            int countPossibleInteractions,
            int countInteractions) {

        _goId1 = goId1;
        _goId2 = goId2;
        _countPossibleInteractions = countPossibleInteractions;
        _countInteractions = countInteractions;
    }

    private GoGoDensity() {
        // nothing - to avoid null attributes
    };

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * @return goId1 which belongs to this group
     */
    public String getGoId1() {
        return _goId1;
    }

    /**
     * @return goId2 which belongs to this group
     */
    public String getGoId2() {
        return _goId2;
    }

    /**
     * The proportion between all possilbe interactions for the two GoIds and the
     * real existing interactions can be expressed as the density.
     * @return density of interactions between goId1 and goId2, depending on the data which was
     * given by the caller.
     */
    public double getDensity() {
        // check first for error code
        if (_countPossibleInteractions == -1) {
            return -1; // error code for no data / no calculation
        }
        // check if there could be interactions
        if (_countPossibleInteractions == 0) {
            return 0;
        }
        // return density
        return ((double) _countInteractions / (double) _countPossibleInteractions);
    }

    /**
     * @return number of real interactions
     */
    public int getCoutInteractions() {
        return _countInteractions;
    }

    /**
     * @return number of possible interactions
     */
    public int getCountPossibleIAs() {
        return _countPossibleInteractions;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("\n\ny: " + _goId1);
        result.append("\nx: " + _goId2);
        result.append("\ndensity: " + this.getDensity());

        return result.toString();
    }

    // =======================================================================
    // Test methods
    // =======================================================================

    public static void main(String[] args) {
        GoGoDensity go = new GoGoDensity("a", "b", 10, 1);
        System.out.println(go);
        GoGoDensity go1 = new GoGoDensity("a", "b", 0, 1);
        System.out.println(go1);
    }
}
