<%@ page language="java"  %>

<%--
    This layout displays the search box to search the CV database.
    Author: Andreas Groscurth (groscurth@ebi.ac.uk)
    Version: $Id: sidebarInput.jsp,v 1.3 2003/08/29 17:01:47 skerrien Exp $
--%>
<%@ page import="uk.ac.ebi.intact.application.mine.business.IntactUserI,
				uk.ac.ebi.intact.application.mine.business.Constants" %>

<form action="<%=request.getContextPath()%>/do/search" focus="AC">
    <table>
     <tr>
        <th align="left"><strong>Interactor</strong>
        <intact:documentation section="hierarchView.PPIN.expand" /></th>
     </tr>
     <tr>
        <td><input type="text" name="AC" size="16" value=""></td>
     </tr>
     <tr>
        <td><input type="submit" value="Find network"></td>
      </tr>
    </table>
</form>
