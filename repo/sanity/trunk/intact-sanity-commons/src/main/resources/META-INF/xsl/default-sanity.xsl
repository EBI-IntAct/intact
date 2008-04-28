<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:sr="http://uk.ac.ebi.intact/sanity/report">

    <xsl:template match="/">
        <html>
            <head>
                <link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/contents.css" type="text/css"/>
                <link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/userstyles.css" type="text/css"/>
                <link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/sidebars.css" type="text/css"/>
                <link rel="stylesheet" href="http://www.ebi.ac.uk/intact/binary-search/css/intact.css" type="text/css"/>
            </head>


            <body>
                <div style="float:right">
                    <img src="http://www.ebi.ac.uk/intact/site/images/logo_intact_small.gif"/>
                </div>
                <div class="contents" id="contents">
                    <h1>IntAct Sanity Check</h1>
                    <xsl:if test="sr:sanity-report/sr:database">
                        <p><strong>Instance name</strong>:
                            <xsl:value-of select="sr:sanity-report/sr:database"/>
                        </p>
                    </xsl:if>
                    <xsl:for-each select="sr:sanity-report/sr:report-attribute">
                        <strong>
                            <xsl:value-of select="sr:name"/>
                        </strong>
                        :
                        <xsl:value-of select="sr:value"/>
                        <br/>
                    </xsl:for-each>
                    <a id="top"></a>
                    <h2>Failed rules summary</h2>
                    <ul>
                        <xsl:for-each select="sr:sanity-report/sr:sanity-result">
                            <li>
                                <a href="#{sr:key}">
                                    [<xsl:value-of select="sr:key"/>] &#160;
                                    <xsl:value-of select="sr:description"/>
                                </a>
                                &#160;
                                <strong>(<xsl:value-of select="count(sr:insane-object)"/>)
                                </strong>
                            </li>
                        </xsl:for-each>
                    </ul>
                    <xsl:for-each select="sr:sanity-report/sr:sanity-result">
                        <div style="display:block">
                            <table border="0" width="70%">
                                <tr>
                                    <td align="left">
                                        <a id="{sr:key}">
                                            <xsl:if test="sr:level = 'ERROR'">
                                                <h2 style="color:red">
                                                    <xsl:value-of select="sr:description"/>
                                                </h2>
                                            </xsl:if>
                                            <xsl:if test="sr:level = 'WARNING'">
                                                <h2 style="color:tomato">
                                                    <xsl:value-of select="sr:description"/>
                                                </h2>
                                            </xsl:if>
                                            <xsl:if test="sr:level = 'INFO'">
                                                <h2 style="color:orange">
                                                    <xsl:value-of select="sr:description"/>
                                                </h2>
                                            </xsl:if>
                                        </a>
                                    </td>
                                    <td align="right">
                                        <a href="#top">Back to top</a>
                                    </td>
                                </tr>
                            </table>
                            <p>
                                Suggestion:
                                <xsl:value-of select="sr:suggestion"/>
                            </p>

                            <table class="contenttable">
                                <thead>
                                    <tr>
                                        <th>AC</th>
                                        <th>Label</th>
                                        <th>Type</th>
                                        <th>Created</th>
                                        <th>Created by</th>
                                        <th>Updated</th>
                                        <th>Updated by</th>
                                        <th>Owner</th>
                                        <xsl:for-each select="sr:insane-object[1]/sr:field">
                                            <th>
                                                <xsl:value-of select="sr:name"/>
                                            </th>
                                        </xsl:for-each>
                                    </tr>
                                </thead>
                                <tbody>
                                    <xsl:for-each select="sr:insane-object">
                                        <tr>
                                            <td>
                                                <xsl:choose>
                                                    <xsl:when test="sr:url">
                                                        <a href="{sr:url}">
                                                            <xsl:value-of select="sr:ac"/>
                                                        </a>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="sr:ac"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td>
                                                <xsl:value-of select="sr:shortlabel"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="sr:objclass"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="sr:created"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="sr:creator"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="sr:updated"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="sr:updator"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="sr:owner"/>
                                            </td>
                                            <xsl:for-each select="sr:field">
                                                <td>
                                                    <xsl:value-of select="sr:value"/>
                                                </td>
                                            </xsl:for-each>
                                        </tr>
                                    </xsl:for-each>
                                </tbody>
                            </table>
                        </div>
                    </xsl:for-each>
                </div>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>