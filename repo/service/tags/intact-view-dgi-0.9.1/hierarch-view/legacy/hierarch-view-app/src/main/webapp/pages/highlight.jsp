<%@ page import="org.apache.struts.tiles.beans.MenuItem,
                 uk.ac.ebi.intact.application.hierarchview.business.Constants,
                 uk.ac.ebi.intact.application.hierarchview.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchview.struts.view.utils.SourceBean,
                 java.util.ArrayList,
                 java.util.List" %>
<%@ page language="java" %>

<!--
- Copyright (c) 2002 The European Bioinformatics Institute, and others.
- All rights reserved. Please see the file LICENSE
- in the root directory of this distribution.
-
- Displays the Source list available for the current central protein
- of the interaction network.
-
- @author Samuel Kerrien (skerrien@ebi.ac.uk)
- @version $Id$
-
- update : Alexandre Liban (aliban@ebi.ac.uk) | 28/06/2005
"display.tld" updated for a faster HTML output and for some new features
new "Browse" button and new policy for the GO terms linking
New sources available : "All" and Interpro
** XHTML/F ready **
-->


<%@ taglib uri="http://www.ebi.ac.uk/intact/hierarch-view" prefix="hierarchView" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<tiles:useAttribute name="parameterName" classname="java.lang.String"/>
<tiles:useAttribute name="selectedIndex" classname="java.lang.String" id="selectedIndexStr" ignore="true"/>
<tiles:useAttribute name="firstRowTabList" classname="java.util.List"/>
<tiles:useAttribute name="secondRowTabList" classname="java.util.List"/>


<!--Prepare available highlightment source for the selected protein in the session -->
<hierarchView:displaySource/>

<%
    IntactUserI user = ( IntactUserI ) session.getAttribute( Constants.USER_KEY );

    String selectedSourceType = user.getSelectedKeyType();
    String selectedColor = "#336666";
    String notSelectedColor = "#CCCCCC";

    int index = 0; // Loop index
    int selectedIndex = 3;
    // Check if selected come from request parameter
    try {
        // Try to retrieve from http parameter, or previous storage
        // Need to use a more unique id for storage name
        String paramValue = request.getParameter( parameterName );
        String selectedTabIndex = ( String ) session.getAttribute( "selectedTabIndex" );

        if ( paramValue == null ) {
            selectedIndex = ( Integer ) ( session.getAttribute( request.getRequestURI() + parameterName ) );
        } else {
            selectedIndex = Integer.parseInt( paramValue );
        }

        if ( selectedTabIndex != null ) {
            selectedIndex = ( Integer.parseInt( selectedTabIndex ) );
            session.removeAttribute( "selectedTabIndex" );
        }
    }
    catch ( java.lang.NumberFormatException ex ) {
        // do nothing
    }
    catch ( java.lang.NullPointerException ex ) {
        // do nothing
    }

    List<MenuItem> tabList = new ArrayList<MenuItem>();
    tabList.addAll( firstRowTabList);
    tabList.addAll( secondRowTabList );

    // Check selectedIndex bounds
    if ( selectedIndex < 0 || selectedIndex >= tabList.size() ) {
        selectedIndex = 0;
    }
//    System.err.println( "selectedIndex" + selectedIndex + " | firstRow " + firstRowTabList.size() + " | secondRow " + secondRowTabList.size() );
//    // if one tab of the second row is selected
//    if (selectedIndex >= secondRowTabList.size() || selectedIndex <= firstRowTabList.size() ){
//        System.err.println( "Switch first to second and second to first" );
//        // switch firstRow to secondRow and secondRow to firstRow
//        List<MenuItem> tempList = new ArrayList<MenuItem>();
//        tempList = firstRowTabList;
//        secondRowTabList = firstRowTabList;
//        firstRowTabList = tempList;
//    }


    // Selected body
    String selectedBody = ( tabList.get( selectedIndex ) ).getLink();
    String tabType = ( tabList.get( selectedIndex ) ).getValue();

    // Store selected index for future references
    session.setAttribute( request.getRequestURI() + parameterName, selectedIndex );
    session.setAttribute( "tabType", tabType );

    user.setMethodLabel( tabType );
%>

<table border="0" cellspacing="0" cellpadding="0" width="100%">

    <%-- Draw tabs --%>
    <tr>
        <td>
            <table border="0" cellspacing="0" cellpadding="5">
                <tr>
                    <logic:iterate id="tab" name="firstRowTabList" type="org.apache.struts.tiles.beans.MenuItem">

                        <%
                            // compute href
                            String href = request.getRequestURI() + "?" + parameterName + "=" + index;
                            String target = "sourceListFrame";
                            String currentTab = tab.getValue();

                            // will generate a different link in the tab to avoid the highlighting
                            // of a source term not compatible with the selected type tab
                            if ( selectedSourceType != null
                                 && !selectedSourceType.equals( "null" )
                                 && !selectedSourceType.toLowerCase().equals( currentTab.toLowerCase() ) ) {
                                String applicationPath = user.getApplicationPath();
                                String randomParam = "&now=" + System.currentTimeMillis();
                                href = applicationPath + "/source.do?keys=null&clicked=null&type=null&selected=" + index + randomParam;
                                target = "_top";
                            }

                            String color = notSelectedColor;
                            if ( index == selectedIndex ) {
                                selectedBody = tab.getLink();
                                color = selectedColor;
                            } // enf if
                            index++;

                            // compute count results
                            List<SourceBean> ListSources = ( ArrayList ) session.getAttribute( "sources" );

                            int nbResults = 0;
                            String tabValue = tab.getValue().toLowerCase();
                            for ( SourceBean source : ListSources ) {
                                String type = source.getType().toLowerCase();

                                if ( type.equals( tabValue ) ) {
                                    nbResults++;
                                }
                            }
                        %>

                        <td bgcolor="<%=color%>"><a href="<%=href%>" target="<%=target%>" style="text-decoration:none;">
                            &nbsp;
                            <span style="font-size:14px;font-weight:bold;color:#FFFFFF;"><%=tab.getValue()%> (<%=nbResults%>)</span>
                            &nbsp;
                        </a></td>

                    </logic:iterate>
                </tr>
            </table>
        </td>
    </tr>     
    <tr>
        <td>
            <table border="0" cellspacing="0" cellpadding="5">
                <tr>
                    <logic:iterate id="tab" name="secondRowTabList" type="org.apache.struts.tiles.beans.MenuItem">

                        <%
                            // compute href
                            String href = request.getRequestURI() + "?" + parameterName + "=" + index;
                            String target = "sourceListFrame";
                            String currentTab = tab.getValue();

                            // will generate a different link in the tab to avoid the highlighting
                            // of a source term not compatible with the selected type tab
                            if ( selectedSourceType != null
                                 && !selectedSourceType.equals( "null" )
                                 && !selectedSourceType.toLowerCase().equals( currentTab.toLowerCase() ) ) {
                                String applicationPath = user.getApplicationPath();
                                String randomParam = "&now=" + System.currentTimeMillis();
                                href = applicationPath + "/source.do?keys=null&clicked=null&type=null&selected=" + index + randomParam;
                                target = "_top";
                            }

                            String color = notSelectedColor;
                            if ( index == selectedIndex ) {
                                selectedBody = tab.getLink();
                                color = selectedColor;
                            } // enf if
                            index++;

                            // compute count results
                            List<SourceBean> listSources = ( ArrayList ) session.getAttribute( "sources" );

                            int nbResults = 0;
                            String tabValue = tab.getValue().toLowerCase();
                            for ( SourceBean source : listSources ) {
                                String type = source.getType().toLowerCase();

                                if ( type.equals( tabValue ) ) {
                                    nbResults++;
                                }
                            }
                        %>

                        <td bgcolor="<%=color%>"><a href="<%=href%>" target="<%=target%>" style="text-decoration:none;">
                            &nbsp;
                            <span style="font-size:14px;font-weight:bold;color:#FFFFFF;"><%=tab.getValue()%> (<%=nbResults%>)</span>
                            &nbsp;
                        </a></td>

                    </logic:iterate>
                </tr>
            </table>
        </td>
    </tr>


    <%-- Draw body --%>
    <tr>
        <td style="border-right-width:3px;border-left-width:8px;border-top-width:8px;border-bottom-width:3px;border-color:#336666;border-style:solid;"
            colspan="3" align="right">
            <tiles:insert name="<%=selectedBody%>" flush="true"/>
        </td>
    </tr>

</table>