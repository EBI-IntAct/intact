<?xml version="1.0"?>

<!--
    Basic initial XSL stylesheet for formatting intact objects to display.
    This file will be modified to provide better display options as the project
    progresses.

    author: Chris Lewington, Sugath Mudali November, December 2002
    version: $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/TR/REC-html40">
    <!--
        Defaults to zero if none was sepecified. Note the single quotes
        around the value. -->
    <xsl:param name="tableName" select="'tbl_0'"/>

    <xsl:output method="xml" version="1.0" indent="yes"
        doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

    <!--
    ****************************************************************************
    ** Top level template. Creates the framework for the XHTML page.
    *************************************************************************-->
    <xsl:template match="/">
        <table cellpadding="1" cellspacing="0" border="0">
            <xsl:call-template name="draw-table-headings"/>
            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <!-- This is to match XML generated for classes. -->
    <xsl:template match="Interaction">
        <xsl:call-template name="interaction"/>
    </xsl:template>

<!--    <xsl:template match="Interactor">-->
<!--        <xsl:call-template name="interactor"/>-->
<!--    </xsl:template>-->

<!--<xsl:template match="Institution">-->
<!--    <xsl:call-template name="institutionAtt"/>-->
<!--</xsl:template>-->

    <!--
    ****************************************************************************
    ** Template for experiment attribute.
    *************************************************************************-->
    <xsl:template match="Experiment">
        <tr bgcolor="red">
            <xsl:call-template name="draw-checkbox"/>
            <td colspan="4">Exp:
                <xsl:value-of select="@shortLabel"/>
            </td>
            <xsl:apply-templates select="@ac"/>
            <td>xx/12/2002</td>
            <td>xx/12/2002</td>
        </tr>

        <xsl:if test="@fullName">
            <tr>
                <xsl:call-template name="draw-2-empty-cells"/>
                <td colspan="6"><xsl:value-of select="@fullName"/></td>
            </tr>
        </xsl:if>

        <xsl:if test="cvIdentification/@shortLabel | cvInteraction/@shortLabel | bioSource">
            <tr>
                <xsl:call-template name="draw-2-empty-cells"/>
                <td colspan="3">
                    <xsl:value-of select="cvIdentification/@shortLabel"/>
                </td>
                <td>
                    <xsl:value-of select="cvInteraction/@shortLabel"/>
                </td>
                <td>
                    <xsl:value-of select="bioSource/@scientificName"/>
                </td>
                <td>
                    <xsl:value-of select="bioSource/@taxId"/>
                </td>
            </tr>
        </xsl:if>

        <xsl:for-each select="annotation">
            <tr>
                <xsl:call-template name="draw-2-empty-cells"/>
                <td colspan="3"><xsl:value-of select="cvTopic/@shortLabel"/></td>
                <td colspan="3"><xsl:value-of select="@annotationText"/></td>
            </tr>
        </xsl:for-each>

        <xsl:for-each select="xref">
            <tr>
                <xsl:call-template name="draw-2-empty-cells"/>
                <td colspan="3"><xsl:value-of select="cvDatabase/@shortLabel"/></td>
                <td><xsl:value-of select="@primaryId"/></td>
                <td><xsl:value-of select="@secondaryId"/></td>
                <td><xsl:value-of select="@dbRelease"/></td>
            </tr>
        </xsl:for-each>

        <!-- Do only if status is on -->
        <xsl:if test="@status='true'">
            <xsl:apply-templates select="interaction"/>
        </xsl:if>

    </xsl:template>

    <!-- =================================================================== -->

    <!-- template for a protein, attribute match -->
    <xsl:template match="/Protein">
        <tr>
            <td align="center"><input type="checkbox" name="choices" value="select"></input></td>
            <td align="center"><input type="checkbox" name="choices" value="expand"></input></td>
            <td align="center"><input type="checkbox" name="choices" value="contract"></input></td>
            <td>
                <xsl:text>Protein</xsl:text>
            </td>
            <!-- attributes to display -->
            <xsl:apply-templates select="@ac"/>
            <xsl:apply-templates select="@shortLabel"/>
            <xsl:apply-templates select="owner"/>
            <xsl:apply-templates select="xref"/>
        </tr>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Template for interaction attribute.
    *************************************************************************-->
    <xsl:template name="interaction" match="interaction">
        <tr bgcolor="yellow">
            <xsl:call-template name="draw-checkbox"/>
<!--            <td><input value="select" name="choices" type="checkbox"></input></td>-->
            <td></td>
            <td colspan="3">Interaction:
                <xsl:value-of select="@shortLabel"/>
            </td>
            <xsl:apply-templates select="@ac"/>
            <td>xx/12/02</td>
            <td>xx/12/02</td>
        </tr>

        <xsl:if test="@fullName">
            <tr>
                <xsl:call-template name="draw-3-empty-cells"/>
                <td colspan="5"><xsl:value-of select="@fullName"/></td>
            </tr>
        </xsl:if>

        <xsl:if test="cvInteraction/@shortLabel">
            <tr>
                <xsl:call-template name="draw-3-empty-cells"/>
                <td colspan="2">
                    <xsl:value-of select="cvInteraction/@shortLabel"/>
                </td>
                <xsl:call-template name="draw-3-empty-cells"/>
            </tr>
        </xsl:if>

        <xsl:for-each select="annotation">
            <tr>
                <xsl:call-template name="draw-3-empty-cells"/>
                <td colspan="2"><xsl:value-of select="cvTopic/@shortLabel"/></td>
                <td colspan="3"><xsl:value-of select="@annotationText"/></td>
            </tr>
        </xsl:for-each>

        <xsl:for-each select="xref">
            <tr>
                <xsl:call-template name="draw-3-empty-cells"/>
                <td colspan="2"><xsl:value-of select="cvDatabase/@shortLabel"/></td>
                <td><xsl:value-of select="@primaryId"/></td>
                <td><xsl:value-of select="@secondaryId"/></td>
                <td><xsl:value-of select="@dbRelease"/></td>
            </tr>
        </xsl:for-each>

        <!-- Do only if status is on -->
        <xsl:if test="@status='true'">
            <!-- interactor is inside component element -->
            <xsl:apply-templates select="component/interactor"/>
        </xsl:if>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Template for interactor attribute.
    *************************************************************************-->
    <xsl:template name="interactor" match="interactor">
        <tr bgcolor="lightgreen">
            <xsl:call-template name="draw-checkbox"/>
            <xsl:call-template name="draw-2-empty-cells"/>
            <td colspan="2">Interactor:
                <xsl:value-of select="@shortLabel"/>
            </td>
            <xsl:apply-templates select="@ac"/>
            <td>xx/12/02</td>
            <td>xx/12/02</td>
        </tr>

        <xsl:if test="@fullName">
            <tr>
                <xsl:call-template name="draw-4-empty-cells"/>
                <td colspan="4"><xsl:value-of select="@fullName"/></td>
            </tr>
        </xsl:if>

        <xsl:if test="cvComponentRole/@shortLabel">
            <tr>
                <xsl:call-template name="draw-4-empty-cells"/>
                <td><xsl:value-of select="cvComponentRole/@shortLabel"/></td>
                <td></td>
                <td>Tax scientific name</td>
                <td>Taxid</td>
            </tr>
        </xsl:if>

        <xsl:for-each select="annotation">
            <tr>
                <xsl:call-template name="draw-4-empty-cells"/>
                <td colspan="1"><xsl:value-of select="cvTopic/@shortLabel"/></td>
                <td colspan="3"><xsl:value-of select="@annotationText"/></td>
            </tr>
        </xsl:for-each>

        <!-- Do only if status is on -->
        <xsl:if test="@status='true'">
            <xsl:call-template name="draw-xrefs">
                <xsl:with-param name="emptyCells" select="4"/>
                <xsl:with-param name="colSpan" select="1"/>
            </xsl:call-template>
        </xsl:if>

    </xsl:template>

    <!--
    ****************************************************************************
    ** Template for AC attribute.
    *************************************************************************-->
    <xsl:template match="@ac">
        <td>
            <xsl:value-of select="."/>
        </td>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Template to draw a checkbox.
    *************************************************************************-->
    <xsl:template name="draw-checkbox">
        <xsl:variable name="cbName">
            <xsl:variable name="ac">
                <xsl:value-of select="@ac"/>
            </xsl:variable>
            <xsl:value-of select="concat($tableName, '_', $ac)"/>
        </xsl:variable>
        <td><input name="{$cbName}" type="checkbox"></input></td>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Draw Xreferences.
    ** emptyCells - the number of empty cells to print.
    ** colSpan - the number columns to span for the short label of cvDatabase.
    *************************************************************************-->
    <xsl:template name="draw-table-headings">
        <tr>
            <td width="4%"></td>
            <td width="2%"></td>
            <td width="2%"></td>
            <td width="2%"></td>
            <td width="20%"></td>
            <td width="30%"></td>
            <td width="20%"></td>
            <td width="20%"></td>
        </tr>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Draw the button table.
    *************************************************************************-->
    <xsl:template name="draw-table-buttons">
        <table cellpadding="1" cellspacing="0" border="1" width="100%">
            <tr>
                <td align="center">
                    <input type="submit" Name="submit" value="Submit"/>
                </td>
            </tr>
        </table>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Draws two empty cells.
    *************************************************************************-->
    <xsl:template name="draw-2-empty-cells">
        <xsl:call-template name="draw-empty-cells">
            <xsl:with-param name="testValue" select="2"/>
        </xsl:call-template>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Draws three empty cells.
    *************************************************************************-->
    <xsl:template name="draw-3-empty-cells">
        <xsl:call-template name="draw-empty-cells">
            <xsl:with-param name="testValue" select="3"/>
        </xsl:call-template>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Draws four empty cells.
    *************************************************************************-->
    <xsl:template name="draw-4-empty-cells">
        <xsl:call-template name="draw-empty-cells">
            <xsl:with-param name="testValue" select="4"/>
        </xsl:call-template>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Draws an empty cell.
    ** i - the starting value; defaults to 0.
    ** testValue - the value to test after each iteration; will iterate
    **             till i == testValue.
    *************************************************************************-->
    <xsl:template name="draw-empty-cells">
        <xsl:param name="i" select="0"/>
        <xsl:param name="testValue"/>

        <!-- Condition variable for the loop control. -->
        <xsl:variable name="testPassed">
            <!-- Check the condition of the loop. -->
            <xsl:choose>
                <xsl:when test="$i &lt; $testValue">
                    <xsl:text>true</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>false</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="$testPassed='true'">
            <!-- Body of the loop -->
            <td></td>

            <!-- Increment the loop counter and call it again. -->
            <xsl:call-template name="draw-empty-cells">
                <xsl:with-param name="i" select="$i + 1"/>
                <xsl:with-param name="testValue" select="$testValue"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Draw Xreferences.
    ** emptyCells - the number of empty cells to print.
    ** colSpan - the number columns to span for the short label of cvDatabase.
    *************************************************************************-->
    <xsl:template name="draw-xrefs">
        <xsl:param name="emptyCells"/>
        <xsl:param name="colSpan"/>

        <xsl:for-each select="xref">
            <tr bgcolor="lightblue">
                <!-- Draw the empty cells. -->
                <xsl:call-template name="draw-empty-cells">
                    <xsl:with-param name="testValue" select="$emptyCells"/>
                </xsl:call-template>

                <!-- Print xref contents -->
                <td colspan="$colSpan"><xsl:value-of select="cvDatabase/@shortLabel"/></td>
                <td><xsl:value-of select="@primaryId"/></td>
                <td><xsl:value-of select="@secondaryId"/></td>
                <td><xsl:value-of select="@dbRelease"/></td>
            </tr>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
