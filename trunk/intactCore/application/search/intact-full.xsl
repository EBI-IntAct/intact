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
        Defaults to zero if none was specified. Note the single quotes
        around the value. -->
    <xsl:param name="tableName" select="'tbl_0'"/>
    <xsl:param name="searchLink"/>
    <xsl:param name="helpLink"/>
    <!-- <xsl:param name="helpLink" select="'http://web7-node1.ebi.ac.uk:8160/intact/search/search.html'"/> -->
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

        <table cellpadding="1" cellspacing="0" border="1" width="100%">
            <xsl:call-template name="draw-table-headings"/>
            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <!--
    ****************************************************************************
    ** Template for experiment.
    *************************************************************************-->
    <xsl:template match="Experiment">
        <tr class="Experiment">
            <td colspan="2">
                <b>Experiment</b>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'BasicObject.ac'"/>
                </xsl:call-template>

                <xsl:value-of select="@ac"/>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.ShortLabel'"/>
                </xsl:call-template>

                <xsl:call-template name="draw_short_label">
                <xsl:with-param name="label" select="@shortLabel"/>
                </xsl:call-template>
            </td>

            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.FullName'"/>
                </xsl:call-template>

                <xsl:value-of select="@fullName"/>
            </td>
        </tr>

      <!--  <xsl:if test="CvIdentification/@shortLabel | CvInteraction/@shortLabel | bioSource">  -->
            <tr class="Experiment">
                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'CvObject'"/>
                    </xsl:call-template>

                    <xsl:if test="CvInteraction/@shortLabel">
                        <xsl:call-template name="draw_cv_link">
                            <xsl:with-param name="cv" select="CvInteraction/@shortLabel"/>
                            <xsl:with-param name="type" select="'CvInteraction'"/>
                        </xsl:call-template>
                    </xsl:if>
               </td>
                <td>
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'CvObject'"/>
                    </xsl:call-template>

                    <xsl:if test="CvIdentification/@shortLabel">
                        <xsl:call-template name="draw_cv_link">
                            <xsl:with-param name="cv" select="CvIdentification/@shortLabel"/>
                            <xsl:with-param name="type" select="'CvIdentification'"/>
                        </xsl:call-template>
                    </xsl:if>
                </td>

                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'BioSource'"/>
                    </xsl:call-template>

                    <xsl:if test="BioSource">
                        <xsl:value-of select="BioSource/@shortLabel"/>
                    </xsl:if>
                </td>
            </tr>
      <!--  </xsl:if> -->

        <xsl:for-each select="annotations/Annotation">
            <tr class="Experiment">
                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'CvObject'"/>
                    </xsl:call-template>

                    <xsl:call-template name="draw_cv_link">
                        <xsl:with-param name="cv" select="CvTopic/@shortLabel"/>
                        <xsl:with-param name="type" select="'CvTopic'"/>
                    </xsl:call-template>
                </td>

                <td colspan="3">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'Annotation'"/>
                    </xsl:call-template>

                    <xsl:value-of select="@annotationText"/>
                </td>
            </tr>
        </xsl:for-each>

        <xsl:for-each select="xrefs/Xref">
            <tr class="Experiment">
                <xsl:call-template name="draw-xrefs"/>
            </tr>
        </xsl:for-each>

        <xsl:if test="interactions/Interaction">
            <xsl:apply-templates select="interactions/Interaction"/>
        </xsl:if>
    </xsl:template>

    <!-- =================================================================== -->

    <!--
    ****************************************************************************
    ** Template for interaction attribute.
    *************************************************************************-->
    <xsl:template name="Interaction" match="Experiment/interactions/Interaction">

        <tr class="Interaction">
            <xsl:call-template name="draw-checkbox"/>
            <td><b>Interaction</b></td>
            <xsl:call-template name="draw_link">
                <xsl:with-param name="ac" select="@ac"/>
                <xsl:with-param name="type" select="'Interaction'"/>
            </xsl:call-template>

            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.ShortLabel'"/>
                </xsl:call-template>

                <xsl:call-template name="draw_short_label">
                <xsl:with-param name="label" select="@shortLabel"/>
                </xsl:call-template>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.FullName'"/>
                </xsl:call-template>

                <xsl:value-of select="@fullName"/>
            </td>
        </tr>

        <xsl:if test="@status='expand'">
            <xsl:if test="CvInteractionType/@shortLabel">
                <tr class="Interaction">
                    <td colspan="2">
                        <xsl:call-template name="draw_help_link">
                        <xsl:with-param name="item" select="'CvObject'"/>
                        </xsl:call-template>

                        <xsl:call-template name="draw_cv_link">
                            <xsl:with-param name="cv" select="CvInteractionType/@shortLabel"/>
                            <xsl:with-param name="type" select="'CvInteractionType'"/>
                        </xsl:call-template>
                    </td>
                    <td colspan="3">
                        <xsl:value-of select="Float/@value"/>
                    </td>
                </tr>
            </xsl:if>

            <xsl:for-each select="annotations/Annotation">
                <tr class="Interaction">
                    <td colspan="2">
                        <xsl:call-template name="draw_help_link">
                        <xsl:with-param name="item" select="'CvObject'"/>
                        </xsl:call-template>

                        <xsl:call-template name="draw_cv_link">
                            <xsl:with-param name="cv" select="CvTopic/@shortLabel"/>
                            <xsl:with-param name="type" select="'CvTopic'"/>
                        </xsl:call-template>
                    </td>
                    <td colspan="3">
                        <xsl:call-template name="draw_help_link">
                        <xsl:with-param name="item" select="'Annotation'"/>
                        </xsl:call-template>

                        <xsl:value-of select="@annotationText"/>
                    </td>
                </tr>
            </xsl:for-each>

            <xsl:for-each select="xrefs/Xref">
                <tr class="Interaction">
                    <xsl:call-template name="draw-xrefs"/>
                </tr>
            </xsl:for-each>
        </xsl:if>

        <!-- compact view - only show ShortLabel/role etc -->
        <xsl:if test="components/Component/Protein/@status='expand'">
            <xsl:apply-templates select="components/Component/Protein"/>
        </xsl:if>
        <xsl:if test="components/Component/Protein/@status='compact'">
            <tr class="Protein">
                <td colspan="5">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'search.ProteinList'"/>
                    </xsl:call-template>

                    <xsl:for-each select="components/Component/Protein">
                        <!-- sort Protein labels alphabetically -->
                        <xsl:sort select="@shortLabel"></xsl:sort>
                        <xsl:call-template name="draw-checkbox-no-td"/>
                        <xsl:variable name="title">
                            <xsl:value-of select="../CvComponentRole/@shortLabel"/>
                        </xsl:variable>

                        <!-- put link on the Protein short label -->
                       <xsl:call-template name="draw_label_link">
<!--                       <xsl:call-template name="draw_short_label"> -->
                            <xsl:with-param name="label" select="@shortLabel"/>
                            <xsl:with-param name="type" select="'Protein'"/>
                        </xsl:call-template>
                        <!-- Put in a [] tooltip for the CvRole, linked to the CvObject
                            NB can't use the cv_link template here as the link content is
                            different to the text displayed on screen -->
                        <acronym title="{$title}">[
                            <xsl:variable name="link">
                                <xsl:value-of select="concat($searchLink, ../CvComponentRole/@shortLabel, '&amp;', 'searchClass=CvComponentRole')"/>
                            </xsl:variable>
                            <a href="{$link}"><xsl:value-of select="substring(../CvComponentRole/@shortLabel, 1, 1)"/></a>
                            ]</acronym>
                        <!-- Avoid printing  ',' for the last protein -->
                        <xsl:if test="not(position()=last())">, </xsl:if>
                    </xsl:for-each>
                </td>
            </tr>
        </xsl:if>

    </xsl:template>

    <!--
    ****************************************************************************
    ** Template for interactor attribute.
    *************************************************************************-->
    <xsl:template match="components/Component/Protein">

        <tr class="Protein">
            <xsl:call-template name="draw-checkbox"/>
            <td>
                <b>Protein</b>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'BasicObject.ac'"/>
                </xsl:call-template>

                <xsl:value-of select="@ac"/>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.ShortLabel'"/>
                </xsl:call-template>

                <xsl:call-template name="draw_short_label">
                <xsl:with-param name="label" select="@shortLabel"/>
                </xsl:call-template>

                <xsl:variable name="title">
                   <xsl:value-of select="../CvComponentRole/@shortLabel"/>
                </xsl:variable>
                    <!--  can't use the cv_link template here as the link content is
                            different to the text displayed on screen -->
                <acronym title="{$title}">[
                    <xsl:variable name="link">
                        <xsl:value-of select="concat($searchLink, ../CvComponentRole/@shortLabel, '&amp;', 'searchClass=CvComponentRole')"/>
                    </xsl:variable>
                    <a href="{$link}"><xsl:value-of select="substring(../CvComponentRole/@shortLabel, 1, 1)"/></a>
                    ]</acronym>
            </td>

            <td colspan="2">
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.FullName'"/>
                </xsl:call-template>

                <xsl:value-of select="@fullName"/>
            </td>
        </tr>

        <xsl:if test="../CvComponentRole">
            <tr class="Protein">
                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'CvObject'"/>
                    </xsl:call-template>

                    <xsl:call-template name="draw_cv_link">
                        <xsl:with-param name="cv" select="../CvComponentRole/@shortLabel"/>
                        <xsl:with-param name="type" select="'CvComponentRole'"/>
                    </xsl:call-template>
                </td>
                <td>
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'Component.Stoichiometry'"/>
                    </xsl:call-template>

                    <xsl:value-of select="../@stoichiometry"/>
                </td>
                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'BioSource'"/>
                    </xsl:call-template>

                    <xsl:value-of select="../BioSource/@shortLabel"/>
                </td>
            </tr>
        </xsl:if>

        <xsl:for-each select="annotations/Annotation">
            <tr class="Protein">
                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'Annotation'"/>
                    </xsl:call-template>

                    <xsl:value-of select="CvTopic/@shortLabel"/>
                </td>
                <td colspan="3">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'Annotat6ion'"/>
                    </xsl:call-template>

                    <xsl:value-of select="@annotationText"/>
                </td>
            </tr>
        </xsl:for-each>

        <xsl:for-each select="xrefs/Xref">
            <tr class="Protein">
                <xsl:call-template name="draw-xrefs"/>
            </tr>
        </xsl:for-each>

    </xsl:template>

    <!--
        ****************************************************************************
        ** Template for CvObjects.
        *************************************************************************-->
    <xsl:template match="CvAliasType | CvCellCycle | CvCellType | CvCompartment |
    CvComponentRole | CvDagObject | CvDatabase | CvDevelopmentalStage | CvEvidenceType |
CvFeatureIdentification | CvFeatureType | CvIdentification | CvInteractionType |
CvJournal | CvModificationType | CvProductRole | CvProteinForm | CvReferenceQualifier |
CvTissue | CvTopic | CvXrefQualifier">

        <xsl:call-template name="cv_common"/>

    </xsl:template>

    <!--
        ****************************************************************************
        ** Template for Cv DAG objects (currently onlly CvInteraction extends
        the cvDagObject abstract class).
        *************************************************************************-->
    <xsl:template match="CvInteraction">

        <!-- do standard CvObject stuff first -->
        <xsl:call-template name="cv_common"/>

        <!-- now display the parent and child terms of the DAG object -->
        <tr>
            <td>Parent terms:</td>
            <td>
                <xsl:for-each select="child::parents">
                    <xsl:call-template name="draw_cv_link">
                        <xsl:with-param name="cv" select="@shortLabel"/>
                        <xsl:with-param name="type" select="'CvInteraction'"/>
                        <!-- need to also pass the parent's element tag name here... -->
                    </xsl:call-template>
                    <!-- Avoid printing  ',' for the last protein -->
                    <xsl:if test="not(position()=last())">, </xsl:if>
                </xsl:for-each>
            </td>
        </tr>
        <tr>
            <td>Child terms:</td>
            <!-- unfortunate that plural of child is children - not generated!! -->
            <td>
                <xsl:for-each select="child::childs">
                    <xsl:call-template name="draw_cv_link">
                        <xsl:with-param name="cv" select="@shortLabel"/>
                        <xsl:with-param name="type" select="'CvInteraction'"/>
                        <!-- need to also pass the child's element tag name here... -->
                    </xsl:call-template>
                    <!-- Avoid printing  ',' for the last protein -->
                    <xsl:if test="not(position()=last())">, </xsl:if>
                    <!-- Avoid printing  ',' for the last protein -->
                    <xsl:if test="not(position()=last())">, </xsl:if>
                </xsl:for-each>
            </td>
        </tr>

    </xsl:template>


    <!--
        ****************************************************************************
        ** Template for common CvObject data.
        *************************************************************************-->
    <xsl:template name="cv_common">
        <tr class="CvObject">
            <td colspan="2">
                <b>Controlled vocabulary term</b>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'BasicObject.ac'"/>
                </xsl:call-template>
                <xsl:value-of select="@ac"/>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.ShortLabel'"/>
                </xsl:call-template>

                <xsl:call-template name="draw_short_label">
                <xsl:with-param name="label" select="@shortLabel"/>
                </xsl:call-template>
            </td>

            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.FullName'"/>
                </xsl:call-template>

                <xsl:value-of select="@fullName"/>
            </td>
        </tr>

        <xsl:for-each select="annotations/Annotation">
            <tr class="Interaction">
                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'CvObject'"/>
                    </xsl:call-template>

                    <xsl:value-of select="CvTopic/@shortLabel"/>
                </td>

                <td colspan="3">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'Annotation'"/>
                    </xsl:call-template>

                    <xsl:value-of select="@annotationText"/>
                </td>
            </tr>
        </xsl:for-each>

        <xsl:for-each select="xrefs/Xref">
            <tr class="CvObject">
                <xsl:call-template name="draw-xrefs"/>
            </tr>
        </xsl:for-each>
    </xsl:template>

    <!--
        ****************************************************************************
        ** Template for single Protein display - same basically as CvObject,
        ** but kept seperate in case we want to display other things for Proteins later
        *************************************************************************-->
    <xsl:template match="intact-root/Protein">
        <!-- use a CvObject display format here -->
        <tr class="CvObject">
            <td colspan="2">
                <b>Protein</b>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'BasicObject.ac'"/>
                </xsl:call-template>

                <xsl:value-of select="@ac"/>
            </td>
            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.ShortLabel'"/>
                </xsl:call-template>

                <xsl:call-template name="draw_short_label">
                <xsl:with-param name="label" select="@shortLabel"/>
                </xsl:call-template>
            </td>

            <td>
                <xsl:call-template name="draw_help_link">
                <xsl:with-param name="item" select="'AnnotatedObject.FullName'"/>
                </xsl:call-template>

                <xsl:value-of select="@fullName"/>
            </td>
        </tr>

        <xsl:for-each select="annotations/Annotation">
            <tr class="Interaction">
                <td colspan="2">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'CvObject'"/>
                    </xsl:call-template>

                    <xsl:value-of select="CvTopic/@shortLabel"/>
                </td>

                <td colspan="3">
                    <xsl:call-template name="draw_help_link">
                    <xsl:with-param name="item" select="'Annotation'"/>
                    </xsl:call-template>

                    <xsl:value-of select="@annotationText"/>
                </td>
            </tr>
        </xsl:for-each>

        <xsl:for-each select="xrefs/Xref">
            <tr class="CvObject">
                <xsl:call-template name="draw-xrefs"/>
            </tr>
        </xsl:for-each>
    </xsl:template>


    <!--
    ****************************************************************************
    ** Template for AC attribute.
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
    ** Template for drawing a cv link (short label).
    *************************************************************************-->
    <xsl:template name="draw_cv_link">
        <xsl:param name="cv"/>
        <xsl:param name="type"/>
        <xsl:variable name="link">
            <xsl:value-of select="concat($searchLink, $cv, '&amp;', 'searchClass=', $type)"/>
        </xsl:variable>
            <a href="{$link}"><xsl:value-of select="$cv"/></a>
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
    ** Template for short label attribute.
    *************************************************************************-->
    <xsl:template match="@shortLabel">
        <td>
            <xsl:value-of select="."/>
        </td>
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
    ****************************************************************************
    ** Draw table dimensions
    *************************************************************************-->
    <xsl:template name="draw-table-headings">
        <tr>
            <td width="2%"></td>
            <td width="10%"></td>
            <td width="15%"></td>
            <td width="10%"></td>
            <td width="63%"></td>
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
    ** Draws one empty cell.
    *************************************************************************-->
    <xsl:template name="draw-1-empty-cell">
        <xsl:call-template name="draw-empty-cells">
            <xsl:with-param name="testValue" select="1"/>
        </xsl:call-template>
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
    *************************************************************************-->
    <xsl:template name="draw-xrefs">
        <td colspan="2">
            <xsl:call-template name="draw_help_link">
            <xsl:with-param name="item" select="'XrefDb'"/>
            </xsl:call-template>

            <xsl:call-template name="draw_cv_link">
                <xsl:with-param name="cv" select="CvDatabase/@shortLabel"/>
                <xsl:with-param name="type" select="'CvDatabase'"/>
            </xsl:call-template>
        </td>
        <td>
            <xsl:call-template name="draw_help_link">
            <xsl:with-param name="item" select="'XrefPrimaryId'"/>
            </xsl:call-template>

            <!-- now check all the CvDatabase annotations to get any relevant URLs
            which should be built into a primaryId link...
            NB for info, check the Annotation/CvTopic shortlabels for the Xref to find
            the 'search-url' one, then go back to the Annotation comment to get the template-->
            <xsl:choose>
                <xsl:when test="CvDatabase/annotations/Annotation/CvTopic/@shortLabel='search-url'">
                    <!-- got a template - need to build a link -->
                    <xsl:call-template name="draw_xref_link">
                <xsl:with-param name="label" select="@primaryId"/>
                <xsl:with-param name="link_template" select="CvDatabase/annotations/Annotation/@annotationText"/>
            </xsl:call-template>
                </xsl:when>

                <xsl:otherwise>
                    <xsl:value-of select="@primaryId"/>
                </xsl:otherwise>
            </xsl:choose>

        </td>
        <td>
            <xsl:call-template name="draw_help_link">
            <xsl:with-param name="item" select="'XrefSecondId'"/>
            </xsl:call-template>

            <xsl:value-of select="@secondaryId"/>
        </td>
        <td>
            <xsl:call-template name="draw_help_link">
            <xsl:with-param name="item" select="'DbRelease'"/>
            </xsl:call-template>

            <xsl:value-of select="@dbRelease"/>
        </td>
    </xsl:template>

    <!--
    ****************************************************************************
    ** template for drawing a short label - needs highlighting if searched for
    *************************************************************************-->
    <xsl:template name="draw_short_label">
        <xsl:param name="label"/>

        <!-- if the short label identifies a search-for item highlight it,
             otherwise just use the label itself -->
            <xsl:choose>
                <xsl:when test="contains($searchParams, $label)">
                   <b><i><xsl:value-of select="$label"/></i></b>
                </xsl:when>

                <xsl:otherwise>
                <xsl:value-of select="$label"/>
                </xsl:otherwise>
            </xsl:choose>

    </xsl:template>

    <!--
    ****************************************************************************
    ** Template for drawing an Xref PrimaryID link
    *************************************************************************-->
    <xsl:template name="draw_xref_link">
        <xsl:param name="label"/>
        <xsl:param name="link_template"/>
        <xsl:variable name="link">
            <!-- need here to replace the '$(ac)' with the label... -->
            <xsl:call-template name="replace_substring">
                <xsl:with-param name="text" select="$link_template"/>
                <xsl:with-param name="replace" select="'$(ac)'"/>
                <xsl:with-param name="with" select="$label"/>
            </xsl:call-template>
        </xsl:variable>
            <a href="{$link}" target="new">[?]</a>
    </xsl:template>

     <!--
    ****************************************************************************
    ** Template to replace a substring with another in some text (copied from sourceforge)
    *************************************************************************-->
    <xsl:template name="replace_substring">
        <xsl:param name="text"/>
        <xsl:param name="replace"/>
        <xsl:param name="with"/>

        <xsl:choose>
          <xsl:when test="string-length($replace) = 0">
            <xsl:value-of select="$text"/>
          </xsl:when>
          <xsl:when test="contains($text, $replace)">

        <xsl:variable name="before" select="substring-before($text, $replace)"/>
        <xsl:variable name="after" select="substring-after($text, $replace)"/>

        <xsl:value-of select="$before"/>
        <xsl:value-of select="$with"/>
            <xsl:call-template name="replace_substring">
          <xsl:with-param name="text" select="$after"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="with" select="$with"/>
        </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$text"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:template>


</xsl:stylesheet>
