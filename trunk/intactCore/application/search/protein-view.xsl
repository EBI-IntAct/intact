<?xml version="1.0"?>

<!--
    XSL stylesheet for formatting intact objects to be displyaed using the 'protein view'
    format suggested at the Bordeaux workshop April 2003.

    author: Chris Lewington
    version: $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://www.w3.org/TR/REC-html40">
    <!--
        Defaults to zero if none was specified. Note the single quotes
        around the value. -->
    <xsl:param name="tableName" select="'tbl_0'"/>
    <xsl:param name="searchLink"/>
    <xsl:param name="helpLink"/>
    <xsl:param name="experimentLink"/>
   <!-- <xsl:param name="helpLink" select="'http://web7-node1.ebi.ac.uk:8160/intact/search/search.html'"/>  -->
    <!-- <xsl:param name="helpLink" select="'http://localhost:8080/search/search.html'"/>    -->
    <xsl:param name="searchParams"/> <!-- list of strings identifying search details -->

    <xsl:output method="xml" version="1.0" indent="yes"
        doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>
    <!--
    ****************************************************************************
    ** Top level template. Creates the framework for the XHTML page.
    *************************************************************************-->
    <xsl:template match="/">

        <!-- Styles for various rows; change them to suit you. -->
        <style>
            <xsl:comment>
                tr.Experiment {background-color: rgb(255, 255, 102)}
                tr.Interaction {background-color: rgb(255, 255, 204)}
                tr.Protein {background-color: rgb(255, 255, 51)}
                tr.CvObject {background-color: rgb(255, 255, 102)}
            </xsl:comment>
        </style>

<!--        <table cellpadding="2" cellspacing="2" border="1" style="text-align: left; width: 100%;">-->
<!--            <xsl:call-template name="draw-table-headings"/>-->
<!--            <xsl:apply-templates/>-->
<!--        </table>-->
        <xsl:apply-templates/>
    </xsl:template>

    <!--
        ****************************************************************************
        ** Template to match the top level (searched-for) Protein
        *************************************************************************-->
        <xsl:template match="intact-root/Protein">

            <br>Interaction partners of <b><i><xsl:value-of select="@shortLabel"/></i></b>:</br>
            <br></br>
            <table cellpadding="2" cellspacing="2" border="1" style="text-align: left; width: 100%;">
              <!--  <xsl:call-template name="draw-table-headings"/> -->
                <!-- <xsl:apply-templates/> -->

                <!-- do the main first row -->
                 <xsl:call-template name="draw_protein_row">
                    <xsl:with-param name="view_type" select="'main'"/>
                 </xsl:call-template>

                <!-- now do rows for all the partners... -->
                <xsl:for-each select="partners/Protein">
                    <!-- sort alphabetically by short label? -->
                    <xsl:sort select="@shortLabel"></xsl:sort>
                    <xsl:call-template name="draw_protein_row">
                        <xsl:with-param name="view_type" select="'partner'"/>
                     </xsl:call-template>
                 </xsl:for-each>

            </table>
        </xsl:template>

    <!--
        ****************************************************************************
        ** Template to draw a row of Protein data.
        ** NB This is very poor - the tests for main and partner are in each
        ** cell. This needs to be better organised - done like this now just
        ** to get things going. Will fix up later...
        *************************************************************************-->
        <xsl:template name="draw_protein_row">
            <xsl:param name="view_type"/>
            <tr class="Protein">
                <xsl:call-template name="draw-checkbox"/>
                <td>
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'AnnotatedObject.ShortLabel'"/>
                    </xsl:call-template>

                    <xsl:if test="$view_type='main'">
                        <!-- highlight the main search result in bold -->
                    <b>
                        <i>
                        <xsl:call-template name="draw_label_link">
                            <xsl:with-param name="label" select="@shortLabel"/>
                            <xsl:with-param name="type" select="'Protein'"/>
                        </xsl:call-template>
                        </i>
                    </b>
                    </xsl:if>

                    <xsl:if test="$view_type='partner'">
                        <xsl:call-template name="draw_label_link">
                            <xsl:with-param name="label" select="@shortLabel"/>
                            <xsl:with-param name="type" select="'Protein'"/>
                        </xsl:call-template>
                    </xsl:if>
                </td>

                <td>
                    <!-- this should be the protein description cell - assume it
                         is the FullName for now, but may later be Annotation info.. -->
                    <xsl:if test="$view_type='main'">
                        <!-- highlight the main search result in bold -->
                        <b>
                            <xsl:call-template name="draw_help_link">
                            <xsl:with-param name="item" select="'AnnotatedObject.FullName'"/>
                            </xsl:call-template>
                            <xsl:value-of select="@fullName"/>
                        </b>
                    </xsl:if>
                    <xsl:if test="$view_type='partner'">
                        <!-- no highlighting -->
                        <xsl:call-template name="draw_help_link">
                        <xsl:with-param name="item" select="'AnnotatedObject.FullName'"/>
                        </xsl:call-template>
                        <xsl:value-of select="@fullName"/>
                    </xsl:if>
                </td>
                <td>
                    <!-- a link to all the experiments containing this protein, OR
                    for a partner row a list of Experiments containing both this
                    Protein and the main one.....
                   -->
                    <xsl:if test="$view_type='main'">
                        <!-- highlight the main search result in bold -->
                        <b>
                            <xsl:call-template name="draw_exp_link">
                                <xsl:with-param name="listItems" select="@beanList"/>
                            </xsl:call-template>
                        </b>
                    </xsl:if>
                    <xsl:if test="$view_type='partner'">
                        <!-- no highlighting -->
                        <xsl:call-template name="draw_exp_link">
                            <xsl:with-param name="listItems" select="@beanList"/>
                        </xsl:call-template>
                    </xsl:if>
                </td>
                <td>
                    <!-- a link to (simple, no class) search with this shortLabel as
                         a string -->
                    <xsl:if test="$view_type='main'">
                        <!-- highlight the main search result in bold -->
                        <b>
                            <xsl:call-template name="draw_simple_search_link">
                                <xsl:with-param name="ac" select="@shortLabel"/>
                            </xsl:call-template>
                        </b>
                    </xsl:if>
                    <xsl:if test="$view_type='partner'">
                        <!-- no highlighting -->
                        <xsl:call-template name="draw_simple_search_link">
                            <xsl:with-param name="ac" select="@shortLabel"/>
                        </xsl:call-template>
                    </xsl:if>
                </td>

            </tr>
        </xsl:template>

    <!--
    ****************************************************************************
    ** Template for fully specified (ie String plus class) search link
    *************************************************************************-->
    <xsl:template name="draw_link">
        <xsl:param name="ac"/>
        <xsl:param name="type"/>
        <xsl:variable name="link">
            <xsl:value-of select="concat($searchLink, $ac, '&amp;', 'searchClass=', $type)"/>
        </xsl:variable>
        <td>
            <xsl:call-template name="draw_help_link">
            <xsl:with-param name="item" select="'BasicObject.ac'"/>
            </xsl:call-template>

            <a href="{$link}"><xsl:value-of select="$ac"/></a>
        </td>
    </xsl:template>

    <!--
        ****************************************************************************
        ** Template for 'standard' (ie String only) search link
        *************************************************************************-->
        <xsl:template name="draw_simple_search_link">
            <xsl:param name="ac"/>
            <xsl:variable name="link">
                <xsl:value-of select="concat($searchLink, $ac)"/>
            </xsl:variable>
            <a href="{$link}">Query with <xsl:value-of select="$ac"/></a>
        </xsl:template>

       <!--
        ****************************************************************************
        ** Template for drawing an Experiment link
        *************************************************************************-->
        <xsl:template name="draw_exp_link">
            <xsl:param name="listItems"/>
            <xsl:variable name="link">
                <xsl:value-of select="concat($experimentLink, $listItems)"/>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="contains($listItems, ',')">
                    <!-- have more than one Experiment - count them.. -->

                        <xsl:call-template name="do_multi_exp_link">
                            <xsl:with-param name="counter" select="1"/>
                            <xsl:with-param name="refLink" select="$link"/>
                            <xsl:with-param name="subString" select="$listItems"/>
                    </xsl:call-template>

                <!--   <a href="{$link}">View <xsl:value-of select="$count"/> Experiments</a> -->
                </xsl:when>
                <xsl:otherwise>
                    <a href="{$link}">View 1 Experiment</a>
                </xsl:otherwise>
            </xsl:choose>

        </xsl:template>


    <!--
    ****************************************************************************
    ** Template for drawing a help page link.
    *************************************************************************-->
    <xsl:template name="draw_help_link">
        <xsl:param name="item"/>
        <xsl:variable name="link">
            <xsl:value-of select="concat($helpLink, $item)"/>
            <!-- try to embed the local target in the href, AND use a target for a new window -->
        </xsl:variable>
            <a href="{$link}" target="new">[?]</a>
    </xsl:template>

    <!--
    ****************************************************************************
    ** template for turning a short label into a link.
    *************************************************************************-->
    <xsl:template name="draw_label_link">
        <xsl:param name="label"/>
        <xsl:param name="type"/>
        <xsl:variable name="link">
            <xsl:value-of select="concat($searchLink, $label, '&amp;', 'searchClass=', $type)"/>
        </xsl:variable>

        <xsl:choose>
                <xsl:when test="contains($searchParams, $label)">
                   <a href="{$link}"><b><i><xsl:value-of select="$label"/></i></b></a>
                </xsl:when>

                <xsl:otherwise>
                <a href="{$link}"><xsl:value-of select="$label"/></a>
                </xsl:otherwise>
            </xsl:choose>

    </xsl:template>

    <!--
    ****************************************************************************
    ** Template to draw a checkbox.
    *************************************************************************-->
    <xsl:template name="draw-checkbox">
        <!-- Checkbox name; table name + _ + ac -->
        <xsl:variable name="cbName">
            <xsl:variable name="ac">
                <xsl:value-of select="@ac"/>
            </xsl:variable>
            <xsl:value-of select="concat($tableName, '_', $ac)"/>
        </xsl:variable>
        <td>
            <input name="{$cbName}" type="checkbox"></input>
        </td>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Template to draw a checkbox.
    *************************************************************************-->
    <xsl:template name="draw-checkbox-no-td">
        <!-- Checkbox name; table name + _ + ac -->
        <xsl:variable name="cbName">
            <xsl:variable name="ac">
                <xsl:value-of select="@ac"/>
            </xsl:variable>
            <xsl:value-of select="concat($tableName, '_', $ac)"/>
        </xsl:variable>
        <input name="{$cbName}" type="checkbox"></input>
    </xsl:template>

    <!--
    ******************************************************************
    ** template to draw an Experiment link when there is more than one
    ** Experiment. Need this because we have to count the commas in a String,
    ** and you can't do this in XSLT as there is no string function for it and
    ** you can't change variable values either.
    ** Isn't XSLT wonderful? I think NOT!
    *******************************************************************!
 -->
    <xsl:template name="do_multi_exp_link">
            <xsl:param name="counter" select="1"/>
            <xsl:param name="refLink"/>
            <xsl:param name="subString"/>
            <xsl:choose>
                <xsl:when test="contains($subString, ',')">
                    <xsl:call-template name="do_multi_exp_link">
                        <xsl:with-param name="counter" select="$counter+1"/>
                        <xsl:with-param name="refLink" select="$refLink"/>
                        <xsl:with-param name="subString" select="substring-after($subString, ',')"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <!-- need to return the value of counter somehow -->
                    <a href="{$refLink}">View <xsl:value-of select="$counter"/> Experiments</a>
                </xsl:otherwise>
            </xsl:choose>

        </xsl:template>

    <!--
    ****************************************************************************
    ** Draw table dimensions
    *************************************************************************-->
    <xsl:template name="draw-table-headings">
        <tr>
            <td width="2%"></td>
            <td width="20%"></td>
            <td width="30%"></td>
            <td width="5%"></td>
            <td width="43%"></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
