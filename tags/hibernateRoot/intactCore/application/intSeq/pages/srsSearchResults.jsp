<%@ page import="uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants"%><!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This should be displayed in the content part of the IntAct layout,
   - it displays the Srs Search results list for an accession number previously
   - captured by the user, via the SRS engine.
   -
   - @author Sophie Huet (shuet@ebi.ac.uk)
   - @version $Id$
-->


<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/display.tld" prefix="display"%>



<table width="100%" height="100%">
    <tbody>
        <tr>
           <td valign="top" height="*">

                <b><bean:message key="srs.result.page.searchString"/></b>
                '<%= request.getAttribute(SeqIdConstants.SEARCH_STRING) %>'
            <br>
                <bean:message key="srs.result.page.presentation"/>
            <br>
            <br>
                <bean:message key="srs.results.todo"/>
            <br>
            <br>

                   <!-- Displays the available accession numbers -->
            <display:table align="center"
                           name="accList" width="80%"
                           decorator="uk.ac.ebi.intact.application.intSeq.struts.view.utils.ProteinDecorator">

                    <display:column property="acc" title="Accession Number" width="25%" align="center"/>
                    <display:column property="description" title="Protein Description" width="75%" align="center"/>

                    <display:setProperty name="basic.msg.empty_list" value="No entry found." />
            </display:table>

            <hr width="100%" size="2"><bean:message key="common.head.details"/>
            <br>
                <bean:message key="srs.details.process"/>
                <bean:message key="srs.details.forward"/>
            <br>

           </td>

        </tr>
    </tbody>
</table>