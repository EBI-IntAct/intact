<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<html>

<f:view>
    <head>
        <title>PSI Validator</title>
        <t:stylesheet path="css/basic.css"/>
    </head>

    <body>

    <h:form id="psiValidationForm" enctype="multipart/form-data">

        <h:panelGrid columns="1" style="width:100%">

            <%-- Header --%>
            <h:panelGrid id="header" columns="3"
                         style="width:100%"
                         columnClasses="align-left,page-title,align-right">
                <h:outputLink value="http://psidev.sourceforge.net/">
                    <h:graphicImage url="images/psi.gif" style="border:0"/>
                </h:outputLink>

                <h:outputText value="The PSI Validator"/>

                <h:outputLink value="http://hupo.org/">
                    <h:graphicImage url="images/hupo.gif" style="border:0"/>
                </h:outputLink>
            </h:panelGrid>

            <%-- Description --%>
            <h:panelGroup>
                <h:outputText value="The PSI Validator validate files in "/>
                <h:outputLink value="http://psidev.sourceforge.net/mi/rel25/" target="_blank">
                    <h:outputText value="PSI MI 2.5"/>
                </h:outputLink>
                <h:outputText value=" XML format."/>
            </h:panelGroup>

            <%-- File upload --%>
            <h:outputText value="Upload file:" styleClass="secion-title"/>

            <h:selectOneRadio value="local"
                              valueChangeListener="#{psiValidatorBean.uploadTypeChanged}"
                              onchange="submit()">
                <f:selectItem itemLabel="From local file" itemValue="local"/>
                <f:selectItem itemLabel="From URL" itemValue="url"/>
            </h:selectOneRadio>

            <h:panelGrid columns="2" rendered="#{psiValidatorBean.uploadLocalFile}">
                <h:outputLabel for="inputFile" value="File:"/>
                <t:inputFileUpload id="inputFile"
                        storage="file"
                        value="#{psiValidatorBean.psiFile}"/>
            </h:panelGrid>

            <h:panelGrid columns="2" rendered="#{!psiValidatorBean.uploadLocalFile}">
                <h:outputLabel for="inputUrl" value="Url"/>
                <h:inputText id="inputUrl" size="60" value="#{psiValidatorBean.psiUrl}"/>
            </h:panelGrid>

            <h:commandButton value="Upload" actionListener="#{psiValidatorBean.uploadFile}"/>

            <%-- Uploaded file report table --%>
            <h:panelGrid columns="3"
                         rendered="#{psiValidatorBean.currentPsiReport != null}"
                         columnClasses="info-header,none,none">

                <h:outputText value="Filename"/>
                <h:outputText value="#{psiValidatorBean.currentPsiReport.name}"/>
                <h:outputText value=""/>

                <h:outputText value="XML Syntax"/>
                <h:outputText value="#{psiValidatorBean.currentPsiReport.xmlSyntaxStatus}"/>

                <h:panelGrid columns="1">
                    <h:outputLink value="#xml_report">
                        <h:outputText value="Report"/>
                    </h:outputLink>
                    <h:outputLink value="#html_view" rendered="#{psiValidatorBean.currentPsiReport.htmlView != null}">
                        <h:outputText value="HTML View"/>
                    </h:outputLink>
                </h:panelGrid>

                <h:outputText value="PSI MI 2.5 semantics"/>
                <h:outputText value="#{psiValidatorBean.currentPsiReport.semanticsStatus}"/>
                <h:outputLink value="#semantics_report">
                    <h:outputText value="Report"/>
                </h:outputLink>

            </h:panelGrid>

        </h:panelGrid>

        <h:panelGrid columns="1" rendered="#{psiValidatorBean.currentPsiReport != null}">
            <h:outputText value="<a name=\"xml_report\"/>" escape="false"/>
            <h:outputText value="XML Syntax Validation Report" styleClass="section-header"/>
            <h:outputText value="#{psiValidatorBean.currentPsiReport.xmlSyntaxReport}"
                          escape="false"/>

        </h:panelGrid>

        <h:panelGrid columns="1" rendered="#{psiValidatorBean.currentPsiReport != null}">
            <h:outputText value="<a name=\"semantics_report\"/>" escape="false"/>
            <h:outputText value="PSI MI 2.5 semantic validation report" styleClass="section-header"/>
            <h:outputText value="#{psiValidatorBean.currentPsiReport.semanticsReport}"
                          escape="false"/>

        </h:panelGrid>

        <h:panelGrid columns="1" rendered="#{psiValidatorBean.currentPsiReport != null && psiValidatorBean.currentPsiReport.xmlSyntaxStatus == 'valid'}">
            <h:outputText value="<a name=\"html_view\"/>" escape="false"/>
            <h:outputText value="HTML View" styleClass="section-header"/>
            <h:outputText value="#{psiValidatorBean.currentPsiReport.htmlView}"
                          escape="false"/>

        </h:panelGrid>

    </h:form>

    </body>

</f:view>

</html>
