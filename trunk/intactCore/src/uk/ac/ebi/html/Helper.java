package uk.ac.ebi.html;
import java.io.Writer;
import java.io.PrintWriter;


/**
	This class is provided to help generate EBI specific HTML pages from within
    a Java program.
	The static methods allow the prodution of HTML on-the-fly.
	The instance methods expose a bean interface for use in bean obsessed
    platforms (e.g. tag based jsp).
	@author Adam Lowe, modified by Sugath Mudali (smudali@ebi.ac.uk) for IntAct
	@version $Id$
*/

public class Helper
{
	/** list of internal website tabs:
		identifying string, link to appropriate page, text to be displayed
	*/
	private static final String[][] intTags = {
		{"seqdb/introduction", "http://www3.ebi.ac.uk/internal/seqdb/introduction/", "INTRODUCTION"},
		{"seqdb/organisation", "http://www3.ebi.ac.uk/internal/seqdb/organisation/", "GROUP ORGANISATION"},
		{"seqdb/projects", "http://www3.ebi.ac.uk/internal/seqdb/projects/", "GROUP PROJECTS"},
		{"seqdb/curators", "http://www3.ebi.ac.uk/internal/seqdb/curators/", "CURATION &amp; SUBMISSION"},
		{"seqdb/computing", "http://www3.ebi.ac.uk/internal/seqdb/computing/", "COMPUTING PROCEDURES"},
		{"seqdb/release", "http://www3.ebi.ac.uk/internal/seqdb/release/", "RELEASE &amp; STATUS"}
	};

	/**
		Internal pages use the same stylesheet, so this lot is static
	*/
	private static final String intBodyStart =
		"<body  bgcolor=\"#ffffff\" marginwidth=\"0\" marginheight=\"0\" leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\">\n"
		+ "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"menubody\">\n"
		+ "  <form action=\"http://www.ebi.ac.uk/cgi-bin/search/internal.cgi\" method=\"get\" name=\"search\">\n"
		+ "    <tr>\n"
		+ "      <td width=\"100%\" align=\"center\"><a href=\"http://www3.ebi.ac.uk/internal/seqdb/index.html\" class=\"headlink\">Home</a><span class=\"headtext\"> | </span><a href=\"http://www3.ebi.ac.uk/internal/seqdb/organisation/contact/index.html\" class=\"headlink\">Contact</a><span class=\"headtext\"> | </span><a href=\"http://www3.ebi.ac.uk/internal/seqdb/site/index.html\" class=\"headlink\">Site</a><span class=\"headtext\"> | <a href=\"#\" class=\"headlink\"></a></span><a href=\"http://www3.ebi.ac.uk/internal/seqdb/help/index.html\" class=\"headlink\">Help</a> </td>\n"
		+ "      <td valign = \"bottom\" rowspan=\"2\"><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/spacer.gif\" width=\"10\" height=\"10\"></td>\n"
		+ "      <input type=\"hidden\" name=\"w\" value=\"1\">\n"
		+ "      <input type=\"hidden\" name=\"sort\" value = \"Scores\">\n"
		+ "      <input type=\"hidden\" name=\"display\" value = \"20\">\n"
		+ "      <input type=\"hidden\" name=\"b\" value=\"1\">\n"
		+ "      <input type=\"hidden\" name=\"t\" value=\"1\">\n"
		+ "      <input type=\"hidden\" name=\"default\" value=\"1\">\n"
		+ "      <input type=\"hidden\" name=\"d\" value=\"1\">\n"
		+ "      <input type=\"hidden\" name=\"k\" value=\"1\">\n"
		+ "      <input type=\"hidden\" name=\"showm\" value=\"5\">\n"
		+ "      <td  align = \"center\" valign = \"middle\" nowrap class=\"headtext\"><nobr>&nbsp;Site search&nbsp;</nobr></td>\n"
		+ "      <td  align = \"center\" valign = \"middle\">\n"
		+ "  <span class = \"small\">\n"
		+ "        <input type=\"text\" name=\"terms\" value=\"\" onFocus=\"select(this);\"  maxlength = \"50\" size = \"15\" class = \"headtext\">\n"
		+ "  </span></td>\n"
		+ "      <td  align = \"center\" valign = \"middle\"><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/spacer.gif\" width = \"10\" height = \"10\"></td>\n"
		+ "      <td  align = \"center\" valign = \"middle\">\n"
		+ "  <span class = \"small\">\n"
		+ "        <input type=\"submit\" value=\"Go\" class = \"headtext\">\n"
		+ "  </span></td>\n"
		+ "      <td  align = \"center\" valign = \"middle\" ><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/spacer.gif\" width = \"10\" height = \"10\"></td>\n"
		+ "    </tr>\n"
		+ "  </form>\n"
		+ "</table>\n"
		+ "<table width=\"100%\" height=\"51\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n"
		+ " <tr>\n"
		+ "    <td width=\"129\" height=\"51\" align=\"left\"><a href=\"http://www3.ebi.ac.uk/internal/seqdb/index.html\"><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/bann_left.gif\" width=\"129\" height=\"51\" hspace=\"0\" vspace=\"0\" border=\"0\"></a></td>\n"
		+ "    <td width=\"100%\" height=\"51\" background=\"http://www3.ebi.ac.uk/internal/seqdb/images/bann_mid.gif\">&nbsp;</td> \n"
		+ "    <td width=\"219\" height=\"51\" align=\"right\"><a href=\"http://www.ebi.ac.uk/\"><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/bann_right.gif\" width=\"219\" height=\"51\" border=\"0\"></a></td>\n"
		+ "  </tr>\n"
		+ "</table>\n"
	;

	/** instance fields for bean access... */	

	private String topMenuItem = "services";

	/** set the tab to which this page belongs
		
		if it starts with "internal/" an internal page is generated,
		current meaningful topMenuItems are:
			"internal/seqdb/introduction","internal/seqdb/organisation",
			"internal/seqdb/projects", "internal/seqdb/curators", 
			"internal/seqdb/computing" and "internal/seqdb/release"
			
		else an external page is generated, current meaningful internal menuitems are:
			"aboutebi", "groups", "services", "tools", "databases", "submissions"
		
		default is to use the stylesheets and images in /services
	*/
	public void setTopMenuItem(String menuItem) {
		this.topMenuItem = menuItem;
	}

	/** javascript to call on loading the page
		the surrounding html tags are generated - just javascript please!*/
	private String loadMethods;

	/**
		Returns the value previously set by setLoadMethods
		@see #setLoadMethods
	*/
	public String getLoadMethods ()  {
		return loadMethods; 
	}

	/** Sets the javascript to call on loading the page the surrounding html tags are generated - just javascript please!*/
	public void setLoadMethods(String loadMethods)  {
	 	this.loadMethods = loadMethods; 
	}	
	
	/** javascript to call on leaving the page
		the surrounding html tags are generated - just javascript please!
		Also note that it isn't good practice to annoy people with popups and such at this point!
	*/
	private String unLoadMethods;

	/** Sets the javascript to call on leaving the page
		the surrounding html tags are generated - just javascript please!
		Also note that it isn't good practice to annoy people with popups and such at this point!
	*/
	public void setUnLoadMethods (String unLoadMethods)  {
        this.unLoadMethods = unLoadMethods;
	}	

	
	/** embedded in page &lt;head&gt;
		You need the appropriate tags (i.e. &lt;script&gt;&lt/script&gt;) for this to work.
	*/
	private String headerScript;

	/** Sets a string to be embedded in page &lt;head&gt;
		You need the appropriate tags (i.e. &lt;script&gt;&lt/script&gt;) for this to work.
	*/
	public void setHeaderScript(String headerScript)  {
        this.headerScript = headerScript;
	}	

	private PrintWriter out = null;
	
	/** Set the Writer to output the page to. */
	public void setOut (Writer out) {
        this.out = new PrintWriter(out);
	}

    public String getHeader() {
        out.print(
            "<!-- InstanceBegin template=\"http://www3.ebi.ac.uk/Templates/organisation.dwt\" codeOutsideHTMLIsLocked=\"false\" -->\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n"
            + "<META HTTP-EQUIV=\"Owner\" CONTENT= \"EMBL Outstation - Hinxton, European Bioinformatics Institute\">\n"
            + "<META NAME=\"Author\" CONTENT=\"EBI External Servces\">\n"
            + "<script language=\"javascript\" src=\"http://www.ebi.ac.uk/internal/seqdb/cgi_files/master.js\"></script>\n"
        );

        // START User-defined JAVASCRIPT-----------------------------------------------------------------------------------------
        // headerScript
        if (headerScript != null) out.print(headerScript);

        out.print("<script language = \"javascript\">\n");

        // loadMethods..
        out.print("function callLoadMethods()" + "\n" + "{" + "\n");
        if (loadMethods != null)
            out.print(loadMethods);
        out.print(";" + "\n" + "}" + "\n");

        // unLoadMethods..
        out.print("function callUnLoadMethods()" + "\n" + "{" + "\n");
        if (unLoadMethods != null)
            out.print(unLoadMethods);
        out.print(";" + "\n" + "}" + "\n");

        out.print("</script>");
        // END User-defined JAVASCRIPT-----------------------------------------------------------------------------------------
        return "";
    }

    public String getHeaderBody() {
        int topmenuindex = 0;
		for (int i=0; i<intTags.length; i++) {
			if (intTags[i][0].equals(topMenuItem)) {
				topmenuindex = i;
				break;
			}
		}
        out.print("<!-- InstanceBeginEditable name=\"doctitle\" -->\n");

        out.print(intBodyStart);

        // main navigation bar..
        out.print("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n"
                    + "  <tr align=\"center\">\n");

        for (int i=0; i<intTags.length; i++) {
            if (i==topmenuindex) {
                out.print("    <td  width=\"10\" height=\"16\" nowrap><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/menu_left.gif\" width=\"10\" height=\"16\"></td>\n");
                out.print("    <td nowrap class=\"quickbody\"><a href=\"" + intTags[i][1] + "\" class=\"menulinkover\">");
                out.print(intTags[i][2] + "</a></td>\n");
            } else {
                if (i==topmenuindex+1)
                    out.print ( "    <td  width=\"10\" height=\"16\" nowrap><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/menu_right.gif\" width=\"10\" height=\"16\"></td>\n");
                else
                    out.print("    <td  width=\"10\" height=\"16\" nowrap><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/menubar.gif\" width=\"10\" height=\"16\"></td>\n");
                out.print("    <td nowrap><a href=\"" + intTags[i][1]);
                out.print("\" class=\"menulink\">" + intTags[i][2] + "</a></td>\n");
            }
        }
        if (topmenuindex==intTags.length)
            out.print ( "    <td  width=\"10\" height=\"16\" nowrap><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/menu_right.gif\" width=\"10\" height=\"16\" hspace=\"0\" vspace=\"0\" border=\"0\" align=\"left\"></td>\n");
        else
            out.print("      <td  width=\"10\" height=\"16\" nowrap><img src=\"http://www3.ebi.ac.uk/internal/seqdb/images/menubar.gif\" width=\"10\" height=\"16\" hspace=\"0\" vspace=\"0\" border=\"0\" align=\"left\"></td>\n");

        out.print ("<td  width=\"50%\" height=\"16\" nowrap><img src=\"/internal/seqdb/images/spacer.gif\" width=\"2\" height=\"16\"></td>\n"
                 + "</tr>\n" );

        out.print(" <tr  class=\"quickbody\">\n");

        for (int i=0; i<intTags.length+1; i++) {
            if (i==intTags.length) {
                out.print(" <td  width=\"10\" height=\"3\" nowrap><img src=\"/internal/seqdb/images/spacer.gif\" width=\"10\" height=\"3\"></td>\n"
                    + " <td height=\"3\" width=\"" + "100%" + "\"><img src=\"/internal/seqdb/images/spacer.gif\" width=\""
                    + "1" + "\" height=\"3\"></td>\n");
            } else {
                out.print(" <td  width=\"10\" height=\"3\" nowrap><img src=\"/internal/seqdb/images/spacer.gif\" width=\"10\" height=\"3\"></td>\n"
                    + " <td height=\"3\" width=\"" + "93" + "\"><img src=\"/internal/seqdb/images/spacer.gif\" width=\""
                    + "93" + "\" height=\"3\"></td>\n");
            }
        }

        out.print("</tr>\n</table>\n");
        return "";
    }
}
