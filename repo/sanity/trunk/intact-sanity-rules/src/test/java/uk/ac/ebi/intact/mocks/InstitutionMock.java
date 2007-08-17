/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import uk.ac.ebi.intact.model.Institution;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class InstitutionMock {
    public static final String SHORT_LABEL = "ebi";
    public static final String FULLNAME = "European BioInformatics Institute";
    public static final String ADDRESS = "EMBL Outstation - Hinxton, " +
            "European Bioinformatics Institute, " +
            "Wellcome Trust Genome Campus, " +
            "Hinxton, " +
            "Cambridge, CB10 1SD, " +
            "United Kingdom.";
    public static final String URL = "www.ebi.ac.uk";
    public static final String CREATOR = "BILL";
    public static final String UPDATOR = "JACK";

    /**
     * Create an Instutition with :
     *      ac = EBI-1
     *      shortlabel = ebi
     *      fullname = European BioInformatics Institute
     *      postalAddress = EMBL Outstation - Hinxton, " +
     *                      "European Bioinformatics Institute, " +
     *                      "Wellcome Trust Genome Campus, " +
     *                      "Hinxton, " +
     *                      "Cambridge, CB10 1SD, " +
     *                      "United Kingdom.";
     *      url = www.ebi.ac.uk
     *      creator = BILL
     *      updator = JACK
     *      created = 2006, 11th of March
     *      updated = 2007, 22nd of January
     * and returns it.
     * @return  an Institucion object
     */
    public static Institution getMock(){
        Institution institution = new Institution(SHORT_LABEL);
        institution.setAc(AcGenerator.getNextVal());
        institution.setFullName(FULLNAME);
        institution.setPostalAddress(ADDRESS);
        institution.setUrl(URL);
        institution.setCreator(CREATOR);
        institution.setUpdator(UPDATOR);
        institution.setCreated(DateGetter.getCreatedDate());
        institution.setUpdated(DateGetter.getUpdatedDate());
        return institution;
    }
}