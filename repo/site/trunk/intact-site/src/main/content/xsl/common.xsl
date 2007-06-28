<?xml version="1.0" ?>

<!-- XSL introduction -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:param name="format">normal</xsl:param>

    <!-- Common formating tools -->

    <xsl:template match="search-box">
        <form action="{/browser/header/menu[@zone=/browser/page/@zone]/search/@action}" method="get">

            <xsl:if test="not(string-length($format)=0)">
                <input type="hidden" name="format" value="{$format}"/>
            </xsl:if>
            <INPUT size="40" name="query" maxlength="400" class="text" value="{/browser/page/search-value/@value}"/>
            <br/>
            <select name="mode">
                <xsl:for-each select="/browser/header/menu[@zone=/browser/page/@zone]/search-option">
                    <option value="{@value}"><xsl:value-of select="text()"/></option>
                </xsl:for-each>
            </select>
            <input type="submit" value="{/browser/header/menu[@zone=/browser/page/@zone]/search/@name}"/>

        </form>
    </xsl:template>

    <xsl:template match="version-info">
        <table width="100%">
            <tr><td>Database</td><td>Version</td><td>Entries</td></tr>
        <xsl:variable name="zone" select="@zone"/>
        <xsl:apply-templates select="/browser/version[@zone=$zone]"/>
        </table>
    </xsl:template>



    <!-- Link rewriters (preserves format parameter in URLs) -->

    <xsl:template match="form">
        <xsl:element name="form">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
            <xsl:if test="not(child::input[@name='format'])">
                <xsl:if test="not(string-length($format)=0)">
                    <input type="hidden" name="format" value="{$format}"/>
                </xsl:if>
            </xsl:if>
        </xsl:element>
    </xsl:template>






    <xsl:template match="@href">

        <xsl:attribute name="href"><xsl:if test="not(contains(.,'#'))">
            <xsl:value-of select="."/><xsl:if test="not(starts-with(.,'ftp')) and not(starts-with(.,'http')) and not(starts-with(.,'#'))"><xsl:if test="not(contains(.,'?'))">?</xsl:if><xsl:value-of select="$formatopt"/></xsl:if></xsl:if><xsl:if test="contains(.,'#')"><xsl:value-of select="substring-before(.,'#')"/><xsl:if test="not(starts-with(.,'http')) and not(starts-with(.,'#'))"><xsl:if test="not(contains(.,'?'))">?</xsl:if><xsl:value-of select="$formatopt"/></xsl:if>#<xsl:value-of select="substring-after(.,'#')"/></xsl:if></xsl:attribute>


    </xsl:template>

    <!-- Top level rules -->

    <xsl:template match="browser">
        <xsl:apply-templates select="page"/>
        <xsl:for-each select="comment">
            <xsl:comment><xsl:value-of select="."/></xsl:comment>
        </xsl:for-each>
    </xsl:template>


    <!-- Default copy rules -->

    <!-- Copy child nodes without processing -->

    <xsl:template match="include-html">
        <xsl:copy-of select="child::*|child::text()"/>
    </xsl:template>

    <!-- Copy child text without output escaping, useful for javascript in the input document.
Eg:
<![CDATA[<>]]>
transforms to:
<>
    -->

    <xsl:template match="include-raw">
        <xsl:value-of  disable-output-escaping="yes" select="text()"/>
    </xsl:template>



    <xsl:template match="@*">
        <xsl:copy/>
    </xsl:template>


    <xsl:template match="*">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
