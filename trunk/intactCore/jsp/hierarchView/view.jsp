<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<%@ page import="uk.ac.ebi.intact.application.hierarchView.highlightment.*" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.highlightment.source.HighlightmentSource" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.graph.*" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.Constants" %>

<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.OptionGenerator" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean" %>
<%@ page import="java.util.ArrayList" %>
<%@page import="java.io.*" %>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Collection"%>


<html:html locale="true">

<head>

    <META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
    <META HTTP-EQUIV="Expires" CONTENT="-1">


    <title>
       <bean:message key="hierarchView.view.title"/>
    </title>

    <link rel="stylesheet" href="hierarchview.css" type="text/css">

    <script language="JavaScript">
     <!--
     /**
      * Allows to forward to a specified URL inside a frame
      */
     function forward ( absoluteUrl ) {
        parent.frameHierarchy.location.href = absoluteUrl;
     }

     //-->
    </script>

    <html:base/>

</head>


<body>

<html:errors/>

<%
   /**
    * Retreive data from the session
    */
   String AC           = (String)  session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_AC);
   String depth        = (String)  session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_DEPTH);
   Boolean depthLimit  = (Boolean) session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_NO_DEPTH_LIMIT);
   String noDepthLimit = "null";
   if (null != depthLimit) {
     noDepthLimit = depthLimit.toString();
   }

   ImageBean imageBean   = (ImageBean) session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_IMAGE_BEAN); 

  // String keys           = (String)  session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_KEYS);
   Collection keys       = (Collection)  session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_KEYS);
   String methodLabel    = (String)  session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_METHOD_LABEL);
   String methodClass    = (String)  session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_METHOD_CLASS);
   String behaviour      = (String)  session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_BEHAVIOUR);
   InteractionNetwork in = (InteractionNetwork) session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_GRAPH);

   Properties properties = PropertyLoader.load (uk.ac.ebi.intact.application.hierarchView.struts.Constants.PROPERTY_FILE);
   String debug = null;
   if (null != properties) {
      debug = properties.getProperty ("application.debug");
   } else {
      debug = "disable";
   } 


   /**
    * get the data to display in the highlightment form
    */   
   String fieldAC     = (null == AC ? "" : AC);
   String fieldDepth  = (null == depth ? "" : depth);
   String fieldMethod = (null == methodLabel ? "" : methodLabel);

   if (null == keys) {
      // don't refresh the right frame if the user try to highlight. 
%>
    <script language="JavaScript">
     <!--

        forward ("/hierarchView/hierarchy.jsp");

     //-->
    </script>
<%
   } // if
%>


<center>

 
 <html:form action="/visualize" target="_self" focus="AC">

    <table width =" 50%" cellspacing =" 0" cellpadding =" 4" border="1" bordercolor ="#999999"   bgcolor ="#cee3f7" >
      <tr bgcolor="#a5bace"> 
	<td width="100%" align ="center" valign =" top"> 
	  <!-- Insert your title here -->

	    <font class="tableTitle">
	      Highlighting form
	    </font>

	  <!-- End of title section -->
	</td>
      </tr>

      <tr align="left">
	<td valign =" top">
	    <!-- Insert your table body here -->

		<table border="0" width="100%">
		  <tr>
		    <td align="right">
		      <bean:message key="hierarchView.view.visualizeForm.AC.prompt"/>
		    </td>
		    <td align="left">
		      <html:text property="AC" size="10" maxlength="10" value="<%= fieldAC %>"/>
		    </td>
		  </tr>

		  <tr>
		    <td align="right" valign="top">
		      <bean:message key="hierarchView.view.visualizeForm.depth.prompt"/>
		    </td>
		    <td align="left">
		      <html:text property="depth" size="6" maxlength="5" value="<%= fieldDepth %>"/>
		      <br>
		      <html:checkbox property="hasNoDepthLimit"/> 
		      <bean:message key="hierarchView.view.visualizeForm.noDepthLimit.prompt"/>
		    </td>
		  </tr>

		  <tr>
		    <td align="right">
		      <bean:message key="hierarchView.view.visualizeForm.method.prompt"/>
		    </td>
		    <td align="left">

		    <%		
			/**
			 * get a collection of highlightment sources.
			 */
			ArrayList sources = OptionGenerator.getHighlightmentSources ();
			pageContext.setAttribute("sources", sources, PageContext.PAGE_SCOPE);

		    %>

		    <html:select property="method" size="1" value="<%= fieldMethod %>">
		      <html:options collection="sources" property="value" labelProperty="label"/>
		    </html:select>


		    </td>
		  </tr>

		  <tr>
		    <td align="right">
		      <html:submit>
			 <bean:message key="hierarchView.view.visualizeForm.submit.label"/>
		      </html:submit>
		    </td>
		    <td align="left">
		      <html:reset>
			 <bean:message key="hierarchView.view.visualizeForm.reset.label"/>
		      </html:reset>
		    </td>
		  </tr>
		</table>

	    <!-- End of table body-->
	</td>
      </tr>
    </table>

 

 </html:form>
</center> 
<hr>



 <% if (debug.equalsIgnoreCase ("enable")) { %> 

    <p align="left">

      AC = <%= AC %><br>
      depth = <%= depth %><br>
      noDepthLimit = <%= noDepthLimit %><br>
      keys = <%= keys%><br> 
      methodLabel = <%= methodLabel %><br>
      methodClass = <%= methodClass %><br>
      behaviour = <%= behaviour %><br> 

    </p>

 <% } %>



<% 

// Write a link to display the GO term list for the selected AC number
if (AC != null)
{
%>
<a href="/hierarchView/hierarchy.jsp" target="frameHierarchy"> Display the source element list for the current AC number </a> <br>
<%

}

   /**
   * Apply an highlighting if AC != null, keys != null and behaviour != null
   */
   if ((null != AC) && (null != keys) && (behaviour != null) && (null != in)) {
       HighlightProteins hp = new HighlightProteins(methodClass, behaviour, session, in);

       imageBean = (ImageBean) session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_IMAGE_BEAN);
   }


    /**
     *  Display only the picture if an AC is in the session
     */
    if ((null != AC) && (null != imageBean)) { 
    
       // Display the HTML code map
       out.println(imageBean.getMapCode());

       // read the ApplicationResource.proterties file
       String mapName = null;

       Properties propertiesBusiness = PropertyLoader.load (uk.ac.ebi.intact.application.hierarchView.business.Constants.PROPERTY_FILE);

       if (null != propertiesBusiness) {
	  mapName = propertiesBusiness.getProperty ("hierarchView.image.map.name");
       }
       
%>

       <p align="left">
	 <center>          
             <img src="/hierarchView/GenerateImage" USEMAP="#<%= mapName %>" border ="0">
           <br>
	 </center>
       </p>

<% 
     } // end of the displaying of the image



    /**
     *  Display only that form if an AC is in the session and the user have already requested an highlighting 
     */
    if (null != AC && keys != null && null != in) { 

%>

    <hr>
    <center>
     <html:form action="/highlightment">

    <table width ="50%" cellspacing ="0" cellpadding ="4" border="1" bordercolor ="#999999"   bgcolor ="#cee3f7" >
      <tr bgcolor="#a5bace"> 
	<td width="100%" align ="center" valign ="top"> 
	  <!-- Insert your title here -->

	    <font class="tableTitle">
	      Highlighting's behaviour and options
	    </font>

	  <!-- End of title section -->
	</td>
      </tr>

      <tr align="left">
	<td valign =" top">
	    <!-- Insert your table body here -->

	      <table border="0" width="100%">
		<tr>
		  <!-- Available behaviour for the selected method -->
		  <td align="right">
		      <bean:message key="hierarchView.view.highlightmentForm.behaviour.prompt"/>
		  </td>
		  <td align="left">

		    <%
			/**
			 * get a collection of authorized behaviour for the selected highlightment.
			 */
			ArrayList sources = OptionGenerator.getAuthorizedBehaviour (methodLabel);
			pageContext.setAttribute("behaviours", sources, PageContext.PAGE_SCOPE);
		    %>

		    <html:select property="behaviour" size="1" value="<%= behaviour %>">
		       <html:options collection="behaviours" property="value" labelProperty="label"/>
		    </html:select>

		  </td>
		</tr>


		<tr>
		  <!-- Highlightment option available for the selected method -->
		  <td align="right">
		 
		     <bean:message key="hierarchView.view.highlightmentForm.options.prompt"/>
		      
		  </td>
		  <td align="left">
		  <br>

		  <%

		  // Search the list of protein to highlight
		   HighlightmentSource highlightmentSource = HighlightmentSource.getHighlightmentSource(methodClass);
		   String htmlCode = null;
		   if (null != highlightmentSource) {
		   htmlCode = highlightmentSource.getHtmlCodeOption(session);
		   }
		   out.println(htmlCode);

		  //   <input type="text" name="option">
		   //   <!--html:text name="option" property="" size="6" maxlength="5"/-->
		  %>
		   
		  </td>
		</tr>

		<tr>
		  <td align="right">
		    <html:submit>
		       <bean:message key="hierarchView.view.highlightmentForm.submit.label"/>
		    </html:submit>
		  </td>
		  <td align="left">
		    <html:reset>
		       <bean:message key="hierarchView.view.highlightmentForm.reset.label"/>
		    </html:reset>
		  </td>
		</tr>

	      </table> 
  
	    <!-- End of table body-->
	</td>
      </tr>
    </table>

     </html:form>
    </center>

<% 
   } // end - displaying of the highlightment form
%>


</body>
</html:html>






