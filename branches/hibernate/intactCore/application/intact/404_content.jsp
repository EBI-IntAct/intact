<%
    // Generate here the URL of the web application we are running.
    // That code should work wherever you install IntAct.

    // eg. http://localhost:8080/intact/inda/wrongLink.asp
    StringBuffer requestUrl = request.getRequestURL();

    // that would be /intact
    String applicationPath = request.getContextPath();

    int index = requestUrl.indexOf( applicationPath ) + applicationPath.length();
    String webSiteUrl = requestUrl.substring( 0, index );

    // here we should have webSiteUrl = "http://localhost:8080/intact"

    // just in case something goes wrong ... that should not happen though
    if( webSiteUrl == null || "".equals( webSiteUrl.trim() ) ) {
        webSiteUrl = "Click here !";
    }
%>


    <table border="0" cellpadding="0" cellspacing="0" width="434">
      <tbody>
        <tr>
          <td>
          <table border="0" cellpadding="4" cellspacing="0" width="434">
            <tbody>
              <tr>
                <td class="tablegreen" align="center" nowrap="nowrap"><nobr>&nbsp;<span class="whitetitle">IntAct Project</span>&nbsp;</nobr></td>
                <td class="pagetitle" width="100%"><nobr>&nbsp;</nobr></td>
              </tr>
            </tbody>
          </table>

          <table border="0" cellpadding="0" cellspacing="0" width="434">
            <tbody>
              <tr>
                <td background="http://www.ebi.ac.uk/Groups/images/hor.gif" height="3"><img src="http://www.ebi.ac.uk/Groups/images/trans.gif" height="3" width="25"></td>
              </tr>
            </tbody>
          </table>

          <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tbody>
              <tr>
                <td>

                <div align="center">
                   <span class ="plargerbold">An error has occurred when dealing with your request:</span><br>

                   <span class ="plargebold">404:File Not Found</span>
                     <table width="250" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                           <td>
                                <span class ="text">
                                    <br>
                                    <br>
                                    The document you were searching for was not found. It has either been moved, never existed, or you might just have mistyped it. To find documents which have moved, you might try starting from the home page:
                                    <br><br>
                                    <center>
                                    <a href="<%=request.getContextPath()%>" target="_top">IntAct home page</a>
                                    </center>
                                    <br><br>
                                    Also, if you feel this page should exist, send us an <a href="mailto:intact-help@ebi.ac.uk">email</a>.
                                    <br>
                           </td>
                        </tr>
                      </table>
                </div>

                <br><br>
                <br><br>

                </td>
              </tr>
            </tbody>
          </table>
          </td>
        </tr>
      </tbody>
    </table>