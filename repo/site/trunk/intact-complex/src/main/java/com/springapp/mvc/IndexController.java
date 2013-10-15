package com.springapp.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {
    private String QueryBegin = "\t<form id=\"complex-search\" name=\"complex-search\" action=\"\" method=\"get\">\n" +
            "\t\t<fieldset>\n" +
            "\t\t\t<div>\n" +
            "\t\t\t\t<h3>Query: </h3>\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<div class=\"left\" style=\"float: left; text-align: center;\">\n" +
            "\t\t\t\t<label>\n" +
            "\t\t\t\t\t<input type=\"text\" name=\"complex-searchbox\" id=\"complex-searchbox\" size=\"50\" cols=\"50\" rows=\"25\" style=\"min-weight:100px; max-weight:150px; min-height:50px; max-height:100px; resize: none;\" value=\"";
    private String QueryEnd = "\"></input>\n" +
            "\t\t\t\t</label>\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<br>\n" +
            "\t\t\t<div class=\"right\" style=\"float: center; text-align: center;\">\n" +
            "\t\t\t\t<input type=\"submit\" name=\"submit\" value=\"Search\" class=\"submit\">\n" +
            "\t\t\t</div>\n" +
            "\t\t</fieldset>\n" +
            "\t</form>";
    private String BeginResult = "\t<form>\n" +
            "\t\t<fieldset>\n" +
            "\t\t\t<div>\n" +
            "\t\t\t\t<h3>Result:</h3>\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<div>\n" +
            "\t\t\t\t<label>\n" +
            "\t\t\t\t\t<textarea name=\"complex-answer\" cols=\"50\" rows=\"500\" style=\"max-weight:150px; max-height:300px; resize: none;\">";
    private String EndResult = "</textarea>\n" +
            "\t\t\t\t</label>\n" +
            "\t\t\t</div>\n" +
            "\t\t</fieldset>\n" +
            "\t</form>";
	@RequestMapping(method = RequestMethod.GET)
	public String index(HttpServletRequest request, ModelMap model) {
		model.addAttribute( "title", "Complex Searcher &lt; IntAct &lt; EMBL-EBI" ) ;
        model.addAttribute( "complex_searcher", "Complex Searcher" ) ;
        String query = request.getParameter("complex-searchbox") ;
        if ( query != null ) {
            query = URLDecoder.decode(query);
            model.addAttribute( "query", QueryBegin + query + QueryEnd ) ;
            String result = "" ;
            //ComplexSearcher complexSearcher = new
            model.addAttribute( "result", BeginResult + result + EndResult);
        }
        return "index";
	}
}