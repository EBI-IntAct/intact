<?xml version="1.0"?>

<!--
    Basic initial XSL stylesheet for formatting intact objects to display.
    This file will be modified to provide better display options as the project
    progresses.

    author: Chris Lewington November 2002
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
    <xsl:apply-templates/>
</xsl:template>

<!-- template for a protein, attribute match -->
<xsl:template name="proteinAtt" match="protein">

    <!-- main label plus attributes to display -->
    <xsl:text>[Protein: </xsl:text>
    <xsl:text>AC: </xsl:text>
    <xsl:value-of select="@ac"/>
    <xsl:text>, Label: </xsl:text>
    <xsl:value-of select="@shortLabel"/>

    <!-- reference objects to display details for -->
    <xsl:apply-templates select="owner"/><br/>
    <xsl:apply-templates select="xref"/><br/>

    <xsl:text> /Protein]</xsl:text><br/>

</xsl:template>

<!-- template for owner attribute -->
<xsl:template name="institutionAtt" match="owner">
    <xsl:text>, Owner: </xsl:text>
    <xsl:value-of select="@shortLabel"/>
</xsl:template>

<!-- template for xref attribute -->
<xsl:template name="xrefAtt" match="xref">

    <xsl:text>[Xref: </xsl:text>

    <!-- attributes to display -->
    <xsl:text>AC: </xsl:text>
    <xsl:value-of select="@ac"/>
    <xsl:text>, Label: </xsl:text>
    <xsl:value-of select="@shortLabel"/>
    <xsl:apply-templates select="owner"/>
    <xsl:text>, Primary ID: </xsl:text>
    <xsl:value-of select="@primaryId"/>

    <xsl:text> /Xref]</xsl:text><br/>
</xsl:template>

<!-- template for experiment attribute -->
<xsl:template name="experimentAtt" match="experiment">

    <xsl:text>[Experiment: </xsl:text>

    <!-- attributes to display -->
    <xsl:text>AC: </xsl:text>
    <xsl:value-of select="@ac"/>
    <xsl:text>, Label: </xsl:text>
    <xsl:value-of select="@shortLabel"/>

    <!-- references to display -->
    <xsl:apply-templates select="owner"/><br/>
    <xsl:apply-templates select="xref"/><br/>
    <xsl:apply-templates select="interaction"/><br/>

    <xsl:text> /Experiment]</xsl:text><br/>

</xsl:template>

<!-- template for interaction attribute -->
<xsl:template name="interactionAtt" match="interaction">

    <xsl:text>[Interaction: </xsl:text>
    <xsl:text>AC: </xsl:text>

    <!-- attributes to display -->
    <xsl:value-of select="@ac"/>
    <xsl:text>, Label: </xsl:text>
    <xsl:value-of select="@shortLabel"/>

    <!-- references to display -->
    <xsl:apply-templates select="owner"/>
    <xsl:apply-templates select="xref"/>
    <xsl:apply-templates select="component"/>

    <xsl:text> /Interaction]</xsl:text><br/>

</xsl:template>

<!-- template to match a component. NB attributes are not
normally displayed for this object as it is basically a container
for interaction/interactor pairs
-->
<xsl:template match="component">

    <!-- references to display -->
    <xsl:apply-templates select="interaction"/><br/>
    <xsl:apply-templates select="interactor"/><br/>

</xsl:template>

<!-- template for an interactor attribute -->
<xsl:template match="interactor">

    <xsl:text>[Interactor: </xsl:text>
    <xsl:text>AC: </xsl:text>

    <!-- attributes to display -->
    <xsl:value-of select="@ac"/>
    <xsl:text>, Label: </xsl:text>
    <xsl:value-of select="@shortLabel"/>

    <!-- references to display -->
    <xsl:apply-templates select="owner"/><br/>
    <xsl:apply-templates select="xref"/><br/>

    <xsl:text> /Interactor]</xsl:text>

</xsl:template>

<!-- ******************* class templates ******* -->

<!-- NB these currently delegate to the attribute templates, and
they are present to allow formatting of individual classes rather than just as attributes
of others. Typically there should be one template for each class that is searchable
-->

<xsl:template match="Protein">
    <xsl:call-template name="proteinAtt"/>
</xsl:template>

<xsl:template match="Experiment">
    <xsl:call-template name="experimentAtt"/>
</xsl:template>

<xsl:template match="Institution">
    <xsl:call-template name="institutionAtt"/>
</xsl:template>

<xsl:template match="Interaction">
    <xsl:call-template name="interactionAtt"/>
</xsl:template>

</xsl:stylesheet>