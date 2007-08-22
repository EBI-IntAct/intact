<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
                    <xsl:if test="sanity-report/database">
                        <p><strong>Instance name</strong>:
                            <xsl:value-of select="sanity-report/database"/>
                        </p>
                    </xsl:if>
                    <xsl:for-each select="sanity-report/report-attribute">
                        <strong>
                            <xsl:value-of select="name"/>
                        </strong>
                        :
                        <xsl:value-of select="value"/>
                        <br/>
                    </xsl:for-each>
                    <h2>Failed rules summary</h2>
                    <ul>
                        <xsl:for-each select="sanity-report/sanity-result">
                            <li>
                                <a href="#{description}">
                                    <xsl:value-of select="description"/>
                                </a>
                                &#160;
                                <strong>(<xsl:value-of select="count(insane-object)"/>)
                                </strong>
                            </li>
                        </xsl:for-each>
                    </ul>
                    <xsl:for-each select="sanity-report/sanity-result">
                        <div style="display:block">
                            <a id="{description}">
                                <xsl:if test="level = 'MAJOR'">
                                    <h2 style="color:red">
                                        <xsl:value-of select="description"/>
                                    </h2>
                                </xsl:if>
                                <xsl:if test="level = 'NORMAL'">
                                    <h2 style="color:tomato">
                                        <xsl:value-of select="description"/>
                                    </h2>
                                </xsl:if>
                                <xsl:if test="level = 'MINOR'">
                                    <h2 style="color:orange">
                                        <xsl:value-of select="description"/>
                                    </h2>
                                </xsl:if>
                            </a>
                            <p>
                                Suggestion:
                                <xsl:value-of select="suggestion"/>
                            </p>

                            <table class="contenttable">
                                <thead>
                                    <tr>
                                        <th>AC</th>
                                        <th>Label</th>
                                        <th>Type</th>
                                        <th>When</th>
                                        <th>User</th>
                                        <xsl:for-each select="insane-object[1]/field">
                                            <th>
                                                <xsl:value-of select="name"/>
                                            </th>
                                        </xsl:for-each>
                                    </tr>
                                </thead>
                                <tbody>
                                    <xsl:for-each select="insane-object">
                                        <tr>
                                            <td>
                                                <xsl:choose>
                                                    <xsl:when test="url">
                                                        <a href="{url}">
                                                            <xsl:value-of select="ac"/>
                                                        </a>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="ac"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td>
                                                <xsl:value-of select="shortlabel"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="objclass"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="updated"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="updator"/>
                                            </td>
                                            <xsl:for-each select="field">
                                                <td>
                                                    <xsl:value-of select="value"/>
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