<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This should be displayed in the content part of the IntAct layout,
   - it displays the Srs Search text field where the user should capture
   - a protein reference or a protein word.
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
                <bean:message key="srs.search.status"/>
            <br>
            <br>
                <bean:message key="srs.todo.query"/>
            <br>

            <table>

              <tbody>
                <html:form action="/srs" focus="searchString">
                <tr>
                  <td><html:text property="searchString" size="16" value="Search String" />
                  </td>
                  <td>
                    <html:submit property="submit" title="Searches the IntAct database">
                        <bean:message key="button.submit.srs"/>
                    </html:submit>
                  </td>
                </tr>
                </html:form>

              </tbody>
            </table>

            <hr width="100%" size="2"><bean:message key="common.head.details"/>
            <br>
                <bean:message key="srs.details.explanation"/>
            <br>
            <br>
                <bean:message key="srs.details.todo"/>
            <br>
            <br>
                <bean:message key="srs.details.particular.result"/>
            <br>
          </td>

        </tr>
        </tbody>
</table>
