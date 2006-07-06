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

    <f:loadBundle basename="uk.ac.ebi.imex.psivalidator.resource.buildInfo" var="buildInfo"/>

    <h:form id="psiValidationForm" enctype="multipart/form-data">

        <h:panelGrid columns="1" style="width:100%">

            <%-- Header --%>
            <h:panelGrid id="header" columns="3"
                         style="width:100%"
                         columnClasses="align-left,align-center,align-right">
                <h:outputLink value="http://psidev.sourceforge.net/">
                    <h:graphicImage url="images/psi.gif" style="border:0"/>
                </h:outputLink>

                <t:div>
                    <h:outputText value="The PSI Validator" styleClass="page-title"/>
                    <h:outputText value=" (v. #{buildInfo.version})" styleClass="page-subtitle"/>
                </t:div>


                <h:outputLink value="http://hupo.org/">
                    <h:graphicImage url="images/hupo.gif" style="border:0"/>
                </h:outputLink>
            </h:panelGrid>

            <%-- Description --%>
            <t:div styleClass="section-header">
                <h:outputText value="Overview" />
            </t:div>

            <h:panelGroup>
                <h:outputText value="The PSI Validator validate files in "/>
                <h:outputLink value="http://psidev.sourceforge.net/mi/rel25/" target="_blank">
                    <h:outputText value="PSI MI 2.5"/>
                </h:outputLink>
                <h:outputText value=" XML format."/>
            </h:panelGroup>

            <%-- File upload --%>
            <t:div styleClass="section-header">
                <h:outputText value="Upload file"/>
             </t:div>

            <t:div>
                <h:outputLabel for="sourceSelector" value="Source: "/>
                <h:selectOneRadio id="sourceSelector" value="local" layout="pageDirection"
                                  valueChangeListener="#{psiValidatorBean.uploadTypeChanged}"
                                  onchange="submit()">
                    <f:selectItem itemLabel="From local file" itemValue="local"/>
                    <f:selectItem itemLabel="From URL" itemValue="url"/>
                </h:selectOneRadio>
            </t:div>

            <h:panelGrid columns="2"
                         rendered="#{psiValidatorBean.uploadLocalFile}">
                <h:outputLabel for="inputFile" value="File: " />
                <h:panelGroup>
                    <t:inputFileUpload id="inputFile"
                        storage="file" size="80" required="true"
                        value="#{psiValidatorBean.psiFile}"/>
                    <t:message for="inputFile" styleClass="error-message" showDetail="true" showSummary="false"/>
                </h:panelGroup>
            </h:panelGrid>

            <h:panelGrid columns="2" rendered="#{!psiValidatorBean.uploadLocalFile}">
                <h:outputLabel for="inputUrl" value="Url: "/>

                <h:panelGroup>
                    <h:inputText id="inputUrl" size="80" value="#{psiValidatorBean.psiUrl}"
                                 required="true" validator="#{psiValidatorBean.validateUrlFormat}"/>
                    <t:message for="inputUrl" styleClass="error-message" showDetail="true" showSummary="false"/>
                </h:panelGroup>
            </h:panelGrid>

            <h:commandButton value="Upload" actionListener="#{psiValidatorBean.uploadFile}"/>

            <%-- Uploaded file report table --%>
            <t:div styleClass="section-header" rendered="#{psiValidatorBean.currentPsiReport != null}">
                <h:outputText value="Validation summary"/>
             </t:div>

            <h:panelGrid columns="4" cellspacing="1"
                         rendered="#{psiValidatorBean.currentPsiReport != null}"
                         columnClasses="column-status,column-title,column-value,column-value">

                <h:outputText value=""/>
                <h:outputText value="Filename"/>
                <h:outputText value="#{psiValidatorBean.currentPsiReport.name}"/>
                <h:outputText value=""/>

                <h:graphicImage url="images/icon_success_sml.gif"
                                rendered="#{psiValidatorBean.currentPsiReport.xmlSyntaxStatus == 'valid'}"/>
                <h:graphicImage url="images/icon_error_sml.gif"
                                rendered="#{psiValidatorBean.currentPsiReport.xmlSyntaxStatus == 'invalid'}"/>
                <h:outputText value="XML Syntax"/>
                <h:outputText value="#{psiValidatorBean.currentPsiReport.xmlSyntaxStatus}"/>
                <h:panelGrid columns="2" cellpadding="0" cellspacing="0">
                    <h:outputLink value="#xml_report">
                        <h:outputText value="Report"/>
                    </h:outputLink>
                    <h:outputLink value="#html_view" rendered="#{psiValidatorBean.currentPsiReport.htmlView != null}">
                        <h:outputText value="HTML View"/>
                    </h:outputLink>
                </h:panelGrid>

                <h:graphicImage url="images/icon_success_sml.gif"
                                rendered="#{psiValidatorBean.currentPsiReport.semanticsStatus == 'valid'}"/>
                <h:graphicImage url="images/icon_warning_sml.gif"
                                rendered="#{psiValidatorBean.currentPsiReport.semanticsStatus == 'warnings'}"/>
                <h:graphicImage url="images/icon_error_sml.gif"
                                rendered="#{psiValidatorBean.currentPsiReport.semanticsStatus == 'invalid'}"/>
                <h:outputText value=" " rendered="#{psiValidatorBean.currentPsiReport.xmlSyntaxStatus != 'valid'}"/>
                <h:outputText value="PSI MI 2.5 semantics"/>
                <h:outputText value="#{psiValidatorBean.currentPsiReport.semanticsStatus}"/>
                <h:outputLink value="#semantics_report" rendered="#{psiValidatorBean.currentPsiReport.semanticsReport != null}">
                    <h:outputText value="Report"/>
                </h:outputLink>

            </h:panelGrid>

        </h:panelGrid>

        <%-- XML Syntax Validation Report --%>
        <h:panelGrid columns="1" rendered="#{psiValidatorBean.currentPsiReport != null}">
            <h:outputText value="<a name=\"xml_report\"/>" escape="false"/>
            <t:div styleClass="section-header">
                <h:outputText value="XML Syntax Validation Report" />
            </t:div>

            <h:outputText value="#{psiValidatorBean.currentPsiReport.xmlSyntaxReport}"
                          styleClass="report"/>

        </h:panelGrid>

        <%-- Semantic Validation Report --%>
        <h:panelGrid columns="1" rendered="#{psiValidatorBean.currentPsiReport.semanticsReport != null}">
            <h:outputText value="<a name=\"semantics_report\"/>" escape="false"/>
            <t:div styleClass="section-header">
                <h:outputText value="PSI MI 2.5 semantic validation report" />
            </t:div>
            <h:outputText value="#{psiValidatorBean.currentPsiReport.semanticsReport}"
                          styleClass="report"/>

            <t:dataTable var="msg" value="#{psiValidatorBean.currentPsiReport.validatorMessages}"
                         cellspacing="1"
                         headerClass="table-header"
                         rowClasses="odd-row,even-row"
                         renderedIfEmpty="false" >
                <t:column style="width: 1%, text-align:center, padding-left:2px, padding-right:2px">
                    <h:graphicImage url="images/icon_warning_sml.gif" rendered="#{msg.level == 'WARN'}" title="#{msg.level}"/>
                    <h:graphicImage url="images/icon_error_sml.gif" rendered="#{msg.level == 'ERROR'}" title="#{msg.level}"/>
                    <h:graphicImage url="images/icon_error_sml.gif" rendered="#{msg.level == 'FATAL'}" title="#{msg.level}"/>
                </t:column>
                <t:column style="width: 20%">
                    <f:facet name="header">
                        <h:outputText value="Message" />
                    </f:facet>

                    <h:outputText value="#{msg.message}" />
               </t:column>
                <t:column style="width: 20%, text-align:center">
                    <f:facet name="header">
                        <h:outputText value="Context" />
                    </f:facet>

                    <h:outputText value="#{msg.context}" />
               </t:column>
                <t:column style="width: 10%">
                    <f:facet name="header">
                        <h:outputText value="Rule Name" />
                    </f:facet>

                    <h:outputText value="#{msg.rule.name}" />
                </t:column>
                 <t:column style="width: 25%">
                    <f:facet name="header">
                        <h:outputText value="Rule Description" />
                    </f:facet>

                    <h:outputText value="#{msg.rule.description}" />
                </t:column>
                 <t:column style="width: 29%">
                    <f:facet name="header">
                        <h:outputText value="Fix tips" />
                    </f:facet>

                    <h:outputText value="#{msg.rule.howToFixTips}" />
                </t:column>


            </t:dataTable>

        </h:panelGrid>

        <%-- HTML View --%>
        <h:panelGrid columns="1" rendered="#{psiValidatorBean.currentPsiReport != null && psiValidatorBean.currentPsiReport.xmlSyntaxStatus == 'valid'}">
            <h:outputText value="<a name=\"html_view\"/>" escape="false"/>
            <t:div styleClass="section-header">
                <h:outputText value="HTML View"/>
            </t:div>
            <h:outputText value="#{psiValidatorBean.currentPsiReport.htmlView}"
                          escape="false"/>

        </h:panelGrid>

    </h:form>

    </body>

</f:view>

</html>
