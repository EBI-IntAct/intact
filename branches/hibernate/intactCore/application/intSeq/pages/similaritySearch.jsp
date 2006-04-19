<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This should be displayed in the content part of the IntAct layout,
   - it displays the Similarity Search textarea where the user should capture
   - a protein sequence in the Fasta format.
   -
   - @author Sophie Huet (shuet@ebi.ac.uk)
   - @version $Id$
-->


<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<table width="100%" height="100%">
         <tbody>
         <tr>

           <td valign="top" height="*">
            <html:errors/>
            <br>
                <bean:message key="sim.page.presentation"/>
            <br>
            <br>
                <bean:message key="sim.query.todo"/>
            <br>

            <table>
              <tbody>
                <html:form action="/similarity" focus="similarity">
                <tr>
                  <td><html:textarea property="sequence" cols="80" rows="20" value="Fasta sequence" />
                  </td>
                  <td>
                    <html:submit property="submit" title="Searches the IntAct database">
                        <bean:message key="button.submit.sim"/>
                    </html:submit>
                  </td>
                </tr>
                </html:form>

              </tbody>
            </table>
            <br>
            <br>
            <br>


            <hr width="100%" size="2"><bean:message key="common.head.details"/>
            <br>
                <bean:message key="sim.details.process"/>
            <br>
            <br>
                <bean:message key="sim.details.todo"/>
            <br>
           </td>

         </tr>

    </tbody>
</table>