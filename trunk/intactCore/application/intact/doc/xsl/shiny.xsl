<?xml version="1.0" ?>

<!-- XSL introduction -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


    <!-- Parameter declarations - these will normally be overidden by:
    1) Programatic parameters
    2) Stylesheets which include this one.
    -->

    <xsl:param name="menu">false</xsl:param>
    <xsl:param name="formatopt"></xsl:param>
    <xsl:param name="format">normal</xsl:param>

    <xsl:param name="table_border_colour">#336666</xsl:param>
    <xsl:param name="table_division_colour">#000000</xsl:param>
    <!-- <:param name="table_label_bg_colour">#b3d9d9</xsl:param> -->
    <xsl:param name="table_label_bg_colour">#e0f0f0</xsl:param>
    <xsl:param name="table_title_bg_colour">#c0e0e0</xsl:param>
    <xsl:param name="table_data_bg_colour">#ffffff</xsl:param>
    <xsl:param name="target">_self</xsl:param>
    <xsl:param name="targetdocument"></xsl:param>

    <xsl:variable name="apos">
        <xsl:text>'</xsl:text>
    </xsl:variable>

    <!-- EBI's humungous template, definitions thereof -->

    <xsl:template name="ebi-standard-definitions">

        <xsl:text disable-output-escaping="yes"><![CDATA[
        <html><!-- #BeginTemplate "/Templates/databases_template.dwt" -->
        <script language = "JavaScript">
        if (top != self)
           {
            top.location.href = location.href;
           }
        </script>
        <head>
        <!-- #BeginEditable "doctitle" -->
        ]]></xsl:text>
        <title>
            <xsl:value-of select="/browser/header/menu[@zone=/browser/page/@zone]/@title"/>:
            <xsl:value-of select="@title"/>
        </title>
        <xsl:text disable-output-escaping="yes"><![CDATA[
        <script language = "javascript">
        function callLoadMethods()
        {
        ;
        }
        function callUnLoadMethods()
        {
        ;
        }
        </script>
        <!-- #EndEditable -->
        <META HTTP-EQUIV="Last-Modified" CONTENT="Friday, 01 February, 2002">
        <META HTTP-EQUIV="Created" CONTENT="12/07/01">
        <META HTTP-EQUIV="Owner" CONTENT= "EMBL Outstation - Hinxton, European Bioinformatics Institute">
        <META NAME="Author" CONTENT="EBI External Servces">
        <META NAME="Generator" CONTENT="Dreamweaver UltraDev 4">
        <link rel="stylesheet" href="../services/include/stylesheet.css" type="text/css">
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <script language = "javascript"  src = "../include/master.js"></script>
        </head>

        <body  marginwidth="0" marginheight="0" leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" onLoad="MM_preloadImages('../services/images/home_o.gif','../services/images/about_o.gif','../services/images/databases_o.gif','../services/images/utilities_o.gif','../services/images/submissions_o.gif','../services/images/research_o.gif','../services/images/downloads_o.gif','../services/images/services_o.gif')">

        ]]></xsl:text>
    </xsl:template>


    <!-- EBI's humungous template, header thereof -->

    <xsl:template name="ebi-standard-header">

        <xsl:text disable-output-escaping="yes"><![CDATA[


<table width="100%" border="0" cellspacing="0" cellpadding="0"  class = "tabletop">
<tr>
<td width="270" height="85" align = "right"><img src="../services/images/ebi_banner_1.jpg" width="270" height="85"></td>
<td valign = "top" align = "right" width = "100%">
            <table border = "0" cellspacing="0" cellpadding="0" class = "tabletop" width = "100%" height="85">
              <tr>
                <td valign = "top" align = "right" colspan="2">
                  <table border = "0" cellspacing="0" cellpadding="0" height = "28" class = "tablehead">
                    <tr>

                      <td   class = "tablehead"  align = "left" valign = "bottom"><img src="../services/images/top_corner.gif" width="28" height="28"></td>
                      <form name = "Text1293FORM" action = "javascript:querySRS(document.forms[0].db[document.forms[0].db.selectedIndex].value, document.forms[0].qstr.value)" method = "post">
                        <td align = "center" valign = "middle"   class = "small" nowrap><span class = "smallwhite"><nobr>Get&nbsp;</nobr></span></td>
                        <td align = "center" valign = "middle"   class = "small"><span class = "small">
                          <select  id = "FormsComboBox2" name = "db" class = "small">
                            <option value = "EMBL" selected >Nucleotide sequences</option>
                            <option value = "SWALL">Protein sequences</option>
                            <option value = "PDB">Protein structures</option>
							<option value = "INTERPRO">Protein signatures</option>
                            <option value = "MEDLINE">Literature</option>
                          </select>
                  </span></td>
                        <td align = "center" valign = "middle"   class = "small" nowrap><span class = "smallwhite">&nbsp;for&nbsp;</span></td>
                        <td align = "center" valign = "middle"   class = "small">
				  <span class = "small">
                          <input id = "FormsEditField3" maxlength = "50" size = "7" name = "qstr"  class = "small">
                          </span></td>
                        <td align = "center" valign = "middle"   class = "small">&nbsp;</td>
                        <td align = "center" valign = "middle"   class = "small">
				  <span class = "small">
                          <input id = "FormsButton3" type = "submit" value = "Go" name = "FormsButton1" class = "small">
                          </span></td>
                        <td align = "center" valign = "middle"   class = "small" width="10" nowrap><!-- #BeginEditable "firstlink" --><a href = "#" class = "small" onClick = "openWindow('../help/DBhelp/dbhelp_frame.html')">
                          <nobr>&nbsp;?&nbsp</nobr></a><!-- #EndEditable --></td>
                      </form>
                      <form name = "Text1295FORM" action = "http://search.ebi.ac.uk/compass" method = "get" onSubmit ="if (document.Text1295FORM.scope.value=='') { alert('Please enter query.'); return false;}">
                        <input type = "hidden" value = "sr" name = "ui">
                        <td align = "center" valign = "middle"   class = "smallwhite" nowrap><span class = "smallwhite"><nobr>&nbsp;Site search&nbsp;</nobr></span></td>
                        <td align = "center" valign = "middle"   class = "small">
				  <span class = "small">
                          <input id = "FormsEditField4" maxlength = "50" size = "7" name = "scope" class = "small">
                          </span></td>
                        <td align = "center" valign = "middle"   class = "small">&nbsp;</td>
                        <td align = "center" valign = "middle"   class = "small">
				  <span class = "small">
                          <input id = "FormsButton2" type = "submit" value = "Go" name = "FormsButton2" class = "small">
                          </span></td>
                        <td align = "center" valign = "middle"   class = "small" nowrap><!-- #BeginEditable "secondlink" --><nobr>
                          <a href = "#" class = "small" onClick = "openWindow('../help/help/sitehelp_frame.html')">
                          &nbsp;?&nbsp;</a></nobr><!-- #EndEditable --></td>
                      </form>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr>
                <td align = "left" valign = "bottom"><img src="../services/images/ebi_banner_2.jpg" width="169" height="29"></td>
                <td align = "right" valign = "top"><img src="../Groups/images/topbar3.gif" width="156" height="25" usemap="#Map" border="0"></td>
              </tr>
            </table>
</td>
</tr>
<tr><td colspan = "2"><img src="../services/images/trans.gif" width = "1" height = "5"></td></tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0"  class = "tabletop" >
<tr>
<td width = "100%">
<table width="679" border="0" cellspacing="0" cellpadding="0">
       <tr>
                <td width="97" height="18"><a href="../index.html" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image8','','../services/images/home_o.gif',1)"><img name="Image8" border="0" src="../services/images/home.gif" width="97" height="18"></a></td>
                <td width="97" height="18"><a href="../Information" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image9','','../services/images/about_o.gif',1)"><img name="Image9" border="0" src="../services/images/about.gif" width="97" height="18"></a></td>
                <td width="97" height="18"><a href="../Groups" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image10','','../services/images/research_o.gif',1)"><img name="Image10" border="0" src="../services/images/research.gif" width="97" height="18"></a></td>
				<td width="97" height="18"><a href="../services" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image11','','../services/images/services_o.gif',1)"><img name="Image11" border="0" src="../services/images/services.gif" width="97" height="18"></a></td>
                <td width="97" height="18"><a href="../Tools" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image12','','../services/images/utilities_o.gif',1)"><img name="Image12" border="0" src="../services/images/utilities.gif" width="97" height="18"></a></td>
                <td width="97" height="18"><a href="../Databases" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image13','','../services/images/databases_o.gif',1)"><img name="Image13" border="0" src="../services/images/databases_o.gif" width="97" height="18"></a></td>
                <td width="97" height="18"><a href="../FTP" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image14','','../services/images/downloads_o.gif',1)"><img name="Image14" border="0" src="../services/images/downloads.gif" width="97" height="18"></a></td>

                <td width="97" height="18"><a href="../Submissions" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image15','','../services/images/submissions_o.gif',1)"><img name="Image15" border="0" src="../services/images/submissions.gif" width="97" height="18"></a></td>
	  </tr>
</table></td></tr>
      <tr>
          <td width="100%" height = "5"  class = "tablehead" >
		    <table width="100%" height = "5"  border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width = "100%" height = "20" align = "center"><!-- #BeginEditable "topnav" -->
                  <!-- TOP LINK HERE -->
                  <nobr><a href="index.html"  class = "white">]]></xsl:text>
        <xsl:value-of select="/browser/header/menu[@zone=/browser/page/@zone]/@title"/>
        <xsl:text disable-output-escaping="yes"><![CDATA[</a></nobr>
                         <!-- TOP LINK HERE -->
                         <!-- #EndEditable --></td>
                     </tr>
                   </table>
                 </td>
            </tr>
            <tr>
              <td  class = "tableborder"><img src="../services/images/trans.gif" height =  "3" width = "1"></td>
            </tr>
            </table>

               ]]></xsl:text>

    </xsl:template>

    <xsl:template name="ebi-standard-footer">

        <!-- footer -->

        <xsl:text disable-output-escaping="yes"><![CDATA[
        </body>
        </html>
        ]]></xsl:text>

    </xsl:template>


    <!-- Begin Menu-->

    <xsl:template name="top-menu">

        <table border="0" align="center" bgcolor="{$table_border_colour}">
            <tr>
                <td>

                    <table border="0" cellspacing="1" cellpadding="3" bgcolor="{$table_division_colour}">
                        <tr>
                            <xsl:for-each select="/browser/header/menu[@zone=/browser/page/@zone]/menu-item">
                                <td width="130" height="18" align="center" bgcolor="{$table_label_bg_colour}">
                                    <a>
                                        <xsl:apply-templates select="@href"/>
                                        <xsl:value-of select="@name"/>
                                    </a>
                                </td>
                            </xsl:for-each>

                        </tr>
                        <xsl:if test="/browser/header/menu[@zone=/browser/page/@zone]/search">
                            <tr>

                                <form>
                                    <xsl:attribute name="action">
                                        <xsl:value-of select="/browser/header/menu[@zone=/browser/page/@zone]/search/@action"/>
                                    </xsl:attribute>

                                    <td colspan="{count(/browser/header/menu[@zone=/browser/page/@zone]/menu-item)}" align="center" bgcolor="{$table_label_bg_colour}">
                                        Search:
                                        <INPUT size="25" name="query" maxlength="400" value="{/browser/page/search-value/@value}"/>
                                        <xsl:if test="not(string-length($format)=0)">
                                            <input type="hidden" name="format" value="{$format}"/>
                                        </xsl:if>

                                        <select name="mode">
                                            <xsl:for-each select="/browser/header/menu[@zone=/browser/page/@zone]/search-option">
                                                <option value="{@value}">
                                                    <xsl:value-of select="text()"/>
                                                </option>
                                            </xsl:for-each>
                                        </select>
                                        <input type="submit">
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="/browser/header/menu[@zone=/browser/page/@zone]/search/@name"/>
                                            </xsl:attribute>
                                        </input>
                                    </td>
                                </form>

                            </tr>
                        </xsl:if>
                    </table>
                </td>
            </tr>
        </table>

        <br/>

    </xsl:template>


    <xsl:template name="page-title-only">

        <!-- Page title: left aligned green box for application name with green text for page name -->

        <table width="100%" border="0" cellspacing="0" cellpadding="4">
            <tr>
                <td class="tablegreen" nowrap="true" width="1%">
                    <span class="whitetitle">
                        <nobr>
                            <xsl:value-of select="/browser/header/menu[@zone=/browser/page/@zone]/@title"/>
                        </nobr>
                    </span>
                </td>
                <td>
                    <nobr class="pagetitle">
                        <xsl:value-of select="@title"/>
                    </nobr>
                </td>

                <xsl:if test="@manual">
                    <td align="right">
                        <a href="#" onClick="{concat('w=window.open(',$apos,@manual,'?format=contentframe&amp;fragment=',$apos,', ',$apos,'helpwindow',$apos,', ',$apos,'width=800,height=400,toolbar=no,directories=no,menu bar=no,scrollbars=yes,resizable=yes',$apos,');w.focus();')}">
                            <font color="red">[?]</font>
                            <xsl:text> = help</xsl:text>
                        </a>
                    </td>
                </xsl:if>

            </tr>
        </table>

    </xsl:template>

    <xsl:template name="page-title">

        <xsl:call-template name="page-title-only"/>

        <!-- auto hr -->
        <xsl:if test="count(child::*[(position()=1)][(name()='section') or (name()='table') or (name()='errorbox')])=0">
            <xsl:call-template name="hr"/>
        </xsl:if>


    </xsl:template>

    <xsl:template name="cbox">
        <xsl:param name="mode"/>
        <xsl:param name="name"/>
        <td width="130" align="center">
            <xsl:attribute name="bgcolor">
                <xsl:if test="$format=$mode">
                    <xsl:value-of select="$table_border_colour"/>
                </xsl:if>
                <xsl:if test="$format!=$mode">
                    <xsl:value-of select="$table_label_bg_colour"/>
                </xsl:if>
            </xsl:attribute>
            <a href="{concat($baseurl,'&amp;format=',$mode)}">
                <xsl:value-of select="$name"/>
            </a>
        </td>
    </xsl:template>

    <xsl:template name="shiny-footer">
        <!-- This is the xsl transform chooser -->

        <br/>

        <table border="0" align="center">
            <xsl:attribute name="bgcolor">
                <xsl:value-of select="$table_border_colour"/>
            </xsl:attribute>
            <tr>
                <td>
                    <table border="0" cellspacing="1" cellpadding="3">
                        <xsl:attribute name="bgcolor">
                            <xsl:value-of select="$table_division_colour"/>
                        </xsl:attribute>
                        <tr>
                            <xsl:call-template name="cbox">
                                <xsl:with-param name="mode" select="'normal'"/>
                                <xsl:with-param name="name" select="'Normal'"/>
                            </xsl:call-template>
                            <xsl:call-template name="cbox">
                                <xsl:with-param name="mode" select="'nomenu'"/>
                                <xsl:with-param name="name" select="'Printer Friendly'"/>
                            </xsl:call-template>
                            <xsl:call-template name="cbox">
                                <xsl:with-param name="mode" select="'text'"/>
                                <xsl:with-param name="name" select="'Text'"/>
                            </xsl:call-template>
                            <xsl:call-template name="cbox">
                                <xsl:with-param name="mode" select="'simple'"/>
                                <xsl:with-param name="name" select="'Simple HTML'"/>
                            </xsl:call-template>
                            <xsl:call-template name="cbox">
                                <xsl:with-param name="mode" select="'xml'"/>
                                <xsl:with-param name="name" select="'XML'"/>
                            </xsl:call-template>
                            <xsl:call-template name="cbox">
                                <xsl:with-param name="mode" select="'curator'"/>
                                <xsl:with-param name="name" select="'Curator View'"/>
                            </xsl:call-template>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>

        <!-- end chooser -->

        <!-- Show the version numbers -->

        <font size='-8'>
            <div align='center'>
                <i>
                    <xsl:apply-templates select="/browser/version[@zone=/browser/header/menu[@zone=/browser/page/@zone]/@version]"/>
                </i>

            </div>
        </font>
    </xsl:template>



    <!-- Standard content formatting -->

    <xsl:template match="table[@type='compact-border']">
        <table border="0" align="center">
            <xsl:attribute name="bgcolor">
                <xsl:value-of select="$table_border_colour"/>
            </xsl:attribute>
            <tr>
                <td>
                    <table border="0" cellspacing="1" cellpadding="3">
                        <xsl:attribute name="bgcolor">
                            <xsl:value-of select="$table_division_colour"/>
                        </xsl:attribute>
                        <xsl:apply-templates mode="border"/>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="table[@type='border']">
        <table width="100%" border="0" align="center">
            <xsl:attribute name="bgcolor">
                <xsl:value-of select="$table_border_colour"/>
            </xsl:attribute>
            <tr>
                <td>
                    <table width="100%" border="0" cellspacing="1" cellpadding="3">
                        <xsl:attribute name="bgcolor">
                            <xsl:value-of select="$table_division_colour"/>
                        </xsl:attribute>

                        <xsl:apply-templates mode="border"/>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="table[@type='coloured']">
        <table width="98%" border="0" align="center" bgcolor="{$table_border_colour}">
            <tr>
                <td>
                    <table width="100%" border="0" cellspacing="1" cellpadding="3" bgcolor="{$table_division_colour}">
                        <xsl:apply-templates mode="coloured"/>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="table[@type='invisible']">
        <table border="0" cellspacing="0" cellpadding="0">
            <xsl:apply-templates mode="invisible"/>
        </table>
    </xsl:template>

    <xsl:template match="table">
        <table>
            <xsl:apply-templates mode="invisible"/>
        </table>
    </xsl:template>

    <xsl:template match="errorbox">
        <table width="100%" bgcolor="#cc0000" border="0">
            <tr>
                <td>
                    <table border="0" bgcolor="#ffcfcf" cellspacing="1" width="100%" cellpadding="3">
                        <tr>
                            <td>
                                <xsl:copy-of select="child::*|child::text()"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="tr" mode="invisible">
        <tr>
            <xsl:apply-templates mode="invisible"/>
        </tr>
    </xsl:template>

    <xsl:template match="tr" mode="border">
        <tr>
            <xsl:apply-templates mode="border" select="td[position()=1]"/>
            <xsl:apply-templates mode="border" select="td[position()>1]"/>
        </tr>
    </xsl:template>

    <xsl:template match="tr" mode="coloured">
        <tr>
            <xsl:apply-templates mode="coloured"/>
        </tr>
    </xsl:template>

    <xsl:template match="td[@type='title']" mode="border">
        <td bgcolor="{$table_title_bg_colour}">
            <xsl:copy-of select="@*"/>
            <b>
                <xsl:apply-templates/>
            </b>
        </td>
    </xsl:template>

    <xsl:template match="td[@type='data']" mode="border">
        <td bgcolor="{$table_data_bg_colour}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>

    <xsl:template match="td[@type='left-label']" mode="border">
        <td bgcolor="{$table_label_bg_colour}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>


    <xsl:template match="td" mode="border">
        <td bgcolor="{$table_label_bg_colour}" align="right">
            <xsl:copy-of select="@*"/>
            <b>
                <xsl:apply-templates/>
            </b>
        </td>
    </xsl:template>

    <xsl:template match="td" mode="coloured">
        <td bgcolor="{$table_label_bg_colour}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>

    <xsl:template match="td" mode="invisible">
        <td>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </td>
    </xsl:template>

    <xsl:template match="help">

        <xsl:copy-of select="text()"/>
        <xsl:text> </xsl:text>
        <a href="#" onClick="{concat('w=window.open(',$apos,@manual,'?format=contentframe&amp;fragment=',@topic,$apos,', ',$apos,'helpwindow',$apos,', ',$apos,'width=800,height=400,toolbar=no,directories=no,menu bar=no,scrollbars=yes,resizable=yes',$apos,');w.focus();')}">
            <font color="red">[?]</font>
        </a>

    </xsl:template>

    <xsl:template name="rh-column">

        <xsl:param name="list"/>

        <xsl:for-each select="/browser/header/rightnav[@list=$list]/link">

            <table width="140" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="tablehead">
                        <img src="../services/images/sm_l.gif" width="6" height="6"/>
                    </td>
                    <td colspan="3" class="tablehead">
                        <img src="../services/images/trans.gif" width="100" height="6"/>
                    </td>
                    <td class="tablehead">
                        <div align="right">
                            <img src="../services/images/sm_r.gif" width="6" height="6"/>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="tablehead">&#160;</td>
                    <td colspan="3" class="tablehead">
                        <div align="center">
                            <span class="offwhite">
                                <xsl:value-of select="@name"/>
                            </span>
                        </div>
                    </td>
                    <td class="tablehead">&#160;</td>
                </tr>
                <tr class="tableborder">
                    <td colspan="5" class="tableborder">
                        <img src="../services/images/trans.gif" height="3" width="1"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="5" class="tablebody">
                        <div align="center">
                            <img src="../services/images/trans.gif" width="50" height="8"/>
                            <br/>
                            <a href="{@href}">
                                <img src="{@image}" width="{@width}" height="{@height}" border="0"/>
                            </a>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="tablebody">&#160;</td>
                    <td colspan="3" class="tablebody">
                        <img src="../services/images/trans.gif" width="50" height="5"/>
                        <br/>
                        <span class="psmall">
                            <xsl:copy-of select="*|text()"/>
                        </span>
                    </td>
                    <td class="tablebody">&#160;</td>
                </tr>
                <tr>
                    <td colspan="2">
                        <img src="../services/images/left.gif" width="20" height="20"/>
                    </td>
                    <td class="tablebody">
                        <img src="../services/images/trans.gif" width="100" height="20"/>
                    </td>
                    <td colspan="2">
                        <img src="../services/images/right.gif" width="20" height="20"/>
                    </td>
                </tr>
            </table>
            <br/>

        </xsl:for-each>
    </xsl:template>

    <xsl:template match="column">

        <table width="560" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                    <!--
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <xsl:text disable-output-escaping="yes"><![CDATA[<tr><td height="3" background="../Groups/images/hor.gif"><img src="../Groups/images/trans.gif" width="25" height="3"/></td></tr>]]></xsl:text>
                    </table>
                    -->

                    <xsl:apply-templates/>
                </td>
                <td>
                    <img src="../services/images/trans.gif" width="6" height="8"/>
                </td>

                <td width="3" background="../services/images/vert.gif">
                    <img src="../services/images/trans.gif" height="3" width="3"/>
                </td>

                <td width="20">
                    <img src="../services/images/trans.gif" height="8" width="6"/>
                </td>
                <td valign="top">

                    <xsl:call-template name="rh-column">
                        <xsl:with-param name="list" select="@list"/>
                    </xsl:call-template>

                </td>
            </tr>
        </table>

    </xsl:template>



    <!-- Sections, numbers and indexes -->


    <!--
    <xsl:template match="auto-index">

        <xsl:variable name="index">
            <xsl:value-of select="@index"/>
        </xsl:variable>
        <xsl:variable name="format">
            <xsl:value-of select="@format"/>
        </xsl:variable>

        <xsl:for-each select="../../index[ @name = $index ]">
            <xsl:call-template name="mkindex"/>
        </xsl:for-each>

    </xsl:template>


    <xsl:template name="mkindex">

        <xsl:if test="section">
            <ul>
                <xsl:for-each select="section">
                    <li>
                        <xsl:call-template name="numberof"/>
                        <a href="#{@name}">
                            <xsl:value-of select="@title"/>
                        </a>
                        <xsl:call-template name="mkindex"/>
                        <xsl:text>
                        </xsl:text>
                    </li>
                </xsl:for-each>
            </ul>
        </xsl:if>

    </xsl:template>


    <xsl:template name="numberof">

        <xsl:if test="ancestor::index">
            <xsl:call-template name="numberof-internal"/>)
        </xsl:if>

    </xsl:template>


    <xsl:template name="numberof-internal">


        <xsl:variable name="format">
            <xsl:if test="not(../@format)">1</xsl:if>
            <xsl:if test="../@format">
                <xsl:value-of select="../@format"/>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="name">
            <xsl:value-of select="@name"/>
        </xsl:variable>

        <xsl:for-each select="parent::section">
            <xsl:call-template name="numberof-internal"/>.
        </xsl:for-each>
        <xsl:for-each select="../section">

            <!- - concat(x,'') fixes a bug in TrAX where (Node)x=(String)x is false - ->
            <xsl:if test=" concat(@name,'') = $name ">
                <xsl:number value="position()" format="{$format}"/>
            </xsl:if>
        </xsl:for-each>


    </xsl:template>
    -->

    <xsl:template match="auto-index">

        <xsl:variable name="index">
            <xsl:value-of select="@index"/>
        </xsl:variable>
        <xsl:variable name="format">
            <xsl:value-of select="@format"/>
        </xsl:variable>

        <xsl:for-each select="/descendant::division[ @name = $index ]">
            <xsl:call-template name="mkindex"/>
        </xsl:for-each>

    </xsl:template>

    <xsl:template name="mkindex">

        <ul>
            <xsl:for-each select="section">
                <li>
                    <xsl:call-template name="numberof"/>
                    <xsl:text>) </xsl:text>

                    <!-- ###################################################### -->
                    <!-- don't generate a number ... keep the asigned link name -->
                    <!-- ###################################################### -->
                    <!--
                    <a target="{$target}" href="{concat($targetdocument,'#',generate-id())}">
                      -->
                    <a target="{$target}" href="{concat($targetdocument,'#',@name)}">

                        <xsl:value-of select="@title"/>
                        <xsl:copy-of select="title/text()"/>
                    </a>
                    <xsl:call-template name="mkindex"/>
                    <xsl:text>
                    </xsl:text>
                </li>
            </xsl:for-each>
        </ul>

    </xsl:template>

    <xsl:template name="numberof">

        <xsl:if test="ancestor::division">

            <!--<xsl:variable name="format">1</xsl:variable>-->

            <xsl:variable name="level" select="count(ancestor::section)-count(ancestor::division/ancestor::section)"/>

            <xsl:variable name="format">
                <xsl:if test="string-length(ancestor::division[1]/@format)>=$level*2+1"><xsl:value-of select="substring(ancestor::division[1]/@format,$level*2+1,1)"/></xsl:if>
                <xsl:if test="not(string-length(ancestor::division[1]/@format)>=$level*2+1)">1</xsl:if>
            </xsl:variable>

            <xsl:for-each select="parent::section">
                <xsl:call-template name="numberof"/>
                <xsl:if test="string-length(ancestor::division[1]/@format)>=$level*2+2"><xsl:value-of select="substring(ancestor::division[1]/@format,$level*2+2,1)"/></xsl:if>
                <xsl:if test="not(string-length(ancestor::division[1]/@format)>=$level*2+1)">.</xsl:if>

            </xsl:for-each>

            <xsl:variable name="uid" select="generate-id()"/>

            <xsl:for-each select="../section">

                <xsl:if test="generate-id() = $uid ">
                    <xsl:number value="position()" format="{$format}"/>
                </xsl:if>
            </xsl:for-each>

        </xsl:if>

    </xsl:template>

    <xsl:template match="hr" name="hr">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <xsl:text disable-output-escaping="yes"><![CDATA[<tr><td height="3" background="../Groups/images/hor.gif"><img src="../Groups/images/trans.gif" width="25" height="3"/></td></tr>]]></xsl:text>
        </table>
    </xsl:template>

    <xsl:template match="division">
        <xsl:apply-templates select="section"/>
    </xsl:template>

    <xsl:template match="section">

        <xsl:if test="@name">
            <a name="{@name}"/>
        </xsl:if>

        <a name="{generate-id()}"/>

        <xsl:variable name="title">
            <xsl:value-of select="@title"/>
            <xsl:copy-of select="title/text()"/>
        </xsl:variable>

        <xsl:choose>

            <xsl:when test="ancestor::division[1]/@style='numbered'">
                <p>
                    <b>
                        <xsl:call-template name="numberof"/>
                        <xsl:text>) </xsl:text>
                        <xsl:value-of select="$title"/>
                    </b>
                </p>
            </xsl:when>

            <xsl:when test="count(ancestor::section)>0">
                <p>
                    <b>
                        <xsl:value-of select="$title"/>
                    </b>
                </p>
            </xsl:when>


            <xsl:otherwise>

                <xsl:call-template name="hr"/>

                <xsl:if test=" concat(@style,'') = 'bold'">
                    <div align="center">
                        <b>
                            <xsl:value-of select="$title"/>
                        </b>
                    </div>
                </xsl:if>

                <xsl:if test="not(@style)">

                    <table width="100%" border="0" cellspacing="0" cellpadding="4">
                        <tr>
                            <td width="100%"> </td>
                            <td class="tablebody" nowrap="true">
                                <span class="green">
                                    <nobr>
                                        <xsl:value-of select="$title"/>
                                    </nobr>
                                </span>
                            </td>
                        </tr>
                    </table>
                </xsl:if>

            </xsl:otherwise>

        </xsl:choose>

        <xsl:apply-templates/>

    </xsl:template>

</xsl:stylesheet>
