/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.view;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This form captures the user action of the srsSearch.jsp.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class ProteinSearchForm extends ActionForm {


            /**
             * The protein topic captured by the user in the provided text field in srsSearch.jsp
             */
           private String searchString = "";

           public void setSearchString (String srsQuery) {
               this.searchString = srsQuery;
           }

           public String getSearchString (){
                return (this.searchString);
           }



       //--------- PUBLIC METHODS --------------//

           /* validate the whole request
                * except if the identifier text field is empty

              In this case, this form should return an <code>ActionErrors<code>
              with the appropriate message in the jsp page */

           public ActionErrors validate (ActionMapping map, HttpServletRequest query)  {

               ActionErrors errors = new ActionErrors();

               if ((searchString == null || searchString.length() < 1)) {
                   errors.add("idseqerror", new ActionError("error.id.required"));
               }

               if (errors.size() > 0) {
                       // delete properties of the bean, so can't be saved in the session.
                   reset(map, query);
               }

               return errors;
           }



           /*
           */
           public void reset (ActionMapping map, HttpServletRequest query) {
              this.searchString = null;
           }


}
