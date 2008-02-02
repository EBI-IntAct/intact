<?xml version="1.0" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:import href="common.xsl"/>
    <xsl:import href="shiny.xsl"/>

    <xsl:output method="html" indent="no" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>


    <xsl:param name="baseurl">./documentation.html</xsl:param>
    <xsl:param name="format">nomenu</xsl:param>
    <xsl:param name="menu">small</xsl:param>
    <xsl:param name="formatopt"></xsl:param>
    <xsl:param name="editlink"></xsl:param>
    <xsl:param name="request-index"></xsl:param>
    <xsl:param name="target">text</xsl:param>
    <xsl:param name="targetdocument">
        <xsl:value-of select="concat($baseurl,'')"/>
    </xsl:param>


    <xsl:template match="page">

        <html>
            <head>
                <!--                <link rel="stylesheet" href="../services/include/stylesheet.css" type="text/css"/>-->
                <link rel="stylesheet" href="stylesheet.css" type="text/css"/>

                <!-- Don't cache the documentation -->
                <meta http-equiv="cache-control" content="no-cache"/>
                <meta http-equiv="pragma" content="no-cache"/>
                <meta http-equiv="expires" content="-1"/>

            </head>
            <body>

                <xsl:call-template name="page-title-only"/>
                <xsl:call-template name="hr"/>

                <!-- Apply all other templates - fill in the body of the page tag. -->
                <xsl:apply-templates select="descendant::auto-index"/>
            </body>
        </html>

    </xsl:template>


    <xsl:template match="p">
        <p style="margin-left:10px">
            <xsl:apply-templates/>
        </p>
    </xsl:template>


</xsl:stylesheet>
