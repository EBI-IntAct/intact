<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This should be displayed in the content part of the IntAct layout,
   - it displays the Similarity Search results list for the sequence previously captured
   - compared with the IntAct sequences set.
   -
   - @author Sophie Huet (shuet@ebi.ac.uk)
   - @version $Id$
-->

<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/tld/display.tld" prefix="display"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<table width="100%" height="100%">
    <tbody>
           <tr>
               <td valign="top" height="*">
                    <bean:message key="sim.result.page"/>
                <br>
                <br>
                    <bean:message key="sim.results.todo"/>
                <br>
                <br>

                   <!-- Displays the available accession numbers -->
                <display:table
                     name="similarityList" width="80%" align="center"
                     decorator="uk.ac.ebi.intact.application.intSeq.struts.view.utils.SimilarityDecorator">
                        <display:column property="intactacc" title="Accession Number" width="25%" align="center"/>
                        <display:column property="percentageidentity" title= "Percentage Identity" width="25%" align="center"/>
                        <display:column property="fragmentstart" title= "Fragment Start ( Q / S )" width="25%" align="center"/>
                        <display:column property="fragmentend" title= "Fragment End ( Q / S )" width="25%" align="center"/>

                        <display:setProperty name="basic.msg.empty_list" value="No IntAct entry available for that protein" />
                </display:table>

                <hr width="100%" size="2"><bean:message key="common.head.details"/>
                <br>
                    <bean:message key="sim.details.results.origin"/>
                    <bean:message key="sim.details.percentage"/>
                    <bean:message key="sim.details.query"/>
                    <bean:message key="sim.details.subject"/>
                <br>
               </td>

          </tr>
    </tbody>
</table>




