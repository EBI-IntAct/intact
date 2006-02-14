<html>
<head>

    <!-- START EBI Header -->

    <jsp:include page="ebi_head.html"/>

    <!-- END EBI HEADER -->

    <title>IntAct - Home page</title>

    <meta name="description"
          content="The IntAct database provides a freely available, open source database system and analysis tools for protein interaction data. All interactions are derived from literature curation or direct user submissions and are freely available."/>
    <meta name="keywords" content="EBI, EMBL, UniProt, Swiss-Prot, TrEMBL, bioinformatics, proteomics, molecular,
                                 software, database, sequence, protein, interaction, interacting, open source,
                                 analysis, curator, curation, scientific literature, high throughput, graph,
                                 visualization, rendering, network, hub, local installation, free,
                                 computational biology, nucleotide, DNA, RNA, science, leading edge, protein-protein,
                                 annotation, controlled vocabulary, CV, PSI, PSI-MI, Proteome Standards Initiative,
                                 IMEx, binding-site, binding site, domain, GO classification, GO, PDB"/>

    <!-- IntAct dynamic application should not be indexed by search engines -->
    <meta name='robots' content='nofollow'>

</head>


<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" onunload="callUnLoadMethods()"
      onload="EbiPreloadImages('services');" marginheight="0" marginwidth="0">

<!-- START EBI Header -->

<!-- START EBI HEADER -->

<jsp:include page="ebi_body_header.html"/>

<!-- END EBI HEADER -->


<!-- END EBI HEADER -->


<!-- START Intact home page -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tbody>
        <tr>
            <td colspan="6" class="tablebody" height="6"><img src="http://www.ebi.ac.uk/services/images/trans.gif"
                                                              height="6"
                                                              width="1"></td>
        </tr>
        <tr>
            <td class="tablebody" height="20" width="1%"><img src="http://www.ebi.ac.uk/services/images/trans.gif"
                                                              height="20"
                                                              width="160"></td>
            <td align="left" bgcolor="#ffffff" height="20" valign="top" width="20"><img
                    src="http://www.ebi.ac.uk/services/images/corner.gif" height="20" width="20"></td>
            <td>&nbsp;</td>
            <td colspan="3" width="100%"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="1" width="1">
            </td>
        </tr>
        <tr>
            <td align="left" valign="top" width="170">

                <!-- leftnav START -->

                <jsp:include page="sidebar.html"/>

                <!-- leftnav END -->

                <table border="0" cellpadding="0" cellspacing="0" height="100%" width="170">
                    <tbody>
                        <tr>
                            <td class="tablebody" height="20" valign="top" width="150"><img
                                    src="http://www.ebi.ac.uk/services/images/trans.gif" height="20" width="150"></td>
                            <td align="right" height="20" valign="top" width="20"><img
                                    src="http://www.ebi.ac.uk/services/images/right.gif" height="20" width="20"></td>
                        </tr>
                        <tr>
                            <td colspan="2" bgcolor="#ffffff">&nbsp;</td>
                        </tr>
                    </tbody>
                </table>
                <table border="0" cellpadding="0" cellspacing="0" height="100%" width="170">
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
            <td align="left" valign="top" width="100%"> <!-- 593 -->
                <table border="0" cellpadding="0" cellspacing="0">
                    <tbody>
                        <tr>
                            <td valign="top">

                                <!-- contents START -->

                                <jsp:include page="content.jsp"/>

                                <!-- contents END -->

                            </td>

                            <td><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="8" width="6"></td>
                            <td background="http://www.ebi.ac.uk/services/images/vert.gif" width="3"><img
                                    src="http://www.ebi.ac.uk/services/images/trans.gif" height="3" width="3"></td>
                            <td width="20"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="8"
                                                width="6"></td>
                            <td valign="top" witdh="200">

                                <!-- rightnav START -->

                                <jsp:include page="news.html"/>

                                <!-- rightnav END -->

                            </td>

                        </tr>
                        <tr>
                            <td class="tablebody" background="http://www.ebi.ac.uk/services/images/hor.gif" height="3"
                                width="500"><img
                                    src="http://www.ebi.ac.uk/services/images/trans.gif" height="3" width="3"></td>
                            <td colspan="2"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="1"
                                                 width="1"></td>
                        </tr>
                    </tbody>
                </table>
            </td>
            <td width="16"><img src="http://www.ebi.ac.uk/services/images/trans.gif" height="8" width="6"></td>
        </tr>
    </tbody>
</table>
<div align="center">
    <p>
        Please contact <a target="_top" title="Contact EBI Support" class="normaltext"
                          href="http://www.ebi.ac.uk/support/index.php?query=IntAct">EBI Support</a> with any problems
        or suggestions regarding this site.
        <br>
        <br>
        <span class="psmall"> <a href="/Information/termsofuse.html" class="small_list">Terms of Use</a> </span>
        <br><br>
    </p>


</div>
<map name="Map">
    <area shape="rect" coords="70,1,156,25" href="http://srs.ebi.ac.uk/" alt="Start SRS Session"
          title="Start SRS Session">
    <area shape="rect" coords="1,1,69,25" href="http://www.ebi.ac.uk/Information/sitemap.html" alt="EBI Site Map"
          title="EBI Site Map">
</map>
</body>
</html>
