<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - sidebarInput.jsp includes the imput section of goDensity
   -
   - @author Markus Brosch (markus @ brosch.cc)
   - @version $Id$
-->

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<% String applicationPath = request.getContextPath();

    String radio = (String)session.getAttribute("radio");

    String colorRadio = null;
    String bwRadio = null;

    if (radio == null || radio.equals("color")) {
        radio = "Color";
        colorRadio  = "<img src=\""+ applicationPath +"/images/select-chk.gif\" border=\"0\">";
        bwRadio = "<img src=\""+ applicationPath +"/images/select.gif\" border=\"0\">";
    } else {
        colorRadio = "<img src=\""+ applicationPath +"/images/select.gif\" border=\"0\">";
        bwRadio = "<img src=\""+ applicationPath +"/images/select-chk.gif\" border=\"0\">";
    }

    String colorAction = "<a href=\""+ applicationPath +"/color.do?radio=color\">" + colorRadio + "</a>";
    String bwAction = "<a href=\""+ applicationPath +"/color.do?radio=bw\">" + bwRadio + "</a>";
%>

<!-- the form  -->
<html:form action="/input" focus="goIds">
    <table>
        <tr>
            <td>
                &nbsp;<br>
                <bean:message key="form.goDensity.field.goIds.please"/><br/>
                <html:text property="goIds" size="23"/>
<%--            <bean:message key="form.goDensity.field.goIds.prompt"/><br/>--%>
            </td>
        </tr>
        <tr>
            <td>
                <html:submit property="action" titleKey="form.goDensity.button.submit.title">
                    <bean:message key="form.goDensity.button.submit.label"/>
                </html:submit>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;<br>
                <b>Direct GO entry points</b><br>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="<%=request.getContextPath()%>/children.do?goId1=GO:0008150&goId2=GO:0008150">biological process</a><br>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="<%=request.getContextPath()%>/children.do?goId1=GO:0005575&goId2=GO:0005575">cellular component</a><br>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="<%=request.getContextPath()%>/children.do?goId1=GO:0003674&goId2=GO:0003674">molecular function</a>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;<br>
                <b>Change figure property</b><br>
                &nbsp;&nbsp;&nbsp;&nbsp;<%=colorAction%> Color<br>
                &nbsp;&nbsp;&nbsp;&nbsp;<%=bwAction%> Black&White
           </td>
        </tr>
    </table>
</html:form>
