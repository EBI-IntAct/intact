<%@ page language="java"%>

<html>
<head>
  <script language="JavaScript">
    if (top != self)
    {
     top.location.href = location.href;
    }
  </script>

  <title>EBI Services</title>

<script language="javascript">
    function callLoadMethods()
    {
     ;
    }
    function callUnLoadMethods()
    {
     ;
    }
  </script>

  <meta http-equiv="Owner" content="EMBL Outstation - Hinxton, European Bioinformatics Institute">
  <meta name="Author" content="EBI External Servces">
  <link rel="stylesheet" href="http://www.ebi.ac.uk/services/include/stylesheet.css" type="text/css">
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <script language="javascript" src="http://www.ebi.ac.uk/include/master.js"></script>
</head>



<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" onunload="callUnLoadMethods()" onload="EbiPreloadImages('services');" marginheight="0" marginwidth="0">

<!-- START EBI Header -->

<table class="tabletop" border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td align="right" height="85" width="270"><img src="http://www.ebi.ac.uk/services/images/ebi_banner_1.jpg" height="85" width="270"></td>
      <td align="right" valign="top" width="100%">
      <table class="tabletop" border="0" cellpadding="0" cellspacing="0" height="85" width="100%">
        <tbody>
          <tr>
            <td colspan="2" align="right" valign="top"> <input value="sr" name="ui" type="hidden">
            <table class="tablehead" border="0" cellpadding="0" cellspacing="0" height="28">
              <tbody>
                <tr>
                  <td class="tablehead" align="left" valign="bottom"><img src="http://www.ebi.ac.uk/services/images/top_corner.gif" height="28" width="28"></td>
                  <form name="Text1293FORM" action="javascript:querySRS(document.forms[0].db[document.forms[0].db.selectedIndex].value, document.forms[0].qstr.value)" method="post"></form>
                  <td class="small" align="center" nowrap="nowrap" valign="middle"><span class="smallwhite"><nobr>Get&nbsp;</nobr></span></td>
                  <td class="small" align="center" valign="middle"><span class="small">
                      <select id="FormsComboBox2" name="db" class="small">
                      <option value="EMBL" selected="selected">Nucleotide sequences</option>
                      <option value="SWALL">Protein sequences</option>
                      <option value="PDB">Protein structures</option>
                      <option value="INTERPRO">Protein signatures</option>
                      <option value="MEDLINE">Literature</option>
                      </select>
                      </span>
                  </td>
                  <td class="small" align="center" nowrap="nowrap" valign="middle"><span class="smallwhite">&nbsp;for&nbsp;</span></td>
                  <td class="small" align="center" valign="middle"> <span class="small"> <input id="FormsEditField3" maxlength="50" size="7" name="qstr" class="small"> </span></td>
                  <td class="small" align="center" valign="middle">&nbsp;</td>
                  <td class="small" align="center" valign="middle"> <span class="small"> <input id="FormsButton3" value="Go" name="FormsButton1" class="small" type="submit"> </span></td>
                  <td class="small" align="center" nowrap="nowrap" valign="middle" width="10"><a href="#" class="small" onclick="openWindow('http://www.ebi.ac.uk/help/DBhelp/dbhelp_frame.html')"> <nobr>&nbsp;?&nbsp;</nobr></a></td>

                  <form name="Text1295FORM" action="http://search.ebi.ac.uk/compass" method="get" onsubmit="if (document.Text1295FORM.scope.value=='') { alert('Please enter query.'); return false;}"></form>

                  <td class="smallwhite" align="center" nowrap="nowrap" valign="middle"><span class="smallwhite"><nobr>&nbsp;Site search&nbsp;</nobr></span></td>
                  <td class="small" align="center" valign="middle"> <span class="small"> <input id="FormsEditField4" maxlength="50" size="7" name="scope" class="small"> </span></td>
                  <td class="small" align="center" valign="middle">&nbsp;</td>
                  <td class="small" align="center" valign="middle"> <span class="small"> <input id="FormsButton2" value="Go" name="FormsButton2" class="small" type="submit"> </span></td>
                  <td class="small" align="center" nowrap="nowrap" valign="middle"><nobr> <a href="#" class="small" onclick="openWindow('http://www.ebi.ac.uk/help/help/sitehelp_frame.html')">&nbsp;?&nbsp;</a></nobr></td>
                </tr>
              </tbody>
            </table>
            </td>
          </tr>
          <tr>
            <td align="left" valign="bottom"><img src="http://www.ebi.ac.uk/services/images/ebi_banner_2.jpg" height="29" width="169"></td>
            <td align="right" valign="top"><img src="http://www.ebi.ac.uk/Groups/images/topbar3.gif" usemap="#Map" border="0" height="25" width="156"></td>
          </tr>
        </tbody>
      </table>
      </td>
    </tr>
    <tr>
      <td colspan="2"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="5" width="1"></td>
    </tr>
  </tbody>
</table>
<table class="tabletop" border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="100%">
      <table border="0" cellpadding="0" cellspacing="0" width="679">
        <tbody>
          <tr>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/index.html" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image8','','http://www.ebi.ac.uk/services/images/home_o.gif',1)"><img name="Image8" src="http://www.ebi.ac.uk/services/images/home.gif" border="0" height="18" width="97"></a></td>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/Information" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image9','','http://www.ebi.ac.uk/services/images/about_o.gif',1)"><img name="Image9" src="http://www.ebi.ac.uk/services/images/about.gif" border="0" height="18" width="97"></a></td>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/Groups" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image10','','http://www.ebi.ac.uk/services/images/research_o.gif',1)"><img name="Image10" src="http://www.ebi.ac.uk/services/images/research.gif" border="0" height="18" width="97"></a></td>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/services" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image11','','http://www.ebi.ac.uk/services/images/services_o.gif',1)"><img name="Image11" src="http://www.ebi.ac.uk/services/images/services.gif" border="0" height="18" width="97"></a></td>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/Tools" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image12','','http://www.ebi.ac.uk/services/images/utilities_o.gif',1)"><img name="Image12" src="http://www.ebi.ac.uk/services/images/utilities.gif" border="0" height="18" width="97"></a></td>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/Databases" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image13','','http://www.ebi.ac.uk/services/images/databases_o.gif',1)"><img name="Image13" src="http://www.ebi.ac.uk/services/images/databases_o.gif" border="0" height="18" width="97"></a></td>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/FTP" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image14','','http://www.ebi.ac.uk/services/images/downloads_o.gif',1)"><img name="Image14" src="http://www.ebi.ac.uk/services/images/downloads.gif" border="0" height="18" width="97"></a></td>
            <td height="18" width="97"><a href="http://www.ebi.ac.uk/Submissions" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image15','','http://www.ebi.ac.uk/services/images/submissions_o.gif',1)"><img name="Image15" src="http://www.ebi.ac.uk/services/images/submissions.gif" border="0" height="18" width="97"></a></td>
          </tr>
        </tbody>
      </table>
      </td>
    </tr>
    <tr>
      <td class="tablehead" height="5" width="100%">
      <table border="0" cellpadding="0" cellspacing="0" height="5" width="100%">
        <tbody>
          <tr>
            <td align="center" height="20" width="100%">

<!-- topnav START -->

             <nobr><a href="http://www.ebi.ac.uk/" class="white" target="_top"></a></nobr><nobr><a href="http://www.ebi.ac.uk/" class="white" target="_top">HOME PAGE</a></nobr>

<!-- topnav END -->

            </td>
          </tr>
        </tbody>
      </table>
      </td>
    </tr>
    <tr>
      <td class="tableborder"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="3" width="1"></td>
    </tr>
  </tbody>
</table>

<!-- END EBI HEADER -->



<!-- START Intact home page -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td colspan="6" class="tablebody" height="6"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="6" width="1"></td>
    </tr>
    <tr>
      <td class="tablebody" height="20" width="1%"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="20" width="160"></td>
      <td align="left" bgcolor="#ffffff" height="20" valign="top" width="20"><img src="http://www.ebi.ac.uk/services/images/corner.gif" height="20" width="20"></td>
      <td>&nbsp;</td>
      <td colspan="3" width="100%"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="1" width="1"></td>
    </tr>
    <tr>
      <td align="left" valign="top" width="160">

      <!-- leftnav START -->

         <jsp:include page="sidebar.html" />

      <!-- leftnav END -->

     <table border="0" cellpadding="0" cellspacing="0" height="100%" width="160">
        <tbody>
            <tr>
                <td class="tablebody" height="20" valign="top" width="140"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="20" width="140"></td>
                <td align="right" height="20" valign="top" width="20"><img src="http://www.ebi.ac.uk/services/images/right.gif" height="20" width="20"></td>
            </tr>
            <tr>
                <td colspan="2" bgcolor="#ffffff">&nbsp;</td>
            </tr>
        </tbody>
     </table>
     <table border="0" cellpadding="0" cellspacing="0" height="100%" width="160">
        <tbody>
          <tr valign="top">
            <td height="20" valign="top">
            <div align="center"></div>
            <br>
            </td>
          </tr>
        </tbody>
      </table>
      </td>
      <td width="20">&nbsp;</td>
      <td align="left" valign="top" width="593">
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tbody>
          <tr>
            <td valign="top">

            <!-- contents START -->

                   <jsp:include page="content.jsp" />

            <!-- contents END --> </td>

            <td><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="8" width="6"></td>
            <td background="http://www.ebi.ac.uk/services/images/vert.gif" width="3"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="3" width="3"></td>
            <td width="20"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="8" width="6"></td>
            <td valign="top">

            <!-- rightnav START -->

                   <jsp:include page="news.html" />

            <!-- rightnav END -->

           </td>

          </tr>
          <tr>
            <td class="tablebody" background="http://www.ebi.ac.uk/services/images/hor.gif" height="3" width="500"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="3" width="3"></td>
            <td colspan="2"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="1" width="1"></td>
          </tr>
        </tbody>
      </table>
      </td>
      <td width="16"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="8" width="6"></td>
    </tr>
  </tbody>
</table>
<div align="center">
<p>Page maintained by <a href="mailto:support@ebi.ac.uk"> support@ebi.ac.uk</a>.&nbsp;&nbsp;Last updated:&nbsp; <script language="javascript" src="http://www.ebi.ac.uk/include/lastmod_noprint.js"></script></p>
</div>
<map name="Map">
<area shape="rect" coords="70,1,156,25" href="http://srs.ebi.ac.uk/" alt="Start SRS Session" title="Start SRS Session">
<area shape="rect" coords="1,1,69,25" href="http://www.ebi.ac.uk/Information/sitemap.html" alt="EBI Site Map" title="EBI Site Map">
</map>
</body>
</html>
