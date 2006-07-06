/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents the contact details of a person. 
 * The person may be internal, e.g. a curator, or external, e.g. a submitter.
 *
 * @author Henning Hermjakob
 */
public class Person extends BasicObject {

  ///////////////////////////////////////
  //attributes


/**
 * The first names of a person, as they should be shown in any output.
 */
    protected String firstNames;

/**
 * The surname.
 */
    protected String surName;

/**
 * The full, international telephone number. Represented as a String,
 * but should contain only digits and blanks, e.g. 0049 122349 5555.
 */
    protected String telephone;

/**
 * The full email address.
 * @example test@nowhere.com
 */
    protected String email;

   ///////////////////////////////////////
   // associations

/**
 * Describes the relationship between a person and his/her
 * institution. A person belongs to zero or one institution,
 * if he/she should have e.g. more than one employer, one
 * must be chosen.
 */
    public Institution institution;


  ///////////////////////////////////////
  //access methods for attributes

    public String getFirstNames() {
        return firstNames;
    }
    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }
    public String getSurName() {
        return surName;
    }
    public void setSurName(String surName) {
        this.surName = surName;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

   ///////////////////////////////////////
   // access methods for associations

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    } 

} // end Person




